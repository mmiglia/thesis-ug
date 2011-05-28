package businessobject;

import java.util.List;

import dao.OntologyDatabase;

import valueobject.SingleActionLocation;
import valueobject.SingleItemLocation;
import valueobject.SingleLocationLocation;
import valueobject.Location;

/* 27-4-2011
 * 
 * This interface is used everytime the user wants to retrieve or add events,
 * it saves in the local event databases
 * @author Anuska
*/

public interface OntologyInterface {
	
//ITEM
	/**
	 * Enter in the database the couple item-location (this item can be 
	 * found in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param item the item that has been entered
	 * @param location the location in wich the item can be found in
	 * @return the couple item-location if it has been entered with success
	 */
	public SingleItemLocation addItemInLocation(String user, String item,String location);
	
	public SingleItemLocation addItemInLocation(String userID, SingleItemLocation taskObj);
	
	/**
	 * vote for an item-location
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple item-location if the vote is been saved
	 */
	public SingleItemLocation voteItem(String userid,String item, String location) ;
	
	/**
	 * vote for an item-locationList
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple item-location if the vote is been saved
	 */
	public void voteItemLocationList(String userid,String item,List<String> location); 
	
	/**
	 * Retrieve all item-location entered by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all item-location entered by the user
	 */
	public List<SingleItemLocation> retrieveAllItemLocation(String userid);
	
	/**
	 * view location for an item
	 * 
	 * @param userid unique UUID of the user
	 * @param item 
	 * @return list of locations where the item can be found
	 */
	public List<String> viewLocationForItem(String userid,String item);
	
	/**
	 * view location for item voted by userid
	 * 
	 * @param userid unique UUID of the user
	 * @param item 
	 * @return list of locations where the item can be found
	 */
	public List<String> viewLocationForItemVoted(String userid,String item);
	
	/**
	 * delete vote for an item-location by the user userid
	 * 
	 * @param userid unique UUID of the user
	 * @param item 
	 * @param location
	 * @return true if the vote is correct deleted
	 */
	public boolean deleteVoteForItemLocation(String userid,String item,String location);

	/**
	 * retrieve all item-location voted by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all item-location entered by the user
	 */
	public List<SingleItemLocation> retrieveAllItemLocationVoted(String userid);
//ACTION
	/**
	 * Enter in the database the couple action-location (this action can be 
	 * made in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param action the action that has been entered
	 * @param location the location in wich the item can be made
	 * @return the couple item-location if it has been entered with success
	*/
	public SingleActionLocation addActionInLocation(String user, String item,
			String location) ;
	
	/**
	 * retrieve all action-location entered by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all action-location entered by the user
	 */
	public List<SingleActionLocation> retrieveAllActionLocation(String userid);
	
	/**
	 * vote for an action-location
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple action-location if the vote is been saved
	 */
	public SingleActionLocation voteAction(String userid,String action, String location);
	
	/**
	 * vote for an action-location
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple action-location if the vote is been saved
	 */
	public void voteActionLocationList(String userid,String action, List<String> location);
	
	/**
	 * view location for an action
	 * 
	 * @param userid unique UUID of the user
	 * @param action
	 * @return list of locations where the action can be made
	 */
	public List<String> viewLocationForAction(String userid,String action);

	/**
	 * view location for an action voted by userid
	 * 
	 * @param userid unique UUID of the user
	 * @param action
	 * @return list of locations where the item can be found
	 */
	public List<String> viewLocationForActionVoted(String userid,String action); 
	
	/**
	 * delete vote for an action-location by the user userid
	 * 
	 * @param userid unique UUID of the user
	 * @param action 
	 * @param location
	 * @return true if the vote is correct delete
	 */
	public boolean deleteVoteForActionLocation(String userid,String action,String location);

	/**
	 * retrieve all action-location entered by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all action-location entered by the user
	 */
	public List<SingleActionLocation> retrieveAllActionLocationVoted(String userid); 
	

	
//LOCATION
	/**
	 * Enter in the database the couple location-location (this location can be 
	 * made in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param location1 the location that has been entered
	 * @param location2 the location in wich the location1 can be made
	 * @return the couple item-location if it has been entered with success
	*/

	public SingleLocationLocation addLocationInLocation(String user, String location1,
			String location2);
	/**
	 * retrieve all action-location entered by this user
	 * 
	 * @param userid unique UUID of the user
	 * @return list that contains all action-location entered by the user
	 */
	public List<SingleLocationLocation> retrieveAllLocationLocation(String userid);
	
	/**
	 * vote for a location-location
	 * 
	 * @param userid unique UUID of the user
	 * @return the couple location-location if the vote is been saved
	 */
	public SingleLocationLocation voteLocation(String userid,String location1, String location2);

	/**
	 * view location for a location
	 * 
	 * @param userid unique UUID of the user
	 * @param location
	 * @return list of locations where the location can be found
	 */
	public List<Location> viewLocationForLocation(String userid,String location);

}
