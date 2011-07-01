package dao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleItemLocation;
import valueobject.SingleActionLocation;
import valueobject.SingleLocationLocation;
import valueobject.Location;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

import businessobject.DateUtils;
import businessobject.OntologyReasoner;

/**
 * Singleton class that acts as a database that will save all the item-location, with this class
 * you can connect to the OntologyDatabase (with openDatabase method) or create and manage
 * item-location saved into the database
 * @author Anuska
 */

/*
 * i campi Vote e VoteNegative nelle tabelle Item_foundIn_Loc e Action_foundIn_Loc
 * non vengono sempre aggiornati in quanto vengono ricalcolati quando serve,
 * servono solo da appoggio in qualche caso in cui è necessario ritornare
 * il voto all'utente
 */
public enum OntologyDatabase {
	istance;
	
	private static final Logger log = LoggerFactory.getLogger(OntologyDatabase.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();

	/*
	 * Method that save that the user has just voted for a thing
	 */
	
	public static void vote(String user,String object)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		
		object = object.toLowerCase();
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...VOTE for object not added");
			log.error("Error during transaction starting...VOTE for object not added");
			dbManager.dbDisconnect(conn);
			return;
		}	
		
		
		String insertQuery="Insert into Voted (object,username) values ('"+object+"','"+user+"')";
		
		qs=dbManager.customQuery(conn, insertQuery);
		
		System.out.println(insertQuery);
		
		if(qs.execError)
		{
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("ERRORE: object-user già inserito nella tabella Voted");
			log.error("Error during Location adding... Assertion not added");
			dbManager.dbDisconnect(conn);
		}
	
		log.info("Assertion added!");	

		dbManager.commitTransaction(conn);	
		dbManager.dbDisconnect(conn);	
		
	}
	
	/*
	 * Method that control if  the user has already voted for a thing
	 */
	public static boolean hasVoted(String user,String object)
	{
		object=object.toLowerCase();
		Connection conn= (Connection) dbManager.dbConnect();
		boolean toReturn=false;
		String selectQuery="Select * from Voted where username='"+user+"' and object='"+object+"'";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				toReturn= true;
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return toReturn;
		
	}
//return the number of user	
	public int userNumber()
	{
		int userN = 0;
		String selectQuery="SELECT COUNT(*) as userN FROM User where active=1";
		System.out.println(selectQuery);
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				userN = rs.getInt("userN");
				System.out.println("numero di utenti:"+ userN);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return userN;
	}
	
