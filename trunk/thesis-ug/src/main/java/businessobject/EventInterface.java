package businessobject;

import java.util.List;

import valueobject.SingleEvent;


/**
 * Everytime the user wants to retrieve/add events, it saves in the local event
 * databases, and also to the 3rd party database (Google)
 */
public interface EventInterface {
	/**
	 * this method will create directly an events from a specified date, and
	 * title.
	 * 
	 * @param userid unique UUID of the user
	 * @param event SingleEvent object from local database
	 * @return boolean 0 if unsuccessful, 1 if successful
	 */
	public boolean createEvents(String userid, SingleEvent event);
	
	/**
	 * this method will remove the specific event
	 * @param userid unique UUID of the user
	 * @param eventID unique UUID of the event
	 * @return boolean 0 if unsuccessful, 1 if successful
	 */
	public boolean removeEvent(String userid, String eventID);

	/**
	 * this method will particularly use quick add features in Google Calendar,
	 * which can parse directly any type of string.
	 * 
	 * @param userid unique UUID of the user
	 * @param toParse text to be parsed
	 * @return boolean 0 if unsuccessful, 1 if successful
	 */
	public boolean quickAdd(String userid, String toParse);

	/**
	 * Authenticate to the backend service (e.g. Google Calendar)
	 * 
	 * @param username username to the service provider
	 * @param password password to the service provider
	 * @return 0 if unsuccessful, 1 if successful
	 */
	public boolean Authenticate(String username, String password);

	/**
	 * Update the event by a given UUID to a new one
	 * @param userid unique UUID of the user
	 * @param newEvent the new event object
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean updateEvent(String userid, SingleEvent newEvent);

	/**
	 * retrieve all events from this user Calendar
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all events from the user
	 */
	public List<SingleEvent> retrieveAllEvents(String userid);

	/**
	 * retrieve all events that occur during a defined period.
	 * 
	 * @param userid unique UUID of the user
	 * @param startTime start time of event execution
	 * @param endTime end time of event execution
	 * @return list that contains events during specified period
	 */
	public List<SingleEvent> retrieveEventsbyDate(String userid, String startTime,
			String endTime);
}
