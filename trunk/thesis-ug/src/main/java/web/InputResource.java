package web;

import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a resource for sending input to the server in the form of string. It
 * should be asynchronous.
 */
@Path("/input/{username}")
public class InputResource {
	private static Logger log = LoggerFactory.getLogger(InputResource.class);
	/**
	 * input any string which will be parsed as either event or task. It should
	 * be asynchronous call, so the client will not need to wait.
	 * 
	 * @param toParse
	 *            string to be parsed
	 */
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public void input(String toParse) {
		log.info("Receive request from ... to parse ...");
	}
}
