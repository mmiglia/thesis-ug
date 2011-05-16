package dao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleItemLocation;
import valueobject.SingleActionLocation;
import valueobject.SingleLocationLocation;
import valueobject.Location;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

import businessobject.DateUtils;

/**
 * Singleton class that acts as a database that will save all the item-location, with this class
 * you can connect to the OntologyDatabase (with openDatabase method) or create and manage
 * item-location saved into the database
 * @author Anuska
 */
public enum OntologyDatabase {
	istance;
	
	private static final Logger log = LoggerFactory.getLogger(OntologyDatabase.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();

//ITEM
	/**
	 * Enter in the database the couple item-location (this item can be 
	 * found in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param item the item that has been entered
	 * @param location the location in wich the item can be found in
	 * @return
	 */
	public static SingleItemLocation addItemInLocation(String user, String item,
			String location) {
		SingleItemLocation itemLocationToReturn=null;
		
		
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...item-location not added");
			log.error("Error during transaction starting...item-location not added");
			dbManager.dbDisconnect(conn);
			return null;
		}	
		String insertQuery="Insert into Item_foundIn_Loc (Item,Location,Username,N_views,N_votes) values ('"+item+"','"+location+"','"+user+"',1,0)";
		
		qs=dbManager.customQuery(conn, insertQuery);
		System.out.println(insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("ERRORE: item-location già inseriti nel db");
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during Item-Location adding... Assertion not added");
			dbManager.dbDisconnect(conn);
			
			return null;
		}
	
		log.info("Assertion added!");	
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		itemLocationToReturn = OntologyDatabase.istance.voteItem(user,item,location);

		return itemLocationToReturn;
		
	}
	
