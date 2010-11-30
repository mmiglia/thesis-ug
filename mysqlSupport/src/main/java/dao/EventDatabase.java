package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;
import valueobject.SingleTask;
import businessobject.Configuration;
import businessobject.Converter;

import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.cs.Db4oClientServer;
import com.db4o.cs.config.ServerConfiguration;
import com.db4o.query.Constraint;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import com.db4o.ta.Activatable;
import com.db4o.ta.TransparentActivationSupport;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;


/**
 * Singleton class that acts as a database that will save all the events, with this class
 * you can connect to the EventDatabase (with openDatabase method) or create and manage
 * events saved into the database
 */
public enum EventDatabase {
	instance; // singleton instance
	private static final String DATABASE_NAME = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/EventDatabase";
	private static ObjectServer server ; //db4o server
	private static boolean databaseOpen = true; // true means database server is initialized
	private static final Object lock = new Object(); // mutex lock
	
	private static final Logger log = LoggerFactory.getLogger(TaskDatabase.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	
	/**
	 * Add new event to the database
	 * @param userID unique UUID of the user
	 * @param event event object to be saved
	 * @return true if addition successful
	 */
	public static SingleEvent addEvent(String userID,SingleEvent e){
		return addEvent(userID,e.dueDate,e.startTime,e.endTime,e.location,
				e.title,e.description);
	}
	
	public static SingleEvent addEvent(String userID, String dueDate,String startTime,String endTime,
			String location,String title, String description) {
		SingleEvent eventToReturn=null;

		
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... Event not added");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		//Reminder creation
		String insertQuery="Insert into Reminder (title,description,type) values ('"+title+"','"+description+"','1')";
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			dbManager.rollbackTransaction(conn);
			log.error("Error during remider adding... Event not added");
			dbManager.dbDisconnect(conn);
			return null;
		}
		log.info("Reminder added!");	
		//Task Creation with the reminder ID. The Task has always type=2
		insertQuery="Insert into Event (User,dueDate,startTime,endTime,location,ReminderId) values ('"+userID+"','"+dueDate+"','"+startTime+"','"+endTime+"','"+location+"',LAST_INSERT_ID())";
		log.info(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		
		if(qs.execError){
			log.error(qs.explainError()+"_");
			log.error(qs.occourtedErrorException.getMessage());
			qs.occourtedErrorException.printStackTrace();
			log.info(insertQuery);
			log.error("Error during task adding... Event not added");
			int count=0;
			do{
				count++;
			}while(dbManager.rollbackTransaction(conn).execError && count < 100);	
			dbManager.dbDisconnect(conn);
			return null;
			
		}else{
			qs=dbManager.customQuery(conn, "select * from Event join Reminder on Event.ReminderId=Reminder.id where Event.id=LAST_INSERT_ID()");
			ResultSet rs=(ResultSet)qs.customQueryOutput;
			try{
			//Creating SingleTask object from data inserted into the database
			if(rs.next()){
				eventToReturn=new SingleEvent(
					rs.getString("Event.id"),
					rs.getString("title"),
					rs.getString("startTime") ,
					rs.getString("endTime") , 
					rs.getString("location") ,
					rs.getString("description") ,
					rs.getInt("priority"),
					rs.getString("Reminder.id")
				);

			}else{
				dbManager.dbDisconnect(conn);
				return null;
			}
			}catch(SQLException sqlE){
				//TODO manage exception
				sqlE.printStackTrace();
				dbManager.dbDisconnect(conn);
			}
			
			int count=0;
			do{
				count++;
			}while(dbManager.commitTransaction(conn).execError && count < 100);	
			
			log.info("Event and related Reminder added correctly to the database");
			return eventToReturn;
		}
	}	
	
	/**
	 * Delete the specific event from database
	 * @param userID unique UUID of the user
	 * @param eventID unique UUID of the task
	 * @return true if deletion success, false otherwise 
	 */	
	public static boolean deleteEvent(final String eventID) {

		Connection conn= (Connection) dbManager.dbConnect();
		
		//Get id of reminder 
		String selectQuery="Select ReminderId from Event where id="+eventID;
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		SingleTask task=null;
		
		String reminderID="";
		try{
			while(rs.next()){
				reminderID=rs.getString("ReminderId");
			}
			
		}catch(SQLException sqlE){
			//TODO
			return false;
		}
		
		
		String deleteQuery="delete from Event where id="+eventID;
		//TODO verify if there are uncaugth exception here 
		qs=dbManager.customQuery(conn, deleteQuery);
		
		log.info(deleteQuery);
		
		//TODO verify if there are uncaugth exception here		
		deleteQuery=" delete from Reminder where id="+reminderID;
		//TODO verify if there are uncaugth exception here
		qs=dbManager.customQuery(conn, deleteQuery);
		
		log.info(deleteQuery);
    	try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbManager.dbDisconnect(conn);
			if(!qs.execError){
				return true;
			}else{
				return false;
			}
		}
	}
	
