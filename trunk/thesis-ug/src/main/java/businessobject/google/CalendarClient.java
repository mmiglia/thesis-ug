package businessobject.google;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;
import businessobject.EventSubscriber;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;
import com.google.gdata.client.calendar.CalendarQuery;
import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.calendar.CalendarEventFeed;
import com.google.gdata.data.extensions.When;
import com.google.gdata.data.extensions.Where;
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
	private boolean authenticated;
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
			authenticated = true;
		} catch (AuthenticationException e) {
			log.warn("Unable to authenticate to Google Calendar");
		}
		return false;
	}

	@Override
	public boolean Authenticate(String username, String password) {
		try {
			myCalendar.setUserCredentials(username, password);
			authenticated = true;
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

			CalendarEventEntry myEntry = convertToCalendarEntry(event);

			// Send the request and receive the response
			CalendarEventEntry insertedEntry = myCalendar.insert(postUrl,
					myEntry);
			// Save the object in local database
			CalendarEventEntryDatabase.instance.addEntry(userid, event.ID,
					insertedEntry.getEditLink().getHref());
			log.info("editlink " + insertedEntry.getEditLink().getHref());
			log.info("id " + insertedEntry.getId());
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
			if (URItoDelete == null)
				return false;
			CalendarEventEntry toDelete = myCalendar.getEntry(new URL(
					URItoDelete), CalendarEventEntry.class);
			myCalendar.setHeader("If-Match", toDelete.getEtag());
			// send remote request to Google
			myCalendar.delete(new URL(URItoDelete));
			// delete the entry in local database
			CalendarEventEntryDatabase.instance.deleteEntry(userid, eventID);
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
		CalendarEventFeed myEvents;
		DateTime haha = new DateTime();
		try {
			myEvents = myCalendar.getFeed(new URL("http://www.google.com/calendar/feeds/"
						+ username + "@gmail.com/private/full"), CalendarEventFeed.class);

			List<CalendarEventEntry> retrievedEvents = myEvents.getEntries();
			List<SingleEvent> result = new LinkedList<SingleEvent>();
			for (CalendarEventEntry o: retrievedEvents)	result.add(convertToSingleEvent(o));			
			return result;
		} catch (IOException e) {
			log.error("IO exception is catched");
			e.printStackTrace();
			return null;
		} catch (ServiceException e) {
			log.error("Service exception is catched");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<SingleEvent> retrieveEventsbyDate(String userid,
			String startTime, String endTime) {
		try {
			URL feedUrl = new URL(
					"http://www.google.com/calendar/feeds/default/private/full");
			CalendarQuery myQuery = new CalendarQuery(feedUrl);
			myQuery.setMinimumStartTime(DateTime.parseDateTime(startTime));
			myQuery.setMaximumStartTime(DateTime.parseDateTime(endTime));

			// Send the request and receive the response:
			CalendarEventFeed resultFeed;
			resultFeed = myCalendar.query(myQuery, CalendarEventFeed.class);
			List<CalendarEventEntry> retrievedEvents = resultFeed.getEntries();
			List<SingleEvent> result = new LinkedList<SingleEvent>();
			for (CalendarEventEntry o : retrievedEvents)
				result.add(convertToSingleEvent(o));
			return result;
		} catch (IOException e) {
			log.error("IO exception is catched");
			e.printStackTrace();
			return null;
		} catch (ServiceException e) {
			log.error("Service exception is catched");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean updateEvent(String userid, SingleEvent newEvent) {
		try {
			String URItoUpdate = CalendarEventEntryDatabase.instance
					.getCalendarEntry(userid, newEvent.ID);
			if (URItoUpdate == null)
				return false;
			CalendarEventEntry toUpdate = myCalendar.getEntry(new URL(
					URItoUpdate), CalendarEventEntry.class);
			
			// Create new CalendarEntry instance
			CalendarEventEntry newEntry = convertToCalendarEntry(newEvent);
			
			// send remote request to Google
			myCalendar.setHeader("If-Match", toUpdate.getEtag());
			CalendarEventEntry updatedEntry = (CalendarEventEntry) myCalendar
					.update(new URL(URItoUpdate), newEntry);

			// Delete-add the object in local database
			CalendarEventEntryDatabase.instance
					.deleteEntry(userid, newEvent.ID);
			CalendarEventEntryDatabase.instance.addEntry(userid, newEvent.ID,
					updatedEntry.getEditLink().getHref());

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
	
	private SingleEvent convertToSingleEvent (CalendarEventEntry e){
		SingleEvent result = new SingleEvent(e.getTitle().getPlainText(),
				e.getTimes().get(0).getStartTime().toString(),
				e.getTimes().get(0).getEndTime().toString(),
				e.getLocations().get(0).getValueString(),
				((TextContent)e.getContent()).getContent().getPlainText());
		return result;
	}
	
	private CalendarEventEntry convertToCalendarEntry (SingleEvent e){
		CalendarEventEntry result = new CalendarEventEntry();
		result.setTitle(new PlainTextConstruct(e.title));
		result.setContent(new PlainTextConstruct(e.description));
		//add location
		Where eventLocation = new Where();
		eventLocation.setValueString(e.location);
		result.addLocation(eventLocation);
		//add period
		DateTime start = DateTime.parseDateTime(e.startTime);
		DateTime end = DateTime.parseDateTime(e.endTime);
		When eventTimes = new When();
		eventTimes.setStartTime(start);
		eventTimes.setEndTime(end);
		result.addTime(eventTimes);
		return result;
	}

	private static enum CalendarEventEntryDatabase {
		instance;
		private static final String DATABASE_NAME = "src/main/resources/GoogleCalendarEntry.db";
		private final static Logger log = LoggerFactory
				.getLogger(CalendarEventEntryDatabase.class);

		public static void addEntry(String userID, String eventID, String cal) {
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

			public CalendarTuple(String userID, String eventID, String cal) {
				this.userID = userID;
				this.eventID = eventID;
				this.googleCalendarEntry = cal;
			}
		}
	}
}
