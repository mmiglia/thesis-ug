package BusinessObject;

import java.util.List;

import ValueObject.SingleEvent;

/**
 * Everytime the user wants to retrieve/add events, it saves in the local event
 * databases, and also to the 3rd party database (Google)
 */
public interface EventManager {
	/**
	 * this method will create directly an events from a specified date, and
	 * title.
	 * 
	 * @param userid unique UUID of the user
	 * @param title the main title for the event
	 * @param startTime start time of event execution
	 * @param endTime end time of event execution
	 * @param location where this event is held
	 * @param description brief descriptions regarding the event
	 */
	public void createEvents(String userid, String title, String startTime,
			String endTime, String location, String description);

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
	 * @param userid unique UUID of the user
	 * @param password password to Google Service
	 * @return 0 if unsuccessful, 1 if successful
	 */
	public boolean Authenticate(String userid, String password);

	/**
	 * Update the event by a given UUID to a new one
	 * @param userid unique UUID of the user
	 * @param eventID unique ID of the event to be replaced
	 * @param newEvent the new event object
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean updateEvent(String userid, String eventID, SingleEvent newEvent);

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
