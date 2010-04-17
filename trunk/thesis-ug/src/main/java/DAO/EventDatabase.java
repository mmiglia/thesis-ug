package DAO;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.google.gdata.data.calendar.CalendarEntry;

import ValueObject.SingleEvent;

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
		EventTuple toAdd = instance.new EventTuple(userID, event, new CalendarEntry());
		ObjectContainer db = openDatabase();
		try {			
			db.store(toAdd);
		} finally{
			db.close();			
		}
	}
	
	/**
	 * Add new event to the database
	 * @param userID unique UUID of the user
	 * @param event event object to be saved
	 * @param googlecalendar google calendar entry
	 */
	public static void addEvent(String userID, SingleEvent event, CalendarEntry googlecalendar) {
		if (eventExist(event)) { //check for redundant entry, because db40 saves redundant object
			log.warn ("Event "+event.ID+" already exist");
			return; 
		}
		EventTuple toAdd = instance.new EventTuple(userID, event, googlecalendar);
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
		/**
		 * this variable represents the equivalent CalendarEntry object for this
		 * event. Useful for updating/removing from Google Calendar. Needs to
		 * divide this entry into separate database, in order to be able to
		 * extend to different service.
		 */
		public CalendarEntry googleCalendarEntry;
		public EventTuple (String userID, SingleEvent event, CalendarEntry googlecal){
			this.userID = userID;
			this.event = event;
			this.googleCalendarEntry = googlecal;
		}
	}
}
