package businessobject.google;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;

import businessobject.EventSubscriber;

import com.google.gdata.client.calendar.CalendarService;
import com.google.gdata.util.AuthenticationException;

public class CalendarClient extends EventSubscriber{
	private CalendarService myCalendar;
	private final static Logger log = LoggerFactory.getLogger(CalendarClient.class);
	
	public CalendarClient(){
		 super();
		 myCalendar = new CalendarService("UG-thesis-1");//default name for this application
	}
	
	@Override
	public boolean Authenticate(String username, String password) {
		try {
			myCalendar.setUserCredentials(username, password);
		} catch (AuthenticationException e) {
			log.warn("Unabe to authenticate to Google Calendar");
		}		
		return false;
	}

	@Override
	public void createEvents(String userid, String title, String startTime,
			String endTime, String location, String description) {
		// TODO Auto-generated method stub
		
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