	/**
	 * Get all item location entered by a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all ItemLocation of the user
	 * @author anuska
	 */
	public static List<SingleItemLocation> getAllItemLocation(final String userID){
		
		ArrayList<SingleItemLocation> itemLocationList=new ArrayList<SingleItemLocation>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="Select * from Item_foundIn_Loc where Username='"+userID+"'";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				itemLocationList.add(
						new SingleItemLocation(
								rs.getString("Item"),
								rs.getString("Location") ,
								rs.getString("Username") , 
								rs.getInt("N_views") ,
								rs.getInt("N_votes"),
								rs.getDouble("Vote")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return itemLocationList;
	}
	
	/**
	 * Get all item location voted by a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all ItemLocation voted from the user
	 * @author anuska
	 */
	public static List<SingleItemLocation> getAllItemLocationVoted(final String userID){
		
		ArrayList<SingleItemLocation> itemLocationList=new ArrayList<SingleItemLocation>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="Select * from Item_foundIn_Loc join Item_voted on " +
				"Item_foundIn_Loc.Item = Item_voted.Item and " +
				"Item_foundIn_Loc.Location = Item_voted.Location " +
				"where Item_voted.Username='"+userID+"'" +
						" and Item_voted.vote=1";
		System.out.println(" Sono in OntologyDatabase");
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				itemLocationList.add(
						new SingleItemLocation(
								rs.getString("Item_foundIn_Loc.Item"),
								rs.getString("Item_foundIn_Loc.Location") ,
								rs.getString("Item_foundIn_Loc.Username") , 
								rs.getInt("Item_foundIn_Loc.N_views") ,
								rs.getInt("Item_foundIn_Loc.N_votes"),
								rs.getDouble("Item_foundIn_Loc.Vote")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return itemLocationList;
	}
	
   //voteItem
	public SingleItemLocation voteItem(String user,String item, String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote for item not added");
			log.error("Error during transaction starting... Vote for item not added");
			dbManager.dbDisconnect(conn);
			return null;
		}	
		
		// Inserisco nella tabella Item_Voted che l'utente user ha votato per una item-Location
		String insertQuery="Insert into Item_voted(Item,Location,Username,Vote) values ('"+item+"','"+location+"','"+user+"',1) ";
		System.out.println(insertQuery);
		
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("Error during vote item '"+item+"','"+location+"' "+"... not added - the user has been voted");
			log.error("Error during vote item '"+item+"','"+location+"' "+"... not added - - the user has been voted");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		/*Seleziono il rank dell'utente che ha votato per aggiornare il voto totale della
		  coppia item-location
		*/
		String rank=  userRank(user);
		System.out.println("rank utente: "+ rank);
		
		String updateQuery= "update Item_foundIn_Loc set Vote = (Vote+"+rank+"),N_votes=(N_votes + 1) where Item='"+item+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		log.info("Vote from "+user+" for (item: "+item+" location: "+location+")... added!");
		
		String selectQuery= "select * from Item_foundIn_Loc where Item='"+item+"' and Location='"+location+"'";
		System.out.println(selectQuery);
		
		qs=dbManager.customQuery(conn, selectQuery);
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		SingleItemLocation itemLocationToReturn = null;
		try{
			//Creating SingleItemLocation object from data inserted into the database
			if(rs.next()){
				itemLocationToReturn = new SingleItemLocation(
												rs.getString("Item"),
												rs.getString("Location") ,
												rs.getString("Username") , 
												rs.getInt("N_views") ,
												rs.getInt("N_votes"),
												rs.getDouble("Vote")
										);
			}else{
				//Rolling back
				dbManager.rollbackTransaction(conn);
				
				dbManager.dbDisconnect(conn);
				return null;
			}
		}catch(SQLException sqlE){
				//TODO manage exception
				sqlE.printStackTrace();
				
				//Rolling back
				dbManager.rollbackTransaction(conn);
				
				dbManager.dbDisconnect(conn);
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return itemLocationToReturn;
	}

	public List<Location> viewLocationForItem(String userid,String item) 
	{
		ArrayList<Location> LocationList=new ArrayList<Location>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="select * from Item_foundIn_Loc where Item='"+item+"'";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				LocationList.add( new Location(rs.getString("Location")));
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return LocationList;
		
	}
	
	public String deleteVoteForItemLocation(String userid,String item,String location)
	{
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
			return "Error during transaction starting... Delete vote for item-location not done";
		}	
		DateUtils cancDate = new DateUtils();
		String cancellationDate = cancDate.now();
	
		String updateQuery= "update Item_voted set Vote = 0,CancellationDate ='"+cancellationDate+"' where Item='"+item+"' and Location='"+location+"' and Username = '"+userid+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete vote operation in Item_voted .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete vote operation in Item_voted .. aborting operation");
			dbManager.dbDisconnect(conn);
			return "Error during delete vote operation in Item_voted .. aborting operation";
		}
		
		String rank=  userRank(userid);
		System.out.println("rank utente: "+ rank);
		
		updateQuery= "update Item_foundIn_Loc set Vote = (Vote-"+rank+"),N_votes=(N_votes - 1) where Item='"+item+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete Vote operation in Item_foundIn_Loc .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete Vote operation in Item_foundIn_Loc .. aborting operation");
			dbManager.dbDisconnect(conn);
			return "Error during delete Vote operation in Item_foundIn_Loc .. aborting operation";
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return "Il voto è stato cancellato con successo";
	}
	
	
	
//ACTION
	/**
	 * Enter in the database the couple action-location (this item can be 
	 * found in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param action the action that has been entered
	 * @param location the location in wich the item can be found in
	 * @return
	 */
	public static SingleActionLocation addActionInLocation(String user, String action,
			String location) {
		SingleActionLocation actionLocationToReturn=null;
		
		
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...action-location not added");
			log.error("Error during transaction starting...action-location not added");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		
		String insertQuery="Insert into Action_foundIn_Loc (Action,Location,Username,N_views,N_votes) values ('"+action+"','"+location+"','"+user+"',1,0)";
		
		qs=dbManager.customQuery(conn, insertQuery);
		
		System.out.println(insertQuery);
		
		if(qs.execError)
		{
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("ERRORE: action-location già inseriti nel db");
			log.error("Error during Item-Location adding... Assertion not added");
			dbManager.dbDisconnect(conn);
			
			return null;
		}
		
		log.info("Assertion added!");	
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		actionLocationToReturn = OntologyDatabase.istance.voteAction(user,action,location);
	
		return actionLocationToReturn;
		
	}
	
	
	/**
	 * Get all action-location entered by a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all ActionLocation of the user
	 * @author anuska
	 */
	public static List<SingleActionLocation> getAllActionLocation(final String userID){
		
		ArrayList<SingleActionLocation> actionLocationList=new ArrayList<SingleActionLocation>();
		
		Connection conn= (Connection) dbManager.dbConnect();
		

		String selectQuery="Select * from Action_foundIn_Loc where Username='"+userID+"'";
		
	
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		
		try{
			while(rs.next()){
				actionLocationList.add(
						new SingleActionLocation(
								rs.getString("Action"),
								rs.getString("Location") ,
								rs.getString("Username") , 
								rs.getInt("N_views") ,
								rs.getInt("N_votes"),
								rs.getDouble("Vote")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return actionLocationList;
	}
	
	/**
	 * Get all action-location voted by a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all ActionLocation of the user
	 * @author anuska
	 */
	public static List<SingleActionLocation> getAllActionLocationVoted(final String userID){
		
		ArrayList<SingleActionLocation> actionLocationList=new ArrayList<SingleActionLocation>();
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Action_foundIn_Loc join Action_voted on " +
		"Action_foundIn_Loc.Action = Action_voted.Action and " +
		"Action_foundIn_Loc.Location = Action_voted.Location " +
		"where Action_voted.Username='"+userID+"'" +
				" and Action_voted.vote=1";

		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		
		try{
			while(rs.next()){
				actionLocationList.add(
						new SingleActionLocation(
								rs.getString("Action"),
								rs.getString("Location") ,
								rs.getString("Username") , 
								rs.getInt("N_views") ,
								rs.getInt("N_votes"),
								rs.getDouble("Vote")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return actionLocationList;
	}
	
   //voteAction
	public SingleActionLocation voteAction(String user,String action, String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote for action not added");
			log.error("Error during transaction starting... Vote for action not added");
			dbManager.dbDisconnect(conn);
			return null;
		}	
		
		// Inserisco nella tabella Action_Voted che l'utente user ha votato per una action-Location
		String insertQuery="Insert into Action_voted(Action,Location,Username,Vote) values ('"+action+"','"+location+"','"+user+"',1) ";
		System.out.println(insertQuery);
		
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("Error during vote action '"+action+"','"+location+"' "+"... not added - the user has been voted");
			log.error("Error during vote action '"+action+"','"+location+"' "+"... not added - the user has been voted");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		/*Seleziono il rank dell'utente che ha votato per aggiornare il voto totale della
		  coppia action-location
		*/
		
		String rank=  userRank(user);
		System.out.println("rank utente: "+ rank);
		
		String updateQuery= "update Action_foundIn_Loc set Vote = (Vote+"+rank+"),N_votes=(N_votes + 1) where Action ='"+action+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		log.info("Vote from "+user+" for (action: "+action+" location: "+location+")... added!");
		
		String selectQuery= "select * from Action_foundIn_Loc where Action='"+action+"' and Location='"+location+"'";
		System.out.println(selectQuery);
		
		qs=dbManager.customQuery(conn, selectQuery);
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		SingleActionLocation actionLocationToReturn = null;
		try{
			//Creating SingleActionLocation object from data inserted into the database
			if(rs.next()){
				actionLocationToReturn = new SingleActionLocation(
												rs.getString("Action"),
												rs.getString("Location") ,
												rs.getString("Username") , 
												rs.getInt("N_views") ,
												rs.getInt("N_votes"),
												rs.getDouble("Vote")
										);
			}else{
				//Rolling back
				dbManager.rollbackTransaction(conn);
				
				dbManager.dbDisconnect(conn);
				return null;
			}
		}catch(SQLException sqlE){
				//TODO manage exception
				sqlE.printStackTrace();
				//Rolling back
				dbManager.rollbackTransaction(conn);
				
				dbManager.dbDisconnect(conn);
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return actionLocationToReturn;
	}
	
	public List<Location> viewLocationForAction(String userid,String action) 
	{
		ArrayList<Location> LocationList=new ArrayList<Location>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="select * from Action_foundIn_Loc where Action='"+action+"'";
		System.out.println(selectQuery);
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				LocationList.add( new Location(rs.getString("Location")));
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return LocationList;
		
	}
	
	public String deleteVoteForActionLocation(String userid,String action,String location)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Delete vote for action-location not done");
			log.error("Error during transaction starting... Delete vote for action-location not done");
			dbManager.dbDisconnect(conn);
			return "Error during transaction starting... Delete vote for action-location not done";
		}	
		DateUtils cancDate = new DateUtils();
		String cancellationDate = cancDate.now();
	
		String updateQuery= "update Action_voted set Vote = 0,CancellationDate ='"+cancellationDate+"' where Action='"+action+"' and Location='"+location+"' and Username = '"+userid+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete vote operation in Action_voted .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete vote operation in Action_voted .. aborting operation");
			dbManager.dbDisconnect(conn);
			return "Error during delete vote operation in Action_voted .. aborting operation";
		}
		
		String rank=  userRank(userid);
		System.out.println("rank utente: "+ rank);
		
		updateQuery= "update Action_foundIn_Loc set Vote = (Vote-"+rank+"),N_votes=(N_votes - 1) where Action='"+action+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete Vote operation in Action_foundIn_Loc .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete Vote operation in Action_foundIn_Loc .. aborting operation");
			dbManager.dbDisconnect(conn);
			return "Error during delete Vote operation in Action_foundIn_Loc .. aborting operation";
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return "Il voto è stato cancellato con successo";
	}
	
//LOCATION
	/**
	 * Enter in the database the couple item-location (this item can be 
	 * found in this location)
	 * @param user username of the user that enter the couple item-location
	 * @param item the item that has been entered
	 * @param location the location in wich the item can be found in
	 * @return
	 */
	public static SingleLocationLocation addLocationInLocation(String user, String location1,
			String location2) {
		SingleLocationLocation locationLocationToReturn=null;
		
		
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...location-location not added");
			log.error("Error during transaction starting...location-location not added");
			dbManager.dbDisconnect(conn);
			return null;
		}	
		
		
		String insertQuery="Insert into Loc_foundIn_Loc (Location1,Location2,Username,N_views,N_votes) values ('"+location1+"','"+location2+"','"+user+"',1,0)";
		
		qs=dbManager.customQuery(conn, insertQuery);
		
		System.out.println(insertQuery);
		
		if(qs.execError)
		{
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("ERRORE: location-location già inseriti nel db");
			log.error("Error during Location-Location adding... Assertion not added");
			dbManager.dbDisconnect(conn);
			
			return null;
		}
	
		log.info("Assertion added!");	

		dbManager.commitTransaction(conn);	
		dbManager.dbDisconnect(conn);
	
		locationLocationToReturn = OntologyDatabase.istance.voteLocation(user,location1,location2);
		
		return locationLocationToReturn;
		
	}
	
	
	/**
	 * Get all events of a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all LocationLocation of the user
	 * @author anuska
	 */
	public static List<SingleLocationLocation> getAllLocationLocation(final String userID){
		
		ArrayList<SingleLocationLocation> locationLocationList=new ArrayList<SingleLocationLocation>();
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Loc_foundIn_Loc where Username='"+userID+"'";
		
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		
		try{
			while(rs.next()){
				locationLocationList.add(
						new SingleLocationLocation(
								rs.getString("Location1"),
								rs.getString("Location2") ,
								rs.getString("Username") , 
								rs.getInt("N_views") ,
								rs.getInt("N_votes"),
								rs.getDouble("Vote")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return locationLocationList;
	}

  //voteLocation
	public SingleLocationLocation voteLocation(String user,String location1, String location2)
	{	System.out.println("Sono in OntologyDatabase");
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote for location not added");
			log.error("Error during transaction starting... Vote for location not added");
			dbManager.dbDisconnect(conn);
			return null;
		}	
		
		// Inserisco nella tabella Action_Voted che l'utente user ha votato per una action-Location
		String insertQuery="Insert into Location_voted(Location1,Location2,Username,Vote) values ('"+location1+"','"+location2+"','"+user+"',1) ";
		System.out.println(insertQuery);
		
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("Error during vote location '"+location1+"','"+location2+"' "+"... not added - the user has been voted");
			log.error("Error during vote location '"+location1+"','"+location2+"' "+"... not added - the user has been voted");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		/*Seleziono il rank dell'utente che ha votato per aggiornare il voto totale della
		  coppia action-location
		*/
		
		String rank=  userRank(user);
		System.out.println("rank utente: "+ rank);
		
		String updateQuery= "update Loc_foundIn_Loc set Vote = (Vote+"+rank+"),N_votes=(N_votes + 1) where Location1 ='"+location1+"' and Location2='"+location2+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Vote in Loc_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Vote in Loc_foundIn_Loc operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		log.info("Vote from "+user+" for (location1: "+location1+" location2: "+location2+")... added!");
		
		String selectQuery= "select * from Loc_foundIn_Loc where Location1='"+location1+"' and Location2='"+location2+"'";
		System.out.println(selectQuery);
		
		qs=dbManager.customQuery(conn, selectQuery);
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		SingleLocationLocation locationLocationToReturn = null;
		try{
			//Creating SingleLocationLocation object from data inserted into the database
			if(rs.next()){
				locationLocationToReturn = new SingleLocationLocation(
												rs.getString("Location1"),
												rs.getString("Location2") ,
												rs.getString("Username") , 
												rs.getInt("N_views") ,
												rs.getInt("N_votes"),
												rs.getDouble("Vote")
										);
			}else{
				//Rolling back
				dbManager.rollbackTransaction(conn);
				
				dbManager.dbDisconnect(conn);
				return null;
			}
		}catch(SQLException sqlE){
				//TODO manage exception
				sqlE.printStackTrace();
				//Rolling back
				dbManager.rollbackTransaction(conn);
				
				dbManager.dbDisconnect(conn);
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return locationLocationToReturn;
	}
	
	public List<Location> viewLocationForLocation(String userid,String location) 
	{
		ArrayList<Location> LocationList=new ArrayList<Location>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="select * from Loc_foundIn_Loc where Location1='"+location+"'";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				LocationList.add( new Location(rs.getString("Location2")));
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return LocationList;
		
	}
	
	/* Recupero il rank dell'utente 
	*/
	public String userRank(String user){
	
	Connection conn= (Connection) dbManager.dbConnect();
	String selectQuery= "select rank from User where username='"+user+"'";
	System.out.println(selectQuery);
	
	QueryStatus qs=dbManager.customQuery(conn, selectQuery);
	ResultSet rs=(ResultSet)qs.customQueryOutput;
	String rank="0";
	try{
		if(rs.next())
		{
			rank= rs.getString("rank");
		}
	}catch(SQLException sqlE){
		//TODO
		sqlE.printStackTrace();
		
		dbManager.dbDisconnect(conn);
		
	}finally{	
		dbManager.dbDisconnect(conn);
	}
	
	return rank;
}
}
