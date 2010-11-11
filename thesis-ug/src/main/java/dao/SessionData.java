package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.db4o.query.Query;
import com.db4o.ta.Activatable;
import com.db4o.ta.TransparentActivationSupport;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

/**
 * Singleton class that acts as a database that will save the current logged-in users.
 * You can create a session with the method createSessionforUser
 */
public enum SessionData {
	instance; //singleton instance
	private static final String DATABASE_NAME = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/SessionData";
	private final static Logger log = LoggerFactory.getLogger(SessionData.class);
	private static ObjectServer server ; //db4o server
	private static boolean databaseOpen = true; // true means database server is initialized
	private static final Object lock = new Object(); // mutex lock
	
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	
	/**
	 * Create a session for user and return the session key
	 * @param userID UUID of the user
	 * @return Fresh session key of the user if user is not in the database, or old ones if user already logged in
	 */
	public static String createSessionforUser (String userID){
		String retrieveSession = checkUserLogin(userID);
		log.debug("Actual session for user:"+retrieveSession);
		if (!retrieveSession.equals("-1")){
			return retrieveSession;
		}else {
			Connection conn= (Connection) dbManager.dbConnect();
			
			QueryStatus qs=new QueryStatus();
			
			String sessionKey =UUID.randomUUID().toString();
			String updateQuery="UPDATE User set ";

			updateQuery+="sessionKey='"+sessionKey+"',";
			updateQuery+="active=1";
			
			updateQuery+=" where username='"+userID+"'";
			
			updateQuery+=";";
			
			qs=dbManager.customQuery(conn, updateQuery);
			
			if(qs.execError){
				log.error(qs.explainError());
				log.error(updateQuery);
				log.error(qs.occourtedErrorException.getMessage());
				qs.occourtedErrorException.printStackTrace();
				dbManager.rollbackTransaction(conn);
				log.error("Error during session creation");
				
				dbManager.dbDisconnect(conn);
				
				
				return "-1";
			}
			
			
			
			return sessionKey;
		}
	}
	
	/**
	 * Remove all sessions by user
	 * @param userID UUID of the user	 
	 */
	public static void removeAllSessionbyUser (final String userID){
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=new QueryStatus();
		
		String sessionKey =UUID.randomUUID().toString();
		String updateQuery="UPDATE User set ";

		updateQuery+="sessionKey='',";
		updateQuery+="active=0,";
		
		updateQuery+=" where username='"+userID+"'";
		
		updateQuery+=";";
		
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			dbManager.rollbackTransaction(conn);
			log.error("Error during session remove");
			
			dbManager.dbDisconnect(conn);
		}
	}
	
	/**
	 * This method check if a given UUID is currently logged in the system
	 * @param userID user UUID in the system
	 * @return -1 if no match, sessionKey if there's a match
	 */
	private static String checkUserLogin(final String userID){
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, "Select * from User where username='"+userID+"'");
		String sessionKey="-1";
		if(!qs.execError){
			ResultSet rs = (ResultSet) qs.customQueryOutput;
			try {				
				if(rs.next()){
					if(rs.getInt("active")==1){
						sessionKey= rs.getString("sessionKey");
						log.debug("User already has an active Session key");
					}else{
						sessionKey= "-1";
					}
				}
			}catch (SQLException sqlE){
				
			}finally {
				dbManager.dbDisconnect(conn);
				return sessionKey;
			}
		}else{
			
			System.out.println(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
		}
		
		return sessionKey;
	}
	
	
	/**
	 * This method is used to initialize the database and return
	 * a reference to the client of the database 
	 * (if the class attribute databaseOpen is set to false)
	 * otherwise it only returns the reference to the client of the database 
	 * (see com.db40.ObjectContainer)
	 * TransparentActivationSupport is used to let db4o manage how objects are 
	 * loaded in memory so we don't have to manage it in our code.
	 * For more details about see
	 * http://developer.db4o.com/Projects/useful_snippets/activation_in_depth.html
	 * @return a reference to the database client
	 * 
	 * 
	 */	
	private static ObjectContainer openDatabase() {
		if (!databaseOpen) { //outer selection to enable faster access
			synchronized (lock){
			/*to avoid racing condition after outer IF above
			 e.g. possible to acquire same databaseOpen value
			 and thus open server multiple times*/
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			config.common().add(new TransparentActivationSupport());
			config.common().activationDepth(2);
			if (databaseOpen) return server.openClient(); 
			server= Db4oClientServer.openServer(config, DATABASE_NAME, 0);
			databaseOpen=true;
			}
		}
		ObjectContainer db = server.openClient();
		return db;
	}
	
	/**
	 * This class represent how session data are stored into the SessionData database.
	 * We can think it's like a record of the Session Table.
	 * The table schema is: Session(userID,sessionkey)
	 * where all the table fields are of type String
	 * 
	 */	
	private class Session implements Activatable{
		private transient Activator _activator;
		/**
		 * UUID of the user
		 */
		private String userID;
		/**
		 * generated session key for this user
		 */
		private String sessionkey;
		/**
		 * Constructor for the class
		 * @param userID UUID of the user
		 * @param sessionKey generated session key for this user
		 */
		public Session (String userID, String sessionKey){
			this.userID = userID;
			this.sessionkey = sessionKey;
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