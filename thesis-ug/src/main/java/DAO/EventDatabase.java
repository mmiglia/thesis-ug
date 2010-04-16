package DAO;
import ValueObject.*;
import java.lang.*;
/**
*/
public class EventDatabase{
/**
 * UUID of the user
*/
private String userID;
/**
 * list of events saved in the database
*/
private SingleEvent event;
/**
 * this variable represents the equivalent CalendarEntry object for this event. Useful for updating/removing from Google Calendar. Needs to divide this entry into separate database, in order to be able to extend to different service.
*/
private CalendarEntry googleCalendarEntry;
}

