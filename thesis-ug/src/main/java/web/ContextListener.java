package web;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ValueObject.SingleTask;
import java.util.List;

/**
 * Responsible for getting the right task/event near user location/time.
 */
@Path("/context/{username}")
public class ContextListener {
	private static Logger log = LoggerFactory.getLogger(ContextListener.class);

	/**
	 * 
	 * This method will report those task that can be completed near the
	 * location
	 * 
	 * @param latitude
	 *            latitude location from GPS sensor
	 * @param longitude
	 *            longitude location from GPS sensor
	 * @param userid
	 *            user id of the user
	 * @param sessionid
	 *            session token acquired by login
	 * @return list of tasks that can be completed nearby
	 */
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleTask> checkLocation(float latitude, float longitude,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Receive request from user " + userid + " from location "
				+ latitude + ":" + longitude);
		return null;
	}

	/**
	 * Check the first few task that is sorted based on a closeness to deadline
	 * 
	 * @param userid
	 *            user id of the user
	 * @param sessionid
	 *            session token acquired by login
	 * @return list of tasks that is near deadline
	 */
	@GET
	@Produces("application/xml")
	public List<SingleTask> checkNearDeadlineTask(
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Receive request to check near deadline tasks from user "
				+ userid + ", session " + sessionid);
		return null;
	}
}
