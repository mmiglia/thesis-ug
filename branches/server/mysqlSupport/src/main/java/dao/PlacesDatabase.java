package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.google.MapsClient;

import valueobject.Coordinate;
import valueobject.Place;
import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;



public enum PlacesDatabase {

      istance;
      
      private static final Logger log = LoggerFactory.getLogger(PlacesDatabase.class);
      
      //MySQL database manager
      private static final MySQLDBManager dbManager=new MySQLDBManager();

      public static List<Place> getAllPublicPlacesVoted(final String userID){
              
              ArrayList<Place> publicPlacesList=new ArrayList<Place>();
              
              Connection conn= (Connection) dbManager.dbConnect();
                      
              String selectQuery="Select * from Place_voted join Place on Place_voted.title=Place.title and " +
                              "Place_voted.lat=Place.lat and Place_voted.lng=Place.lng where Place_voted.username='"+ userID +"' and Place_voted.vote=1";
              
              System.out.println(selectQuery);
              
              
              QueryStatus qs=dbManager.customSelect(conn, selectQuery);
              
              ResultSet rs=(ResultSet)qs.customQueryOutput;

              try{
                      while(rs.next()){
                              String title = rs.getString("title");
                              String lat = rs.getString("lat");
                              String lng = rs.getString("lng");
                              String streetAddress = rs.getString("streetAddress");
                              String streetNumber = rs.getString("streetNumber");
                              String cap = rs.getString("cap");
                              String city = rs.getString("city");
                              
                              
                              publicPlacesList.add(
                                              new Place(title,lat,lng,streetAddress,streetNumber,cap,city)                                        
                                      );
                      }
              }catch(SQLException sqlE){
                      //TODO
          
              }finally{        
                      dbManager.dbDisconnect(conn);
              }
              
              return publicPlacesList;
      }
      
      public static List<Place> getAllPrivatePlaces(final String userID){
          
          ArrayList<Place> publicPlacesList=new ArrayList<Place>();
          
          Connection conn= (Connection) dbManager.dbConnect();
                  
          String selectQuery="Select * from Place" +
                  " where user='"+ userID +"'and  userGroup=0";
          
          System.out.println(selectQuery);
          
          
          QueryStatus qs=dbManager.customSelect(conn, selectQuery);
          
          ResultSet rs=(ResultSet)qs.customQueryOutput;

          try{
                  while(rs.next()){
                          String title = rs.getString("title");
                          String lat = rs.getString("lat");
                          String lng = rs.getString("lng");
                          String streetAddress = rs.getString("streetAddress");
                          String streetNumber = rs.getString("streetNumber");
                          String cap = rs.getString("cap");
                          String city = rs.getString("city");
                          
                          
                          publicPlacesList.add(
                                          new Place(title,lat,lng,streetAddress,streetNumber,cap,city)                                        
                                  );
                  }
          }catch(SQLException sqlE){
                  //TODO
      
          }finally{        
                  dbManager.dbDisconnect(conn);
          }
          
          return publicPlacesList;
      }
      
      
      
      
      public static void deletePrivatePlace(final String userID, final String title, final String lat, final String lng){

          
       Connection conn= (Connection) dbManager.dbConnect();
       
       //Starting transaction
       QueryStatus qs=dbManager.startTransaction(conn);
       
       if(qs.execError){
           //TODO decide what to do in this case (transaction not started)
           log.error(qs.explainError());
           qs.occourtedErrorException.printStackTrace();
           System.out.println("Error during transaction starting... Delete vote for item-location not done");
           log.error("Error during transaction starting... Delete vote for item-location not done");
           dbManager.dbDisconnect(conn);
           return;
       }    
       //cancello il voto in Item_voted
       String deleteQuery="Delete * from Place" +
       " where user='"+ userID +"'and title='" + title + "' and lat='" + lat +"' and lng='" + lng + "' and userGroup=0";
       System.out.println(deleteQuery);
       qs=dbManager.customQuery(conn, deleteQuery);
       
       if(qs.execError){
           log.error(qs.explainError());
           System.out.println("Error during delete vote operation in Item_voted .. aborting operation");
           qs.occourtedErrorException.printStackTrace();
           
           //Rolling back
           dbManager.rollbackTransaction(conn);
           
           log.error("Error during delete vote operation in Item_voted .. aborting operation");
           dbManager.dbDisconnect(conn);
           return;
       }
       
       dbManager.commitTransaction(conn);
       dbManager.dbDisconnect(conn);
       
       return;    
       
          
  }
      
      
      public static void addPrivatePlace( String userID,  String title,  String streetAddress,  String streetNumber,  String cap, String city ){
          
         Connection conn= (Connection) dbManager.dbConnect();
         
         //Starting transaction
         QueryStatus qs=dbManager.startTransaction(conn);
         
         if(qs.execError){
             //TODO decide what to do in this case (transaction not started)
             log.error(qs.explainError());
             qs.occourtedErrorException.printStackTrace();
             System.out.println("Error during transaction starting... Delete vote for item-location not done");
             log.error("Error during transaction starting... Delete vote for item-location not done");
             dbManager.dbDisconnect(conn);
             return;
         }    
         
         Coordinate placeCoordinate = convertAddressToCoordinate(streetAddress,streetNumber,cap,city);
         System.out.println("coordinate- lat:"+placeCoordinate.getLat());
         System.out.println("coordinate- lng:"+placeCoordinate.getLng());
         
         String query="Insert into Place" +
          " (title,lat,lng,streetAddress,streetNumber,cap,city,user,userGroup) values ('" + title + "','"+ placeCoordinate.getLat() + "','" + placeCoordinate.getLng() + "','" + 
              streetAddress + "','" + streetNumber + "','"+ cap+"','"+city + "','"+userID+"',0)";
         
         System.out.println(query);
         qs=dbManager.customQuery(conn, query);
         
         if(qs.execError){
             log.error(qs.explainError());
             System.out.println("Error during add private Place .. aborting operation");
             qs.occourtedErrorException.printStackTrace();
             
             //Rolling back
             dbManager.rollbackTransaction(conn);
             
             log.error("Error during delete vote operation in Item_voted .. aborting operation");
             dbManager.dbDisconnect(conn);
             return;
         }
         
         dbManager.commitTransaction(conn);
         dbManager.dbDisconnect(conn);
         
         return;    
         
             
     }
      
      public static final Coordinate convertAddressToCoordinate(String streetAddress,String streetNumber, String cap, String city)
      {
          streetAddress = streetAddress.replaceAll(" ", "+");
          streetNumber = streetNumber.replaceAll(" ","+");
          city = city.replaceAll(" ","+");
          streetAddress = streetAddress.replaceAll("%20", "+");
          streetNumber = streetNumber.replaceAll("%20","+");
          city = city.replaceAll("%20","+");
          String address = streetAddress + ","+ streetNumber+","+cap+" "+city;
          System.out.println("placeDatabase- convertAddressToCoordinate, address:"+address);
          MapsClient geocoding = new MapsClient(); // currently there's only google, so we use direct call
            Coordinate c = geocoding.covertAddressToCoordinate(address);
          //Coordinate c = new Coordinate("12","45");
          return c;
          
      }
      
}