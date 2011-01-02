package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.GroupData;
import valueobject.SingleEvent;
import valueobject.SingleTask;
import businessobject.Configuration;
import businessobject.Converter;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.query.Constraint;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.ta.Activatable;
import com.db4o.ta.TransparentActivationSupport;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;


/**
 * Singleton class that acts as a database that will save all the groups into the database
 * and manage them
 */
public enum GroupDatabase {
	instance; // singleton instance
	private static final String DATABASE_NAME = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/GroupDatabase";
	private static final Logger log = LoggerFactory.getLogger(GroupDatabase.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	/*
	 * Add the request to the GroupRequest table
	 */
	public  static boolean  inviteToGroup(String sender, String receiver,String groupID, String message){
		Connection conn= (Connection) dbManager.dbConnect();
		
		String insertQuery="Insert into GroupRequest (Sender,UserGroup,User,Message) values ('"+sender+"','"+groupID+"','"+receiver+"','"+message+"')";
		QueryStatus qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();

			log.error("Error during group invite (from "+sender+") for adding("+receiver+" invited to group "+groupID+")... not added");
			dbManager.dbDisconnect(conn);
			return false;
		}
		log.info("Group invite from "+sender+" ("+receiver+" invited to group "+groupID+")... added!");
		
		
		return true;
	}
	
	/*
	 * Add the group to the database and set the owner, finally add the owner as group member
	 */
	public static int createGroup(String groupName, String owner){
		int groupID=-1;
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... group not created");
			dbManager.dbDisconnect(conn);
			return -1;
		}
		
		String insertQuery="Insert into UserGroup (group_name,owner) values ('"+groupName+"','"+owner+"');";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during group creation ('"+groupName+"','"+owner+"').. not created");
			dbManager.dbDisconnect(conn);
			return -1;
		}
		
		//Retrive groupID
		String selectQuery="Select LAST_INSERT_ID() as 'groupID' FROM UserGroup";
		qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		try{
			//Get groupID from database
			if(rs.next()){
				groupID=Integer.parseInt(rs.getString("groupID"));
				
			}else{
				//Rolling back
				dbManager.rollbackTransaction(conn);
				dbManager.dbDisconnect(conn);
				return -1;
			}
		}catch(SQLException sqlE){
			//TODO manage exception
			sqlE.printStackTrace();
			//Rolling back
			dbManager.rollbackTransaction(conn);
			dbManager.dbDisconnect(conn);
		}
			
		log.info("Group ('"+groupName+"','"+owner+"') created! GroupID="+groupID);
		
		//Add the owner to the GroupMember table

		insertQuery="Insert into GroupMember (UserGroup,User) values ('"+groupID+"','"+owner+"');";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during add of the owner to the GroupMember Table");
			dbManager.dbDisconnect(conn);
			return -1;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		return groupID;
	}
	
	
	/*
	 * Get from the database the list of all the group in wich the user is involved
	 */
	public static List<GroupData> getUserGroups(String username){
		List<GroupData> userGroupList=new ArrayList<GroupData>();
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Groups in wich the user is member
		String selectQuery="Select * from UserGroup where id in(Select UserGroup from GroupMember where User='"+username+"')";

		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		GroupData group=null;
		
		
		try{
			while(rs.next()){
				userGroupList.add(
						new GroupData(
								rs.getString("id"),
								rs.getString("group_name"),
								rs.getString("owner")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		
		return userGroupList;
		
	}
	
	/*
	 * Accept the invite to join the group
	 */
	
	public static boolean acceptGroupInviteRequest(String requestId){
		
		//Starting transaction
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... Invite not accepted");
			dbManager.dbDisconnect(conn);
			return false;
		}	
		//Get the request data
		String selectQuery="Select * from GroupRequest where id ="+requestId;

		
		qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		GroupData group=null;
		
		String groupID="";
		String userToAdd="";
		
		try{
			while(rs.next()){
				groupID=rs.getString("UserGroup");
				userToAdd=rs.getString("User");
			}
		}catch(SQLException sqlE){
			//Rolling back
			dbManager.rollbackTransaction(conn);
			log.error("Error during the retreive of data for the request "+ requestId+" rolling back");
			dbManager.dbDisconnect(conn);
			
		}
		
		//Add user to the group
		String insertQuery="Insert into GroupMember (UserGroup,User) values ('"+groupID+"','"+userToAdd+"');";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during add of the user "+userToAdd+" to the GroupMember Table, rolling back");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		//Delete the request
		//Add user to the group
		String deleteQuery="Delete from GroupRequest where id='"+requestId+"'";
		System.out.println(deleteQuery);
		qs=dbManager.customQuery(conn, deleteQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during deleting of the join group request with id="+requestId+" rolling back");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return true;
		
	}
	

}
