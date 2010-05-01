package test;

import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import valueobject.LoginReply;
import valueobject.SingleEvent;

public interface SimpleClient {
	@GET
	@Path("/login/{username}")
	@Produces("application/xml")
	public LoginReply Authenticate(@PathParam("username") String username, @QueryParam ("p") String password);
	
	@GET
	@Path("{username}/event/all")
	@Produces("application/xml")
	public List<SingleEvent> getAllEvents(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid);
}
