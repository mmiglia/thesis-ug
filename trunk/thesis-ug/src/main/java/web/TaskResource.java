package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.TaskManager;
import valueobject.SingleTask;

/**
 * Task Resource is responsible for GETTING the task from the server
 */
@Path("{username}/task")
public class TaskResource {
	private static Logger log = LoggerFactory.getLogger(TaskResource.class);

	/**
	 * Create new task to the database
	 * @param userid unique UUID of the user
	 * @param toAdd SingleTask instance
	 * @return true upon successful addition
	 */
	@POST
	@Path("/add")	
	@Consumes("application/xml")	
	public Response createTask(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleTask toAdd) {
		log.info("Request to create task from user " + userid + ", session "+ sessionid);
		boolean result = TaskManager.getInstance().createTask(userid, toAdd);
		return result ? Response.ok().build() : Response.notModified().build();
	}
	
	/**
	 * Get all tasks of this user
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login
	 * @return list of tasks from this user
	 */
	@GET
	@Path("/all")
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
	@Path("/first")
	@Produces("application/xml")
	public List<SingleTask> getFirstTasks(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get first important tasks from user " + userid
				+ ", session " + sessionid);
		return null;
	}

	@POST
	@Path("update")
	@Consumes("application/xml")
	public Response updateTasks(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleTask oldTask, SingleTask newTask) {
		log.info("Request to update task from user " + userid + ", session " + sessionid);
		boolean result = TaskManager.getInstance().updateTask(userid, oldTask, newTask);		
		return result ? Response.ok().build() : Response.notModified().build();
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
	@Path("/erase")
	@Consumes("application/xml")
	public Response removeTasks(String taskID,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to remove task " + taskID + " from user " + userid
				+ ", session " + sessionid);
		boolean result = TaskManager.getInstance().removeTask(userid, taskID);
		return result ? Response.ok().build() : Response.notModified().build();
	}
}
