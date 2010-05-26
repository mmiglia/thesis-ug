package dao;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.Configuration;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.cs.Db4oClientServer;
import com.db4o.query.Predicate;

/**
 * Singleton class that acts as a database that will save the current logged-in users
 */
public enum SessionData {
	instance; //singleton instance
	private static final String DATABASE_NAME = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/SessionData";
	private final static Logger log = LoggerFactory.getLogger(SessionData.class);
	private static ObjectServer server ; //db4o server
	private static boolean databaseOpen = false; // true means database server is initialized
	private static final Object lock = new Object(); // mutex lock
	
	/**
	 * Create a session for user and return the session key
	 * @param userID UUID of the user
	 * @return Fresh session key of the user if user is not in the database, or old ones if user already logged in
	 */
	public static String createSessionforUser (String userID){
		String retrieveSession = checkUserLogin(userID);
		if (!retrieveSession.equals("-1")) return retrieveSession;
		else {
			String sessionKey = UUID.randomUUID().toString();
			Session toAdd = instance.new Session(userID, sessionKey);
			ObjectContainer db = openDatabase();
			try {
				db.store(toAdd);
				return sessionKey; // return the fresh session key upon successful add
			} finally {
				db.close();
			}		
		}
	}
	
	/**
	 * Remove all sessions by user
	 * @param userID UUID of the user	 
	 */
	public static void removeAllSessionbyUser (final String userID){
		ObjectContainer db = openDatabase();
		try {			
			List <Session> result = db.query(new Predicate<Session>() {
			    public boolean match(Session session) {
			        return session.userID.equals(userID);
			    }
			});
			while (result.iterator().hasNext())	db.delete(result.iterator().next());			
		} finally {
			db.close();
		}
	}
	
	/**
	 * This method check if a given UUID is currently logged in the system
	 * @param userID user UUID in the system
	 * @return -1 if no match, sessionKey if there's a match
	 */
	private static String checkUserLogin(final String userID){
		ObjectContainer db = openDatabase();
		try {			
			List <Session> result = db.query(new Predicate<Session>() {
			    public boolean match(Session session) {
			        return session.userID.equals(userID);
			    }
			});
			return result.isEmpty() ? "-1":result.get(0).sessionkey;
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
			server= Db4oClientServer.openServer(Db4oClientServer
		        .newServerConfiguration(), DATABASE_NAME, 0);
			databaseOpen=true;
			}
		}
		ObjectContainer db = server.openClient();
		return db;
	}
	
	/**
	 * 
	 * The basic class to be saved in SessionData database
	 *
	 */
	private class Session {
		/**
		 * UUID of the user
		 */
		public String userID;
		/**
		 * generated session key for this user
		 */
		public String sessionkey;
		/**
		 * Constructor for the class
		 * @param userID UUID of the user
		 * @param sessionKey generated session key for this user
		 */
		public Session (String userID, String sessionKey){
			this.userID = userID;
			this.sessionkey = sessionKey;
		}
	}
}