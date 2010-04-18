package web;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.CookieParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;

import java.util.List;

/**
 * Event Resource is responsible for GETTING the result from the server
 */
@Path("/event/{username}")
public class EventResource {
	private static Logger log = LoggerFactory.getLogger(EventResource.class);
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
	@Produces("application/xml")
	public List<SingleEvent> getAllEvents(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get all events from user "+userid+" session "+sessionid);
		return null;
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
	@Produces("application/xml")
	
	public List<SingleEvent> getEventToday(
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get event today from user "+userid+" session "+sessionid);
		return null;
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
	@Produces("application/xml")
	public List<SingleEvent> getEvent(String DateFrom, String DateTo,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get events between "+DateFrom+" until "+DateTo+" from user "+userid+" session "+sessionid);
		return null;
	}
	
	/**
	 * remove event specified by UUID
	 * @param eventID UUID of the event
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login
	 * @return 0 for unsuccessful, 1 for successful
	 */
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public boolean removeEvent(String eventID,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to remove event " + eventID + " from user " + userid
				+ ", session " + sessionid);
		return false;
	}
}
