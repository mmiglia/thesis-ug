package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;
import businessobject.EventManager;

/**
 * Event Resource is responsible for GETTING events from the server.
 * This class uses mainly method of EventManager class
 */
@Path("/{username}/event")
public class EventResource {
	private static Logger log = LoggerFactory.getLogger(EventResource.class);
	
	/**
	 * Create new event in the database
	 * @param userid unique UUID of the user
	 * @param sessionid current session id
	 * @param toAdd SingleEvent instance to be added
	 * @return true if successful
	 */
	@POST
	@Path("/add")	
	@Consumes("application/xml")	
	public void createEvent(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleEvent toAdd) {
		log.info("Request to create event from user " + userid + ", session "+ sessionid);
		EventManager.getInstance().createEvent(userid, toAdd);		
	}
		
	/**
	 * This method will return maximum 100 events to prevent slow transmission.
	 * First it checks HTTP headers for session and userID and then use that
	 * userID to retrieve events from BO.
	 * 
	 * @param userid
	 *            user id of the user
	 * @param sessionid
	 *            session token required by login
	 * @return All events by this user
	 */
	@GET
	@Path("/all")
	@Produces("application/xml")
	public List<SingleEvent> getAllEvents(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get all events from user "+userid+" session "+sessionid);
		return EventManager.getInstance().retrieveAllEvents(userid);
	}

	/**
	 * Get events today based on date in server
	 * 
	 * @param userid
	 *            userid of the user
	 * @param sessionid
	 *            session token acquired by login
	 * @return list of events occured today
	 */
	@GET
	@Path("/today")
	@Produces("application/xml")	
	public List<SingleEvent> getEventToday(
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get event today from user "+userid+" session "+sessionid);
		return EventManager.getInstance().retrieveEventToday(userid);
	}

	/**
	 * 
	 * Returns a list of events in a given range
	 * 
	 * @param DateFrom
	 *            starting date of event to be retrieved
	 * @param DateTo
	 *            ending date of event to be retrieved
	 * @param userid
	 *            userid of the user
	 * @param session
	 *            session token acquired by login
	 * @return list of events
	 */
	@GET
	@Path("/between")
	@Produces("application/xml")
	public List<SingleEvent> getEvent(@QueryParam("s")String DateFrom, @QueryParam("e")String DateTo,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get events between "+DateFrom+" until "+DateTo+" from user "+userid+" session "+sessionid);
		return EventManager.getInstance().retrieveEventsbyDate(userid, DateFrom, DateTo);
	}
	
	/**
	 * Update the event specified by newEvent.ID 
	 * @param userid userid of the user
	 * @param sessionid session token acquired by login
	 * @param newEvent the new event that will replace the old one
	 */
	@POST
	@Path("/update")
	@Consumes("application/xml")
	public void updateEvent(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleEvent newEvent) {
		log.info("Request to update event from user " + userid + ", session " + sessionid);
		EventManager.getInstance().updateEvent(userid, newEvent);
	}
	
	/**
	 * remove event specified by UUID
	 * @param eventID UUID of the event
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login
	 * @return 0 for unsuccessful, 1 for successful
	 */
	@POST
	@Path("/erase")
	@Consumes("application/xml")
	public void removeEvent(String eventID,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to remove event " + eventID + " from user " + userid
				+ ", session " + sessionid);
		EventManager.getInstance().removeEvent(userid, eventID);
	}
}
