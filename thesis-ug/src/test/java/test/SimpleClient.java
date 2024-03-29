package test;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import valueobject.Hint;
import valueobject.LoginReply;
import valueobject.QueryReply;
import valueobject.SingleEvent;
import valueobject.SingleTask;

@Consumes("application/xml")
public interface SimpleClient {
	@GET
	@Path("/{username}/input")
	@Produces("application/xml")
	public QueryReply input(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid,@QueryParam("q") String command);
	
	@GET
	@Path("/{username}/login")
	@Produces("application/xml")
	public LoginReply Authenticate(@PathParam("username") String username, @CookieParam ("p") String password);
	
	@POST
	@Path("{username}/event/add")	
	public void createEvent(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid, SingleEvent toAdd);
	
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
	public List<SingleEvent> getEvent(@QueryParam("s")String DateFrom, @QueryParam("e")String DateTo, @PathParam("username") String userid,	@CookieParam("sessionid") String sessionid);
	
	@POST
	@Path("{username}/event/update")
	public void updateEvent(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid, SingleEvent newEvent);
	
	@POST
	@Path("{username}/event/erase")
	public void removeEvent(String eventID, @PathParam("username") String userid, @CookieParam("sessionid") String sessionid) ;

	@POST
	@Path("{username}/task/add")	
	public void createTask(@PathParam("username") String userid, @CookieParam("sessionid") String sessionid, SingleTask toAdd);
	
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
	public void removeTasks(String taskID, @PathParam("username") String userid,	@CookieParam("sessionid") String sessionid);

	@POST
	@Path("{username}/task/update")
	public void updateTasks(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleTask newTask);

	@GET
	@Path("/{username}/location/all")
	@Produces("application/xml")
	public List<Hint> checkLocation(@QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) ;
	
	
}
