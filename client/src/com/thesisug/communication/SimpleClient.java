package com.thesisug.communication;

/*
 * This class was intended to use using RESTEasy client implementation
 * it would be straightforward to use RESTEasy library.
 * However, Android does not support importing RESTEasy library
 * hence, we implement our own XML parser and HTTP call for each resources
 * We saved this class for future reference (in case RESTEasy got supported by Android)
 */
public interface SimpleClient {
	/*@GET
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
	public Response coba (@PathParam("username") String userid, String hallo);*/

}
