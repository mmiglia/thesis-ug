package DAO;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.google.gdata.data.calendar.CalendarEntry;

import ValueObject.SingleEvent;

/**
 * Singleton class that acts as a database that will save all the events
 */
public enum EventDatabase {
	instance; // singleton instance
	private static final String DATABASE_NAME = "src/main/resources/EventDatabase";

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
	}
}
