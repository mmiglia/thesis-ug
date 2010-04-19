package dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;


/**
 * Singleton class that acts as a database that will save all the events
 */
public enum EventDatabase {
	instance; // singleton instance
	private static final String DATABASE_NAME = "src/main/resources/EventDatabase";
	private final static Logger log = LoggerFactory.getLogger(EventDatabase.class);

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
	 * @param eventID unique UUID of the task
	 * @param event the new task, must have the same UUID
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
			toChange.event = event;
			db.store(toChange);
			return true;
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
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DATABASE_NAME);		
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
