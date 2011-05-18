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

import valueobject.SingleItemLocation;
import valueobject.SingleActionLocation;
import valueobject.SingleLocationLocation;
import valueobject.Location;

import businessobject.OntologyManager;
import businessobject.OntologyReasoner;


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
	
	@POST
	@Path("/addItemInLocationObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	/*
	@return null if the couple item-location is already entered in ontology 
			file or in the db, a SingleItemLocation otherwise
	*/
	public SingleItemLocation addItemInLocationObject(@PathParam("username") String userid, 
			@CookieParam("sessionid") String sessionid, SingleItemLocation itemLocation)
	{
		log.info("Request to add item-location from user " + userid + 
				", session "+ sessionid);
		log.info("Add"+ itemLocation.item + "in location" + itemLocation.location);
		
		return OntologyManager.getInstance().addItemInLocation(userid, itemLocation.item,itemLocation.location);
	}

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

	@POST
	@Path("/voteItemObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleItemLocation voteItemObject(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,SingleItemLocation itemLocation)
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteItem(userid,itemLocation.item,itemLocation.location);
	}
	
	
	@GET
	@Path("/viewLocationForItem")	
	@Produces("application/xml")
	public List<Location> viewLocationForItem(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item)
	{
			log.info("Request to view location for item: " + item);
			return OntologyManager.getInstance().viewLocationForItem(userid,item);
	}
	
	@GET
	@Path("/deleteVoteForItemLocation")	
	//cancella il voto di un utente
	public boolean deleteVoteForItemLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("item") String item,@QueryParam("location") String location)
	{
			log.info("Request to delete vote for item location: " + item);
			return OntologyManager.getInstance().deleteVoteForItemLocation(userid,item,location);
	}
	
	@POST
	@Path("/deleteVoteForItemLocationObject")	
	@Consumes("application/xml")
	//cancella il voto di un utente
	public boolean deleteVoteForItemLocationObject(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,SingleItemLocation itemLocation)
	{
			log.info("Request to delete vote for item location: " + itemLocation.item);
			return OntologyManager.getInstance().deleteVoteForItemLocation(userid,itemLocation.item,itemLocation.location);
	}
	
	/*
	 * Visualizza tutti gli item-location votati da {username}
	 */
	@GET
	@Path("/viewItemLocationVoted")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleItemLocation> viewItemLocationVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view item-location voted from user " + userid + ", session "+ sessionid);
			System.out.println(" Sono in OntologyListener /viewItemLocationVoted");
			return OntologyManager.getInstance().retrieveAllItemLocationVoted(userid);
	}
	
	/*
	 * Prova inserimento di un item-location nell'ontologia
	 */
	@GET
	@Path("/updateOntology")	
	@Consumes("application/xml")
	public boolean updateOntology(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{       
			//OntologyReasoner reasoner= new OntologyReasoner();
			return OntologyReasoner.getInstance().updateOntology();
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
	
	@POST
	@Path("/addActionInLocationObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	/*
	@return null if the couple action-location is already entered in ontology 
			file or in the db, a SingleActionLocation otherwise
	*/
	public SingleActionLocation addActionInLocationObject(@PathParam("username") String userid, 
			@CookieParam("sessionid") String sessionid, SingleActionLocation actionLocation) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ actionLocation.action + "in location" + actionLocation.location);
		
		return OntologyManager.getInstance().addActionInLocation(userid, actionLocation.action,actionLocation.location);
	}
	
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
	
	@POST
	@Path("/voteActionObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleActionLocation voteItemObject(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleActionLocation actionLocation ) 
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteAction(userid,actionLocation.action,actionLocation.location);
	}

	
	@GET
	@Path("/viewLocationForAction")	
	@Produces("application/xml")
	public List<Location> viewLocationForAction(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action)
	{
			log.info("Request to view location for action: " + action);
			return OntologyManager.getInstance().viewLocationForAction(userid,action);
	}
	
	/*
	 * Visualizza tutti le action-location votate da {username}
	 */
	@GET
	@Path("/viewActionLocationVoted")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<SingleActionLocation> viewActionLocationVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
	{
			log.info("Request to view action-location voted from user " + userid + ", session "+ sessionid);
			System.out.println(" Sono in OntologyListener /viewActionLocationVoted");
			return OntologyManager.getInstance().retrieveAllActionLocationVoted(userid);
	}
	
	@GET
	@Path("/deleteVoteForActionLocation")	
	//@Produces("application/xml")
	public boolean deleteVoteForActionLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("action") String action,@QueryParam("location") String location)
	{
			log.info("Request to delete vote for item location: " + action);
			return OntologyManager.getInstance().deleteVoteForActionLocation(userid,action,location);
	}
	
	@POST
	@Path("/deleteVoteForActionLocationObject")	
	@Consumes("application/xml")
	//cancella il voto di un utente
	public boolean deleteVoteForActionLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,SingleActionLocation actionLocation)
	{
			log.info("Request to delete vote for item location: " + actionLocation.action);
			return OntologyManager.getInstance().deleteVoteForItemLocation(userid,actionLocation.action,actionLocation.location);
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

	
	@POST
	@Path("/addLocationInLocationObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleLocationLocation addLocationInLocationObject(@PathParam("username") String userid, 
			SingleLocationLocation locationLocation,
			@CookieParam("sessionid") String sessionid) 
	{
		log.info("Request to add action-location from user " + userid + ", session "+ sessionid);
		log.info("Add "+ locationLocation.location1 + "in location" + locationLocation.location2);
		
		return OntologyManager.getInstance().addLocationInLocation(userid, locationLocation.location1, locationLocation.location2);
	}

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

	
	@POST
	@Path("/voteLocationObject")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public SingleLocationLocation voteLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid, SingleLocationLocation locationLocation ) 
	{
			log.info("Request to vote item-location from user " + userid + ", session "+ sessionid);
			return OntologyManager.getInstance().voteLocation(userid,locationLocation.location1,locationLocation.location2);
	}

	
	@GET
	@Path("/viewLocationForLocation")	
	@Produces("application/xml")
	public List<Location> viewLocationForLocation(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid,@QueryParam("location") String location)
	{
			log.info("Request to view location for location: " + location);
			return OntologyManager.getInstance().viewLocationForLocation(userid,location);
	}
	
	
}
