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
import businessobject.LoginManager;

@Path("/{username}/logout")
/**
 * Resource class for logout users
 * This class uses mainly method of LoginManager class
*/
public class LogoutResource{
	private static Logger log = LoggerFactory.getLogger(LogoutResource.class);
/**
 * This method will authenticate the user based on the given username. It will call BO.LoginManager.Logout() method to delete authentication in database.
 * @param username unique ID of the user
*/

@GET
@Produces("application/xml")
public void Authenticate(@PathParam("username") String username) {	
    log.info("Receiving authentication request from client");
    LoginManager.logout(username);

}
}

