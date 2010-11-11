package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.SingleTask;
import businessobject.LocationAwareManager;
import businessobject.TaskManager;

/**
 * Responsible for getting the right task/event near user location/time.
 * This class uses mainly method of LocationAwareManager class
 */
@Path("/{username}/location")
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
	@Path("/all")
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationAll(@QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Receive ALL context from user " + userid + " from location "
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
	@Path("/single")
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationSingle(@QueryParam("q")String sentence, @QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request single context from user " + userid + " sentence "+sentence+" from location "
				+ latitude + ":" + longitude);
		return LocationAwareManager.checkLocationSingle(userid, sentence, latitude, longitude, distance);
	}	
}
