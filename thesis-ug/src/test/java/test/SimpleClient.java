package test;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import valueobject.LoginReply;
import valueobject.SingleEvent;
import valueobject.SingleTask;

@Consumes("application/xml")
public interface SimpleClient {
	@GET
	@Path("/login/{username}")
	@Produces("application/xml")
	public LoginReply Authenticate(@PathParam("username") String username, @CookieParam ("p") String password);
	
	@PUT
	@Path("{username}/event/add")	
	@Produces("application/xml")
	public boolean createEvent(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid, SingleEvent toAdd);
	
	@GET
	@Path("{username}/event/all")
	@Produces("application/xml")
	public List<SingleEvent> getAllEvents(@PathParam("username") String userid,	@CookieParam("sessionid") String sessionid);
	
	@GET
	@Path("{username}/event/today")
	@Produces("application/xml")	
	public List<SingleEvent> getEventToday(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) ;

	@GET
	@Path("{username}/event/between")
	@Produces("application/xml")
	public List<SingleEvent> getEvent(String DateFrom, String DateTo, @PathParam("username") String userid,	@CookieParam("sessionid") String sessionid);
	
	@PUT
	@Path("{username}/event/update")
	@Produces("application/xml")
	public boolean updateEvent(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid, SingleEvent oldEvent, SingleEvent newEvent);
	
	@POST
	@Path("{username}/event/erase")
	@Produces("application/xml")
	public boolean removeEvent(String eventID, @PathParam("username") String userid, @CookieParam("sessionid") String sessionid) ;

	@PUT
	@Path("{username}/task/add")	
	@Produces("application/xml")
	public boolean createTask(@PathParam("username") String userid,	@CookieParam("sessionid") String sessionid, SingleTask toAdd);
	
	@GET
	@Path("{username}/task/all")
	@Produces("application/xml")
	public List<SingleTask> getAllTasks(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid);

	@GET
	@Path("{username}/task/first")
	@Produces("application/xml")
	public List<SingleTask> getFirstTasks(@PathParam("username") String userid,	@CookieParam("sessionid") String sessionid);

	@POST
	@Path("{username}/task/erase")
	@Produces("application/xml")
	public boolean removeTasks(String taskID, @PathParam("username") String userid,	@CookieParam("sessionid") String sessionid);

	@PUT
	@Path("{username}/task/update")
	@Produces("application/xml")
	public boolean updateTasks(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleTask oldTask, SingleTask newTask);
}
