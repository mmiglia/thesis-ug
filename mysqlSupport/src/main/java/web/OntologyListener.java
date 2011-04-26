package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.OntologyManager;
;


/**
 * Responsible for add/delete/vote assertion to update ontology file
 */
@Path("/{username}/Ontology")
public class OntologyListener {
	private static Logger log = LoggerFactory.getLogger(ContextListener.class);
	

	@POST
	@Path("/addItemInLocation")	
	@Consumes("application/xml")	
	public String addItemInLocation(@PathParam("username") String userid, @QueryParam("item") String item, @QueryParam("location") String location,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to add item-location from user " + userid + ", session "+ sessionid);
		log.info("Add"+ item + "in location" + location);
		OntologyManager.getInstance().addItemInLocation(userid, item, location);
		return "Add"+ item + "in location" + location;
	}
}
