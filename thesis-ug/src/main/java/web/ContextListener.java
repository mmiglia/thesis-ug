package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.SingleTask;
import businessobject.LocationAwareManager;
import businessobject.TaskManager;

/**
 * Responsible for getting the right task/event near user location/time.
 */
@Path("/{username}/context")
public class ContextListener {
	private static Logger log = LoggerFactory.getLogger(ContextListener.class);

	/**
	 * 
	 * This method will report those task that can be completed near the
	 * location for ALL user tasks
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
	@GET
	@Path("/location/all")
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationAll(@QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Receive request for context listener from user " + userid + " from location "
				+ latitude + ":" + longitude);
		return LocationAwareManager.checkLocationAll(userid, latitude, longitude, distance);
	}

	/**
	 * 
	 * This method will report those task that can be completed near the
	 * location for specific task string
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
	@GET
	@Path("/location/single")
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationSingle(@QueryParam("q")String sentence, @QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Receive request for single context listener from user " + userid + " from location "
				+ latitude + ":" + longitude);
		return LocationAwareManager.checkLocationSingle(userid, sentence, latitude, longitude, distance);
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
		// currently is just returning the result from task manager
		return TaskManager.getInstance().getFirstTasks(userid);
	}
}