//return the average rank of all user
	public double avgRank()
	{
		double avgRank = 0;
		String selectQuery="SELECT AVG(rank) as avgRank FROM User where active=1";
		System.out.println(selectQuery);
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				avgRank = rs.getDouble("avgRank");
				System.out.println("rank medio:"+ avgRank);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return avgRank;
	}
	
	public void rankUpdateUser(String item,String location, String username)
	{
		Connection conn= (Connection) dbManager.dbConnect();
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
		
		String updateQuery = "update User set rank=if (rank+(rank/5)>1 , 1 , rank+(rank/5) )" +
				" where username='"+username+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update User rank.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update User Rank.. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return;
	}
	
	public void rankUpdateUserNeg(String item,String location, String username)
	{
		Connection conn= (Connection) dbManager.dbConnect();
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
		
		String updateQuery = "update User set rank=if (rank-(rank/5)<0.1 , 0.1 , rank-(rank/5) )" +
				" where username='"+username+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update User rank.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update User Rank.. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return;
	}
	//ITEM
	
	/*
	 * Method that do the control and if necessary  cancell an item-location
	 * in the db
	 */
	
	public void cancellationItem(String item,String o)
	{
		int n_views=0;
		int n_votes=0;
		int n_votes_neg=0;
		String username="";// username of the user that have insert the assertion
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Item_foundIn_Loc where Item='"+item+
		"' and Location='"+o+"' and Promotion=0";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		try{
			while(rs.next()){
				n_views=rs.getInt("N_views");
				n_votes=rs.getInt("N_votes");
				n_votes_neg=rs.getInt("N_votes_neg");
				username=rs.getString("Username");
			}
		}catch(SQLException sqlE){
			//TODO
			return;
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		//controllo se almeno l'80% degli utenti che hanno visualizzato 
		//l'asserzione, l'ha anche votata negativamente
		double rap = (double)n_votes_neg/(double)n_views;
		System.out.println("n_votes_neg/n_views="+rap);
		if (rap >= 0.8)
		{
			//trovo il numero degli utenti
			int userNumber = userNumber();

			 //trovo la media del rank di tutti gli utenti
			double avgRank = avgRank();
			//trovo il voto negativo
			double vote_neg=voteNegUpToDateItemLocation(item,o);
			
			//controllo che il
			//voto >= (50% del N di utenti)* (media rank utenti totali)
			double minVote = (userNumber/2)*avgRank;
			
			if (vote_neg > minVote)
			{
				//funzione che mi rende evidente nel db la cancellazione
				cancellationItemUpdate(item,o,username);
			}
			
			/*
			 double userNumberThreshold = userNumber * 0.5;
			 if (n_votes_neg > userNumberThreshold)
			{
				OntologyReasoner.updateOntology(item,o,1);//1 significa item
			
				//funzione che mi rende evidente nel db la promozione in
				//ontologia
				cancellationItemUpdate(item,o,username);
			}*/
		}
		
	}
	
	public void cancellationItemUpdate(String item, String o,String username)
	{
		//il rank dell'utente che ha inserito l'asserzione
		//diminuisce di 1/5 del proprio rank
		rankUpdateUserNeg(item,o,username);
		
		deleteItemFoundInLoc(item,o,username);
		
		//elimino da Item_voted il voto di tutti gli utenti per
		//la coppia item-voted(che cmq rimane memorizzato in historical)
		deleteVoteForItemPromotion(item,o); 
	}
	
	public void deleteItemFoundInLoc(String item,String o,String username)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Delete item-location in Item_foundIn_Loc not done");
			log.error("Error during transaction starting... Delete item-location  in Item_foundIn_Loc not done");
			dbManager.dbDisconnect(conn);
			return;
		}	
		//cancello il voto in Item_voted
		String deleteQuery="delete from Item_foundIn_Loc where Item='"+item+
						   "' and Location='"+o+"' ";
		
		System.out.println(deleteQuery);
		qs=dbManager.customQuery(conn, deleteQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete operation in Item_foundIn_Loc .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete operation in Item_foundIn_Loc .. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return;	
		
	}
	
	
	/*
	 * Method that do the control and if necessary promote a item-location
	 * in the ontology
	 */
	
	public void promotionItem(String item,String o)
	{
		int n_views=0;
		int n_votes=0;
		int n_votes_neg=0;
		String username="";// username of the user that have insert the assertion
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Item_foundIn_Loc where Item='"+item+
		"' and Location='"+o+"' and Promotion=0";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		try{
			while(rs.next()){
				n_views=rs.getInt("N_views");
				n_votes=rs.getInt("N_votes");
				n_votes_neg=rs.getInt("N_votes_neg");
				username=rs.getString("Username");
			}
		}catch(SQLException sqlE){
			//TODO
			return;
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		
		System.out.println("N_views="+n_views);
		System.out.println("N_votes="+n_votes);
		//controllo se almeno l'80% degli utenti che hanno visualizzato 
		//l'asserzione, l'ha anche votata positivamente
		double rap = (double)n_votes/(double)n_views;
		System.out.println("n_votes/n_views="+rap);
		if (rap >= 0.8)
		{
			//trovo il numero degli utenti
			int userNumber = userNumber();
			
			//trovo la media del rank di tutti gli utenti
			double avgRank = avgRank();
			
			//trovo il voto negativo
			double vote=voteUpToDateItemLocation(item,o);
			
			//controllo che il
			//voto >= (50% del N di utenti)* (media rank utenti totali)
			double minVote = (userNumber/2)*avgRank;
			 
			if (vote > minVote)
			{
				OntologyReasoner.updateOntology(item,o,1);//1 significa item
			
				//funzione che mi rende evidente nel db la promozione in
				//ontologia
				promoteUpdate(item,o,username);
			}
			
			/*
			double userNumberThreshold = userNumber * 0.5;
			if (n_votes > userNumberThreshold)
			{
				OntologyReasoner.updateOntology(item,o,1);//1 significa item
			
				//funzione che mi rende evidente nel db la promozione in
				//ontologia
				promoteUpdate(item,o,avgRankVoted,username);
			}
			*/
		}
		
	}
	
	
	public void promoteUpdate(String item,String location,String username)
	{
		//salvo in Item_foundIn_Loc che è stato promosso
		promoteItem(item,location);
		System.out.println("promoteItem(item,location)");
		

		//il rank aumeta di 1/5 del proprio rank
		rankUpdateUser(item,location,username);
		
		//aggiornare rank degli utenti:tutti gli utenti che hanno voto in
		//vigore devono aumentare il rank di 1/10 del proprio rank
		rankUpdateItem(item,location);
		
		System.out.println("rankUpdate(item,location)");
		//aggiornare rank dell'utente che ha inserito per primo l'asserzione
		
		System.out.println("rankUpdateUser(item,location,username)");
		
		//elimino da Item_voted il voto di tutti gli utenti per
		//la coppia item-voted(che cmq rimane memorizzato in historical)
		deleteVoteForItemPromotion(item,location);
		System.out.println("deleteVoteForItemPromotion(item,location)");
		
	}
	
	public void promoteItem(String item,String location)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		QueryStatus qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... PromoteItem not done");
			log.error("Error during transaction starting... PromoteItem not done");
			dbManager.dbDisconnect(conn);
			return;
		}	
		
		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		String updateQuery= "update Item_foundIn_Loc set Promotion = 1,PromotionDate='"+insertDate+"' where Item='"+item+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Promotion in Item_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Promotion in Item_foundIn_Loc operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return;
	}
	
	public void rankUpdateItem(String item,String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		
		//mi salvo i nomi degli utenti che hanno votato per questa asserzione
		List<String> userList= new ArrayList<String>();
		String selectQuery="Select Username from Item_voted where Item='"+item+
		"' and Location='"+location+"' and Vote=1";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		try{
			while(rs.next()){
				userList.add(rs.getString("Username"));
			}
		}catch(SQLException sqlE){
			//TODO
			return;
		}finally{	
			
		}
		String o="";
		ListIterator<String> it = userList.listIterator();
	    while(it.hasNext())
	    {	
	    	o=it.next();  
	       
	    	qs=dbManager.startTransaction(conn);
	    	if(qs.execError){
	    		//TODO decide what to do in this case (transaction not started)
	    		log.error(qs.explainError());
	    		qs.occourtedErrorException.printStackTrace();
	    		System.out.println("Error during transaction starting... update user rank not done");
	    		log.error("Error during transaction starting... update user rank not done");
	    		dbManager.dbDisconnect(conn);
	    		return;
	    	}	
		
		
	    	//aggiorno il rank
	    	String updateQuery = "update User "+
	    	" set rank=if (rank+(rank/10)>1,1,rank+(rank/10))" +
						" where username='" +o+"'";
						
	    	System.out.println(updateQuery);
	    	qs=dbManager.customQuery(conn, updateQuery);
		
	    	if(qs.execError){
	    		log.error(qs.explainError());
	    		System.out.println("Error during update User rank.. aborting operation");
	    		qs.occourtedErrorException.printStackTrace();
			
	    		//Rolling back
	    		dbManager.rollbackTransaction(conn);
			
	    		log.error("Error during update User Rank.. aborting operation");
	    		dbManager.dbDisconnect(conn);
	    		return;
	    	}
		
	    	dbManager.commitTransaction(conn);
	    }
	    dbManager.dbDisconnect(conn);
		return;
	}
	
	public void deleteVoteForItemPromotion(String item,String location)
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
			return;
		}	
		//cancello il voto in Item_voted
		String deleteQuery="delete from Item_voted where Item='"+item+
						   "' and Location='"+location+"'";
		
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
		
		item = item.toLowerCase();
		location = location.toLowerCase();
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
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);

		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("ERRORE: item-location già inseriti nel db");
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during Item-Location adding... Assertion not added");
			dbManager.dbDisconnect(conn);
			
			// Essendo già inserite do solo il mio voto
			System.out.println("// Essendo già inserite do solo il mio voto");
			
			itemLocationToReturn = OntologyDatabase.istance.voteItemByAdding(user,item,location);
	
			return itemLocationToReturn;
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
				String item = rs.getString("Item");
				String location = rs.getString("Location");
				double voto = voteUpToDateItemLocation(item,location);
				itemLocationList.add(
						new SingleItemLocation(
								item,
								location ,
								rs.getString("Username") , 
								rs.getInt("N_views") ,
								rs.getInt("N_votes"),
								voto
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
				String item = rs.getString("Item_foundIn_Loc.Item");
				String location = rs.getString("Item_foundIn_Loc.Location");
				double voto = voteUpToDateItemLocation(item,location);
				itemLocationList.add(
						new SingleItemLocation(
								item,
								location ,
								rs.getString("Item_foundIn_Loc.Username") , 
								rs.getInt("Item_foundIn_Loc.N_views") ,
								rs.getInt("Item_foundIn_Loc.N_votes"),
								voto
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
	
	public static double voteUpToDateItemLocation(String item,String location)
	{
		double voto=0;
		
		String selectQuery="SELECT SUM(rank) as voto FROM User join Item_voted ON " +
				"User.username=Item_voted.Username" +
				" where  Item_voted.Item='"+item+"'" +
						" and Item_voted.Location='"+location+"'" +
								" and Item_voted.Vote=1";
		System.out.println(selectQuery);
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				voto = rs.getDouble("voto");
				System.out.println("voto:"+ voto);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return voto;
		
	}
	
	public double voteNegUpToDateItemLocation(String item,String location)
	{
		double votoNeg=0;
		
		String selectQuery="SELECT SUM(rank) as votoNeg FROM User join Item_voted ON " +
				"User.username=Item_voted.Username" +
				" where  Item_voted.Item='"+item+"'" +
						" and Item_voted.Location='"+location+"'" +
								" and Item_voted.Vote=2";
		System.out.println(selectQuery);
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				votoNeg = rs.getDouble("votoNeg");
				System.out.println("votoNeg:"+ votoNeg);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return votoNeg;
		
	}
   //voteItem
	public SingleItemLocation voteItem(String user,String item, String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		SingleItemLocation itemLocationToReturn = null;
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
		

		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		
		
		
		//Controllo che l'utente non abbia già votato (true ha già votato, 
		//il voto è 1, false non ha votato o ha voto 2 )
		if (!hasVotedItemLocation1(user,item,location))
		{	//l'utente non ha votato positivamente
			
			/*Seleziono il rank dell'utente */
			//String rank=  userRank(user);
			double rank = userRankDouble(user);
			System.out.println("rank utente: "+ rank);
			
			//mi trovo il voto per l'item-location
			double voto = voteUpToDateItemLocation(item,location);
			System.out.println("voto: "+voto);
			voto = voto+rank;
			System.out.println("voto+ rank: "+voto);
			if (!hasVotedItemLocation2(user,item,location))
			{	//l'utente non ha votato per quest'asserzione
				
				// Inserisco nella tabella Item_voted che l'utente user ha votato per una item-Location
				String insertQuery="Insert into Item_voted(Item,Location,Username,Vote,Date) values ('"+item+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote item '"+item+"','"+location+"' "+"... not added - the user has been voted");
					log.error("Error during vote item '"+item+"','"+location+"' "+"... not added - - the user has been voted");
					
					return null;
				}
				
				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
				insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					log.error("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				
				String updateQuery= "update Item_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1) where Item='"+item+"' and Location='"+location+"'";
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
				promotionItem(item,location);
			}
			else //l'utente ha votato negativamente
			{
				String rankVoted = userRankVotedItem(user,item,location);
				String updateQuery= "update Item_voted set Vote = 1,Date='"+insertDate+"' where Item ='"+item+"' and Location='"+location+"' and Username='"+user+"'";
				System.out.println(updateQuery);
				qs=dbManager.customQuery(conn, updateQuery);
				
				if(qs.execError){
					log.error(qs.explainError());
					System.out.println("Error during update Vote in Item_voted operation.. aborting operation");
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					log.error("Error during update Vote in Item_voted operation.. aborting operation");
					dbManager.dbDisconnect(conn);
					return null;
				}
				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
				String insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+location+"','"+user+"',1,'"+insertDate+"',) ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					log.error("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				//mi trovo il voto negativo per l'item-location
				double votoNeg = voteNegUpToDateItemLocation(item,location);
				votoNeg = votoNeg - rank;
				updateQuery= "update Item_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1),N_votes_neg=(N_votes_neg-1),VoteNegative=("+votoNeg+" ) where Item='"+item+"' and Location='"+location+"'";
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
				promotionItem(item,location);
			}
		}
		
		return itemLocationToReturn;
	}
	
	//voteItemLocationList
	public void voteItemLocationList(String user,String item,List<String> location,List<String> locationNeg)
	{	
		/*Seleziono il rank dell'utente che ha votato per aggiornare il voto totale della
		  coppia item-location
		*/
		String rank=  userRank(user);
		System.out.println("rank utente: "+ rank);
		
		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		//voto per le location che desidero
		if (!location.isEmpty())
			voteItemLocationListPositive(user,item,location,rank,insertDate);
		
		//voto negativamente per le location che non desidero contattare
		if (!locationNeg.isEmpty())
			voteItemLocationListNegative(user,item,locationNeg,rank,insertDate);
		
       //salvo nella tabella Voted che ho votato e non voglio più essere 
       //"disturbato" per quell'item
       vote(user,item);
      
	}
	
	public void voteItemLocationListPositive(String user,String item,List<String> location,String rank, String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		System.out.println("Sono in voteItemLocationListPositive ");
		
		String o;
		ListIterator<String> it = location.listIterator();
       while(it.hasNext())
       {	
    	o=it.next();  
    	if (!o.equalsIgnoreCase(""))
    	{
    		log.info("voteItemLocationListPositive- location:"+o);
       
		
    		//Controllo che l'utente non abbia già votato (true ha già votato, 
    		//il voto è 1, false non ha votato o ha voto 2 )
    		if (!hasVotedItemLocation1(user,item,o))
    		{	//l'utente non ha votato positivamente
    			if (!hasVotedItemLocation2(user,item,o))
    			{
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					System.out.println("Error during transaction starting... Vote for item not added");
    					log.error("Error during transaction starting... Vote for item not added");
    					continue;// passa alla prossima iterazione del while
    				}	
    				//l'utente non ha votato per quest'asserzione
    				// Inserisco nella tabella Item_voted che l'utente user ha votato per una item-Location
    				String insertQuery="Insert into Item_voted(Item,Location,Username,Vote,Date) values ('"+item+"','"+o+"','"+user+"',1,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
    					System.out.println("Error during vote item '"+item+"','"+o+"' "+"... not added - the user has been voted");
    					log.error("Error during vote item '"+item+"','"+o+"' "+"... not added - - the user has been voted");
					
    					continue;// passa alla prossima iterazione del while
    				}
				
    				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
    				insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+o+"','"+user+"',1,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					System.out.println("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
    					log.error("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				String updateQuery= "update Item_foundIn_Loc set N_views=N_views+1,N_votes=(N_votes + 1) where Item='"+item+"' and Location='"+o+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				log.info("Vote from "+user+" for (item: "+item+" location: "+o+")... added!");
    				dbManager.commitTransaction(conn);
    				promotionItem(item,o);	
    			}
    			else //l'utente ha votato negativamente per quest'asserzione
    			{
    				String rankVoted = userRankVotedItem(user,item,o);
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					System.out.println("Error during transaction starting... Vote for item not added");
    					log.error("Error during transaction starting... Vote for item not added");
    					continue;// passa alla prossima iterazione del while
    				}	
    				String updateQuery= "update Item_voted set Vote = 1,Date='"+insertDate+"' where Item ='"+item+"' and Location='"+o+"' and Username='"+user+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Item_voted operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Item_voted operation.. aborting operation");
    					continue;
    				}
    				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
    				String insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date,rank) values ('"+item+"','"+o+"','"+user+"',1,'"+insertDate+"',"+rank+") ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					System.out.println("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
    					log.error("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				updateQuery= "update Item_foundIn_Loc set N_votes=(N_votes + 1),N_votes_neg=(N_votes_neg-1) where Item='"+item+"' and Location='"+o+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
    					continue;// passa alla prossima iterazione del while
    				}
    				
    				log.info("Vote from "+user+" for (item: "+item+" location: "+o+")... added!");
    				dbManager.commitTransaction(conn);
    				promotionItem(item,o);
    			}	
    		}
    	}
       }
       dbManager.dbDisconnect(conn);
	}
	
	public static boolean hasVotedItemLocation1(String user,String item,String location)
	{
		item=item.toLowerCase();
		location=location.toLowerCase();
		Connection conn= (Connection) dbManager.dbConnect();
		boolean toReturn=false;
		String selectQuery="Select * from Item_voted where Username='"+user+"' and Item='"+item+"'" +
				" and Location='"+location+"'"+
				" and Vote=1";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				toReturn= true;
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return toReturn;
		
	}
	public static boolean hasVotedItemLocation2(String user,String item,String location)
	{
		item=item.toLowerCase();
		location=location.toLowerCase();
		Connection conn= (Connection) dbManager.dbConnect();
		boolean toReturn=false;
		String selectQuery="Select * from Item_voted where Username='"+user+"' and Item='"+item+"'" +
				" and Location='"+location+"'"+
				" and Vote=2";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				toReturn= true;
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return toReturn;
		
	}
	/*
	public boolean controllPositiveItem(String user,String item,String location,String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		
		//controllo se l'utente ha votato per quest'asserzione in modo positivo
		// o negativo

		String selectQuery="Select Vote from Item_voted where Item='"+item+
		"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		int vote=0;
		try{
			while(rs.next()){
				vote=rs.getInt("Vote");
			}
		}catch(SQLException sqlE){
			//TODO
		}finally{	
			
		}
		if (vote==1)
			return false;// c'è già inserito il voto positivo quindi non lo inserisco più
		
		//se vote ==2 allora devo modificare Item_voted
		//Starting transaction
		qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote for item not added");
			log.error("Error during transaction starting... Vote for item not added");
			dbManager.dbDisconnect(conn);
			return false;
		}
		String updateQuery= "update Item_voted set Vote = 1,Date='"+insertDate+"' where Item ='"+item+"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Vote in Item_voted operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Vote in Item_voted operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return false;
		}
		dbManager.dbDisconnect(conn);
		return true;
	}
	*/
	public void voteItemLocationListNegative(String user,String item,List<String> locationNeg,String rank, String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		System.out.println("Sono in voteItemLocationListNegative ");
		
		String o;
		ListIterator<String> it = locationNeg.listIterator();
       while(it.hasNext())
       {	
    	o=it.next(); 
    	if (!o.equalsIgnoreCase(""))
    	{
       
    		log.info("voteItemLocationListNegative - location:"+o);
       
    		//Controllo che l'utente non abbia già votato (true ha già votato, 
    		//il voto è 2, false non ha votato o ha voto 1 )
    		if (!hasVotedItemLocation2(user,item,o))
    		{	//l'utente non ha votato negativamente
    			if (!hasVotedItemLocation1(user,item,o))
    			{
    				//l'utente non ha votato positivamente per quest'asserzione
    				//quindi non ha proporio votato
				
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
						qs.occourtedErrorException.printStackTrace();
						System.out.println("Error during transaction starting... Vote for item not added");
						log.error("Error during transaction starting... Vote for item not added");
						continue;// passa alla prossima iterazione del while
    				}	
				
    				// Inserisco nella tabella Item_voted che l'utente user ha votato per una item-Location
    				String insertQuery="Insert into Item_voted(Item,Location,Username,Vote,Date) values ('"+item+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
    					System.out.println("Error during negative vote item '"+item+"','"+o+"' "+"... not added - the user has been voted");
    					log.error("Error during negative vote item '"+item+"','"+o+"' "+"... not added - - the user has been voted");
					
    					continue;// passa alla prossima iterazione del while
    				}
				
    				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
    				insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					System.out.println("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
    					log.error("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				String updateQuery= "update Item_foundIn_Loc set N_views=N_views+1,N_votes_neg=(N_votes_neg + 1) where Item='"+item+"' and Location='"+o+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				log.info("Vote from "+user+" for (item: "+item+" location: "+o+")... added!");
    				dbManager.commitTransaction(conn);
    				cancellationItem(item,o);	
    			}
    			else //l'utente ha votato positivamente per quest'asserzione
    			{
    				String rankVoted = userRankVotedItem(user,item,o);
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					System.out.println("Error during transaction starting... Vote for item not added");
    					log.error("Error during transaction starting... Vote for item not added");
    					continue;// passa alla prossima iterazione del while
    				}	
				
    				String updateQuery= "update Item_voted set Vote = 2,Date='"+insertDate+"' where Item ='"+item+"' and Location='"+o+"' and Username='"+user+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Item_voted operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    						dbManager.rollbackTransaction(conn);
					
    						log.error("Error during update Vote in Item_voted operation.. aborting operation");
    						continue;
    				}
    				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
    				String insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
    				System.out.println(insertQuery);
					qs=dbManager.customQuery(conn, insertQuery);
					if(qs.execError){
						log.error(qs.explainError());
						qs.occourtedErrorException.printStackTrace();
					
						//Rolling back
						dbManager.rollbackTransaction(conn);
					
						System.out.println("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
						log.error("Error during vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
						continue;// passa alla prossima iterazione del while
					}
						
					updateQuery= "update Item_foundIn_Loc set N_votes_neg=(N_votes_neg + 1),N_votes=(N_votes-1) where Item='"+item+"' and Location='"+o+"'";
					System.out.println(updateQuery);
					qs=dbManager.customQuery(conn, updateQuery);
				
					if(qs.execError){
						log.error(qs.explainError());
						System.out.println("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
						qs.occourtedErrorException.printStackTrace();
					
						//Rolling back
						dbManager.rollbackTransaction(conn);
					
						log.error("Error during update Vote in Item_foundIn_Loc operation.. aborting operation");
						continue;// passa alla prossima iterazione del while
					}
				
					log.info("Vote negative from "+user+" for (item: "+item+" location: "+o+")... added!");
					dbManager.commitTransaction(conn);
					cancellationItem(item,o);	
    			}	
    		}	
    	}	
       }
       dbManager.dbDisconnect(conn);
	}
/*	public void voteItemLocationListNegative(String user,String item,List<String> locationNeg,String rank, String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		System.out.println("Sono in voteItemLocationListNegative ");
		
		String o;
		ListIterator<String> it = locationNeg.listIterator();
       while(it.hasNext())
       {	
    	o=it.next();  
       
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote negative for item not added");
			log.error("Error during transaction starting... Vote negative for item not added");
			continue;// passa alla prossima iterazione del while
		}	
		
		// Inserisco nella tabella Item_Voted che l'utente user ha votato negativamente per una item-Location
		String insertQuery="Insert into Item_voted(Item,Location,Username,Vote,Date) values ('"+item+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			System.out.println("Error during vote negative item '"+item+"','"+o+"' "+"... not added - the user has been voted");
			log.error("Error during vote negative item '"+item+"','"+o+"' "+"... not added - - the user has been voted");
			//se c'è già dentro controllo vote, 
			//se vote=1 lo metto a 2 e modifico la data, 
			//altrimenti non faccio niente
			boolean vote1=controllNegativeItem(user,item,o,insertDate);
			//se vote1=false significa che è già dentro 2 quindi vado a continue
			if (vote1=false)
			 continue;// passa alla prossima iterazione del while
		}
		
		// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
		insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("Error during negative vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
			log.error("Error during negative vote item '"+item+"','"+o+"' "+"... not added in Item_voted_historical");
			continue;// passa alla prossima iterazione del while
		}
		
		String updateQuery= "update Item_foundIn_Loc set N_views=N_views+1,VoteNegative = (VoteNegative+"+rank+"),N_votes_neg=(N_votes_neg + 1) where Item='"+item+"' and Location='"+o+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update negative Vote in Item_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update negative Vote in Item_foundIn_Loc operation.. aborting operation");
			continue;// passa alla prossima iterazione del while
		}
		
		log.info("Negative Vote from "+user+" for (item: "+item+" location: "+o+")... added!");
		dbManager.commitTransaction(conn);
		//promotionItem(item,o);
       }
       dbManager.dbDisconnect(conn);
		
	}
	*/
	/*
	public boolean controllNegativeItem(String user,String item,String location,String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		
		//controllo se l'utente ha votato per quest'asserzione in modo positivo
		// o negativo

		String selectQuery="Select Vote from Item_voted where Item='"+item+
		"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		int vote=0;
		try{
			while(rs.next()){
				vote=rs.getInt("Vote");
			}
		}catch(SQLException sqlE){
			//TODO
		}finally{	
			
		}
		if (vote==2)
			return false;// c'è già inserito il voto negativo quindi non lo inserisco più
		
		//se vote ==1 allora devo modificare Item_voted
		//Starting transaction
		qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote for item not added");
			log.error("Error during transaction starting... Vote for item not added");
			dbManager.dbDisconnect(conn);
			return false;
		}
		String updateQuery= "update Item_voted set Vote = 2,Date='"+insertDate+"' where Item ='"+item+"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Vote in Item_voted operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Vote in Item_voted operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return false;
		}
		dbManager.dbDisconnect(conn);
		return true;
	}
	
	*/
	
	//voteItemByAdding
	public SingleItemLocation voteItemByAdding(String user,String item, String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		SingleItemLocation itemLocationToReturn = null;
		
		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		
		//Controllo che l'utente non abbia già votato (true ha già votato, 
		//il voto è 1, false non ha votato o ha voto 2 )
		if (!hasVotedItemLocation1(user,item,location))
		{	//l'utente non ha votato positivamente
			
			/*Seleziono il rank dell'utente */
			//String rank=  userRank(user);
			//System.out.println("rank utente: "+ rank);
			double rank = userRankDouble(user);
			System.out.println("rank utente: "+ rank);
			
			//mi trovo il voto per l'item-location
			double voto = voteUpToDateItemLocation(item,location);
			voto = voto+rank;
			
			if (!hasVotedItemLocation2(user,item,location))
			{	//l'utente non ha votato per quest'asserzione
				
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
				String insertQuery="Insert into Item_voted(Item,Location,Username,Vote,Date) values ('"+item+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted");
					log.error("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				
				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
				insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					log.error("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				
				String updateQuery= "update Item_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1),N_views=(N_views +1) where Item='"+item+"' and Location='"+location+"'";
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
				promotionItem(item,location);
			}
			else //l'utente ha votato negativamente per l'asserzione
			{
				String rankVoted = userRankVotedItem(user,item,location);
				
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
				
				String updateQuery= "update Item_voted set Vote = 1,Date='"+insertDate+"' where Item ='"+item+"' and Location='"+location+"' and Username='"+user+"'";
				System.out.println(updateQuery);
				qs=dbManager.customQuery(conn, updateQuery);
				
				if(qs.execError){
					log.error(qs.explainError());
					System.out.println("Error during update Vote in Item_voted operation.. aborting operation");
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					log.error("Error during update Vote in Item_voted operation.. aborting operation");
					dbManager.dbDisconnect(conn);
					return null;
				}
				// Inserisco nella tabella Item_voted_historical che l'utente user ha votato per una item-Location
				String insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					log.error("Error during vote item '"+item+"','"+location+"' "+"... not added in Item_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				//mi trovo il voto negativo per l'item-location
				double votoNeg = voteNegUpToDateItemLocation(item,location);
				votoNeg = votoNeg - rank;
				//tolgo il voto negativo e aggiungo quello positivo
				//non cambio il numero di visualizzazioni
				updateQuery= "update Item_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1),N_votes_neg=(N_votes_neg-1),VoteNegative=("+votoNeg+" )  where Item='"+item+"' and Location='"+location+"'";
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
				promotionItem(item,location);
			}
		}
		
		return itemLocationToReturn;
	}

	public List<String> viewLocationForItem(String userid,String item) 
	{
		item = item.toLowerCase();
		ArrayList<String> LocationList=new ArrayList<String>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="select * from Item_foundIn_Loc where Item='"+item+"' and Promotion=0";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				//OntologyDatabase.istance.updateItemNviews(item,rs.getString("Location"));
				LocationList.add( rs.getString("Location"));
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			
		}
		//aggiorno il numero di visualizzazioni in Item_founIn_Loc
		//updateNviewsItem(item);
		dbManager.dbDisconnect(conn);
		return LocationList;
		
	}
	
	public void updateNviewsItem(String item)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...N_views for item not updated");
			log.error("Error during transaction starting... N_views for item not updated");
			dbManager.dbDisconnect(conn);
			return;
		}
		String updateQuery= "update Item_foundIn_Loc set N_views=(N_views +1) where Item='"+item+"' and Promotion=0";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update N_views in Item_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update N_views in Item_foundIn_Loc operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		return;
	}
	
	public List<String> viewLocationForItemVoted(String userid,String item) 
	{
		item = item.toLowerCase();
		ArrayList<String> LocationList=new ArrayList<String>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="select * from Item_voted join Item_foundIn_Loc on " +
				"Item_foundIn_Loc.Item = Item_voted.Item and " +
				"Item_foundIn_Loc.Location = Item_voted.Location " +
				"where Item_voted.Item='"+item+
				"' and Item_voted.Vote=1 and Item_voted.Username='"+userid+"'"+
				" and Item_foundIn_Loc.Promotion=0";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				LocationList.add( rs.getString("Location"));
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return LocationList;
		
	}
	
	public void updateItemNviews(String item, String location)
	{	
		item = item.toLowerCase();
		location = location.toLowerCase(); 
		Connection conn= (Connection) dbManager.dbConnect();
	
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
	
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...Update N_views for item-location not done");
			log.error("Error during transaction starting... Update N_views for item-location not done");
			dbManager.dbDisconnect(conn);
			return;
		}	
	
		String updateQuery= "update Item_foundIn_Loc set N_views = N_views+1 where Item='"+item+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
	
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update N_views operation in Item_foundInLoc .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
		
			//Rolling back
			dbManager.rollbackTransaction(conn);
		
			log.error("Error during update N_views operation in Item_foundInLoc .. aborting operation");
			dbManager.dbDisconnect(conn);

		}
		
		dbManager.commitTransaction(conn);
		
		return;
		
	}
	
	public boolean deleteVoteForItemLocation(String userid,String item,String location)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		item = item.toLowerCase();
		location = location.toLowerCase(); 
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Delete vote for item-location not done");
			log.error("Error during transaction starting... Delete vote for item-location not done");
			dbManager.dbDisconnect(conn);
			return false;
		}	
		
		DateUtils cancDate = new DateUtils();
		String cancellationDate = cancDate.now();
		
		String rank=  userRank(userid);
		System.out.println("rank utente: "+ rank);
		
		//String rankVoted = userRankVotedItem(userid,item,location);
		
		String updateQuery= "update Item_voted set Vote = 2,Date='"+cancellationDate+"' where Item ='"+item+"' and Location='"+location+"' and Username='"+userid+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete Vote in Item_voted operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete Vote in Item_voted operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return false;
		}
		//aggiorno la tabella Item_voted_historical inserendo 
		//che l'utente ha cancellato il voto
		
		String insertQuery="Insert into Item_voted_historical(Item,Location,Username,Vote,Date) values ('"+item+"','"+location+"','"+userid+"',2,'"+cancellationDate+"') ";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("Error during delete vote item '"+item+"','"+location+"' "+"... in Item_voted_historical");
			log.error("Error during delete vote item '"+item+"','"+location+"' "+"... in Item_voted_historical");
			
			dbManager.dbDisconnect(conn);
			return false;
		}
	
		updateQuery= "update Item_foundIn_Loc set N_votes_neg=(N_votes_neg + 1),N_votes=(N_votes-1) where Item='"+item+"' and Location='"+location+"'";
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
			return false;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return true;
	}
	
	
	
