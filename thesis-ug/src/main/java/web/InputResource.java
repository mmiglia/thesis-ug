package web;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.QueryReply;
import businessobject.ParserManager;

/**
 * This is a resource for sending input to the server in the form of string. It
 * should be asynchronous.
 */
@Path("/{username}/input")
public class InputResource {
	private static Logger log = LoggerFactory.getLogger(InputResource.class);
	/**
	 * input any string which will be parsed as either event or task. It should
	 * be asynchronous call, so the client will not need to wait.
	 * 
	 * @param userid unique id of the user
	 * @param sessionid unique token acquired upon successful login
	 * @param command string to be parsed
	 */
	@GET
	@Consumes("application/xml")
	@Produces("application/xml")
	public void input(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid,@QueryParam("q") String command) {
		log.info("Receive request from "+userid+" to parse '"+command+"'");
		new ParserManager().inputQuery(userid, command);
	}
}
