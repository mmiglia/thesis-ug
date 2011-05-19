package businessobject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.CookieParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.SingleItemLocation;
import valueobject.SingleActionLocation;
import valueobject.SingleLocationLocation;
import valueobject.Location;

import dao.OntologyDatabase;


public class OntologyManager implements OntologyInterface {
	private final static Logger log = LoggerFactory.
	getLogger(OntologyManager.class);
	
	
	private static class InstanceHolder {
		private static final OntologyManager INSTANCE = new OntologyManager();
	}

	public static OntologyManager getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
//ITEM
	/**
	 * Enter in the database the couple item-location (this item can be 
	 * found in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param item the item that has been entered
	 * @param location the location in wich the item can be found in
	 * @return an object SingleItemLocation if the couple action-location has been entered with success
	*/
	public SingleItemLocation addItemInLocation(String user, String item,String location) 
	{
		String ite = item.toLowerCase();
		String loc = location.toLowerCase();
		ite.replaceAll(" ", "_");
		loc.replaceAll(" ", "_");
		
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(ite));
		List<Hint> result = new LinkedList<Hint>(); // list of search result
			
		for (String query : queryList) 
		{	/* se c'è già nell'ontologia la coppia item-location torno null, 
				 * in quanto non lo inserisco nel db
				 */
				if (query.compareToIgnoreCase(loc)==0)
				{
					log.info("item-location: " + item+" - "+location+" già in ontologia");
					System.out.println("item-location già dentro l'ontologia");
					return null;
				}
		}
		
		/*
		 * Se arriva qua significa che la coppia item-location non è già 
		 * presente nell'ontologia, allora la inserisco nel db
		 */
		return OntologyDatabase.istance.addItemInLocation(user,item,location);
	}
	 
	
	/**
	 * Create new item-location couple by supplying SingleItemLocation instance
	 * @param userID unique UUID of the user
	 * @param toAdd SingleItemLocation instance
	 * @return an object SingleItemLocation if the couple action-location has been entered with success
	 */
	
	public SingleItemLocation addItemInLocation(String userID, SingleItemLocation taskObj)
	{
		return OntologyDatabase.istance.addItemInLocation(userID,taskObj.item,taskObj.location);
	}
	
	
	/**
	 * retrieve all item-location entered by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all item-location entered by the user
	 */
	public List<SingleItemLocation> retrieveAllItemLocation(String userid) 
	{
		return OntologyDatabase.istance.getAllItemLocation(userid);
	}
	
	/**
	 * retrieve all item-location voted by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all item-location entered by the user
	 */
	public List<SingleItemLocation> retrieveAllItemLocationVoted(String userid) 
	{
		System.out.println(" Sono in Ontology manager retrieveAllItemLocationVoted");
		return OntologyDatabase.istance.getAllItemLocationVoted(userid);
	}
	
	/**
	 * vote for an item-location
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple item-location if the vote is been saved
	 */
	public SingleItemLocation voteItem(String userid,String item, String location) 
	{
		return OntologyDatabase.istance.voteItem(userid,item,location);
	}
	
	
	/**
	 * view location for an item
	 * 
	 * @param userid unique UUID of the user
	 * @param item 
	 * @return list of locations where the item can be found
	 */
	public List<String> viewLocationForItem(String userid,String item) 
	{
		return OntologyDatabase.istance.viewLocationForItem(userid,item);
	}
	
	/**
	 * delete vote for an item-location by the user userid
	 * 
	 * @param userid unique UUID of the user
	 * @param item 
	 * @param location
	 * @return true if the vote is correct deleted
	 */
	public boolean deleteVoteForItemLocation(String userid,String item,String location)
	{
			log.info("Request to delete vote for item-location: " + item+" - "+location);
			return OntologyDatabase.istance.deleteVoteForItemLocation(userid,item,location);
	}
	
	
