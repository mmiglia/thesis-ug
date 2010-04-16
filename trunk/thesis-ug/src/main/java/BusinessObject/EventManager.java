package BusinessObject;
import java.lang.*;
/**
 * everytime the user wants to retrieve/add events, it saves in the local event databases, and also to the 3rd party database (Google)
*/
public interface EventManager{
/**
 * this method will create directly an events from a specified date, and title.
 * @param startDate 
 * @param title 
 * @param username 
 * @param Return 
 * @param endDate 
*/
public void createEvents(String username, String title, String startDate, String endDate) {
}
/**
 * this method will particularly use quick add features in Google Calendar, which can parse directly any type of string.
 * @param Return 
 * @param toParse 
*/
public void quickAdd(String toParse) {
}
/**
 * Authenticate to the backend service (e.g. Google Calendar)
 * @param username 
 * @param Return 
 * @param password 
*/
public void Authenticate(String username, String password) {
}
/**
 * @param Return 
*/
public void updateEvent() {
}
/**
 * retrieve all events from this user Calendar
 * @param username 
 * @param Return 
*/
public void retrieveAllEvents(String username) {
}
/**
 * retrive all events that occured during a defined period.
 * @param startDate 
 * @param username 
 * @param Return 
 * @param endDate 
*/
public void retrieveEventsbyDate(String username, String startDate, String endDate) {
}
}

