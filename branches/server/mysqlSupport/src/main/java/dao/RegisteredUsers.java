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
	public static void addUsers (String firstname, String lastname, String email, String username, 
			String password, String ver_code){
		if (usernameExist(username)) {
			log.warn("Username already exist, choose different username");
			return;
		}
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String insertQuery="Insert into User (firstname, lastname, email, username, password, verification_code) " +
				"values ('"+firstname+"','"+lastname+"','"+email+"','"+username+"','"+password+"','"+ver_code+"')";
		QueryStatus qs=dbManager.customQuery(conn, insertQuery);
		
		dbManager.dbDisconnect(conn);
		
	}
	
	public static boolean verificationExist(final String code, final String email) {
		Connection conn = (Connection) dbManager.dbConnect();
		String verificationExistQuery = "select username from User where verification_code='"+code+"' and email='"+email+"'";
		QueryStatus qs=dbManager.customSelect(conn, verificationExistQuery);
		boolean codeExist = false;
		if(!qs.execError){
			ResultSet rs=(ResultSet)qs.customQueryOutput;
			try {
				if(rs.next()){
					// the verification code exist -> set verified attibute to 1
					codeExist=true;
					setVerified(true, rs.getString("username"));
				}
				else{					
					codeExist=false;
				}
				dbManager.dbDisconnect(conn);
			}
			catch(SQLException sqlE){
				//TODO che fare con la sqlE?
				return codeExist;
			}
			finally{
					dbManager.dbDisconnect(conn);
			}
		}else{
			qs.occourtedErrorException.printStackTrace();
			qs.explainError();
		}
		
		return codeExist;
	}
	
	public static void setVerified(final boolean value, final String username) {
		Connection conn = (Connection) dbManager.dbConnect();
		int v = 0;
		if (value)
			v=1;
		else
			v=0;
		String query = "UPDATE User SET verified="+v+" WHERE username='"+username+"'";
		dbManager.customQuery(conn, query);
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
	
	/**
	 * This method is used to know if the user has verified his account.
	 * it returns boolean
	 * @param id id of the user
	 * @return boolean true if registration verified, false otherwise
	 */
	public static boolean checkVerified (String username) {
		Connection conn= (Connection) dbManager.dbConnect();
		String query = "Select verified from User where username='"+username+"'";
		QueryStatus qs=dbManager.customSelect(conn, query);
		boolean verified = false;
		if(!qs.execError){
			ResultSet rs = (ResultSet) qs.customQueryOutput;
			try {				
				if(rs.next()){
					verified = rs.getBoolean("verified");
					return verified;
				}
			}catch (SQLException sqlE){
				
			}finally {
				//int newTrialLogin = trialLogin-1;
				//qs = dbManager.customQuery(conn, "update User set trial_login="+newTrialLogin+" where id='"+id+"'");
				dbManager.dbDisconnect(conn);
			}
		}else{			
			System.out.println(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
		}		
		dbManager.dbDisconnect(conn);
		return verified;
	}
	
	/**
	 * This method is used to know if the user still has other trial login.
	 * it returns int, the trial_login value
	 * @param id id of the user
	 * @return int trial_login value
	 */
	public static int checkTrialLogin (String username) {
		Connection conn= (Connection) dbManager.dbConnect();
		String query = "Select trial_login from User where username='"+username+"'";
		QueryStatus qs=dbManager.customSelect(conn, query);
		int trialLogin = 0;
		if(!qs.execError){
			ResultSet rs = (ResultSet) qs.customQueryOutput;
			try {				
				if(rs.next()){
					trialLogin = rs.getInt("trial_login");
					return trialLogin;
				}
			}catch (SQLException sqlE){
				
			}finally {
				int newTrialLogin = trialLogin-1;
				qs = dbManager.customQuery(conn, "update User set trial_login="+newTrialLogin+" where username='"+username+"'");
				dbManager.dbDisconnect(conn);
			}
		}else{			
			System.out.println(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
		}		
		dbManager.dbDisconnect(conn);
		return trialLogin;
	}
	
	public static void logout(String username){
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String updateQuery="Update User set active=0, sessionKey='' where username='"+username+"'";
		QueryStatus qs=dbManager.customQuery(conn, updateQuery);
		dbManager.dbDisconnect(conn);
		
	}
	
	private static String addVerifiedUserQuery (String firstname, String lastname, String email, String username, 
			String password){
		if (usernameExist(username)) {
			log.warn("Username already exist, choose different username");
			return username+"EXSIST!";
		}
		
		return "Insert into User (firstname, lastname, email, username, password, verified) " +
				"values ('"+firstname+"','"+lastname+"','"+email+"','"+username+"','"+password+"','1')";

		
	}
	
	public static void main(String[] args){
		
		
		String[] userIDs=new String[12];
		userIDs[0]="GE-01";
		userIDs[1]="GE-02";
		userIDs[2]="GE-03";
		userIDs[3]="GE-04";
		userIDs[4]="GE-05";
		userIDs[5]="GE-06";
		userIDs[6]="PD-01";
		userIDs[7]="PD-02";
		userIDs[8]="PD-03";
		userIDs[9]="PD-04";
		userIDs[10]="PD-05";
		userIDs[11]="PD-06";
		
		for(int i=0;i<userIDs.length;i++){
			System.out.println(addVerifiedUserQuery(userIDs[i],userIDs[i], userIDs[i], userIDs[i], userIDs[i]));
		}
		
	}
	
}
