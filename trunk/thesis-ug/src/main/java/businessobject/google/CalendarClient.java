package businessobject.google;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;

import businessobject.EventSubscriber;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

/**
 * Implementation for Google Calendar API
 * 
 */
public class CalendarClient extends EventSubscriber {
	private CalendarService myCalendar;
	private String username;
	private String password;
	private final static Logger log = LoggerFactory
			.getLogger(CalendarClient.class);

	/**
	 * Default constructor for this class
	 */
	public CalendarClient() {
		super();
		myCalendar = new CalendarService(CONSTANTS.getProperty("COMPANY_NAME")
				+ "-" + CONSTANTS.getProperty("APP_NAME") + "-"
				+ CONSTANTS.getProperty("VERSION"));

		// default name for this application
	}

	/**
	 * Constructor with supplying username and password for Google
	 * 
	 * @param username
	 *            username to Google Calendar
	 * @param password
	 *            password to Google Calendar
	 */
	public CalendarClient(String username, String password) {
		this();
		this.username = username;
		this.password = password;
	}

	public boolean Authenticate() {
		try {
			myCalendar.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			log.warn("Unable to authenticate to Google Calendar");
		}
		return false;
	}
	
	@Override
	public boolean Authenticate(String username, String password) {
		try {
			myCalendar.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			log.warn("Unable to authenticate to Google Calendar");
		}
		return false;
	}

	@Override
	public boolean createEvents(String userid, SingleEvent event) {
		try {
			URL postUrl;
			postUrl = new URL("http://www.google.com/calendar/feeds/"
					+ username + "@gmail.com/private/full");

			CalendarEventEntry myEntry = new CalendarEventEntry();
			myEntry.setTitle(new PlainTextConstruct(event.title));
			myEntry.setContent(new PlainTextConstruct(event.description));

			DateTime start = DateTime.parseDateTime(event.startTime);
			DateTime end = DateTime.parseDateTime(event.endTime);
			When eventTimes = new When();
			eventTimes.setStartTime(start);
			eventTimes.setEndTime(end);
			myEntry.addTime(eventTimes);

			// Send the request and receive the response
			CalendarEventEntry insertedEntry = myCalendar.insert(postUrl,
					myEntry);
			// Save the object in local database
			CalendarEventEntryDatabase.instance.addEntry(userid, event.ID,insertedEntry.getEditLink().getHref());
			log.info("editlink "+insertedEntry.getEditLink().getHref());
			log.info("id "+insertedEntry.getId());
			return true;
		} catch (IOException e) {
			log.error("IO exception is catched");
			e.printStackTrace();
			return false;
		} catch (ServiceException e) {
			log.error("Service exception is catched");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeEvent(String userid, String eventID) {
		try {
			String URItoDelete = CalendarEventEntryDatabase.instance
					.getCalendarEntry(userid, eventID);	
			if (URItoDelete == null) return false;			
			CalendarEventEntry toDelete = myCalendar.getEntry(new URL(URItoDelete), CalendarEventEntry.class);			
			myCalendar.setHeader("If-Match", toDelete.getEtag());
			// send remote request to Google
			myCalendar.delete(new URL(toDelete.getEditLink().getHref()));			
			// delete the entry in local database
			CalendarEventEntryDatabase.deleteEntry(userid, eventID);
			return true;
		} catch (IOException e) {
			log.error("IO exception is catched");
			e.printStackTrace();
			return false;
		} catch (ServiceException e) {
			log.error("Service exception is catched");
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean quickAdd(String userid, String toParse) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<SingleEvent> retrieveAllEvents(String userid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SingleEvent> retrieveEventsbyDate(String userid,
			String startTime, String endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateEvent(String userid, SingleEvent oldEvent,
			SingleEvent newEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	private static enum CalendarEventEntryDatabase {
		instance;
		private static final String DATABASE_NAME = "src/main/resources/GoogleCalendarEntry.db";
		private final static Logger log = LoggerFactory
				.getLogger(CalendarEventEntryDatabase.class);

		public static void addEntry(String userID, String eventID,
				String cal) {
			if (eventExist(userID, eventID)) { // check for redundant entry,
				// because db40 saves redundant
				// object
				log.warn("Google calendar entry for eventID " + eventID
						+ " already exist");
				return;
			}
			CalendarTuple toAdd = instance.new CalendarTuple(userID, eventID,
					cal);
			ObjectContainer db = openDatabase();
			try {
				db.store(toAdd);
			} finally {
				db.close();
			}
		}

		public static void deleteEntry(final String userID, final String eventID) {
			ObjectContainer db = openDatabase();
			try {
				List<CalendarTuple> result = db
						.query(new Predicate<CalendarTuple>() {
							public boolean match(CalendarTuple current) {
								return current.userID.equals(userID)
										&& current.eventID.equals(eventID);
							}
						});
				if (result.isEmpty())
					return;
				CalendarTuple toDelete = result.get(0);
				db.delete(toDelete);
			} finally {
				db.close();
			}
		}

		public static String getCalendarEntry(final String userID,
				final String eventID) {
			ObjectContainer db = openDatabase();
			try {
				List<CalendarTuple> result = db
						.query(new Predicate<CalendarTuple>() {
							public boolean match(CalendarTuple current) {
								return current.userID.equals(userID)
										&& current.eventID.equals(eventID);
							}
						});
				if (result.isEmpty())
					return null;
				return result.get(0).googleCalendarEntry;
			} finally {
				db.close();
			}
		}

		private static boolean eventExist(final String userID,
				final String eventID) {
			ObjectContainer db = openDatabase();
			try {
				List<CalendarTuple> result = db
						.query(new Predicate<CalendarTuple>() {
							public boolean match(CalendarTuple current) {
								return (current.userID.equals(userID) && current.eventID
										.equals(eventID));
							}
						});
				return !result.isEmpty();
			} finally {
				db.close();
			}
		}

		private static ObjectContainer openDatabase() {
			ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
					.newConfiguration(), DATABASE_NAME);
			return db;
		}

		private class CalendarTuple {
			public String userID;
			public String eventID;
			/**
			 * this variable represents the equivalent CalendarEntry object for
			 * this event. Useful for updating/removing from Google Calendar.
			 * Needs to divide this entry into separate database, in order to be
			 * able to extend to different service.
			 */
			public String googleCalendarEntry;

			public CalendarTuple(String userID, String eventID,
					String cal) {
				this.userID = userID;
				this.eventID = eventID;
				this.googleCalendarEntry = cal;
			}
		}
	}
}