//ACTION	
		
	/**
	 * Enter in the database the couple action-location (this action can be 
	 * made in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param action the action that has been entered
	 * @param location the location in wich the item can be made
	 * @return an object SingleActionLocation if the couple action-location has been entered with success
	*/
	public SingleActionLocation addActionInLocation(String user, String action,
			String location) 
	{	
		String act = action.toLowerCase();
		String loc = location.toLowerCase();
		act.replaceAll(" ", "_");
		loc.replaceAll(" ", "_");
		
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(act));
		List<Hint> result = new LinkedList<Hint>(); // list of search result
			
		for (String query : queryList) 
		{	/* se c'è già nell'ontologia la coppia item-location torno null, 
				 * in quanto non lo inserisco nel db
				 */
				if (query.compareToIgnoreCase(loc)==0)
				{
					log.info("action-location: " + action+" - "+location+" già in ontologia");
					System.out.println("action-location già dentro l'ontologia");
					return null;
				}
		}
		
		/*
		 * Se arriva qua significa che la coppia item-location non è già 
		 * presente nell'ontologia, allora la inserisco nel db
		 */
		return OntologyDatabase.istance.addActionInLocation(user,action,location);
	}
	
	/**
	 * retrieve all action-location entered by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all action-location entered by the user
	 */
	public List<SingleActionLocation> retrieveAllActionLocation(String userid) 
	{
		return OntologyDatabase.istance.getAllActionLocation(userid);
	}
	
	/**
	 * retrieve all action-location voted by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all action-location entered by the user
	 */
	public List<SingleActionLocation> retrieveAllActionLocationVoted(String userid) 
	{
		System.out.println(" Sono in Ontology manager retrieveAllActionLocationVoted");
		return OntologyDatabase.istance.getAllActionLocationVoted(userid);
	}
	
	/**
	 * vote for an action-location
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple action-location if the vote is been saved
	 */
	public SingleActionLocation voteAction(String userid,String action, String location) 
	{
		return OntologyDatabase.istance.voteAction(userid,action,location);
	}
	
	/**
	 * view location for an action
	 * 
	 * @param userid unique UUID of the user
	 * @param action
	 * @return list of locations where the item can be found
	 */
	public List<String> viewLocationForAction(String userid,String action) 
	{
		return OntologyDatabase.istance.viewLocationForAction(userid,action);
	}
	
	/**
	 * delete vote for an action-location by the user userid
	 * 
	 * @param userid unique UUID of the user
	 * @param action 
	 * @param location
	 * @return true if the vote is correct deleted
	 */
	public boolean deleteVoteForActionLocation(String userid,String action,String location)
	{
			log.info("Request to delete vote for action-location: " + action+" - "+location);
			return OntologyDatabase.istance.deleteVoteForActionLocation(userid,action,location);
	}
	
	
//LOCATION
	/**
	 * Enter in the database the couple location-location (this location can be 
	 * made in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param location1 the location that has been entered
	 * @param location2 the location in wich the location1 can be made
	 * @return true if the couple item-location has been entered with success
	*/
	public SingleLocationLocation addLocationInLocation(String user, String location1,
			String location2) 
	{
		return OntologyDatabase.istance.addLocationInLocation(user,location1,location2);
	}
	
	/**
	 * retrieve all action-location entered by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all action-location entered by the user
	 */
	public List<SingleLocationLocation> retrieveAllLocationLocation(String userid) 
	{
		return OntologyDatabase.istance.getAllLocationLocation(userid);
	}
	
	/**
	 * vote for a location-location
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple location-location if the vote is been saved
	 */
	public SingleLocationLocation voteLocation(String userid,String location1, String location2) 
	{
		return OntologyDatabase.istance.voteLocation(userid,location1,location2);
		//return null;
	}
	
	/**
	 * view location for a location
	 * 
	 * @param userid unique UUID of the user
	 * @param location
	 * @return list of locations where the item can be found
	 */
	public List<Location> viewLocationForLocation(String userid,String location) 
	{
		return OntologyDatabase.istance.viewLocationForLocation(userid,location);
	}
	
	
}
