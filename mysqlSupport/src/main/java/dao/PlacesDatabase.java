package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.CachingManager;
import businessobject.DateUtils;
import businessobject.HintManager;
import businessobject.MapManager;
import businessobject.google.MapsClient;

import valueobject.Coordinate;
import valueobject.Hint;
import valueobject.Hint.PhoneNumber;
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
         
         String query="Insert into PlacePrivate" +
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
           //aggiungi categoria
             for (String s:category)
             {
           	  addPrivatePlaceCategory(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s);
             }
             return;
         }
         
         dbManager.commitTransaction(conn);
         dbManager.dbDisconnect(conn);
         //aggiungi categoria
         for (String s:category)
         {
       	  addPrivatePlaceCategory(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s);
         }
         
         return;      
     }
      
      public static List<PlaceClient> getAllPrivatePlaces(final String userID)
      {
          
          ArrayList<PlaceClient> privatePlacesList=new ArrayList<PlaceClient>();
          
          Connection conn= (Connection) dbManager.dbConnect();
                  
          String selectQuery="Select * from Place_category join PlacePrivate on Place_category.title=PlacePrivate.title and " +
                              "Place_category.lat=PlacePrivate.lat and Place_category.lng=PlacePrivate.lng"+
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
                          String category = rs.getString("category");
                          
                          //controllo se l'ho già inserito, se si aggiungo
                          //solo la categoria
                          Iterator it=privatePlacesList.iterator();
                          boolean isInsert = false;
                          while(it.hasNext())
                          {
                        	  PlaceClient value=(PlaceClient)it.next();
                        	  if ( value.title.equals(title) && value.lat.equals(lat) && value.lng.equals(lng))
                        	  {
                        		  isInsert = true;
                        		  value.category = value.category+","+category;
                        		  break;
                        	  }
                          }
                          //se non è già stato inserito lo inserisco
                          if (!isInsert)
                          {
                          privatePlacesList.add(
                                          new PlaceClient(title,lat,lng,streetAddress,streetNumber,cap,city,category)                                        
                                  );
                          }
                  }
          }catch(SQLException sqlE){
                  //TODO
      
          }finally{        
                  dbManager.dbDisconnect(conn);
          }
          
          return privatePlacesList;
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
    	  
    	  String deleteQuery="Delete from PlacePrivate" +
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
    	  deletePlaceCategory(userID,title,lat,lng);
    	  return; 
      }
      
      public static void deletePlaceCategory(String userID,String title,String lat,String lng)
      {
    	  Connection conn= (Connection) dbManager.dbConnect();
          
    	  //Starting transaction
    	  QueryStatus qs=dbManager.startTransaction(conn);
       
    	  if(qs.execError){
    		  //TODO decide what to do in this case (transaction not started)
    		  log.error(qs.explainError());
    		  qs.occourtedErrorException.printStackTrace();
    		  System.out.println("Error during transaction starting... Delete private place category not done");
    		  log.error("Error during transaction starting... Delete private place category not done");
    		  dbManager.dbDisconnect(conn);
    		  return;
    	  }    
    	  
    	  String deleteQuery="Delete from Place_category" +
    	  " where username='"+ userID +"'and title='" + title + "' and lat='" + lat +"' and lng='" + lng + "'";
    	  System.out.println(deleteQuery);
    	  qs=dbManager.customQuery(conn, deleteQuery);
       
    	  if(qs.execError){
    		  log.error(qs.explainError());
    		  System.out.println("Error during delete private place category .. aborting operation");
    		  qs.occourtedErrorException.printStackTrace();
           
    		  //Rolling back
    		  dbManager.rollbackTransaction(conn);
           
    		  log.error("Error during delete private place category .. aborting operation");
    		  dbManager.dbDisconnect(conn);
    		  return;
    	  }
       
    	  dbManager.commitTransaction(conn);
    	  dbManager.dbDisconnect(conn);
       
    	  return;   
      }
      public static void addPrivatePlaceCategory(String userID,String title,double lat,double lng,String category)
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
              return;
          }
          dbManager.commitTransaction(conn);
          dbManager.dbDisconnect(conn);
         
          return;   
    	  
      }
      
      public List<Hint> searchPrivatePlacesDB(String userID,float latitude, float longitude, String query)
      {
    	  ArrayList<Hint> privatePlacesList=new ArrayList<Hint>();
          
          Connection conn= (Connection) dbManager.dbConnect();
                  
          String selectQuery="Select * from Place_category join PlacePrivate on Place_category.title=PlacePrivate.title and " +
                              "Place_category.lat=PlacePrivate.lat and Place_category.lng=PlacePrivate.lng"+
                  " where PlacePrivate.user='"+ userID +"'and  PlacePrivate.userGroup=0 and PlacePrivate.title='"+query+"'";
          
          System.out.println(selectQuery);
          
          
          QueryStatus qs=dbManager.customSelect(conn, selectQuery);
          
          ResultSet rs=(ResultSet)qs.customQueryOutput;

          try{
                  while(rs.next()){
                	  	  System.out.println("ho trovato in private");
                	  	  
                	  	  String title = rs.getString("title");
                          String lat = rs.getString("lat");
                          String lng = rs.getString("lng");
                          String streetAddress = rs.getString("streetAddress");
                          String streetNumber = rs.getString("streetNumber");
                          String cap = rs.getString("cap");
                          String city = rs.getString("city");
                        
                          List<String> addressLinesList = new ArrayList<String>();
                          addressLinesList.add(streetAddress+","+ streetNumber);
                          addressLinesList.add(cap+" "+ city);
                          List<PhoneNumber> phoneNumberList = new ArrayList<PhoneNumber>();
                          
                          String ddUrl = "http://www.google.com/maps?source=uds&daddr="+streetAddress.replaceAll(" ", "+")+","+streetNumber+","+city+"+@"+lat+","+lng+"&saddr="+latitude+","+longitude;
                          String ddUrlToHere = "http://www.google.com/maps?source=uds&daddr="+streetAddress.replaceAll(" ", "+")+","+streetNumber+","+city+"+@"+lat+","+lng+"&iwstate1=dir:to";
                          String ddUrlFromHere = "http://www.google.com/maps?source=uds&daddr="+streetAddress.replaceAll(" ", "+")+","+streetNumber+","+city+"+@"+lat+","+lng+"&iwstate1=dir:from";
                          String staticMapUrl="http://maps.google.com/maps/api/staticmap?maptype=roadmap&format=gif&sensor=false&size=150x100&zoom=13&markers="+lat+","+lng;
                         
                          privatePlacesList.add(
                                        		  new Hint(
                          								title,
                          								"" ,
                          								"" , 
                          								title,
                          								lat ,
                          								lng , 
                          								streetAddress+","+streetNumber ,
                          								city,
                          								ddUrl,
                          								ddUrlToHere , 
                          								ddUrlFromHere,
                          								staticMapUrl,
                          								"local",
                          								"",
                          								"",
                          								phoneNumberList,
                          								addressLinesList		
                          							)			
                                  );
                  }
          }catch(SQLException sqlE){
                  //TODO
      
          }finally{        
                  dbManager.dbDisconnect(conn);
          }
          return privatePlacesList;
    	  
      }
      
