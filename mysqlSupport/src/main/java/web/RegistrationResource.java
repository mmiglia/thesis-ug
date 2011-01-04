package web;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.RegistrationReply;
import businessobject.RegistrationManager;

@Path("/registration")

public class RegistrationResource {
	private static Logger log = LoggerFactory.getLogger(RegistrationResource.class);
	
	@GET
	@Produces("application/xml")
	public RegistrationReply Registration(@CookieParam ("fn") String firstname,
			@CookieParam ("ln") String lastname,
			@CookieParam ("e") String email,
			@CookieParam ("u") String username,
			@CookieParam ("p") String password) {	
	    log.info("Receiving registration request from client");
	    RegistrationReply reply = RegistrationManager.register(firstname, lastname, email, username, password);
	    if (reply != null) {
	        return reply;
	    } else {
	        throw new WebApplicationException(404);
	    }  
	}
}
