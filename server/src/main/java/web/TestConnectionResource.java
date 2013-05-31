package web;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.LoginReply;
import valueobject.TestConnectionReply;
import businessobject.LoginManager;

@Path("/tryConn")
/**
 * Resource class for logout users
 * This class uses mainly method of LoginManager class
*/
public class TestConnectionResource{
	private static Logger log = LoggerFactory.getLogger(LogoutResource.class);
	/**
	 * This method will be used to try if the connection is available
	 * @param username unique ID of the user
	*/
	
	@GET
	@Produces("application/xml")
	public TestConnectionReply tryConnection(@CookieParam ("uri") String serverURI) {	
	    log.info("Receiving try connection request from client");
	    return new TestConnectionReply(1,serverURI);
	
	}
	
	
}

