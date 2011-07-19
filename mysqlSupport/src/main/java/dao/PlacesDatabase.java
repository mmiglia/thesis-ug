package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.DateUtils;
import businessobject.google.MapsClient;

import valueobject.Coordinate;
import valueobject.Place;
import valueobject.PlaceClient;
import valueobject.SingleItemLocation;
import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;



public enum PlacesDatabase {

      istance;
      
      private static final Logger log = LoggerFactory.getLogger(PlacesDatabase.class);
      
      //MySQL database manager
      private static final MySQLDBManager dbManager=new MySQLDBManager();
//PRIVATE
      
      public static void addPrivatePlace( String userID,  String title,  String streetAddress,  String streetNumber,  String cap, String city,List<String> category )
      {
          
         Connection conn= (Connection) dbManager.dbConnect();
         
         //Starting transaction
         QueryStatus qs=dbManager.startTransaction(conn);
         
         if(qs.execError){
             //TODO decide what to do in this case (transaction not started)
             log.error(qs.explainError());
             qs.occourtedErrorException.printStackTrace();
             System.out.println("Error during transaction starting... Add private place not done");
             log.error("Error during transaction starting... Add private place not done");
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
             
             log.error("Error during  Add private place .. aborting operation");
             dbManager.dbDisconnect(conn);
             return;
         }
         
         dbManager.commitTransaction(conn);
         dbManager.dbDisconnect(conn);
         //aggiungi categoria
         
         return;      
     }
      
      public static List<Place> getAllPrivatePlaces(final String userID)
      {
          
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
      
      
      public static void deletePrivatePlace(final String userID, final String title, final String lat, final String lng)
      {
    	  Connection conn= (Connection) dbManager.dbConnect();
       
    	  //Starting transaction
    	  QueryStatus qs=dbManager.startTransaction(conn);
       
    	  if(qs.execError){
    		  //TODO decide what to do in this case (transaction not started)
    		  log.error(qs.explainError());
    		  qs.occourtedErrorException.printStackTrace();
    		  System.out.println("Error during transaction starting... Delete private place not done");
    		  log.error("Error during transaction starting... Delete private place not done");
    		  dbManager.dbDisconnect(conn);
    		  return;
    	  }    
    	  
    	  String deleteQuery="Delete * from Place" +
    	  " where user='"+ userID +"'and title='" + title + "' and lat='" + lat +"' and lng='" + lng + "' and userGroup=0";
    	  System.out.println(deleteQuery);
    	  qs=dbManager.customQuery(conn, deleteQuery);
       
    	  if(qs.execError){
    		  log.error(qs.explainError());
    		  System.out.println("Error during delete private place .. aborting operation");
    		  qs.occourtedErrorException.printStackTrace();
           
    		  //Rolling back
    		  dbManager.rollbackTransaction(conn);
           
    		  log.error("Error during delete private place .. aborting operation");
    		  dbManager.dbDisconnect(conn);
    		  return;
    	  }
       
    	  dbManager.commitTransaction(conn);
    	  dbManager.dbDisconnect(conn);
       
    	  return; 
      }
      
//PUBLIC
      
      public static void addPublicPlace( String userID,  String title,  String streetAddress,  String streetNumber,  String cap, String city, List<String> category)
      {
          
          Connection conn= (Connection) dbManager.dbConnect();
          
          //Starting transaction
          QueryStatus qs=dbManager.startTransaction(conn);
          
          if(qs.execError){
              //TODO decide what to do in this case (transaction not started)
              log.error(qs.explainError());
              qs.occourtedErrorException.printStackTrace();
              System.out.println("Error during transaction starting... Add public place not done");
              log.error("Error during transaction starting... Add public place not done");
              dbManager.dbDisconnect(conn);
              return;
          }    
          
          Coordinate placeCoordinate = convertAddressToCoordinate(streetAddress,streetNumber,cap,city);
          System.out.println("coordinate- lat:"+placeCoordinate.getLat());
          System.out.println("coordinate- lng:"+placeCoordinate.getLng());
          
          String query="Insert into Place" +
           " (title,lat,lng,streetAddress,streetNumber,cap,city,user,userGroup) values ('" + title + "','"+ placeCoordinate.getLat() + "','" + placeCoordinate.getLng() + "','" + 
               streetAddress + "','" + streetNumber + "','"+ cap+"','"+city + "','"+userID+"',-1)";
          
          System.out.println(query);
          qs=dbManager.customQuery(conn, query);
          
          if(qs.execError){
              log.error(qs.explainError());
              System.out.println("Error during add public Place .. aborting operation");
              qs.occourtedErrorException.printStackTrace();
              
              //Rolling back
              dbManager.rollbackTransaction(conn);
              
              log.error("Error during add public Place .. aborting operation");
              dbManager.dbDisconnect(conn);
              for (String s:category)
              {
            	  addPlaceCategory(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s);
              }
              return;
          }
          for (String s:category)
          {
        	  addPlaceCategory(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s);
        	//  votePlace(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s);  
        	//  votePlaceHistorical(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s); 
          }
        
          dbManager.commitTransaction(conn);
          dbManager.dbDisconnect(conn);
         
          return;          
      }
      
      public static List<PlaceClient> getAllPublicPlacesVoted(final String userID)
      {
              
              ArrayList<PlaceClient> publicPlacesList=new ArrayList<PlaceClient>();
              
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
                              String category = rs.getString("category");
                              
                              publicPlacesList.add(
                                              new PlaceClient(title,lat,lng,streetAddress,streetNumber,cap,city,category)                                        
                                      );
                      }
              }catch(SQLException sqlE){
                      //TODO
          
              }finally{        
                      dbManager.dbDisconnect(conn);
              }
              
