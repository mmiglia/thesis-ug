package businessobject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.EventDatabase;

import valueobject.SingleEvent;

/**
 * This SINGLETON class is the only manager/publisher for event. All implemented
 * methods are just doing the operation in local database, and then calling all
 * subsequent methods in the subscriber (3rd party database)
 * 
 */
public class EventManager extends Publisher<EventSubscriber> {
	private final static Logger log = LoggerFactory
			.getLogger(EventManager.class);

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
		return false;
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
	 * retrieve all events that occur during a defined period.
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
	 * this method will create directly an events from a specified date, and
	 * title.
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param title
	 *            the main title for the event
	 * @param startTime
	 *            start time of event execution
	 * @param endTime
	 *            end time of event execution
	 * @param location
	 *            where this event is held
	 * @param description
	 *            brief descriptions regarding the event
	 * 
	 * @return 0 if unsuccessful , 1 if successful
	 */
	public boolean createEvents(String userid, String title, String startTime,
			String endTime, String location, String description) {
		SingleEvent toAdd = new SingleEvent(title, startTime, endTime,
				location, description);		
		for (EventSubscriber o : subscriberlist) {
			if (o.createEvents(userid, toAdd)) continue;
			else return false;
		}
		EventDatabase.instance.addEvent(userid, toAdd);
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
	 * @param oldEvent
	 *            old event object to be replaced
	 * @param newEvent
	 *            the new event object
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean updateEvent(String userid, SingleEvent oldEvent,
			SingleEvent newEvent) {		
		for (EventSubscriber o : subscriberlist) {
			if (o.updateEvent(userid, oldEvent, newEvent)) continue;
			else return false ;
		}
		EventDatabase.instance.updateEvent(oldEvent.ID, newEvent);
		return true;
	}
	
	/**
	 * Remove an event from a database
	 * @param userid unique UUID of the user
	 * @param eventID eventID to be removed
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean removeEvent(String userid, String eventID) {		
		for (EventSubscriber o : subscriberlist) {
			if (o.removeEvent(userid, eventID)) continue;
			else return false;
		}
		EventDatabase.instance.deleteEvent(userid, eventID);
		return true;
	}
}