//ACTION
	
	/*
	 * Method that do the control and if necessary  cancell an action-location
	 * in the db
	 */
	
	public void cancellationAction(String action,String o)
	{
		int n_views=0;
		int n_votes=0;
		int n_votes_neg=0;
		String username="";// username of the user that have insert the assertion
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Action_foundIn_Loc where Action='"+action+
		"' and Location='"+o+"' and Promotion=0";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		try{
			while(rs.next()){
				n_views=rs.getInt("N_views");
				n_votes=rs.getInt("N_votes");
				n_votes_neg=rs.getInt("N_votes_neg");
				username=rs.getString("Username");
			}
		}catch(SQLException sqlE){
			//TODO
			return;
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		//controllo se almeno l'80% degli utenti che hanno visualizzato 
		//l'asserzione, l'ha anche votata negativamente
		double rap = (double)n_votes_neg/(double)n_views;
		System.out.println("n_votes_neg/n_views="+rap);
		if (rap >= 0.8)
		{
			//trovo il numero degli utenti
			int userNumber = userNumber();

			 //trovo la media del rank di tutti gli utenti
			double avgRank = avgRank();
			//trovo il voto negativo
			double vote_neg=voteNegUpToDateActionLocation(action,o);
			
			//controllo che il
			//voto >= (50% del N di utenti)* (media rank utenti totali)
			double minVote = (userNumber/2)*avgRank;
			
			if (vote_neg > minVote)
			{
				//funzione che mi rende evidente nel db la cancellazione
				cancellationActionUpdate(action,o,username);
			}
			
			/*
			 double userNumberThreshold = userNumber * 0.5;
			 if (n_votes_neg > userNumberThreshold)
			{
				OntologyReasoner.updateOntology(action,o,1);//1 significa action
			
				//funzione che mi rende evidente nel db la promozione in
				//ontologia
				cancellationActionUpdate(action,o,username);
			}*/
		}
		
	}
	
	public void cancellationActionUpdate(String action, String o,String username)
	{
		//il rank dell'utente che ha inserito l'asserzione
		//diminuisce di 1/5 del proprio rank
		rankUpdateUserNeg(action,o,username);
		
		deleteActionFoundInLoc(action,o,username);
		
		//elimino da Action_voted il voto di tutti gli utenti per
		//la coppia action-voted(che cmq rimane memorizzato in historical)
		deleteVoteForActionPromotion(action,o); 
	}
	
	public void deleteActionFoundInLoc(String action,String o,String username)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Delete action-location in Action_foundIn_Loc not done");
			log.error("Error during transaction starting... Delete action-location  in Action_foundIn_Loc not done");
			dbManager.dbDisconnect(conn);
			return;
		}	
		//cancello il voto in Action_voted
		String deleteQuery="delete from Action_foundIn_Loc where Action='"+action+
						   "' and Location='"+o+"' ";
		
		System.out.println(deleteQuery);
		qs=dbManager.customQuery(conn, deleteQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete operation in Action_foundIn_Loc .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete operation in Action_foundIn_Loc .. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return;	
		
	}
	
	
	/*
	 * Method that do the control and if necessary promote a action-location
	 * in the ontology
	 */
	
	public void promotionAction(String action,String o)
	{
		int n_views=0;
		int n_votes=0;
		int n_votes_neg=0;
		String username="";// username of the user that have insert the assertion
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Action_foundIn_Loc where Action='"+action+
		"' and Location='"+o+"' and Promotion=0";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		try{
			while(rs.next()){
				n_views=rs.getInt("N_views");
				n_votes=rs.getInt("N_votes");
				n_votes_neg=rs.getInt("N_votes_neg");
				username=rs.getString("Username");
			}
		}catch(SQLException sqlE){
			//TODO
			return;
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		
		System.out.println("N_views="+n_views);
		System.out.println("N_votes="+n_votes);
		//controllo se almeno l'80% degli utenti che hanno visualizzato 
		//l'asserzione, l'ha anche votata positivamente
		double rap = (double)n_votes/(double)n_views;
		System.out.println("n_votes/n_views="+rap);
		if (rap >= 0.8)
		{
			//trovo il numero degli utenti
			int userNumber = userNumber();
			
			//trovo la media del rank di tutti gli utenti
			double avgRank = avgRank();
			
			//trovo il voto negativo
			double vote=voteUpToDateActionLocation(action,o);
			
			//controllo che il
			//voto >= (50% del N di utenti)* (media rank utenti totali)
			double minVote = (userNumber/2)*avgRank;
			 
			if (vote > minVote)
			{
				OntologyReasoner.updateOntology(action,o,1);//1 significa action
			
				//funzione che mi rende evidente nel db la promozione in
				//ontologia
				promoteUpdate(action,o,username);
			}
			
			/*
			double userNumberThreshold = userNumber * 0.5;
			if (n_votes > userNumberThreshold)
			{
				OntologyReasoner.updateOntology(action,o,1);//1 significa action
			
				//funzione che mi rende evidente nel db la promozione in
				//ontologia
				promoteUpdate(action,o,avgRankVoted,username);
			}
			*/
		}
		
	}
	
	
	public void promoteUpdateAction(String action,String location,String username)
	{
		//salvo in Action_foundIn_Loc che è stato promosso
		promoteAction(action,location);
		System.out.println("promoteAction(action,location)");
		

		//il rank aumeta di 1/5 del proprio rank
		rankUpdateUser(action,location,username);
		
		//aggiornare rank degli utenti:tutti gli utenti che hanno voto in
		//vigore devono aumentare il rank di 1/10 del proprio rank
		rankUpdateAction(action,location);
		
		System.out.println("rankUpdate(action,location,avgRankVoted/10)");
		//aggiornare rank dell'utente che ha inserito per primo l'asserzione
		
		System.out.println("rankUpdateUser(action,location,username)");
		
		//elimino da Action_voted il voto di tutti gli utenti per
		//la coppia action-voted(che cmq rimane memorizzato in historical)
		deleteVoteForActionPromotion(action,location);
		System.out.println("deleteVoteForActionPromotion(action,location)");
		
	}
	
	public void promoteAction(String action,String location)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		QueryStatus qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... PromoteAction not done");
			log.error("Error during transaction starting... PromoteAction not done");
			dbManager.dbDisconnect(conn);
			return;
		}	
		
		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		String updateQuery= "update Action_foundIn_Loc set Promotion = 1,PromotionDate='"+insertDate+"' where Action='"+action+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Promotion in Action_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Promotion in Action_foundIn_Loc operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return;
	}
	
	public void rankUpdateAction(String action,String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		
		//mi salvo i nomi degli utenti che hanno votato per questa asserzione
		List<String> userList= new ArrayList<String>();
		String selectQuery="Select Username from Action_voted where Action='"+action+
		"' and Location='"+location+"' and Vote=1";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		
		try{
			while(rs.next()){
				userList.add(rs.getString("Username"));
			}
		}catch(SQLException sqlE){
			//TODO
			return;
		}finally{	
			
		}
		String o="";
		ListIterator<String> it = userList.listIterator();
	    while(it.hasNext())
	    {	
	    	o=it.next();  
	       
	    	qs=dbManager.startTransaction(conn);
	    	if(qs.execError){
	    		//TODO decide what to do in this case (transaction not started)
	    		log.error(qs.explainError());
	    		qs.occourtedErrorException.printStackTrace();
	    		System.out.println("Error during transaction starting... update user rank not done");
	    		log.error("Error during transaction starting... update user rank not done");
	    		dbManager.dbDisconnect(conn);
	    		return;
	    	}	
		
		
	    	//aggiorno il rank
	    	String updateQuery = "update User "+
	    	" set rank=if (rank+(rank/10)>1,1,rank+(rank/10))" +
						" where username='" +o+"'";
						
	    	System.out.println(updateQuery);
	    	qs=dbManager.customQuery(conn, updateQuery);
		
	    	if(qs.execError){
	    		log.error(qs.explainError());
	    		System.out.println("Error during update User rank.. aborting operation");
	    		qs.occourtedErrorException.printStackTrace();
			
	    		//Rolling back
	    		dbManager.rollbackTransaction(conn);
			
	    		log.error("Error during update User Rank.. aborting operation");
	    		dbManager.dbDisconnect(conn);
	    		return;
	    	}
		
	    	dbManager.commitTransaction(conn);
	    }
	    dbManager.dbDisconnect(conn);
		return;
	}
	
	public void deleteVoteForActionPromotion(String action,String location)
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
			return;
		}	
		//cancello il voto in Action_voted
		String deleteQuery="delete from Action_voted where Action='"+action+
						   "' and Location='"+location+"'";
		
		System.out.println(deleteQuery);
		qs=dbManager.customQuery(conn, deleteQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete vote operation in Action_voted .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete vote operation in Action_voted .. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return;	
	}
	
	/**
	 * Enter in the database the couple action-location (this action can be 
	 * found in this location)
	 * @param user username of the user that enter the couple action-location
	 * @param action the action that has been entered
	 * @param location the location in wich the action can be found in
	 * @return
	 */
	public static SingleActionLocation addActionInLocation(String user, String action,
			String location) {
		SingleActionLocation actionLocationToReturn=null;
		
		action = action.toLowerCase();
		location = location.toLowerCase();
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
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);

		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("ERRORE: action-location già inseriti nel db");
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during Action-Location adding... Assertion not added");
			dbManager.dbDisconnect(conn);
			
			// Essendo già inserite do solo il mio voto
			System.out.println("// Essendo già inserite do solo il mio voto");
			
			actionLocationToReturn = OntologyDatabase.istance.voteActionByAdding(user,action,location);
	
			return actionLocationToReturn;
		}
	
		log.info("Assertion added!");	
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		actionLocationToReturn = OntologyDatabase.istance.voteAction(user,action,location);

		return actionLocationToReturn;
		
	}
	
	/**
	 * Get all action location entered by a specific userID
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
				String action = rs.getString("Action");
				String location = rs.getString("Location");
				double voto = voteUpToDateActionLocation(action,location);
				actionLocationList.add(
						new SingleActionLocation(
								action,
								location ,
								rs.getString("Username") , 
								rs.getInt("N_views") ,
								rs.getInt("N_votes"),
								voto
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
	 * Get all action location voted by a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all ActionLocation voted from the user
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
		System.out.println(" Sono in OntologyDatabase");
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				String action = rs.getString("Action_foundIn_Loc.Action");
				String location = rs.getString("Action_foundIn_Loc.Location");
				double voto = voteUpToDateActionLocation(action,location);
				actionLocationList.add(
						new SingleActionLocation(
								action,
								location ,
								rs.getString("Action_foundIn_Loc.Username") , 
								rs.getInt("Action_foundIn_Loc.N_views") ,
								rs.getInt("Action_foundIn_Loc.N_votes"),
								voto
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
	
	public static double voteUpToDateActionLocation(String action,String location)
	{
		double voto=0;
		
		String selectQuery="SELECT SUM(rank) as voto FROM User join Action_voted ON " +
				"User.username=Action_voted.Username" +
				" where  Action_voted.Action='"+action+"'" +
						" and Action_voted.Location='"+location+"'" +
								" and Action_voted.Vote=1";
		System.out.println(selectQuery);
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				voto = rs.getDouble("voto");
				System.out.println("voto:"+ voto);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return voto;
		
	}
	
	public double voteNegUpToDateActionLocation(String action,String location)
	{
		double votoNeg=0;
		
		String selectQuery="SELECT SUM(rank) as votoNeg FROM User join Action_voted ON " +
				"User.username=Action_voted.Username" +
				" where  Action_voted.Action='"+action+"'" +
						" and Action_voted.Location='"+location+"'" +
								" and Action_voted.Vote=2";
		System.out.println(selectQuery);
		Connection conn= (Connection) dbManager.dbConnect();
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				votoNeg = rs.getDouble("votoNeg");
				System.out.println("votoNeg:"+ votoNeg);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return votoNeg;
		
	}
   //voteAction
	public SingleActionLocation voteAction(String user,String action, String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		SingleActionLocation actionLocationToReturn = null;
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
		

		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		
		
		
		//Controllo che l'utente non abbia già votato (true ha già votato, 
		//il voto è 1, false non ha votato o ha voto 2 )
		if (!hasVotedActionLocation1(user,action,location))
		{	//l'utente non ha votato positivamente
			
			/*Seleziono il rank dell'utente */
			//String rank=  userRank(user);
			double rank = userRankDouble(user);
			System.out.println("rank utente: "+ rank);
			
			//mi trovo il voto per l'action-location
			double voto = voteUpToDateActionLocation(action,location);
			System.out.println("voto: "+voto);
			voto = voto+rank;
			System.out.println("voto+ rank: "+voto);
			if (!hasVotedActionLocation2(user,action,location))
			{	//l'utente non ha votato per quest'asserzione
				
				// Inserisco nella tabella Action_voted che l'utente user ha votato per una action-Location
				String insertQuery="Insert into Action_voted(Action,Location,Username,Vote,Date) values ('"+action+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote action '"+action+"','"+location+"' "+"... not added - the user has been voted");
					log.error("Error during vote action '"+action+"','"+location+"' "+"... not added - - the user has been voted");
					
					return null;
				}
				
				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
				insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					log.error("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				
				String updateQuery= "update Action_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1) where Action='"+action+"' and Location='"+location+"'";
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
				promotionAction(action,location);
			}
			else //l'utente ha votato negativamente
			{
				String rankVoted = userRankVotedAction(user,action,location);
				String updateQuery= "update Action_voted set Vote = 1,Date='"+insertDate+"' where Action ='"+action+"' and Location='"+location+"' and Username='"+user+"'";
				System.out.println(updateQuery);
				qs=dbManager.customQuery(conn, updateQuery);
				
				if(qs.execError){
					log.error(qs.explainError());
					System.out.println("Error during update Vote in Action_voted operation.. aborting operation");
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					log.error("Error during update Vote in Action_voted operation.. aborting operation");
					dbManager.dbDisconnect(conn);
					return null;
				}
				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
				String insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+location+"','"+user+"',1,'"+insertDate+"',) ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					log.error("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				//mi trovo il voto negativo per l'action-location
				double votoNeg = voteNegUpToDateActionLocation(action,location);
				votoNeg = votoNeg - rank;
				updateQuery= "update Action_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1),N_votes_neg=(N_votes_neg-1),VoteNegative=("+votoNeg+" ) where Action='"+action+"' and Location='"+location+"'";
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
				promotionAction(action,location);
			}
		}
		
		return actionLocationToReturn;
	}
	
	//voteActionLocationList
	public void voteActionLocationList(String user,String action,List<String> location,List<String> locationNeg)
	{	
		/*Seleziono il rank dell'utente che ha votato per aggiornare il voto totale della
		  coppia action-location
		*/
		String rank=  userRank(user);
		System.out.println("rank utente: "+ rank);
		
		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		//voto per le location che desidero
		if (!location.isEmpty())
			voteActionLocationListPositive(user,action,location,rank,insertDate);
		
		//voto negativamente per le location che non desidero contattare
		if (!locationNeg.isEmpty())
			voteActionLocationListNegative(user,action,locationNeg,rank,insertDate);
		
       //salvo nella tabella Voted che ho votato e non voglio più essere 
       //"disturbato" per quell'action
       vote(user,action);
      
	}
	
	public void voteActionLocationListPositive(String user,String action,List<String> location,String rank, String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		System.out.println("Sono in voteActionLocationListPositive ");
		
		String o;
		ListIterator<String> it = location.listIterator();
       while(it.hasNext())
       {	
    	o=it.next();  
    	if (!o.equalsIgnoreCase(""))
    	{
    		log.info("voteActionLocationListPositive- location:"+o);
       
		
    		//Controllo che l'utente non abbia già votato (true ha già votato, 
    		//il voto è 1, false non ha votato o ha voto 2 )
    		if (!hasVotedActionLocation1(user,action,o))
    		{	//l'utente non ha votato positivamente
    			if (!hasVotedActionLocation2(user,action,o))
    			{
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					System.out.println("Error during transaction starting... Vote for action not added");
    					log.error("Error during transaction starting... Vote for action not added");
    					continue;// passa alla prossima iterazione del while
    				}	
    				//l'utente non ha votato per quest'asserzione
    				// Inserisco nella tabella Action_voted che l'utente user ha votato per una action-Location
    				String insertQuery="Insert into Action_voted(Action,Location,Username,Vote,Date) values ('"+action+"','"+o+"','"+user+"',1,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
    					System.out.println("Error during vote action '"+action+"','"+o+"' "+"... not added - the user has been voted");
    					log.error("Error during vote action '"+action+"','"+o+"' "+"... not added - - the user has been voted");
					
    					continue;// passa alla prossima iterazione del while
    				}
				
    				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
    				insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+o+"','"+user+"',1,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					System.out.println("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
    					log.error("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				String updateQuery= "update Action_foundIn_Loc set N_views=N_views+1,N_votes=(N_votes + 1) where Action='"+action+"' and Location='"+o+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				log.info("Vote from "+user+" for (action: "+action+" location: "+o+")... added!");
    				dbManager.commitTransaction(conn);
    				promotionAction(action,o);	
    			}
    			else //l'utente ha votato negativamente per quest'asserzione
    			{
    				String rankVoted = userRankVotedAction(user,action,o);
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					System.out.println("Error during transaction starting... Vote for action not added");
    					log.error("Error during transaction starting... Vote for action not added");
    					continue;// passa alla prossima iterazione del while
    				}	
    				String updateQuery= "update Action_voted set Vote = 1,Date='"+insertDate+"' where Action ='"+action+"' and Location='"+o+"' and Username='"+user+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Action_voted operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Action_voted operation.. aborting operation");
    					continue;
    				}
    				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
    				String insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date,rank) values ('"+action+"','"+o+"','"+user+"',1,'"+insertDate+"',"+rank+") ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					System.out.println("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
    					log.error("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				updateQuery= "update Action_foundIn_Loc set N_votes=(N_votes + 1),N_votes_neg=(N_votes_neg-1) where Action='"+action+"' and Location='"+o+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
    					continue;// passa alla prossima iterazione del while
    				}
    				
    				log.info("Vote from "+user+" for (action: "+action+" location: "+o+")... added!");
    				dbManager.commitTransaction(conn);
    				promotionAction(action,o);
    			}	
    		}
    	}
       }
       dbManager.dbDisconnect(conn);
	}
	
	public static boolean hasVotedActionLocation1(String user,String action,String location)
	{
		action=action.toLowerCase();
		location=location.toLowerCase();
		Connection conn= (Connection) dbManager.dbConnect();
		boolean toReturn=false;
		String selectQuery="Select * from Action_voted where Username='"+user+"' and Action='"+action+"'" +
				" and Location='"+location+"'"+
				" and Vote=1";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				toReturn= true;
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return toReturn;
		
	}
	public static boolean hasVotedActionLocation2(String user,String action,String location)
	{
		action=action.toLowerCase();
		location=location.toLowerCase();
		Connection conn= (Connection) dbManager.dbConnect();
		boolean toReturn=false;
		String selectQuery="Select * from Action_voted where Username='"+user+"' and Action='"+action+"'" +
				" and Location='"+location+"'"+
				" and Vote=2";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				toReturn= true;
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return toReturn;
		
	}
	/*
	public boolean controllPositiveAction(String user,String action,String location,String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		
		//controllo se l'utente ha votato per quest'asserzione in modo positivo
		// o negativo

		String selectQuery="Select Vote from Action_voted where Action='"+action+
		"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		int vote=0;
		try{
			while(rs.next()){
				vote=rs.getInt("Vote");
			}
		}catch(SQLException sqlE){
			//TODO
		}finally{	
			
		}
		if (vote==1)
			return false;// c'è già inserito il voto positivo quindi non lo inserisco più
		
		//se vote ==2 allora devo modificare Action_voted
		//Starting transaction
		qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote for action not added");
			log.error("Error during transaction starting... Vote for action not added");
			dbManager.dbDisconnect(conn);
			return false;
		}
		String updateQuery= "update Action_voted set Vote = 1,Date='"+insertDate+"' where Action ='"+action+"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Vote in Action_voted operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Vote in Action_voted operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return false;
		}
		dbManager.dbDisconnect(conn);
		return true;
	}
	*/
	public void voteActionLocationListNegative(String user,String action,List<String> locationNeg,String rank, String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		System.out.println("Sono in voteActionLocationListNegative ");
		
		String o;
		ListIterator<String> it = locationNeg.listIterator();
       while(it.hasNext())
       {	
    	o=it.next(); 
    	if (!o.equalsIgnoreCase(""))
    	{
       
    		log.info("voteActionLocationListNegative - location:"+o);
       
    		//Controllo che l'utente non abbia già votato (true ha già votato, 
    		//il voto è 2, false non ha votato o ha voto 1 )
    		if (!hasVotedActionLocation2(user,action,o))
    		{	//l'utente non ha votato negativamente
    			if (!hasVotedActionLocation1(user,action,o))
    			{
    				//l'utente non ha votato positivamente per quest'asserzione
    				//quindi non ha proporio votato
				
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
						qs.occourtedErrorException.printStackTrace();
						System.out.println("Error during transaction starting... Vote for action not added");
						log.error("Error during transaction starting... Vote for action not added");
						continue;// passa alla prossima iterazione del while
    				}	
				
    				// Inserisco nella tabella Action_voted che l'utente user ha votato per una action-Location
    				String insertQuery="Insert into Action_voted(Action,Location,Username,Vote,Date) values ('"+action+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
    					System.out.println("Error during negative vote action '"+action+"','"+o+"' "+"... not added - the user has been voted");
    					log.error("Error during negative vote action '"+action+"','"+o+"' "+"... not added - - the user has been voted");
					
    					continue;// passa alla prossima iterazione del while
    				}
				
    				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
    				insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
    				System.out.println(insertQuery);
    				qs=dbManager.customQuery(conn, insertQuery);
    				if(qs.execError){
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					System.out.println("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
    					log.error("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				String updateQuery= "update Action_foundIn_Loc set N_views=N_views+1,N_votes_neg=(N_votes_neg + 1) where Action='"+action+"' and Location='"+o+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    					dbManager.rollbackTransaction(conn);
					
    					log.error("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
    					continue;// passa alla prossima iterazione del while
    				}
				
    				log.info("Vote from "+user+" for (action: "+action+" location: "+o+")... added!");
    				dbManager.commitTransaction(conn);
    				cancellationAction(action,o);	
    			}
    			else //l'utente ha votato positivamente per quest'asserzione
    			{
    				String rankVoted = userRankVotedAction(user,action,o);
    				//Starting transaction
    				QueryStatus qs=dbManager.startTransaction(conn);
    				if(qs.execError){
    					//TODO decide what to do in this case (transaction not started)
    					log.error(qs.explainError());
    					qs.occourtedErrorException.printStackTrace();
    					System.out.println("Error during transaction starting... Vote for action not added");
    					log.error("Error during transaction starting... Vote for action not added");
    					continue;// passa alla prossima iterazione del while
    				}	
				
    				String updateQuery= "update Action_voted set Vote = 2,Date='"+insertDate+"' where Action ='"+action+"' and Location='"+o+"' and Username='"+user+"'";
    				System.out.println(updateQuery);
    				qs=dbManager.customQuery(conn, updateQuery);
				
    				if(qs.execError){
    					log.error(qs.explainError());
    					System.out.println("Error during update Vote in Action_voted operation.. aborting operation");
    					qs.occourtedErrorException.printStackTrace();
					
    					//Rolling back
    						dbManager.rollbackTransaction(conn);
					
    						log.error("Error during update Vote in Action_voted operation.. aborting operation");
    						continue;
    				}
    				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
    				String insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
    				System.out.println(insertQuery);
					qs=dbManager.customQuery(conn, insertQuery);
					if(qs.execError){
						log.error(qs.explainError());
						qs.occourtedErrorException.printStackTrace();
					
						//Rolling back
						dbManager.rollbackTransaction(conn);
					
						System.out.println("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
						log.error("Error during vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
						continue;// passa alla prossima iterazione del while
					}
						
					updateQuery= "update Action_foundIn_Loc set N_votes_neg=(N_votes_neg + 1),N_votes=(N_votes-1) where Action='"+action+"' and Location='"+o+"'";
					System.out.println(updateQuery);
					qs=dbManager.customQuery(conn, updateQuery);
				
					if(qs.execError){
						log.error(qs.explainError());
						System.out.println("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
						qs.occourtedErrorException.printStackTrace();
					
						//Rolling back
						dbManager.rollbackTransaction(conn);
					
						log.error("Error during update Vote in Action_foundIn_Loc operation.. aborting operation");
						continue;// passa alla prossima iterazione del while
					}
				
					log.info("Vote negative from "+user+" for (action: "+action+" location: "+o+")... added!");
					dbManager.commitTransaction(conn);
					cancellationAction(action,o);	
    			}	
    		}	
    	}	
       }
       dbManager.dbDisconnect(conn);
	}
/*	public void voteActionLocationListNegative(String user,String action,List<String> locationNeg,String rank, String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		System.out.println("Sono in voteActionLocationListNegative ");
		
		String o;
		ListIterator<String> it = locationNeg.listIterator();
       while(it.hasNext())
       {	
    	o=it.next();  
       
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote negative for action not added");
			log.error("Error during transaction starting... Vote negative for action not added");
			continue;// passa alla prossima iterazione del while
		}	
		
		// Inserisco nella tabella Action_Voted che l'utente user ha votato negativamente per una action-Location
		String insertQuery="Insert into Action_voted(Action,Location,Username,Vote,Date) values ('"+action+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			System.out.println("Error during vote negative action '"+action+"','"+o+"' "+"... not added - the user has been voted");
			log.error("Error during vote negative action '"+action+"','"+o+"' "+"... not added - - the user has been voted");
			//se c'è già dentro controllo vote, 
			//se vote=1 lo metto a 2 e modifico la data, 
			//altrimenti non faccio niente
			boolean vote1=controllNegativeAction(user,action,o,insertDate);
			//se vote1=false significa che è già dentro 2 quindi vado a continue
			if (vote1=false)
			 continue;// passa alla prossima iterazione del while
		}
		
		// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
		insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+o+"','"+user+"',2,'"+insertDate+"') ";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("Error during negative vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
			log.error("Error during negative vote action '"+action+"','"+o+"' "+"... not added in Action_voted_historical");
			continue;// passa alla prossima iterazione del while
		}
		
		String updateQuery= "update Action_foundIn_Loc set N_views=N_views+1,VoteNegative = (VoteNegative+"+rank+"),N_votes_neg=(N_votes_neg + 1) where Action='"+action+"' and Location='"+o+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update negative Vote in Action_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update negative Vote in Action_foundIn_Loc operation.. aborting operation");
			continue;// passa alla prossima iterazione del while
		}
		
		log.info("Negative Vote from "+user+" for (action: "+action+" location: "+o+")... added!");
		dbManager.commitTransaction(conn);
		//promotionAction(action,o);
       }
       dbManager.dbDisconnect(conn);
		
	}
	*/
	/*
	public boolean controllNegativeAction(String user,String action,String location,String insertDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		
		//controllo se l'utente ha votato per quest'asserzione in modo positivo
		// o negativo

		String selectQuery="Select Vote from Action_voted where Action='"+action+
		"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		int vote=0;
		try{
			while(rs.next()){
				vote=rs.getInt("Vote");
			}
		}catch(SQLException sqlE){
			//TODO
		}finally{	
			
		}
		if (vote==2)
			return false;// c'è già inserito il voto negativo quindi non lo inserisco più
		
		//se vote ==1 allora devo modificare Action_voted
		//Starting transaction
		qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Vote for action not added");
			log.error("Error during transaction starting... Vote for action not added");
			dbManager.dbDisconnect(conn);
			return false;
		}
		String updateQuery= "update Action_voted set Vote = 2,Date='"+insertDate+"' where Action ='"+action+"' and Location='"+location+"' and Username='"+user+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update Vote in Action_voted operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update Vote in Action_voted operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return false;
		}
		dbManager.dbDisconnect(conn);
		return true;
	}
	
	*/
	
	//voteActionByAdding
	public SingleActionLocation voteActionByAdding(String user,String action, String location)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		SingleActionLocation actionLocationToReturn = null;
		
		DateUtils date = new DateUtils();
		String insertDate = date.now();
		
		
		//Controllo che l'utente non abbia già votato (true ha già votato, 
		//il voto è 1, false non ha votato o ha voto 2 )
		if (!hasVotedActionLocation1(user,action,location))
		{	//l'utente non ha votato positivamente
			
			/*Seleziono il rank dell'utente */
			//String rank=  userRank(user);
			//System.out.println("rank utente: "+ rank);
			double rank = userRankDouble(user);
			System.out.println("rank utente: "+ rank);
			
			//mi trovo il voto per l'action-location
			double voto = voteUpToDateActionLocation(action,location);
			voto = voto+rank;
			
			if (!hasVotedActionLocation2(user,action,location))
			{	//l'utente non ha votato per quest'asserzione
				
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
				String insertQuery="Insert into Action_voted(Action,Location,Username,Vote,Date) values ('"+action+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted");
					log.error("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				
				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
				insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					log.error("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				
				String updateQuery= "update Action_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1),N_views=(N_views +1) where Action='"+action+"' and Location='"+location+"'";
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
				promotionAction(action,location);
			}
			else //l'utente ha votato negativamente per l'asserzione
			{
				String rankVoted = userRankVotedAction(user,action,location);
				
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
				
				String updateQuery= "update Action_voted set Vote = 1,Date='"+insertDate+"' where Action ='"+action+"' and Location='"+location+"' and Username='"+user+"'";
				System.out.println(updateQuery);
				qs=dbManager.customQuery(conn, updateQuery);
				
				if(qs.execError){
					log.error(qs.explainError());
					System.out.println("Error during update Vote in Action_voted operation.. aborting operation");
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					log.error("Error during update Vote in Action_voted operation.. aborting operation");
					dbManager.dbDisconnect(conn);
					return null;
				}
				// Inserisco nella tabella Action_voted_historical che l'utente user ha votato per una action-Location
				String insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+location+"','"+user+"',1,'"+insertDate+"') ";
				System.out.println(insertQuery);
				qs=dbManager.customQuery(conn, insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					qs.occourtedErrorException.printStackTrace();
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					System.out.println("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					log.error("Error during vote action '"+action+"','"+location+"' "+"... not added in Action_voted_historical");
					
					dbManager.dbDisconnect(conn);
					return null;
				}
				//mi trovo il voto negativo per l'action-location
				double votoNeg = voteNegUpToDateActionLocation(action,location);
				votoNeg = votoNeg - rank;
				//tolgo il voto negativo e aggiungo quello positivo
				//non cambio il numero di visualizzazioni
				updateQuery= "update Action_foundIn_Loc set Vote = ("+voto+"),N_votes=(N_votes + 1),N_votes_neg=(N_votes_neg-1),VoteNegative=("+votoNeg+" )  where Action='"+action+"' and Location='"+location+"'";
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
				promotionAction(action,location);
			}
		}
		
		return actionLocationToReturn;
	}

	public List<String> viewLocationForAction(String userid,String action) 
	{
		action = action.toLowerCase();
		ArrayList<String> LocationList=new ArrayList<String>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="select * from Action_foundIn_Loc where Action='"+action+"' and Promotion=0";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				//OntologyDatabase.istance.updateActionNviews(action,rs.getString("Location"));
				LocationList.add( rs.getString("Location"));
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			
		}
		//aggiorno il numero di visualizzazioni in Action_founIn_Loc
		//updateNviewsAction(action);
		dbManager.dbDisconnect(conn);
		return LocationList;
		
	}
	
	public void updateNviewsAction(String action)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...N_views for action not updated");
			log.error("Error during transaction starting... N_views for action not updated");
			dbManager.dbDisconnect(conn);
			return;
		}
		String updateQuery= "update Action_foundIn_Loc set N_views=(N_views +1) where Action='"+action+"' and Promotion=0";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update N_views in Action_foundIn_Loc operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during update N_views in Action_foundIn_Loc operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return;
		}
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		return;
	}
	
	public List<String> viewLocationForActionVoted(String userid,String action) 
	{
		action = action.toLowerCase();
		ArrayList<String> LocationList=new ArrayList<String>();
		
		Connection conn= (Connection) dbManager.dbConnect();
			
		String selectQuery="select * from Action_voted join Action_foundIn_Loc on " +
				"Action_foundIn_Loc.Action = Action_voted.Action and " +
				"Action_foundIn_Loc.Location = Action_voted.Location " +
				"where Action_voted.Action='"+action+
				"' and Action_voted.Vote=1 and Action_voted.Username='"+userid+"'"+
				" and Action_foundIn_Loc.Promotion=0";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				LocationList.add( rs.getString("Location"));
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return LocationList;
		
	}
	
	public void updateActionNviews(String action, String location)
	{	
		action = action.toLowerCase();
		location = location.toLowerCase(); 
		Connection conn= (Connection) dbManager.dbConnect();
	
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
	
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...Update N_views for action-location not done");
			log.error("Error during transaction starting... Update N_views for action-location not done");
			dbManager.dbDisconnect(conn);
			return;
		}	
	
		String updateQuery= "update Action_foundIn_Loc set N_views = N_views+1 where Action='"+action+"' and Location='"+location+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
	
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during update N_views operation in Action_foundInLoc .. aborting operation");
			qs.occourtedErrorException.printStackTrace();
		
			//Rolling back
			dbManager.rollbackTransaction(conn);
		
			log.error("Error during update N_views operation in Action_foundInLoc .. aborting operation");
			dbManager.dbDisconnect(conn);

		}
		
		dbManager.commitTransaction(conn);
		
		return;
		
	}
	
	public boolean deleteVoteForActionLocation(String userid,String action,String location)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		action = action.toLowerCase();
		location = location.toLowerCase(); 
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting... Delete vote for action-location not done");
			log.error("Error during transaction starting... Delete vote for action-location not done");
			dbManager.dbDisconnect(conn);
			return false;
		}	
		
		DateUtils cancDate = new DateUtils();
		String cancellationDate = cancDate.now();
		
		String rank=  userRank(userid);
		System.out.println("rank utente: "+ rank);
		
		//String rankVoted = userRankVotedAction(userid,action,location);
		
		String updateQuery= "update Action_voted set Vote = 2,Date='"+cancellationDate+"' where Action ='"+action+"' and Location='"+location+"' and Username='"+userid+"'";
		System.out.println(updateQuery);
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			System.out.println("Error during delete Vote in Action_voted operation.. aborting operation");
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("Error during delete Vote in Action_voted operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return false;
		}
		//aggiorno la tabella Action_voted_historical inserendo 
		//che l'utente ha cancellato il voto
		
		String insertQuery="Insert into Action_voted_historical(Action,Location,Username,Vote,Date) values ('"+action+"','"+location+"','"+userid+"',2,'"+cancellationDate+"') ";
		System.out.println(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("Error during delete vote action '"+action+"','"+location+"' "+"... in Action_voted_historical");
			log.error("Error during delete vote action '"+action+"','"+location+"' "+"... in Action_voted_historical");
			
			dbManager.dbDisconnect(conn);
			return false;
		}
	
		updateQuery= "update Action_foundIn_Loc set N_votes_neg=(N_votes_neg + 1),N_votes=(N_votes-1) where Action='"+action+"' and Location='"+location+"'";
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
			return false;
		}
		
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
		return true;
	}
	
	
