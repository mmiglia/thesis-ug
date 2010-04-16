package web;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;
import ValueObject.SingleTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task Resource is responsible for GETTING the task from the server
 */
@Path("/task/{username}")
public class TaskResource {
	private static Logger log = LoggerFactory.getLogger(TaskResource.class);

	/**
	 * Get all tasks of this user
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login
	 * @return list of tasks from this user
	 */
	@GET
	@Produces("application/xml")
	public List<SingleTask> getAllTasks(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get all tasks from user " + userid + ", session "
				+ sessionid);
		return null;
	}

	/**
	 * get first few top priority tasks
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login
	 * @return list of top priority tasks
	 */
	@GET
	@Produces("application/xml")
	public List<SingleTask> getFirstTasks(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get first important tasks from user " + userid
				+ ", session " + sessionid);
		return null;
	}

	/**
	 * remove task specified by UUID
	 *
	 * @param taskID
	 *            UUID of a task to be removed
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login          
	 * @return 0 for unsuccessful, 1 for successful
	 */
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public boolean removeTasks(String taskID,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to remove task " + taskID + " from user " + userid
				+ ", session " + sessionid);
		return false;
	}
}
