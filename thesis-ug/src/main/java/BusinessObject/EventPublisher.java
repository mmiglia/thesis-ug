package BusinessObject;

import java.util.ArrayList;
import java.util.List;

import ValueObject.SingleEvent;

/**
 * This singleton class is the only publisher for event. All implemented methods are just
 * calling all subsequent methods in the subscriber, and aggregate the results.
 */
public class EventPublisher extends Publisher<EventSubscriber> implements
		EventManager {
	
	private EventPublisher(){
		super();
	}
	
	private static class InstanceHolder { 
	     private static final EventPublisher INSTANCE = new EventPublisher();
	}
	
	public static EventPublisher getInstance(){
		return InstanceHolder.INSTANCE;
	}

	/**
	 * This method will be called directly in subscriber
	 */
	public boolean Authenticate(String username, String password) {
		return false;
	}

	public void createEvents(String username, String title, String startDate,
			String endDate) {
	
	}

	public List<SingleEvent> retrieveAllEvents(String username) {
		for (EventSubscriber o : subscriberlist) {
			o.retrieveAllEvents(username);
		}

		return new ArrayList<SingleEvent>();
	}

	public List<SingleEvent> retrieveEventsbyDate(String username, String startDate,
			String endDate) {
		for (EventSubscriber o : subscriberlist) {
			o.retrieveEventsbyDate(username, startDate, endDate);
		}
		return new ArrayList<SingleEvent>();
	}

	public void createEvents(String userid, String title, String startTime,
			String endTime, String location, String description) {
		for (EventSubscriber o : subscriberlist) {
			o.createEvents(userid, title, startTime, endTime, location, description);
		}
		
	}

	public boolean quickAdd(String userid, String toParse) {
		for (EventSubscriber o : subscriberlist) {
			o.quickAdd(userid, toParse);
		}
		return false;
	}

	public boolean updateEvent(String userid, String eventID,
			SingleEvent newEvent) {
		for (EventSubscriber o : subscriberlist) {
			o.updateEvent(userid, eventID, newEvent);
		}
		return false;
	}
}
