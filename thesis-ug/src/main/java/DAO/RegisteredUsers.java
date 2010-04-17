package DAO;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

/**
 * Singleton class that acts as a database that will saves unique UUID of the user, username and password to this
 * application; and username, password to Google Service (calendar).
 * 
 */
public enum RegisteredUsers {
	instance; //singleton instance
	private static final String DATABASE_NAME = "src/main/resources/RegisteredUsers";
	private final static Logger log = LoggerFactory.getLogger(RegisteredUsers.class);
	
	/**
	 * Add new user to database 
	 * @param username username to the system
	 * @param password password to the system
	 */
	public static void addUsers (String username, String password){
		if (usernameExist(username)) {
			log.warn("Username already exist, choose different username");
			return; //check for redundant entry, because db40 saves redundant object
		}
		Users toAdd =  instance.new Users (username, password);		
		ObjectContainer db = openDatabase();
		try {			
			db.store(toAdd);
		} finally{
			db.close();			
		}
	}
	
	/**
	 * Add new user to database 
	 * @param username username to the system
	 * @param password password to the system
	 * @param googleID username to logon to Google Services
	 * @param googlePassword password to logon to Google Services
	 */
	public static void addUsers(String username, String password, String googleID, String googlePassword){
		if (usernameExist(username)) {
			log.warn("Username already exist, choose different username");			
			return; //check for redundant entry, because db40 saves redundant object
		}
		Users toAdd = instance.new Users (username, password, googleID, googlePassword);
		ObjectContainer db = openDatabase();
		try {			
			db.store(toAdd);
		} finally {
			db.close();
		}		
	}
	
	/**
	 * Get unique ID of the user 
	 * @param username username to the system
	 */	
	public static String getID(final String username) {
		ObjectContainer db = openDatabase();
		try {
		List <Users> result = db.query(new Predicate<Users>() {
		    public boolean match(Users user) {
		        return user.username.equalsIgnoreCase(username);
		    }
		});
		return result.isEmpty()? null:result.get(0).ID;		
		}finally{
			db.close();
		}
	}

	/**
	 * Delete the user from database
	 * @param ID UUID of the user
	 */	
	public static void deleteUsers(final String ID) {
		ObjectContainer db = openDatabase();
		try {
			List<Users> result = db.query(new Predicate<Users>() {
				public boolean match(Users user) {
					return user.ID.equals(ID);
				}
			});
			if (result.isEmpty()) {
				log.warn("Cannot find user with ID "+ID);
				return;
			}
			Users toDelete = result.get(0);
			db.delete(toDelete);
		} finally {
			db.close();
		}
	}
	
	/**
	 * Set google user name for this user ID
	 * @param ID UUID of the user	 
	 * @param googleID username to logon to Google Services	 * 
	 */	
	public static void setGoogleUsername(final String ID, String googleID){
		ObjectContainer db = openDatabase();
		try {
			List<Users> result = db.query(new Predicate<Users>() {
				public boolean match(Users user) {
					return user.ID.equals(ID);
				}
			});
			if (result.isEmpty()) {
				log.warn("Cannot find user with ID "+ID);
				return;
			}
			Users toChange = result.get(0);
			toChange.googleUsername = googleID;
			db.store(toChange);
		} finally {
			db.close();
		}		
	}
	
	/**
	 * Set google password for this user ID
	 * @param ID UUID of the user	 
	 * @param googlePassword password to logon to Google Services
	 */	
	public static void setGooglePassword(final String ID, String googlePassword){
		ObjectContainer db = openDatabase();
		try {
			List<Users> result = db.query(new Predicate<Users>() {
				public boolean match(Users user) {
					return user.ID.equals(ID);
				}
			});
			if (result.isEmpty()) {
				log.warn("Cannot find user with ID "+ID);
				return;
			}
			Users toChange = result.get(0);
			toChange.googlePassword = googlePassword;
			db.store(toChange);
		} finally {
			db.close();
		}		
	}
	
	/**
	 * This method find a username in database, and check whether its password is equals
	 * @param username username to the system
	 * @param password password to the system
	 */
	public static String checkMatch(final String username, final String password){
		ObjectContainer db = openDatabase();
		try {
			List<Users> result = db.query(new Predicate<Users>() {
				public boolean match(Users user) {
					return (user.username.equalsIgnoreCase(username)&& 
							user.password.equals(password));
				}
			});			
			return result.isEmpty()? null:result.get(0).ID;			
		} finally {
			db.close();
		}		
	}
	
	private static ObjectContainer openDatabase() {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
		        .newConfiguration(), DATABASE_NAME);
		return db;
	}
	
	private static boolean usernameExist(final String username){
		ObjectContainer db = openDatabase();
		try {
		List <Users> result = db.query(new Predicate<Users>() {
		    public boolean match(Users user) {
		        return user.username.equalsIgnoreCase(username);
		    }
		});
		return !result.isEmpty();
		}finally{
			db.close();
		}		
	}
	/**
	 * 
	 * The basic class to be saved in the RegisteredUsers database
	 *
	 */
	private class Users {
		/**
		 * unique UUID for each user
		*/
		public String ID;
		/**
		 * unique username for each user
		*/
		public String username;
		/**
		 * password set by the user
		*/
		public String password;
		/**
		 * Google Account username of the user
		*/
		public String googleUsername;
		/**
		 * Google Account password of the user
		*/
		public String googlePassword;
		public Users (String username, String password){
			this.ID = UUID.randomUUID().toString();
			this.username=username;
			this.password=password;
			this.googleUsername=null;
			this.googlePassword=null;
		}
		public Users (String username, String password, String googleusername, String googlepassword){
			this.ID = UUID.randomUUID().toString();
			this.username=username;
			this.password=password;
			this.googleUsername=googleusername;
			this.googlePassword=googlepassword;
		}
	}
}
