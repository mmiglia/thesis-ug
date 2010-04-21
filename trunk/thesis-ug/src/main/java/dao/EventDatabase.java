package dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
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
		final Date startPeriod = convertToJavaDate(startTime);
		final Date endPeriod = convertToJavaDate(endTime);
		
		try {
			List<EventTuple> queryResult = db.query(new Predicate<EventTuple>() {
				public boolean match(EventTuple current) {
					Date currentStart = convertToJavaDate(current.event.startTime);
					Date currentEnd = convertToJavaDate(current.event.endTime);					
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
	
	private static Date convertToJavaDate (String toParse){
		String xsDateTime = new String(toParse);
		int stringLength = xsDateTime.length();
		// removes the colon ':' at the 3rd position from the end to match ISO 8601
		xsDateTime=xsDateTime.substring(0, stringLength-3)+xsDateTime.substring(stringLength-2,stringLength);
		SimpleDateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
		try {
			return ISO_8601.parse(xsDateTime);
		} catch (ParseException e) {
			log.warn("Parsing error: cannot parse date '"+toParse+"'");
			e.printStackTrace();
			return null;
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