//LOCATION
	
	public static void addLocation(String user,String title,String location)
	{
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
			return;
		}	
		
		
		String insertQuery="Insert into Location (title,location,username) values ('"+title+"','"+location+"','"+user+"')";
		
		qs=dbManager.customQuery(conn, insertQuery);
		
		System.out.println(insertQuery);
		
		if(qs.execError)
		{
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			System.out.println("ERRORE: title già inserito nella tabella location");
			log.error("Error during Location adding... Assertion not added");
			dbManager.dbDisconnect(conn);
		}
	
		log.info("Assertion added!");	

		dbManager.commitTransaction(conn);	
		dbManager.dbDisconnect(conn);	
		
	}
	
	public static String findLocation(String user,String title)
	{
		String location="";
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="select location from Location "+
				"where title='"+title+"'";
		
		System.out.println(selectQuery);
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				location=rs.getString("Location");
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return location;	
	}

	
	
	
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
			
			//Essendo già inserito do solo il mio voto
			locationLocationToReturn = OntologyDatabase.istance.voteLocation(user,location1,location2);
			
			return locationLocationToReturn;
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
	
	/* Recupero il rank dell'utente 
	*/
	public double userRankDouble(String user){
	
	Connection conn= (Connection) dbManager.dbConnect();
	String selectQuery= "select rank from User where username='"+user+"'";
	System.out.println(selectQuery);
	
	QueryStatus qs=dbManager.customQuery(conn, selectQuery);
	ResultSet rs=(ResultSet)qs.customQueryOutput;
	double rank=0;
	try{
		if(rs.next())
		{
			rank= rs.getDouble("rank");
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
	
	/* Recupero il rank dell'utente al momento in cui ha votato per un item-location
	*/
	public String userRankVotedItem(String user,String item, String location){
	
	Connection conn= (Connection) dbManager.dbConnect();
	String selectQuery= "select rank from Item_voted where Item='"+item+"' and Location='"+location+"' and Username='"+user+"'";
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
	
	/* Recupero il rank dell'utente al momento in cui ha votato per un action-location
	*/
	public String userRankVotedAction(String user,String action, String location){
	
	Connection conn= (Connection) dbManager.dbConnect();
	String selectQuery= "select rank from Action_voted where Action='"+action+"' and Location='"+location+"' and Username='"+user+"'";
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
