package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.RegistrationReply;
import businessobject.MailUtility;
import businessobject.OntologyReasoner;
import businessobject.RegistrationManager;

@Path("/registration")

public class RegistrationResource extends javax.servlet.http.HttpServlet {
	private static Logger log = LoggerFactory.getLogger(RegistrationResource.class);
	
	@GET
	@Produces("application/xml")
	public RegistrationReply Registration(@QueryParam ("fn") String firstname,
			@QueryParam ("ln") String lastname,
			@QueryParam ("e") String email,
			@QueryParam ("u") String username,
			@CookieParam ("p") String password) {	
	    log.info("Receiving registration request from client");
	    if (firstname==null || lastname==null || email==null || username==null || password==null) {
	    	RegistrationReply r = new RegistrationReply(4);
	    	return r;
	    }
	    RegistrationReply reply = RegistrationManager.register(firstname, lastname, email, username, password);
	    if (reply != null) {
	        return reply;
	    } else {
	        throw new WebApplicationException(404);
	    }  
	}
	
	@GET
	@Path("/verification")
	@Produces("text/plain")
	public String Verification(@QueryParam ("code") String ver_code, @QueryParam ("email") String email) {
		log.info("Verificating registration from user email code");
		String result = RegistrationManager.verify(ver_code, email);
		return result;
	}
	
	@GET
	@Path("/testmail")
	@Produces("text/plain")
	public String TestMail(@QueryParam ("email") String email) {
		log.info("Test send mail");
		String subject = "Test mail";
		String body = "Mail text: "+email;
		try {
			MailUtility.sendMail(email, subject, body);
			return "Mail inviata con successo!";
		} catch (Exception e) {
			e.printStackTrace();
			return "Error!!! Mail non inviata!";
		}
		
	}
	
	// Aggiunto solo e soltanto da MIrco.
	@GET
	@Path("/reasoner")
	@Produces("text/plain")
	public String reasoner(@QueryParam("keyword") String keyword) {
		log.info("Test the ontology");
		List<String> queryList = new ArrayList<String>();
		queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(keyword));
		String result="Location : ";
		for (String query : queryList) 
			result = result + " " +query;			
		return result;
	}
}