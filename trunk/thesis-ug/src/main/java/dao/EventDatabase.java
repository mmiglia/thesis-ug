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
import com.db4o.cs.Db4oClientServer;
import com.db4o.query.Predicate;


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
	 */
	public static void addEvent(String userID, SingleEvent event) {
		if (eventExist(event)) { //check for redundant entry, because db40 saves redundant object
			log.warn ("Event "+event.ID+" already exist");
			return; 
		}
		EventTuple toAdd = instance.new EventTuple(userID, event);
		ObjectContainer db = openDatabase();
		try {			
			db.store(toAdd);
		} finally{
			db.close();			
		}
	}	
	
	/**
	 * Delete the specific event from database
	 * @param userID unique UUID of the user
	 * @param eventID unique UUID of the task	 
	 */	
	public static void deleteEvent(final String userID, final String eventID) {
		ObjectContainer db = openDatabase();
		try {
			List<EventTuple> result = db.query(new Predicate<EventTuple>() {
				public boolean match(EventTuple current) {
					return current.userID.equals(userID) && current.event.ID.equals(eventID);
				}
			});
			if (result.isEmpty()) return;
			EventTuple toDelete = result.get(0);
			db.delete(toDelete);
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
			List<EventTuple> result = db.query(new Predicate<EventTuple>() {
				public boolean match(EventTuple current) {
					return (current.event.ID.equals(eventID));
				}
			});
			if (result.isEmpty()) {
				log.warn ("Cannot find task with ID "+eventID);
				return false;
			}
			EventTuple toChange = result.get(0);
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
		try {
			List<EventTuple> queryResult = db.query(new Predicate<EventTuple>() {
				public boolean match(EventTuple current) {
					return (current.userID.equals(userID));
				}
			});
			if (queryResult.isEmpty()) {
				log.warn ("Cannot find user ID "+userID);
				return null;
			}
			List<SingleEvent> result= new LinkedList<SingleEvent>();
			for (EventTuple o : queryResult) result.add(o.event);
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
				return null;
			}
			List<SingleEvent> result= new LinkedList<SingleEvent>();
			for (EventTuple o : queryResult) result.add(o.event);
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
		List <EventTuple> result = db.query(new Predicate<EventTuple>() {
			public boolean match(EventTuple current) {
		        return current.event.equals(event);
		    }
		});
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
			if (databaseOpen) return server.openClient(); 
			server= Db4oClientServer.openServer(Db4oClientServer
		        .newServerConfiguration(), DATABASE_NAME, 0);
			databaseOpen=true;
			}
		}
		ObjectContainer db = server.openClient();	
		return db;
	}
	
	private class EventTuple {
		/**
		 * UUID of the user
		 */
		public String userID;
		/**
		 * list of events saved in the database
		 */
		public SingleEvent event;
	
		public EventTuple (String userID, SingleEvent event){
			this.userID = userID;
			this.event = event;
		}
	}
}
