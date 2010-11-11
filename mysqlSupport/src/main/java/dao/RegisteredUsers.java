package dao;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.Configuration;

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

/**
 * Singleton class that acts as a database that will saves unique UUID of the user, username and password to this
 * application; and username, password to Google Service (calendar).
 * 
 */
public enum RegisteredUsers {
	instance; //singleton instance
	private static final String DATABASE_NAME = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/RegisteredUsers";
	private static final Logger log = LoggerFactory.getLogger(RegisteredUsers.class);
	private static ObjectServer server ; //db4o server
	private static boolean databaseOpen = false; // true means database server is initialized
	private static final Object lock = new Object(); // mutex lock
	
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
			Query query = db.query();
			query.constrain(Users.class);
			query.descend("username").constrain(username);
			ObjectSet<Users> result = query.execute();
			if (!result.isEmpty()){
				result.get(0).activateRead();
				return result.get(0).ID;
			}
			else return null;
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
			Query query = db.query();
			query.constrain(Users.class);
			query.descend("ID").constrain(ID);
			ObjectSet<Users> result = query.execute();
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
			Query query = db.query();
			query.constrain(Users.class);
			query.descend("ID").constrain(ID);
			ObjectSet<Users> result = query.execute();
			if (result.isEmpty()) {
				log.warn("Cannot find user with ID "+ID);
				return;
			}
			Users toChange = result.get(0);
			toChange.activateWrite();
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
			Query query = db.query();
			query.constrain(Users.class);
			query.descend("ID").constrain(ID);
			ObjectSet<Users> result = query.execute();
			if (result.isEmpty()) {
				log.warn("Cannot find user with ID "+ID);
				return;
			}
			Users toChange = result.get(0);
			toChange.activateWrite();
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
	 * @return specific userID of the match
	 */
	public static String checkMatch(final String username, final String password){
		ObjectContainer db = openDatabase();
		try {
			Query query = db.query();
			query.constrain(Users.class);
			Constraint constr=query.descend("password").constrain(password);
			query.descend("username").constrain(username).and(constr);
			ObjectSet<Users> result = query.execute();
			if (!result.isEmpty()){
				result.get(0).activateRead();
				return result.get(0).ID;
			}
			else return null;	
		} finally {
			db.close();
		}		
	}
	
	private static ObjectContainer openDatabase() {		
		if (!databaseOpen) { //outer selection to enable faster access
			synchronized (lock){
			/*to avoid racing condition after outer IF above
			 e.g. possible to acquire same databaseOpen value
			 and thus open server multiple times*/
			if (databaseOpen) return server.openClient(); 
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			config.common().add(new TransparentActivationSupport());
			config.common().activationDepth(2);
			server= Db4oClientServer.openServer(config, DATABASE_NAME, 0);
			databaseOpen=true;
			}
		}
		ObjectContainer db = server.openClient();
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
	private class Users implements Activatable{
		private transient Activator _activator;
		/**
		 * unique UUID for each user
		*/
		private String ID;
		/**
		 * unique username for each user
		*/
		private String username;
		/**
		 * password set by the user
		*/
		private String password;
		/**
		 * Google Account username of the user
		*/
		private String googleUsername;
		/**
		 * Google Account password of the user
		*/
		private String googlePassword;
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
		
		public void activateWrite(){
			activate(ActivationPurpose.WRITE);	
		}
		
		public void activateRead(){
			activate(ActivationPurpose.READ);	
		}
		
		@Override
		public void activate(ActivationPurpose purpose) {
			 if(_activator != null) {
		            _activator.activate(purpose);
		        }
		}

		@Override
		public void bind(Activator activator) {
		       if (_activator == activator) {
		            return;
		        }
		        if (activator != null && _activator != null) {
		            throw new IllegalStateException();
		        }
		        _activator = activator;
		}
	}
}
