package businessobject.google;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;

import businessobject.EventSubscriber;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.extensions.When;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

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
				+ "-"
				+ CONSTANTS.getProperty("APP_NAME")
				+ "-"
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
		super();
		this.username = username;
		this.password = password;
		myCalendar = new CalendarService("UG-thesis-1");// default name for this
		// application
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
	public void createEvents(String userid, String title, String startTime,
			String endTime, String location, String description) {
		try {
			URL postUrl;
			postUrl = new URL("http://www.google.com/calendar/feeds/"
					+ username + "@gmail.com/private/full");

			CalendarEventEntry myEntry = new CalendarEventEntry();

			myEntry.setTitle(new PlainTextConstruct(title));
			myEntry.setContent(new PlainTextConstruct(description));

			DateTime start = DateTime.parseDateTime(startTime);
			DateTime end = DateTime.parseDateTime(endTime);
			When eventTimes = new When();
			eventTimes.setStartTime(start);
			eventTimes.setEndTime(end);
			myEntry.addTime(eventTimes);

			// Send the request and receive the response:
			CalendarEventEntry insertedEntry = myCalendar.insert(postUrl,
					myEntry);
		} catch (IOException e) {
			log.error("IO exception is catched");
			e.printStackTrace();
		} catch (ServiceException e) {
			log.error("Service exception is catched");
			e.printStackTrace();
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
	public boolean updateEvent(String userid, String eventID,
			SingleEvent newEvent) {
		// TODO Auto-generated method stub
		return false;
	}

}
