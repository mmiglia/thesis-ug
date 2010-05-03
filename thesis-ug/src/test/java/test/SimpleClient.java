package test;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import valueobject.LoginReply;
import valueobject.SingleEvent;
import valueobject.SingleTask;

@Consumes("application/xml")
public interface SimpleClient {
	@GET
	@Path("/login/{username}")
	@Produces("application/xml")
	public LoginReply Authenticate(@PathParam("username") String username, @CookieParam ("p") String password);
	
	@POST
	@Path("{username}/event/add")	
	public Response createEvent(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid, SingleEvent toAdd);
	
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
	
	@POST
	@Path("{username}/event/update")
	public Response updateEvent(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid, SingleEvent oldEvent, SingleEvent newEvent);
	
	@POST
	@Path("{username}/event/erase")
	public Response removeEvent(String eventID, @PathParam("username") String userid, @CookieParam("sessionid") String sessionid) ;

	@POST
	@Path("{username}/task/add")	
	public Response createTask(@PathParam("username") String userid,	@CookieParam("sessionid") String sessionid, SingleTask toAdd);
	
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
	public Response removeTasks(String taskID, @PathParam("username") String userid,	@CookieParam("sessionid") String sessionid);

	@POST
	@Path("{username}/task/update")
	public Response updateTasks(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleTask oldTask, SingleTask newTask);

	@POST
	@Path("{username}/event/coba")
	@Consumes("application/xml")
	public Response coba (@PathParam("username") String userid, String hallo);

}
