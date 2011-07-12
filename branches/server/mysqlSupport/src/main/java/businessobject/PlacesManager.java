package businessobject;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.Place;
import valueobject.SingleItemLocation;
import dao.OntologyDatabase;
import dao.PlacesDatabase;

public class PlacesManager {
       
       private final static Logger log = LoggerFactory.
       getLogger(PlacesManager.class);
       
       
       private static class InstanceHolder {
               private static final PlacesManager INSTANCE = new PlacesManager();
       }

       public static PlacesManager getInstance() {
               return InstanceHolder.INSTANCE;
       }
       
       
   	/**
   	 * Enter in the database the private place 
   	 * @param user username of the user that enter the couple item-location
   	 * @param title the title of the private place
   	 * @param lat the latitute of the private place to insert
   	 * @param lng the longitude of the private place to insert
   	 * @return the place
   	*/
   	public Place addPrivatePlace(String user, String titlePlace,String streetAddressPlace, String streetNumberPlace, String cityPlace) 
   	{
   		
   		String title = titlePlace.toLowerCase();
   		String streetAddress = streetAddressPlace.toLowerCase();
   		String streetNumber = streetNumberPlace.toLowerCase();
   		String city = cityPlace.toLowerCase();
   		
   		title.replaceAll(" ", "_");
   	
   		return PlacesDatabase.istance.addPrivatePlace(user,title,streetAddress,streetNumber,city);
   	}
       
       /**
        * retrieve all public places voted by this user
        * 
        * @param userid unique UUID of the user
        * @return list that contains all public places voted by the user
        */
       public List<Place> retrieveAllPublicPlacesVoted(String userid) 
       {
               return PlacesDatabase.istance.getAllPublicPlacesVoted(userid);
       }
       
       /**
        * retrieve all private places entered by this user
        * 
        * @param userid unique UUID of the user
        * @return list that contains all private places entered by the user
        */
       public List<Place> retrieveAllPrivatePlaces(String userid) 
       {
               return PlacesDatabase.istance.getAllPrivatePlaces(userid);
       }
       /**
        * delete private place entered by this user
        * 
        * @param userid unique UUID of the user
        * @return list that contains all private places entered by the user
        */
       public void deletePrivatePlace(String userid,String title,String lat, String lng) 
       {
               return PlacesDatabase.istance.deletePrivatePlace(userid, title, lat,  lng);
       }
       
}