              return publicPlacesList;
      }
      
     
      
      public static void deleteVotePublicPlace(String userID,String title,String lat, String lng,String category)
      {
    	  Connection conn= (Connection) dbManager.dbConnect();
          
    	  //Starting transaction
    	  QueryStatus qs=dbManager.startTransaction(conn);
       
    	  if(qs.execError){
    		  //TODO decide what to do in this case (transaction not started)
    		  log.error(qs.explainError());
    		  qs.occourtedErrorException.printStackTrace();
    		  System.out.println("Error during transaction starting... Delete vote for public place not done");
    		  log.error("Error during transaction starting... Delete vote for public place not done");
    		  dbManager.dbDisconnect(conn);
    		  return;
    	  }    
    	  //cancello il voto in Place_voted
    	  String deleteQuery="Delete from Place_voted" +
    	  " where username='"+ userID +"' and title='" + title + "' and lat='" + lat +"' and lng='" + lng + "' and category='"+category+"'";
    	  System.out.println(deleteQuery);
    	  qs=dbManager.customQuery(conn, deleteQuery);
       
    	  if(qs.execError){
    		  log.error(qs.explainError());
    		  System.out.println("Error during delete vote for public place .. aborting operation");
    		  qs.occourtedErrorException.printStackTrace();
           
    		  //Rolling back
    		  dbManager.rollbackTransaction(conn);
           
    		  log.error("Error during delete private place .. aborting operation");
    		  dbManager.dbDisconnect(conn);
    		  return;
    	  }
          //aggiorno lo storico
    	  String query="Insert into Place_voted_historical" +
          " (title,lat,lng,category,username,vote) values ('" + title + "','"+ lat + "','" + lng + "','" + 
              category + "','"+userID+"',2)";
         
         System.out.println(query);
         qs=dbManager.customQuery(conn, query);
         
         if(qs.execError){
             log.error(qs.explainError());
             System.out.println("Error during adding vote in Place historical .. aborting operation");
             qs.occourtedErrorException.printStackTrace();
             
             //Rolling back
             dbManager.rollbackTransaction(conn);
             
             log.error("Error during adding vote in Place historical .. aborting operation");
             dbManager.dbDisconnect(conn);
             return;
         }
    	  
    	  dbManager.commitTransaction(conn);
    	  dbManager.dbDisconnect(conn);
       
    	  return;    
      }

      
      
      public static List<PlaceClient> searchPublicPlace(String userid,String title,String streetAddress,String streetNumber,String cap,String city,String category)
      {
    	  ArrayList<PlaceClient> placeList=new ArrayList<PlaceClient>();
  		
  		  Connection conn= (Connection) dbManager.dbConnect();
  		
    	  boolean whereflag = false;
    	  
    	  //creo la query
    	  String selectQuery = "Select * from Place_category join Place on Place_category.title=Place.title and " +
                              "Place_category.lat=Place.lat and Place_category.lng=Place.lng";
              
    	  
    	  if (!title.equalsIgnoreCase(""))
    	  {    		  
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where title='"+title+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and title='"+title+"' ";
    		  }
    		  
    	  }  
    		  
    	  if(!streetAddress.equalsIgnoreCase(""))
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where streetAddress='"+streetAddress+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and streetAddress='"+streetAddress+"' ";
    		  }
    	  }
    			 
    	  if (!streetNumber.equalsIgnoreCase(""))  
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where streetNumber='"+streetNumber+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and streetNumber='"+streetNumber+"' ";
    		  }
    	  }
    	
    	  if (!cap.equalsIgnoreCase(""))
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where cap='"+cap+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and cap='"+cap+"' ";
    		  }
    	  }  
    		  
    	  if (!city.equalsIgnoreCase(""))	
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where city='"+city+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and city='"+city+"' ";
    		  }
    	  }  
    		
    	  if (!category.equalsIgnoreCase(""))
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where category='"+category+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and category='"+category+"' ";
    		  }
    	  }    
    	
    	  System.out.println(selectQuery);
    	  
    	  QueryStatus qs=dbManager.customSelect(conn, selectQuery);
  		
  		  ResultSet rs=(ResultSet)qs.customQueryOutput;

  		  try{
  			while(rs.next()){
  					placeList.add(
  						new PlaceClient(
  								rs.getString("Place.title") , 
  								rs.getString("Place.lat") , 
  								rs.getString("Place.lng") , 
  								rs.getString("Place.streetAddress") , 
  								rs.getString("Place.streetNumber") , 
  								rs.getString("Place.cap") , 
  								rs.getString("Place.city") , 
  								rs.getString("Place_category.category") 
  							)					
  					);
  			 }
  		  }catch(SQLException sqlE){
  			//TODO
  			
  		  }finally{	
  			dbManager.dbDisconnect(conn);
  		  }
  		
  		return placeList;
    	 
      }
     
      public static void addPlaceCategory(String userID,String title,double lat,double lng,String category)
      {
    	  Connection conn= (Connection) dbManager.dbConnect();
          
          //Starting transaction
          QueryStatus qs=dbManager.startTransaction(conn);
          
          if(qs.execError){
              //TODO decide what to do in this case (transaction not started)
              log.error(qs.explainError());
              qs.occourtedErrorException.printStackTrace();
              System.out.println("Error during transaction starting... Add place-category not done");
              log.error("Error during transaction starting... Add place-category not done");
              dbManager.dbDisconnect(conn);
              return;
          }    
           String query="Insert into Place_category" +
           " (title,lat,lng,category,username) values ('" + title + "','"+ lat + "','" + lng + "','" + 
               category + "','"+userID+"')";
          
          System.out.println(query);
          qs=dbManager.customQuery(conn, query);
          
          if(qs.execError){
              log.error(qs.explainError());
              System.out.println("Error during add Place-category .. aborting operation");
              qs.occourtedErrorException.printStackTrace();
              
              //Rolling back
              dbManager.rollbackTransaction(conn);
              
              log.error("Error during add Place-category .. aborting operation");
              dbManager.dbDisconnect(conn);
              votePlace(userID,title,lat,lng,category);  
         	 
              return;
          }
          votePlace(userID,title,lat,lng,category); 
          dbManager.commitTransaction(conn);
          dbManager.dbDisconnect(conn);
         
          return;   
    	  
      }
	  
      public static void votePlace(String userID,String title, double lat, double lng,String category)
      {
    	  DateUtils date = new DateUtils();
  		  String insertDate = date.now();
  		  
    	  Connection conn= (Connection) dbManager.dbConnect();
          
          //Starting transaction
          QueryStatus qs=dbManager.startTransaction(conn);
          
          if(qs.execError){
              //TODO decide what to do in this case (transaction not started)
              log.error(qs.explainError());
              qs.occourtedErrorException.printStackTrace();
              System.out.println("Error during transaction starting... Vote place not done");
              log.error("Error during transaction starting... Vote place not done");
              dbManager.dbDisconnect(conn);
              return;
          }    
           String query="Insert into Place_voted" +
           " (title,lat,lng,category,username,vote,date) values ('" + title + "','"+ lat + "','" + lng + "','" + 
               category + "','"+userID+"',1,'"+insertDate+"' )";
          
          System.out.println(query);
          qs=dbManager.customQuery(conn, query);
          
          if(qs.execError){
              log.error(qs.explainError());
              System.out.println("Error during vote Place .. aborting operation");
              qs.occourtedErrorException.printStackTrace();
              
              //Rolling back
              dbManager.rollbackTransaction(conn);
              
              log.error("Error during vote Place .. aborting operation");
              dbManager.dbDisconnect(conn);
              //votePlaceHistorical(userID,title,lat,lng,category); 
              return;
          }
          
          //aggiungere anche a vote_historical
          votePlaceHistorical(userID,title,lat,lng,category,insertDate); 
          dbManager.commitTransaction(conn);
          dbManager.dbDisconnect(conn);
         
          return;   
      }
      
      public static void votePlaceHistorical(String userID,String title, double lat, double lng,String category,String insertDate)
      {
    	  Connection conn= (Connection) dbManager.dbConnect();
          
          //Starting transaction
          QueryStatus qs=dbManager.startTransaction(conn);
          
          if(qs.execError){
              //TODO decide what to do in this case (transaction not started)
              log.error(qs.explainError());
              qs.occourtedErrorException.printStackTrace();
              System.out.println("Error during transaction starting... Add vote in place historical not done");
              log.error("Error during transaction starting... Add vote place in historical not done");
              dbManager.dbDisconnect(conn);
              return;
          }    
           String query="Insert into Place_voted_historical" +
           " (title,lat,lng,category,username,vote,date) values ('" + title + "','"+ lat + "','" + lng + "','" + 
               category + "','"+userID+"',1,'"+insertDate+"')";
          
          System.out.println(query);
          qs=dbManager.customQuery(conn, query);
          
          if(qs.execError){
              log.error(qs.explainError());
              System.out.println("Error during adding vote in Place historical .. aborting operation");
              qs.occourtedErrorException.printStackTrace();
              
              //Rolling back
              dbManager.rollbackTransaction(conn);
              
              log.error("Error during adding vote in Place historical .. aborting operation");
              dbManager.dbDisconnect(conn);
              return;
          }
          
          dbManager.commitTransaction(conn);
          dbManager.dbDisconnect(conn);
         
          return;   
      }
 
//CONVERT COORDINATE
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