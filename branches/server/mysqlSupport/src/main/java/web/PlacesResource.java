package web;

import java.util.Arrays;
import java.util.LinkedList;
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

import valueobject.Place;
import valueobject.PlaceClient;
import valueobject.SingleItemLocation;

import businessobject.OntologyManager;
import businessobject.PlacesManager;



/**
* Responsible for add/delete/vote places
* @author Anuska
*/

@Path("/{username}/places")
public class PlacesResource {
      
      private static Logger log = LoggerFactory.getLogger(PlacesResource.class);

//PRIVATE
      
      @GET
      @Path("/addPrivatePlaceGET")        
      public void addPrivatePlaceGET(@PathParam("username") String userid, 
                      @CookieParam("sessionid") String sessionid,@QueryParam("title") String title, @QueryParam("streetAddress") String streetAddress,@QueryParam("streetNumber") String streetNumber
                      ,@QueryParam("cap") String cap,@QueryParam("city") String city)
      {
              log.info("Request to add private place from user " + userid + 
                              ", session "+ sessionid);
             System.out.println("placeResource");
             String category="abitazione";
           //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
       		 List<String> categoryList= new LinkedList<String>();
       		 String[] words = category.split(",");
       		 categoryList.addAll(Arrays.asList(words));
             PlacesManager.getInstance().addPrivatePlace(userid, title,streetAddress,streetNumber,cap,city,categoryList);
      }
       
     @POST
     @Path("/addPrivatePlace")    
     @Consumes("application/xml")
     public void addPrivatePlace(@PathParam("username") String userid, 
             @CookieParam("sessionid") String sessionid, PlaceClient place)
     {
         log.info("Request to add private place from user " + userid + 
                 ", session "+ sessionid);
         log.info("Add"+ place.title + " " + place.lat + " " + place.lng);
         
       //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
 		List<String> categoryList= new LinkedList<String>();
 		String[] words = place.category.split(",");
 		categoryList.addAll(Arrays.asList(words));
 		
        PlacesManager.getInstance().addPrivatePlace(userid, place.title,place.streetAddress,place.streetNumber,place.cap,place.city,categoryList);
     }
     
