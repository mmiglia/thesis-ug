package dao;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;
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


/**
 * Singleton class that acts as a database that will save all the events
 */
public enum EventDatabase {
	instance; // singleton instance
	private static final String DATABASE_NAME = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/EventDatabase";
	private final static Logger log = LoggerFactory.getLogger(EventDatabase.class);
	private static ObjectServer server ; //db4o server
	private static boolean databaseOpen = false; // true means database server is initialized
	private static final Object lock = new Object(); // mutex lock
	
	/**
	 * Add new event to the database
	 * @param userID unique UUID of the user
	 * @param event event object to be saved
	 * @return true if addition successful
	 */
	public boolean addEvent(String userID, SingleEvent event) {
		if (eventExist(event)) { //check for redundant entry, because db40 saves redundant object
			log.warn ("Event "+event.ID+" already exist");
			return false; 
		}
		EventTuple toAdd = instance.new EventTuple(userID, event);
		ObjectContainer db = openDatabase();
		try {			
			db.store(toAdd);
			return true;
		} finally{
			db.close();			
		}
	}	
	
	/**
	 * Delete the specific event from database
	 * @param userID unique UUID of the user
	 * @param eventID unique UUID of the task
	 * @return true if deletion success, false otherwise 
	 */	
	public static boolean deleteEvent(final String userID, final String eventID) {
		ObjectContainer db = openDatabase();
		try {
			Query query = db.query();
			query.constrain(EventTuple.class);
			Constraint constr=query.descend("eventID").constrain(eventID);
			query.descend("userID").constrain(userID).and(constr);
			ObjectSet<EventTuple> result = query.execute();
			if (result.isEmpty()) {
				log.info("cannot delete event");
				return false;
			}
			log.info("Deletion successful");
			EventTuple toDelete = result.get(0);
			db.delete(toDelete);
			return true;
		} finally {
			db.close();
		}
	}
	
	/**
	 * Update the specific task
	 * @param eventID unique UUID of the event
	 * @param event the new event
	 * @return false if update unsuccessful , true if update is successful
	 */
	public static boolean updateEvent(final String eventID, final SingleEvent event){
		ObjectContainer db = openDatabase();
		try {
			Query query = db.query();
			query.constrain(EventTuple.class);
			query.descend("eventID").constrain(eventID);
			ObjectSet<EventTuple> result = query.execute();
			if (result.isEmpty()) {
				log.warn ("Cannot find task with ID "+eventID);
				return false;
			}
			EventTuple toChange = result.get(0);
			toChange.activateWrite();
			event.ID = eventID; //make sure ID is the same
			toChange.event = event;
			db.store(toChange);
			return true;
		} finally {
			db.close();
		}		
	}
	
	/**
	 * Get all events from a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all events from the user
	 */
	public static List<SingleEvent> getAllEvent(final String userID){
		ObjectContainer db = openDatabase();
		List<SingleEvent> result= new LinkedList<SingleEvent>();
		try {
			Query query = db.query();
			query.constrain(EventTuple.class);
			query.descend("userID").constrain(userID);
			ObjectSet<EventTuple> queryResult = query.execute();
			if (queryResult.isEmpty()) {
				log.warn ("Cannot find user ID "+userID);
				return result;
			}			
			for (EventTuple o : queryResult) {
				o.activateRead();
				result.add(o.event);
			}
			return result;
		} finally {
			db.close();
		}
	}
	
	/**
	 * Get all events given a period of time
	 * @param userID unique UUID of the user
	 * @param startTime limit on the starting time of the event
	 * @param endTime limit on the ending time of the event
	 * @return list containing all events in that period
	 */
	public static List<SingleEvent> getEventsDuring(final String userID, String startTime, String endTime){
		ObjectContainer db = openDatabase();
		final Calendar startPeriod = Converter.toJavaDate(startTime);
		final Calendar endPeriod = Converter.toJavaDate(endTime);
		List<SingleEvent> result= new LinkedList<SingleEvent>();
		
		try {
			List<EventTuple> queryResult = db.query(new Predicate<EventTuple>() {
				Calendar currentStart, currentEnd;
				public boolean match(EventTuple current) {
					currentStart = Converter.toJavaDate(current.event.startTime);
					currentEnd = Converter.toJavaDate(current.event.endTime);					
					return (current.userID.equals(userID) &&
							currentStart.after(startPeriod)&&
							currentEnd.before(endPeriod));
				}
			});
			if (queryResult.isEmpty()) {
				log.warn ("Cannot find user ID "+userID);
				return result;
			}
			for (EventTuple o : queryResult) {
				o.activateRead();
				result.add(o.event);
			}
			return result;
		} finally {
			db.close();
		}
	}
	
	/**
	 * This method check if the event exist in the database
	 * @param event object to be checked against database
	 * @return 1 if event is exist, 0 otherwise
	 */
	private static boolean eventExist(final SingleEvent event) {
		ObjectContainer db = openDatabase();
		try {
			Query query = db.query();
			query.constrain(EventTuple.class);
			query.descend("eventID").constrain(event.ID);
			ObjectSet<EventTuple> result = query.execute();
			return !result.isEmpty();
		}finally{
			db.close();
		}		
	}
	
	private static ObjectContainer openDatabase() {
		if (!databaseOpen) { //outer selection to enable faster access
			synchronized (lock){
			/*to avoid racing condition after outer IF above
			 e.g. possible to acquire same databaseOpen value
			 and thus open server multiple times*/
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			config.common().add(new TransparentActivationSupport());
			config.common().activationDepth(3);
			if (databaseOpen) return server.openClient(); 
			server= Db4oClientServer.openServer(config, DATABASE_NAME, 0);
			databaseOpen=true;
			}
		}
		ObjectContainer db = server.openClient();	
		return db;
	}
	
	private class EventTuple implements Activatable{
		private transient Activator _activator;
		/**
		 * UUID of the user
		 */
		public String userID;
		public String eventID;
		/**
		 * list of events saved in the database
		 */
		public SingleEvent event;
	
		public EventTuple (String userID, SingleEvent event){
			this.userID = userID;
			this.eventID = event.ID;
			this.event = event;
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