	/**
	 * Update the specific task
	 * @param eventID unique UUID of the event
	 * @param event the new event
	 * @return false if update unsuccessful , true if update is successful
	 */
	public static boolean updateEvent(final String eventID, final SingleEvent event){
		Connection conn= (Connection) dbManager.dbConnect();
		
		int counter=0;
		do{			
			counter++;
		}while(dbManager.startTransaction(conn).execError && counter<100);
		
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... Event not added");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		String updateQuery="UPDATE Event set ";

		updateQuery+="dueDate='"+event.dueDate+"',";
		updateQuery+="startTime='"+event.startTime+"',";
		updateQuery+="endTime='"+event.endTime+"',";
		updateQuery+="location='"+event.location+"'";
		
		updateQuery+=" where id="+event.eventID+"";
		
		updateQuery+=";";
		
		
		
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			log.info(updateQuery);
			qs.occourtedErrorException.printStackTrace();
			dbManager.rollbackTransaction(conn);
			log.error("Error during Event update");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		
		updateQuery="UPDATE Reminder SET ";
		updateQuery+="description='"+event.description+"',";
		updateQuery+="title='"+event.title+"',";

		updateQuery+="latitude='"+event.gpscoordinate.latitude+"',";
		updateQuery+="longitude='"+event.gpscoordinate.longitude+"',";

		updateQuery+="priority='"+event.priority+"',";
		updateQuery+="title='"+event.title+"',";
		updateQuery+="type="+event.type+"";
		
		updateQuery+=" where id=(select ReminderId from Event where Event.id="+eventID+");";
		
		
		qs=dbManager.customQuery(conn, updateQuery);
		System.out.println(updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			counter=0;
			do{
				counter++;
			}while(dbManager.rollbackTransaction(conn).execError && counter<100);
			log.error("Error during Event update");
			dbManager.dbDisconnect(conn);
			return false;
		}else{
			counter=0;
			do{
				counter++;
			}while(dbManager.commitTransaction(conn).execError && counter<100);			
		}

    	try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbManager.dbDisconnect(conn);
			if(!qs.execError){
				return true;
			}else{
				return false;
			}
		}	
	}
	
	/**
	 * Get all events from a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all events from the user
	 */
	public static List<SingleEvent> getAllEvent(final String userID){
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Event join Reminder on Event.ReminderId=Reminder.id where Event.User="+userID;
		
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		SingleEvent event=null;
		ArrayList<SingleEvent> eventList=new ArrayList<SingleEvent>();
		
		try{
			while(rs.next()){
				eventList.add(
						new SingleEvent(
								rs.getString("Event.id"),
								rs.getString("title"),
								rs.getString("startTime") ,
								rs.getString("endTime") , 
								rs.getString("location") ,
								rs.getString("description") ,
								rs.getInt("priority"),
								rs.getString("Reminder.id")
							)					
					);
			
			
			
			
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return eventList;
	}
	
	/**
	 * Get all events given a period of time
	 * @param userID unique UUID of the user
	 * @param startTime limit on the starting time of the event
	 * @param endTime limit on the ending time of the event
	 * @return list containing all events in that period
	 */
	public static List<SingleEvent> getEventsDuring(final String userID, String startTime, String endTime){
		
		final Calendar startPeriod = Converter.toJavaDate(startTime);
		final Calendar endPeriod = Converter.toJavaDate(endTime);


		Connection conn= (Connection) dbManager.dbConnect();
		
		String selectQuery="Select * from Event join Reminder on Event.ReminderId=Reminder.id where Event.User="+userID+ " and startTime>'"+startPeriod+"' and endTime<'"+endPeriod+"';";
		
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		SingleEvent event=null;
		ArrayList<SingleEvent> eventList=new ArrayList<SingleEvent>();
		
		try{
			while(rs.next()){
				eventList.add(
						new SingleEvent(
								rs.getString("Event.id"),
								rs.getString("title"),
								rs.getString("startTime") ,
								rs.getString("endTime") , 
								rs.getString("location") ,
								rs.getString("description") ,
								rs.getInt("priority"),
								rs.getString("Reminder.id")
							)					
					);
			
			
			
			
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return eventList;
	}
	
	/**
	 * This method check if the event exist in the database
	 * @param event object to be checked against database
	 * @return 1 if event is exist, 0 otherwise
	 */
	private static boolean eventExist(final SingleEvent event) {
		ObjectContainer db = openDatabase();
		try {
			Query query = db.query();
			query.constrain(EventTuple.class);
			query.descend("eventID").constrain(event.ID);
			ObjectSet<EventTuple> result = query.execute();
			return !result.isEmpty();
		}finally{
			db.close();
		}		
	}
	
	/**
	 * This method is used to initialize the database and return
	 * a reference to the client of the database 
	 * (if the class attribute databaseOpen is set to false)
	 * otherwise it only returns the reference to the client of the database 
	 * (see com.db40.ObjectContainer)
	 * TransparentActivationSupport is used to let db4o manage how objects are 
	 * loaded in memory so we don't have to manage it in our code.
	 * For more details about see
	 * http://developer.db4o.com/Projects/useful_snippets/activation_in_depth.html
	 * @return a reference to the database client
	 * 
	 * 
	 */	
	private static ObjectContainer openDatabase() {
		if (!databaseOpen) { //outer selection to enable faster access
			synchronized (lock){
			/*to avoid racing condition after outer IF above
			 e.g. possible to acquire same databaseOpen value
			 and thus open server multiple times*/
			ServerConfiguration config = Db4oClientServer.newServerConfiguration();
			config.common().add(new TransparentActivationSupport());
			if (databaseOpen) return server.openClient(); 
			server= Db4oClientServer.openServer(config, DATABASE_NAME, 0);
			databaseOpen=true;
			}
		}
		ObjectContainer db = server.openClient();	
		return db;
	}
	
	/**
	 * This class represent how an event is stored into the event database.
	 * We can think it's like a record of the Event Table.
	 * The table schema is: Event(userID,eventID,event)
	 * where event is an instance of SingleEvent (see valueobject.SingleEvent)
	 * 
	 */	
	private class EventTuple implements Activatable{
		private transient Activator _activator;
		/**
		 * UUID of the user
		 */
		public String userID;
		public String eventID;
		/**
		 * list of events saved in the database
		 */
		public SingleEvent event;
	
		public EventTuple (String userID, SingleEvent event){
			this.userID = userID;
			this.eventID = event.ID;
			this.event = event;
		}
		public void activateWrite(){
			activate(ActivationPurpose.WRITE);	
		}
		
		public void activateRead(){
			activate(ActivationPurpose.READ);	
		}
		
		@Override
		public void activate(ActivationPurpose purpose) {
			 if(_activator != null) {
		            _activator.activate(purpose);
		        }
		}

		@Override
		public void bind(Activator activator) {
		       if (_activator == activator) {
		            return;
		        }
		        if (activator != null && _activator != null) {
		            throw new IllegalStateException();
		        }
		        _activator = activator;
		}
	}
}