     /*
      * Visualizza tutti i luoghi privati inseriti dall'utente
      */
     @GET
     @Path("/privatePlaces")        
     @Produces("application/xml")
     public List<PlaceClient> privatePlaces(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
     {
    	 log.info("Request to view private places from user " + userid + ", session "+ sessionid);
         System.out.println("-PlacesResource metodo privatePlaces");
         return PlacesManager.getInstance().retrieveAllPrivatePlaces(userid);
     }
   
     /*
      * Cancella un luogo privato
      */
     @GET
     @Path("/deletePrivatePlace")        
     public void deletePrivatePlace(@PathParam("username") String userid,@QueryParam("title") String title,@QueryParam("lat") String lat,@QueryParam("lng") String lng,@CookieParam("sessionid") String sessionid) 
     {
    	 log.info("Request to delete private place from user " + userid + ", session "+ sessionid);
         System.out.println("-PlacesResource metodo deletePrivatePlace");
         PlacesManager.getInstance().deletePrivatePlace(userid,title,lat,lng);
     }
    
//PUBLIC  
     
     @GET
     @Path("/addPublicPlaceGET")  
     // return 0-> ok
     // return 1 -> posto già presente in google
    
     public int addPublicPlaceGET(@PathParam("username") String userid, 
                   @CookieParam("sessionid") String sessionid,@QueryParam("title") String title, @QueryParam("streetAddress") String streetAddress,@QueryParam("streetNumber") String streetNumber
                   ,@QueryParam("cap") String cap,@QueryParam("city") String city,@QueryParam("category") String category)
     {
    	 log.info("Request to add public place from user " + userid + 
                           ", session "+ sessionid);
         System.out.println("placeResource");
         //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
   		 List<String> categoryList= new LinkedList<String>();
   		 String[] words = category.split(",");
   		 categoryList.addAll(Arrays.asList(words));
         
   		 return PlacesManager.getInstance().addPublicPlace(userid, title,streetAddress,streetNumber,cap,city,categoryList);
     }
    
    
     @POST
     @Path("/addPublicPlace")    
     @Consumes("application/xml")
     // return 0-> ok
     // return 1 -> posto già presente in google
     public int addPublicPlace(@PathParam("username") String userid, 
           @CookieParam("sessionid") String sessionid, PlaceClient place)
     {
    	 log.info("Request to add public place from user " + userid + 
               ", session "+ sessionid);
    	 log.info("Add"+ place.title + " " + place.lat + " " + place.lng);
     
    	 //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
    	 List<String> categoryList= new LinkedList<String>();
    	 String[] words = place.category.split(",");
    	 categoryList.addAll(Arrays.asList(words));
		
    	 return PlacesManager.getInstance().addPublicPlace(userid, place.title,place.streetAddress,place.streetNumber,place.cap,place.city,categoryList);
     }
     
     /*
      * Visualizza tutti i luoghi pubblici votati
      */
      
     @GET
     @Path("/publicPlacesVoted")        
     @Produces("application/xml")
     public List<PlaceClient> publicPlacesVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
     {
    	 log.info("Request to view public places from user " + userid + ", session "+ sessionid);
         System.out.println("-PlacesResource metodo publicPlacesVoted");             
         return PlacesManager.getInstance().retrieveAllPublicPlacesVoted(userid);
     }

     /*
      * Cancella il voto a un luogo pubblico-categoria
      */
     @GET
     @Path("/deleteVotePublicPlace")        
     public void deleteVotePublicPlace(@PathParam("username") String userid,@QueryParam("title") String title,@QueryParam("lat") String lat,
    		  @QueryParam("lng") String lng,@QueryParam("category") String category,
    		  @CookieParam("sessionid") String sessionid) 
     {
    	 log.info("Request to delete private place from user " + userid + ", session "+ sessionid);
    	 System.out.println("-PlacesResource metodo deletePrivatePlace");
         PlacesManager.getInstance().deleteVotePublicPlace(userid,title,lat,lng,category);
     }
      
     @GET
     @Path("/searchPublicPlaceGET")     
     @Produces("application/xml")
     public List<PlaceClient> searchPublicPlaceGET(@PathParam("username") String userid, 
                   @CookieParam("sessionid") String sessionid,
                   @QueryParam("title") String title, 
                   @QueryParam("streetAddress") String streetAddress,
                   @QueryParam("streetNumber") String streetNumber,
                   @QueryParam("cap") String cap,
                   @QueryParam("city") String city,
                   @QueryParam("category") String category)
     {
    	 log.info("Request to add public place from user " + userid + 
                           ", session "+ sessionid);
         System.out.println("placeResource");
       
         //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
   		// List<String> categoryList= new LinkedList<String>();
   		// String[] words = category.split(",");
   		// categoryList.addAll(Arrays.asList(words));
         
   		 return PlacesManager.getInstance().searchPublicPlace(userid, title,streetAddress,streetNumber,cap,city,category);
     }   
     
     @GET
     @Path("/votePublicPlaceGET")  
     //vota un solo posto
     public void votePublicPlaceGET(@PathParam("username") String userid, 
                   @CookieParam("sessionid") String sessionid,@QueryParam("title") String title, @QueryParam("lat") String lat,@QueryParam("lng") String lng
                   ,@QueryParam("category") String category)
     {
    	 log.info("Request to vote public places from user " + userid + 
                           ", session "+ sessionid);
         System.out.println("placeResource");
         //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
   		 List<String> categoryList= new LinkedList<String>();
   		 String[] words = category.split(",");
   		 categoryList.addAll(Arrays.asList(words));
         
   		 PlacesManager.getInstance().votePublicPlace(userid, title,lat,lng,categoryList);
     }
    
    
     @POST
     @Path("/votePublicPlace")    
     @Consumes("application/xml")
     //vota un solo posto
     public void votePublicPlace(@PathParam("username") String userid, 
           @CookieParam("sessionid") String sessionid, PlaceClient place)
     {
    	 log.info("Request to vote public places from user " + userid + 
               ", session "+ sessionid);
    	 log.info("Add"+ place.title + " " + place.lat + " " + place.lng);
     
    	 //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
    	 List<String> categoryList= new LinkedList<String>();
    	 String[] words = place.category.split(",");
    	 categoryList.addAll(Arrays.asList(words));
		
    	 PlacesManager.getInstance().votePublicPlace(userid, place.title,place.lat,place.lng,categoryList);
     }
     
     @POST
     @Path("/votePublicPlace")    
     @Consumes("application/xml")
     //vota una lista di posti
     public void votePublicPlace(@PathParam("username") String userid, 
           @CookieParam("sessionid") String sessionid,List<PlaceClient> places)
     {
    	 log.info("Request to vote public places from user " + userid + 
               ", session "+ sessionid);
    	
    	 PlacesManager.getInstance().votePublicPlaces(userid, places);
     }
     
} 