package web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import BusinessObject.LoginManager;
import ValueObject.LoginReply;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/login/{username}")
/**
 * Resource class for login to the system by providing user ID and password
*/
public class LoginResource{
	private static Logger log = LoggerFactory.getLogger(LoginResource.class);
/**
 * This method will authenticate the user based on a given params. It will call BO.LoginManager.Login() method to do authentication in database.
 * Upon successful completion, LoginReply object is created and returned as a JAXB annotated XML
 * @param result result is JAXB annotated LoginReply
 * @param username unique ID of the user
 * @param password 
 * @return result is JAXB annotated LoginReply
*/

@GET
@Produces("application/xml")
public LoginReply Authenticate(@PathParam("username") String username, @QueryParam ("p") String password) {	
    log.info("Receiving authentication request from client");
    LoginReply reply = LoginManager.login(username, password);
    if (reply != null) {
        return reply;
    } else {
        throw new WebApplicationException(404);
    }  
}
}

