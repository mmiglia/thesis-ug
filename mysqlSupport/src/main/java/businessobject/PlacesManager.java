package businessobject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.Place;
import valueobject.PlaceClient;
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
      
//PRIVATE     
     /**
      * Enter in the database the private place 
    */
     public void addPrivatePlace(String user, String titlePlace,String streetAddressPlace, String streetNumberPlace,String capPlace, String cityPlace,List<String> category) 
     {
         
         String title = titlePlace.toLowerCase();
         String streetAddress = streetAddressPlace.toLowerCase();
         String streetNumber = streetNumberPlace.toLowerCase();
         String cap = capPlace.toLowerCase();
         String city = cityPlace.toLowerCase();
         title.replaceAll(" ", "_");
         
         System.out.println("placeManager:"+title+" "+streetAddress+" "+streetNumber+" "+cap+""+city);
     
         PlacesDatabase.istance.addPrivatePlace(user,title,streetAddress,streetNumber,cap,city,category);
     }
     
     /**
      * retrieve all private places entered by this user
      *  
      * @param userid unique UUID of the user
      * @return list that contains all private places entered by the user
      */
     public List<PlaceClient> retrieveAllPrivatePlaces(String userid) 
     {
             return PlacesDatabase.istance.getAllPrivatePlaces(userid);
     }
     
     /**
      * delete private place entered by this user
      */
     public void deletePrivatePlace(String userid,String title,String lat, String lng) 
     {
             PlacesDatabase.istance.deletePrivatePlace(userid, title, lat,  lng);
     }
     
     /*
      * Method called by LocationAwareThread for add to the hint list
      *  private place entered by the user
      */
     public static List<Hint> searchPrivatePlacesDB(String userid,float latitude, float longitude,String query)
     {
    	 	 return PlacesDatabase.istance.searchPrivatePlacesDB(userid,latitude,longitude,query);
     }
//PUBLIC
     /**
      * Enter in the database the public place 
      * @return  0-> ok
     			 1 -> posto già presente in google
      */
     public int addPublicPlace(String user, String titlePlace,String streetAddressPlace, String streetNumberPlace,String capPlace, String cityPlace,List<String> category) 
     {
         
         String title = titlePlace.toLowerCase();
         String streetAddress = streetAddressPlace.toLowerCase();
         String streetNumber = streetNumberPlace.toLowerCase();
         String cap = capPlace.toLowerCase();
         String city = cityPlace.toLowerCase();
         title.replaceAll(" ", "_");
         
         System.out.println("placeManager:"+title+" "+streetAddress+" "+streetNumber+" "+cap+""+city);
     
         return PlacesDatabase.istance.addPublicPlace(user,title,streetAddress,streetNumber,cap,city,category);
     }
      
      /**
       * retrieve all public places voted by this user
       * 
       * @param userid unique UUID of the user
       * @return list that contains all public places voted by the user
       */
      public List<PlaceClient> retrieveAllPublicPlacesVoted(String userid) 
      {
              return PlacesDatabase.istance.getAllPublicPlacesVoted(userid);
      }
      
      /**
       * delete vote for public place-category voted by this user
       */
      public void deleteVotePublicPlace(String userid,String title,String lat, String lng,String category) 
      {
          PlacesDatabase.istance.deleteVotePublicPlace(userid, title, lat,  lng,category);
      }
      
      /**
       * retrieve all public places with certain constraints
       * @return list that contains all public places that corrispond to the request
       */
      public List<PlaceClient> searchPublicPlace(String userid,String title,String streetAddress,String streetNumber,String cap,String city,String category)
      {
              return  PlacesDatabase.istance.searchPublicPlace(userid,title,streetAddress,streetNumber,cap,city,category);
      }
      
      /*
       * Method called by LocationAwareThread for add to the hint list
       *  public place voted by the user
       */
      
      public static List<Hint> searchPublicPlacesDB(String userid,float latitude, float longitude,String query)
      {
     	 	 return PlacesDatabase.istance.searchPublicPlacesDB(userid,latitude,longitude,query);
      }
      
      /**
       * vote a place 
       */
      public void votePublicPlace(String user, String title,String lat, String lng,List<String> category) 
      { 
    	  PlacesDatabase.istance.votePublicPlace(user,title,lat,lng,category);
      }
      
      /**
       * vote a list of place 
       */
      public void votePublicPlaces(String userid,List<PlaceClient> places)
      { 
    	  PlacesDatabase.istance.votePublicPlaces(userid,places);
      }
      
}