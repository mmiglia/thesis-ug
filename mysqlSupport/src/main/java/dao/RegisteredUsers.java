package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.Configuration;


import dao.management.mysql.MySQLDBManager;
import dao.management.QueryStatus;
import java.sql.ResultSet;

/**
 * This class manage the interaction with the MySql database for the User management, it has method to create
 * delete and check credentials for users.
 * There is also one method to store credentials for external services
 * 
 */
public enum RegisteredUsers {
	instance; //singleton instance
	private static final Logger log = LoggerFactory.getLogger(RegisteredUsers.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	/**
	 * Add new user to database 
	 * @param username username to the system
	 * @param password password to the system
	 */
	public static void addUsers (String firstname, String lastname, String email, String username, String password){
		if (usernameExist(username)) {
			log.warn("Username already exist, choose different username");
			return;
		}
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String insertQuery="Insert into User (firstname, lastname, email, username, password) " +
				"values ('"+firstname+"','"+lastname+"','"+email+"','"+username+"','"+password+"')";
		QueryStatus qs=dbManager.customQuery(conn, insertQuery);
		
		dbManager.dbDisconnect(conn);
		
	}
	
	public static boolean usernameExist(final String username){
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String userExistQuery="Select * from User where username='"+username+"'";
		
		QueryStatus qs=dbManager.customSelect(conn, userExistQuery);
		boolean userExist=true;
		
		if(!qs.execError){
			ResultSet rs=(ResultSet)qs.customQueryOutput;
			try {
				if(rs.next()){
					
					userExist=true;
				}else{
					
					userExist=false;
				}
				dbManager.dbDisconnect(conn);
			}
			catch(SQLException sqlE){
				//TODO che fare con la sqlE?
				return userExist;
			}
			finally{
					dbManager.dbDisconnect(conn);
			}
		}else{
			qs.occourtedErrorException.printStackTrace();
			qs.explainError();
		}
		
		return userExist;
		
	}
	
	public static boolean useremailExist(final String email){
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String emailExistQuery="Select * from User where email='"+email+"'";
		
		QueryStatus qs=dbManager.customSelect(conn, emailExistQuery);
		boolean emailExist=true;
		
		if(!qs.execError){
			ResultSet rs=(ResultSet)qs.customQueryOutput;
			try {
				if(rs.next()){
					
					emailExist=true;
				}else{
					
					emailExist=false;
				}
				dbManager.dbDisconnect(conn);
			}
			catch(SQLException sqlE){
				//TODO che fare con la sqlE?
				return emailExist;
			}
			finally{
					dbManager.dbDisconnect(conn);
			}
		}else{
			qs.occourtedErrorException.printStackTrace();
			qs.explainError();
		}
		
		return emailExist;
		
	}
	
	
	
	/**
	 * Get unique ID of the user 
	 * @param username username to the system
	 */	
	public static String getID(final String username) {
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, "Select * from User where username='"+username+"'");
		
		
		if(!qs.execError){
			ResultSet rs=(ResultSet) qs.customQueryOutput;
			try{
				if(rs.next()){
					dbManager.dbDisconnect(conn);
					return Integer.toString(rs.getInt("username"));
				}
			}catch(SQLException sqlE){
				//TODO che fare qui?	
				dbManager.dbDisconnect(conn);
				return "-1";
			}
			
		}else{
			dbManager.dbDisconnect(conn);
			return "-1";			
		}
		
		dbManager.dbDisconnect(conn);
		return "-1";
		
	}

	/**
	 * Delete the user from database
	 * @param ID UUID of the user
	 */	
	public static boolean deleteUsers(final String ID) {
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String deleteQuery="Delete from User where username='"+ID+"'";
		QueryStatus qs=dbManager.customQuery(conn, deleteQuery);
		dbManager.dbDisconnect(conn);
		
		return true;
		
	}
	
	/**
	 * Set google user name for this user ID
	 * @param ID UUID of the user	 
	 * @param googleID username to logon to Google Services	 * 
	 */	
	public static void setServiceUsernamePassword(String userID, String serviceName, String serviceUser,String servicePassword){
		//TODO Implement this method
	}
	
	
	/**
	 * This method is used to know if the user is registered in the database and if there is a match,
	 * it returns the User ID
	 * @param username username to the system
	 * @param password password to the system
	 * @return specific userID of the match
	 */
	public static String checkMatch(final String username, final String password){

		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, "Select * from User where username='"+username+"' and password='"+password+"'");
		if(!qs.execError){
			ResultSet rs = (ResultSet) qs.customQueryOutput;
			try {				
				if(rs.next()){
					return rs.getString("username");
				}
			}catch (SQLException sqlE){
				
			}finally {
				dbManager.dbDisconnect(conn);
			}
		}else{
			
			System.out.println(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
		}

		
		dbManager.dbDisconnect(conn);
		return null;
	}
	
	public static void logout(String username){
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String updateQuery="Update User set active=0, sessionKey='' where username='"+username+"'";
		QueryStatus qs=dbManager.customQuery(conn, updateQuery);
		dbManager.dbDisconnect(conn);
		
	}
	
	public static void main(String[] args){
		
		
		
	}
	
}
