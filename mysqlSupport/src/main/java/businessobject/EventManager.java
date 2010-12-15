package businessobject;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;
import dao.EventDatabase;

/**
 * This SINGLETON class is the only manager/publisher for event. All implemented
 * methods are just doing the operation in local database, and then calling all
 * subsequent methods in the subscriber (3rd party database)
 * 
 */
public class EventManager extends Publisher<EventSubscriber> implements EventInterface {
	private final static Logger log = LoggerFactory.getLogger(EventManager.class);

	private EventManager() {
		super();
	}

	private static class InstanceHolder {
		private static final EventManager INSTANCE = new EventManager();
	}

	public static EventManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * Authenticate to the backend service (e.g. Google Calendar)
	 * 
	 * @param username
	 *            username to the service provider
	 * @param password
	 *            password to the service provider
	 * @return 0 if unsuccessful, 1 if successful
	 */
	public boolean Authenticate(String username, String password) {
		for (EventSubscriber o : subscriberlist) {
			if (o.Authenticate(username, password)) continue;
			else return false;
		}
		return true;
	}

	/**
	 * retrieve all events from this user Calendar
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @return list that contains all events from the user
	 */
	public List<SingleEvent> retrieveAllEvents(String userid) {
		return EventDatabase.instance.getAllEvent(userid);
		/*
		 * Code stub to retrieve from subscriber 
		 * for (EventSubscriber o : subscriberlist) 
		 * { o.retrieveAllEvents(userid); }
		 * 
		 */
	}

	/**
	 * retrieve all events that occur during a defined period between two days.
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param startTime
	 *            start time of event execution
	 * @param endTime
	 *            end time of event execution
	 * @return list that contains events during specified period
	 */
	public List<SingleEvent> retrieveEventsbyDate(String userid,
			String startDate, String endDate) {
		return EventDatabase.instance.getEventsDuring(userid, startDate, endDate);
	}
	
	/**
	 * Get all events for the specified userID from the 0:0:0 to 23:59:59
	 * of today. This function call retrieveEventsbyDate(userid, startTime, endTime). 
	 * @param userid
	 * @return
	 */
	
	public List<SingleEvent> retrieveEventToday(String userid){
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String startTime = Converter.CalendarDatetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 23);
		now.set(Calendar.MINUTE, 59);
		now.set(Calendar.SECOND, 59);
		String endTime = Converter.CalendarDatetoString(now);
		log.info("Start Time "+startTime);
		log.info("End time "+endTime);
		return retrieveEventsbyDate(userid, startTime, endTime);
	}

	
	@Override
	public boolean createEvent(String userid, SingleEvent event) {
		// TODO Auto-generated method stub
		return createEvent(userid,event.dueDate,event.startTime,event.endTime,
				event.location,event.title,event.description);
	}

	/**
	 * create events with the specification of all the elements for the Event and related Reminder creation
	 * 
	 * 
	 * @param userID unique UUID of the user
	 * @param toAdd SingleEvent instance
	 * @return true is successful
	 */
	public boolean createEvent(String userID, String dueDate,String startTime,String endTime,
			String location,String title, String description){
		
		SingleEvent toAdd=EventDatabase.instance.addEvent(userID, dueDate,startTime,endTime,
				location,title, description);
		
		for (EventSubscriber o : subscriberlist) o.createEvent(userID, toAdd);
		return true;
	}
		
	
	/**
	 * this method will particularly use quick add features in Google Calendar,
	 * which can parse directly any type of string.
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param toParse
	 *            text to be parsed
	 * @return boolean 0 if unsuccessful, 1 if successful
	 */
	public boolean quickAdd(String userid, String toParse) {
		for (EventSubscriber o : subscriberlist) {
			o.quickAdd(userid, toParse);
		}
		return false;
	}

	/**
	 * Update the event by a given UUID to a new one
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param newEvent
	 *            the new event object
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean updateEvent(String userid, SingleEvent newEvent) {		
		for (EventSubscriber o : subscriberlist) o.updateEvent(userid, newEvent);
		EventDatabase.instance.updateEvent(newEvent.eventID, newEvent);
		return true;
	}
	
	/**
	 * Remove an event from the database
	 * 
	 * @param userid unique UUID of the user
	 * @param eventID eventID to be removed
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean removeEvent(String userid, String eventID) {		
		for (EventSubscriber o : subscriberlist) {
			if (o.removeEvent(userid, eventID)) continue;
			else return false;
		}
		EventDatabase.instance.deleteEvent(eventID);
		return true;
	}



}
