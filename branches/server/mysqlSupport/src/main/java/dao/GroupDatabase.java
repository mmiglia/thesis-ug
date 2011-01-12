package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.GroupData;
import valueobject.GroupInviteData;
import businessobject.Configuration;
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
		
		if(isUserAlreadyInGroup(receiver,groupID)){
			return false;
		}
		
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
	
	private static boolean isUserAlreadyInGroup(String username,String groupID){
		
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Groups in wich the user is member
		String selectQuery="Select count(*) as member from GroupMember where UserGroup='"+groupID+"' and User='"+username+"'";

		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		boolean isMember=false;
		try{
			while(rs.next()){
				isMember=(rs.getInt("member")==0)?false:true;
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return isMember;
	}
	
	/*
	 * Add the group to the database and set the owner, finally add the owner as group member
	 */
	public static GroupData createGroup(GroupData group){
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... group not created");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		String insertQuery="Insert into UserGroup (group_name,owner) values ('"+group.groupName+"','"+group.owner+"');";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during group creation ('"+group.groupName+"','"+group.owner+"').. not created");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		//Retrive groupID
		String selectQuery="Select LAST_INSERT_ID() as 'groupID' FROM UserGroup";
		qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		try{
			//Get groupID from database
			if(rs.next()){
				group.groupID=rs.getString("groupID");
				
			}else{
				//Rolling back
				dbManager.rollbackTransaction(conn);
				dbManager.dbDisconnect(conn);
				return null;
			}
		}catch(SQLException sqlE){
			//TODO manage exception
			sqlE.printStackTrace();
			//Rolling back
			dbManager.rollbackTransaction(conn);
			dbManager.dbDisconnect(conn);
		}
			
		log.info("Group ('"+group.groupName+"','"+group.owner+"') created! GroupID="+group.groupID);
		
		//Add the owner to the GroupMember table

		insertQuery="Insert into GroupMember (UserGroup,User) values ('"+group.groupID+"','"+group.owner+"');";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during add of the owner to the GroupMember Table");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		return group;
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
	
	public static boolean acceptGroupInviteRequest(GroupInviteData invite){
		
		if(isUserAlreadyInGroup(invite.userToInvite,invite.groupID)){
			return false;
		}
		
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
		
		
		//Add user to the group
		String insertQuery="Insert into GroupMember (UserGroup,User) values ('"+invite.groupID+"','"+invite.userToInvite+"');";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during add of the user "+invite.userToInvite+" to the GroupMember Table, rolling back");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		//Delete the invite
		String deleteQuery="Delete from GroupRequest where id='"+invite.requestID+"'";
		System.out.println(deleteQuery);
		qs=dbManager.customQuery(conn, deleteQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during deleting of the join group request with id="+invite.requestID+" rolling back");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return true;
		
	}
	
	public boolean refuseGroupInviteRequest(GroupInviteData invite) {

		//Starting transaction
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... Invite not refused");
			dbManager.dbDisconnect(conn);
			return false;
		}	
		
		
		//Delete the invite 
		String deleteQuery="Delete from GroupRequest where id='"+invite.requestID+"'";
		System.out.println(deleteQuery);
		qs=dbManager.customQuery(conn, deleteQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during deleting of the join group request with id="+invite.requestID+" rolling back");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return true;
		
	}

	public List<GroupInviteData> getGroupInviteRequest(String username) {
		List<GroupInviteData> groupInviteList=new ArrayList<GroupInviteData>();
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Join to group invite
		String selectQuery="Select * from GroupRequest join UserGroup on GroupRequest.UserGroup=UserGroup.id where GroupRequest.User='"+username+"'";

		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		
		
		try{
			while(rs.next()){
				groupInviteList.add(
						new GroupInviteData(
								rs.getString("id"),
								rs.getString("UserGroup"),
								rs.getString("group_name"),
								rs.getString("User"),
								rs.getString("message"),
								rs.getString("Sender")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		
		return groupInviteList;
	}


	

}