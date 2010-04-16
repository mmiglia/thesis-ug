package web;
import java.lang.*;
/**
 * Event Resource is responsible for GETTING the result from the server
*/
public class EventResource{
/**
 * This method will return maximum 100 events to prevent slow transmission. First it check HTTP headers for session and userID and then use that userID to retrieve events from BO.
 * @param Return 
 * @return 
*/
public String getAllEvents() {
    return null;
}
/**
 * return the events today based on date on server.
 * @param Return 
 * @return 
*/
public String getEventToday() {
    return null;
}
/**
 * Returns a list of events in a given range
 * @param DateTo ending date of event to be retrieved
 * @param DateFrom starting date of event to be retrieved
 * @param Return XML annotated events
 * @return XML annotated events
*/
public String getEvent(String DateFrom, long DateTo) {
    return null;
}
}