//PUBLIC
      // return 0-> ok
      // return 1 -> posto già presente in google

      public static int addPublicPlace( String userID,  String title,  String streetAddress,  String streetNumber,  String cap, String city, List<String> category)
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
              return 3;
          }    
          
          Coordinate placeCoordinate = convertAddressToCoordinate(streetAddress,streetNumber,cap,city);
          System.out.println("coordinate- lat:"+placeCoordinate.getLat());
          System.out.println("coordinate- lng:"+placeCoordinate.getLng());
          float latitude = (float)placeCoordinate.getLat();
          float longitude = (float)placeCoordinate.getLng();
          
          //controllo che non ci sia già il posto in Google
          List<Hint> toReturn = new LinkedList<Hint>();
          List<Hint> toReturn2 = new LinkedList<Hint>();
          int distance = 20; //20 metri dalle coordinate
          //mando la query a google e mi salvo i dati in cache 
          //così cmq mal che vada ho popolato la cache 
          for (String query : category) 
		  {
				List<Hint> result2 = new LinkedList<Hint>(); // list of search result IN GOOGLE
				List<Hint> listToAdd = new LinkedList<Hint>();
				listToAdd = MapManager.getInstance().searchLocalBusiness(
						latitude, longitude, query);
				System.out.println("for string query:"+query);
				System.out.println("listToAdd:"+listToAdd);
				CachingManager.cachingListHint(userID, query, latitude, longitude, distance,listToAdd);
			
				//filter the result
				toReturn2 = new HintManager().filterLocation(distance, latitude, longitude, listToAdd);
				System.out.println("Risultato ricerca in Google filtrato:"+ listToAdd);
				toReturn.addAll(toReturn2);
		  }
          //qui devo controllare che il titolo di mio interesse 
          //non sia nella lista dei risulati di Google(distanza di Lei...)
          //intanto controllo semplicemente che il mio indirizzo non ci sia
          //toReturn
          Iterator it=toReturn.iterator();
          boolean isInsert = false;
          while(it.hasNext())
          {
        	  Hint value=(Hint)it.next();
        	  if ( value.streetAddress.equalsIgnoreCase(streetAddress+", "+streetNumber) && value.city.equalsIgnoreCase(city) )
        	  {
        		  isInsert = true;
        		  System.out.println("Luogo già inserito in Google");
        		  return 1;
        	  }
          }
          
         //se non è già in google lo inserisco nel mio db
          String latitudeString = ""+placeCoordinate.getLat()+"";
          String longitudeString = ""+placeCoordinate.getLng()+"";
          
          String query="Insert into Place" +
          "(title,lat,lng,streetAddress,streetNumber,cap,city,user,userGroup) " +
          "values ('" + title + "','"+ placeCoordinate.getLat() + "','" + placeCoordinate.getLng() + "','" + 
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
    			  addPlaceCategory(userID,title,latitudeString,longitudeString,s);
    		  }
    		  return 0;
    	  }
    	  for (String s:category)
    	  {
    		  addPlaceCategory(userID,title,latitudeString,longitudeString,s);
    		  //  votePlace(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s);  
    		  //  votePlaceHistorical(userID,title,placeCoordinate.getLat(),placeCoordinate.getLng(),s); 
    	  }
        
    	  dbManager.commitTransaction(conn);
          
    	  dbManager.dbDisconnect(conn);
         
    	  return 0;          
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
                              
                            //controllo se l'ho già inserito, se si aggiungo
                              //solo la categoria
                              Iterator it=publicPlacesList.iterator();
                              boolean isInsert = false;
                              while(it.hasNext())
                              {
                            	  PlaceClient value=(PlaceClient)it.next();
                            	  if ( value.title.equals(title) && value.lat.equals(lat) && value.lng.equals(lng))
                            	  {
                            		  isInsert = true;
                            		  value.category = value.category+","+category;
                            		  break;
                            	  }
                              }
                              //se non è già stato inserito lo inserisco
                              if (!isInsert)
                              {
                              publicPlacesList.add(
                                              new PlaceClient(title,lat,lng,streetAddress,streetNumber,cap,city,category)                                        
                                      );
                              }
                             
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
    			  selectQuery = selectQuery + " where title='"+title.toLowerCase()+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and title='"+title.toLowerCase()+"' ";
    		  }
    		  
    	  }  
    		  
    	  if(!streetAddress.equalsIgnoreCase(""))
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where streetAddress='"+streetAddress.toLowerCase()+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and streetAddress='"+streetAddress.toLowerCase()+"' ";
    		  }
    	  }
    			 
    	  if (!streetNumber.equalsIgnoreCase(""))  
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where streetNumber='"+streetNumber.toLowerCase()+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and streetNumber='"+streetNumber.toLowerCase()+"' ";
    		  }
    	  }
    	
    	  if (!cap.equalsIgnoreCase(""))
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where cap='"+cap.toLowerCase()+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and cap='"+cap.toLowerCase()+"' ";
    		  }
    	  }  
    		  
    	  if (!city.equalsIgnoreCase(""))	
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where city='"+city.toLowerCase()+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and city='"+city.toLowerCase()+"' ";
    		  }
    	  }  
    		
    	  if (!category.equalsIgnoreCase(""))
    	  {
    		  if (whereflag==false) 
    		  {
    			  selectQuery = selectQuery + " where category='"+category.toLowerCase()+"' ";
    			  whereflag = true;
    		  }
    		  else
    		  {
    			  selectQuery = selectQuery + " and category='"+category.toLowerCase()+"' ";
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
      
      
      
      //voto una lista di posti
      public static void votePublicPlaces(String userid,List<PlaceClient> places)
      { 
    	 
     	 for (PlaceClient p:places)
 		 {
     		 //creo la lista di category, dato che mi arriva una stringa con le location separate da una virgola
     		 List<String> categoryList= new LinkedList<String>();
     		 String[] words = p.category.split(",");
     		 categoryList.addAll(Arrays.asList(words));
 		
     		 votePublicPlace(userid, p.title,p.lat,p.lng,categoryList);
 		 } 
      }
      
    //voto un posto e le sue categorie
      public static void votePublicPlace(String userID,String title,String lat,String lng,List<String> category)
      {
    	  for (String s:category)
		  {
    		  votePlace(userID,title,lat,lng,s); 
		  }  
      }
     
      public static void addPlaceCategory(String userID,String title,String lat,String lng,String category)
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
	  
      public static void votePlace(String userID,String title, String lat, String lng,String category)
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
      
      public static void votePlaceHistorical(String userID,String title, String lat, String lng,String category,String insertDate)
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
      
      public List<Hint> searchPublicPlacesDB(String userID,float latitude, float longitude, String query)
      {
    	  ArrayList<Hint> publicPlacesList=new ArrayList<Hint>();
          
          Connection conn= (Connection) dbManager.dbConnect();
                  
          String selectQuery="Select * from Place_voted join Place on Place_voted.title=Place.title and " +
                              "Place_voted.lat=Place.lat and Place_voted.lng=Place.lng"+
                  " where Place_voted.username='"+ userID +"'and  Place.userGroup=-1 and Place_voted.category='"+query+"'";
          
          System.out.println(selectQuery);
          
          
          QueryStatus qs=dbManager.customSelect(conn, selectQuery);
          
          ResultSet rs=(ResultSet)qs.customQueryOutput;

          try{
                  while(rs.next()){
                	  	  System.out.println("ho trovato in public");
                	  	  
                	  	  String title = rs.getString("Place.title");
                          String lat = rs.getString("Place.lat");
                          String lng = rs.getString("Place.lng");
                          String streetAddress = rs.getString("Place.streetAddress");
                          String streetNumber = rs.getString("Place.streetNumber");
                          String cap = rs.getString("Place.cap");
                          String city = rs.getString("Place.city");
                         
                          List<String> addressLinesList = new ArrayList<String>();
                          addressLinesList.add(streetAddress+","+ streetNumber);
                          addressLinesList.add(cap+" "+ city);
                          List<PhoneNumber> phoneNumberList = new ArrayList<PhoneNumber>();
                          
                          String ddUrl = "http://www.google.com/maps?source=uds&daddr="+streetAddress.replaceAll(" ", "+")+","+streetNumber+","+city+"+@"+lat+","+lng+"&saddr="+latitude+","+longitude;
                          
                          /*
                           * http://www.google.com/maps?source=uds&daddr=Via+4+Novembre,+6,+Rossano+Veneto,+Veneto+(Supermercato+Geremia+Di+Geremia+Giampietro+%26+C.+S.N.C.)+@45.704687,11.802871&saddr=45.69553,11.830902
                           */
                          
                          String ddUrlToHere = "http://www.google.com/maps?source=uds&daddr="+streetAddress.replaceAll(" ", "+")+","+streetNumber+","+city+"+@"+lat+","+lng+"&iwstate1=dir:to";
                          
                          /*
                           * http://www.google.com/maps?source=uds&daddr=Via+4+Novembre,+6,+Rossano+Veneto,+Veneto+(Supermercato+Geremia+Di+Geremia+Giampietro+%26+C.+S.N.C.)+@45.704687,11.802871&iwstate1=dir:to
                           */
                          
                          String ddUrlFromHere = "http://www.google.com/maps?source=uds&daddr="+streetAddress.replaceAll(" ", "+")+","+streetNumber+","+city+"+@"+lat+","+lng+"&iwstate1=dir:from";
                          
                          
                          /*
                           * <ddUrlFromHere>
							http://www.google.com/maps?source=uds&saddr=Via+4+Novembre,+6,+Rossano+Veneto,+Veneto+(Supermercato+Geremia+Di+Geremia+Giampietro+%26+C.+S.N.C.)+@45.704687,11.802871&iwstate1=dir:from
                           */
                          String staticMapUrl="http://maps.google.com/maps/api/staticmap?maptype=roadmap&format=gif&sensor=false&size=150x100&zoom=13&markers="+lat+","+lng;
                          
                          
                          
                          
                          
                          publicPlacesList.add(
                                        		  new Hint(
                          								title,
                          								"" ,
                          								"" , 
                          								title,
                          								lat ,
                          								lng , 
                          								streetAddress+","+streetNumber ,
                          								city,
                          								ddUrl,
                          								ddUrlToHere , 
                          								ddUrlFromHere,
                          								staticMapUrl,
                          								"local",
                          								"",
                          								"",
                          								phoneNumberList,
                          								addressLinesList		
                          							)			
                                  );
                          System.out.println(publicPlacesList);
                  }
          }catch(SQLException sqlE){
                  //TODO
      
          }finally{        
                  dbManager.dbDisconnect(conn);
          }
          System.out.println(publicPlacesList);
          return publicPlacesList;
    	  
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