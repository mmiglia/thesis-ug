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

import valueobject.Reminder.GPSLocation;
import valueobject.SingleTask;
import businessobject.TaskManager;

/**
 * Task Resource is responsible for GETTING the task from the server.
 * This class uses mainly method of TaskManager class
 */
@Path("/{username}/task")
public class TaskResource 
{
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
	public void createTask(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleTask toAdd) {
		log.info("Request to create task from user " + userid + ", session "+ sessionid);
		log.info("GroupID for task:" + toAdd.groupId);
		TaskManager.getInstance().createTask(userid, toAdd);
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
		return TaskManager.getInstance().retrieveAllTask(userid);
	}

	/**
	 * get first few top 5 priority tasks
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
		return TaskManager.getInstance().getFirstTasks(userid);
	}
	
	/**
	 * set the specified task to DONE, set the user position as the one passed
	 * and set the DoneTime to Now
	 *
	 * @param taskID
	 *            UUID of a task to be removed
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login          
	 * @return 0 for unsuccessful, 1 for successful
	 */
	@GET
	@Path("/done")
	public void markTaskAsDone(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid,@QueryParam("taskID") String taskID,@QueryParam("lat") float latitude, @QueryParam("lon") float longitude) {
		GPSLocation userLocation=new GPSLocation();
		userLocation.latitude=latitude;
		userLocation.longitude=longitude;
		log.info("Request to set task " + taskID + " to DONE from user " + userid
				+ "in location "+latitude+","+longitude+", session " + sessionid);
		TaskManager.getInstance().markTaskAsDone(taskID, userLocation);
	}	
	

	/**
	 * Update the task identified by newTask.ID in the database
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login
	 * @param newTask SingleTask object that will replace the old task
	 */
	@POST
	@Path("update")
	@Consumes("application/xml")
	public void updateTasks(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid, SingleTask newTask) {
		log.info("Request to update task from user " + userid + ", session " + sessionid);
		TaskManager.getInstance().updateTask(userid, newTask);		
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
	public void removeTasks(String taskID,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to remove task " + taskID + " from user " + userid
				+ ", session " + sessionid);
		TaskManager.getInstance().removeTask(userid, taskID);
	}


	
}
