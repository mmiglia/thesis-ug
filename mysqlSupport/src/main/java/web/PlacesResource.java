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

import valueobject.Place;
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

      @GET
      @Path("/addPrivatePlaceGET")        
      public void addPrivatePlaceGET(@PathParam("username") String userid, 
                      @CookieParam("sessionid") String sessionid,@QueryParam("title") String title, @QueryParam("streetAddress") String streetAddress,@QueryParam("streetNumber") String streetNumber
                      ,@QueryParam("cap") String cap,@QueryParam("city") String city)
      {
              log.info("Request to add private place from user " + userid + 
                              ", session "+ sessionid);
              System.out.println("placeResource");
             PlacesManager.getInstance().addPrivatePlace(userid, title,streetAddress,streetNumber,cap,city);
      }
       
   @POST
     @Path("/addPrivatePlace")    
     @Consumes("application/xml")
  
     public void addPrivatePlace(@PathParam("username") String userid, 
             @CookieParam("sessionid") String sessionid, Place place)
     {
         log.info("Request to add private place from user " + userid + 
                 ", session "+ sessionid);
         log.info("Add"+ place.title + " " + place.lat + " " + place.lng);
         
         PlacesManager.getInstance().addPrivatePlace(userid, place.title,place.streetAddress,place.streetNumber,place.cap,place.city);
     }
   
   @GET
   @Path("/addPublicPlaceGET")        
   public void addPublicPlaceGET(@PathParam("username") String userid, 
                   @CookieParam("sessionid") String sessionid,@QueryParam("title") String title, @QueryParam("streetAddress") String streetAddress,@QueryParam("streetNumber") String streetNumber
                   ,@QueryParam("cap") String cap,@QueryParam("city") String city)
   {
           log.info("Request to add public place from user " + userid + 
                           ", session "+ sessionid);
           System.out.println("placeResource");
          //PlacesManager.getInstance().addPublicPlace(userid, title,streetAddress,streetNumber,cap,city);
   }
    
@POST
   @Path("/addPrivatePlace")    
   @Consumes("application/xml")

   public void addPublicPlace(@PathParam("username") String userid, 
           @CookieParam("sessionid") String sessionid, Place place)
   {
       log.info("Request to add item-location from user " + userid + 
               ", session "+ sessionid);
       log.info("Add"+ place.title + " " + place.lat + " " + place.lng);
       
       //PlacesManager.getInstance().addPublicPlace(userid, place.title,place.streetAddress,place.streetNumber,place.cap,place.city);
   }
      
      
      
      /*
       * Visualizza tutti i luoghi pubblici inseriti e votati
       */
      
      @GET
      @Path("/deletePrivatePlace")        
      public void deletePrivatePlace(@PathParam("username") String userid,@QueryParam("title") String title,@QueryParam("lat") String lat,@QueryParam("lng") String lng,@CookieParam("sessionid") String sessionid) 
      {
                      log.info("Request to delete private place from user " + userid + ", session "+ sessionid);
                      System.out.println("-PlacesResource metodo deletePrivatePlace");
                      
                      PlacesManager.getInstance().deletePrivatePlace(userid,title,lat,lng);
      }
      
      /*
       * Visualizza tutti i luoghi pubblici inseriti e votati
       */
      
      @GET
      @Path("/publicPlacesVoted")        
      @Produces("application/xml")
      public List<Place> publicPlacesVoted(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
      {
                      log.info("Request to view public places from user " + userid + ", session "+ sessionid);
                      System.out.println("-PlacesResource metodo publicPlacesVoted");
                      
                      return PlacesManager.getInstance().retrieveAllPublicPlacesVoted(userid);
      }
      
      /*
       * Visualizza tutti i luoghi privati inseriti dall'utente
       */
      
      @GET
      @Path("/privatePlaces")        
      @Produces("application/xml")
      public List<Place> privatePlaces(@PathParam("username") String userid,@CookieParam("sessionid") String sessionid) 
      {
                      log.info("Request to view private places from user " + userid + ", session "+ sessionid);
                      System.out.println("-PlacesResource metodo privatePlaces");
                      
                      return PlacesManager.getInstance().retrieveAllPrivatePlaces(userid);
      }
}