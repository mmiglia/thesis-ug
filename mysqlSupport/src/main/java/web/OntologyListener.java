package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleItemLocation;
import valueobject.SingleActionLocation;
import valueobject.SingleLocationLocation;
import valueobject.Location;

import businessobject.OntologyManager;


/**
 * Responsible for add/delete/vote assertion to update ontology file
 * @author Anuska
 */

@Path("/{username}/ontology")

public class OntologyListener {
	private static Logger log = LoggerFactory.getLogger(OntologyListener.class);

//ITEM	
	@GET
	@Path("/addItemInLocation")	
	@Produces("application/xml")
	public SingleItemLocation addItemInLocation(@PathParam("username") String userid, 
			@QueryParam("item") String item, @QueryParam("location") String location,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add item-location from user " + userid + 
				", session "+ sessionid);
		log.info("Add"+ item + "in location" + location);
		return OntologyManager.getInstance().addItemInLocation(userid, item, location);
	}
	
/*	@GET
	@Path("/addItemInLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleItemLocation addItemInLocation(@PathParam("username") String userid, 
			@CookieParam("sessionid") String sessionid, SingleItemLocation itemLocation)
	{
		log.info("Request to add item-location from user " + userid + 
				", session "+ sessionid);
		log.info("Add"+ itemLocation.item + "in location" + itemLocation.location);
		
		return OntologyManager.getInstance().addItemInLocation(userid, itemLocation.item,itemLocation.location);
	}
*/
	/*
	 * Visualizza tutti gli item-location inserite da {username}
	 */
	@GET
	@Path("/viewItemLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleItemLocation> viewItemLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view item-location from user " + userid + ", session "+ sessionid);
			System.out.println("-OntologyListener metodo viewItemLocation");
			
			return OntologyManager.getInstance().retrieveAllItemLocation(userid);
	}
	
	/*
	 * L'utente {username} vota una coppia item-location
	 */
	@GET
	@Path("/voteItem")	
	@Produces("application/xml")
	public SingleItemLocation voteItem(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item, @QueryParam("location") String location)
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteItem(userid,item,location);
	}

/*	@GET
	@Path("/voteItem")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleItemLocation voteItem(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,SingleItemLocation itemLocation)
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteItem(userid,itemLocation.item,itemLocation.location);
	}
*/	
	
	@GET
	@Path("/viewLocationForItem")	
	@Produces("application/xml")
	public List<Location> viewLocationForItem(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item)
	{
			log.info("Request to view location for item: " + item);
			return OntologyManager.getInstance().viewLocationForItem(userid,item);
	}
	
// ACTION
	@GET
	@Path("/addActionInLocation")	
	@Produces("application/xml")
	public SingleActionLocation addActionInLocation(@PathParam("username") String userid, 
			@QueryParam("action") String action, @QueryParam("location") String location,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ action + "in location" + location);
		
		return OntologyManager.getInstance().addActionInLocation(userid, action, location);
	}
/*	
	@GET
	@Path("/addActionInLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleActionLocation addActionInLocation(@PathParam("username") String userid, 
			@CookieParam("sessionid") String sessionid, SingleActionLocation actionLocation) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ actionLocation.action + "in location" + actionLocation.location);
		
		return OntologyManager.getInstance().addActionInLocation(userid, actionLocation.action,actionLocation.location);
	}
*/	
	/*
	 * Visualizza tutti le action-location inserite da {username}
	 */
	@GET
	@Path("/viewActionLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleActionLocation> viewActionLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view action-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().retrieveAllActionLocation(userid);
	}
	
	/*
	 * L'utente {username} vota una coppia action-location
	 */
	@GET
	@Path("/voteAction")	
	@Produces("application/xml")
	public SingleActionLocation voteAction(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action, @QueryParam("location") String location)
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteAction(userid,action,location);
	}
/*	
	@GET
	@Path("/voteAction")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleActionLocation voteItem(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleActionLocation actionLocation ) 
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteAction(userid,actionLocation.action,actionLocation.location);
	}
*/
	
	@GET
	@Path("/viewLocationForAction")	
	@Produces("application/xml")
	public List<Location> viewLocationForAction(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action)
	{
			log.info("Request to view location for action: " + action);
			return OntologyManager.getInstance().viewLocationForAction(userid,action);
	}
	
//LOCATION	
	
	@GET
	@Path("/addLocationInLocation")	
	@Produces("application/xml")
	public SingleLocationLocation addLocationInLocation(@PathParam("username") String userid, 
			@QueryParam("location1") String location1, @QueryParam("location2") String location2,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ location1 + "in location" + location2);
		
		return OntologyManager.getInstance().addLocationInLocation(userid, location1, location2);
	}
/*
	
	@GET
	@Path("/addLocationInLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleLocationLocation addLocationInLocation(@PathParam("username") String userid, 
			SingleLocationLocation locationLocation,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ locationLocation.location1 + "in location" + locationLocation.location2);
		
		return OntologyManager.getInstance().addLocationInLocation(userid, locationLocation.location1, locationLocation.location2);
	}
*/
	/*
	 * Visualizza tutti le location-location inserite da {username}
	 */
	@GET
	@Path("/viewLocationLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleLocationLocation> viewLocationLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view action-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().retrieveAllLocationLocation(userid);
	}
	
	/*
	 * L'utente {username} vota una coppia location-location
	 */
	@GET
	@Path("/voteLocation")	
	@Produces("application/xml")
	public SingleLocationLocation voteLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("location1") String location1, @QueryParam("location2") String location2)
	{
			log.info("Request to vote location-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteLocation(userid,location1,location2);
	}

/*	
	@GET
	@Path("/voteLocation")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleLocationLocation voteLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleLocationLocation locationLocation ) 
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteLocation(userid,locationLocation.action,locationLocation.location2);
	}
*/
	
	@GET
	@Path("/viewLocationForLocation")	
	@Produces("application/xml")
	public List<Location> viewLocationForLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("location") String location)
	{
			log.info("Request to view location for location: " + location);
			return OntologyManager.getInstance().viewLocationForLocation(userid,location);
	}
	
	
}
