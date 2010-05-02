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
	@PUT
	@Path("/add")	
	@Consumes("application/xml")	
	@Produces("application/xml")
	public boolean createTask(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleTask toAdd) {
		log.info("Request to create task from user " + userid + ", session "+ sessionid);
		return TaskManager.getInstance().createTask(userid, toAdd);
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

	@PUT
	@Path("update")
	@Consumes("application/xml")
	@Produces("application/xml")
	public boolean updateTasks(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleTask oldTask, SingleTask newTask) {
		log.info("Request to update task from user " + userid + ", session " + sessionid);
		return TaskManager.getInstance().updateTask(userid, oldTask, newTask);		
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
	@Produces("application/xml")
	public boolean removeTasks(String taskID,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to remove task " + taskID + " from user " + userid
				+ ", session " + sessionid);
		return false;
	}
}
