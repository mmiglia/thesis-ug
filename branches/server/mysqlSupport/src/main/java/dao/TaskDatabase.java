package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Reminder.GPSLocation;
import valueobject.SingleTask;
import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

/**
 * Singleton class that acts as a database that will save all the tasks
*/
public enum TaskDatabase {
	instance; //singleton instance
	private static final Logger log = LoggerFactory.getLogger(TaskDatabase.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	/**
	 * Add new task to the database and return a SingleTask object with data from the database
	 * @param userID unique UUID of the user
	 * @param task task object to be saved
	 */
	public static SingleTask addTask(String userID, String title,
			String notifyTimeStart, String notifyTimeEnd, String dueDate,
			String description, int priority,String userGroup) {
		
		
		SingleTask taskToReturn=null;
		
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... Task not added");
			dbManager.dbDisconnect(conn);
			return null;
		}
		
		
		//Reminder creation
		String insertQuery="Insert into Reminder (title,description,priority,type) values ('"+title+"','"+description+"','"+priority+"',2)";
		//System.out.println("TaskDatabase query: "+insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			dbManager.rollbackTransaction(conn);
			log.error("Error during remider adding... Task not added");
			dbManager.dbDisconnect(conn);
			return null;
		}
		log.info("Reminder added!");	
		//Task Creation with the reminder ID. The Task has always type=2
		insertQuery="Insert into Task (User,dueDate,notifyTimeStart,notifyTimeEnd,ReminderId,UserGroup) values ('"+userID+"','"+dueDate+"','"+notifyTimeStart+"','"+notifyTimeEnd+"',LAST_INSERT_ID(),'"+userGroup+"')";
//		System.out.println("TaskDatabase query: "+insertQuery);
		log.info(insertQuery);
		qs=dbManager.customQuery(conn, insertQuery);
		
		if(qs.execError){
			log.error(qs.explainError()+"_");
			log.error(qs.occourtedErrorException.getMessage());
			qs.occourtedErrorException.printStackTrace();
			log.info(insertQuery);
			log.error("Error during task adding... Task not added");
			int count=0;
			do{
				count++;
			}while(dbManager.rollbackTransaction(conn).execError && count < 100);	
			dbManager.dbDisconnect(conn);
			return null;
			
		}else{
			qs=dbManager.customQuery(conn, "select * from Task join Reminder on Task.ReminderId=Reminder.id where Task.id=LAST_INSERT_ID()");
			ResultSet rs=(ResultSet)qs.customQueryOutput;
			try{
			//Creating SingleTask object from data inserted into the database
			if(rs.next()){
				taskToReturn=new SingleTask(
					rs.getString("Task.id"),
					rs.getString("title"),
					rs.getString("notifyTimeStart") ,
					rs.getString("notifyTimeEnd") , 
					rs.getString("dueDate") ,
					rs.getString("description") ,
					rs.getInt("priority"),
					rs.getString("Reminder.id"),
					rs.getString("Task.UserGroup")
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
			
			log.info("Task and related Reminder added correctly to the database");
		}		
		
		dbManager.dbDisconnect(conn);
	
		return taskToReturn;
	}
	
	/**
	 * Get all tasks from a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all tasks from the user
	 */
	public static List<SingleTask> getAllTask(final String userID) {
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Task Utente
		String selectQuery="Select * from Task join Reminder on Task.ReminderId=Reminder.id where Done=0 and Task.User='"+userID+"' ";
		//Task dei gruppi a cui l'utente è iscritto
		selectQuery+=" OR UserGroup In (select UserGroup from GroupMember where User='"+userID+"')";
		
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;
		SingleTask task=null;
		ArrayList<SingleTask> taskList=new ArrayList<SingleTask>();
		
		try{
			while(rs.next()){
				taskList.add(
						new SingleTask(
								rs.getString("Task.id"),
								rs.getString("title"),
								rs.getString("notifyTimeStart") ,
								rs.getString("notifyTimeEnd") , 
								rs.getString("dueDate") ,
								rs.getString("description") ,
								rs.getInt("priority"),
								rs.getString("Reminder.id"),
								rs.getString("Task.UserGroup")
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return taskList;
	}
	
	/**
	 * Delete the specific task from database and the releted Reminder
	 * @param userID unique UUID of the user
	 * @param taskID unique UUID of the task
	 * @return false if deletion fails 
	 */	
	public static boolean deleteTask(String taskID) {
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		//Get id of reminder 
		String selectQuery="Select ReminderId from Task where id="+taskID;
		
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
		
		String deleteQuery="delete from Task where id='"+taskID+"';";
		
		
		
		//TODO verify if there are uncaugth exception here 
		qs=dbManager.customQuery(conn, deleteQuery);
		
		log.info(deleteQuery);
		
		//TODO verify if there are uncaugth exception here		
		deleteQuery=" delete from Reminder where id='"+reminderID+"';";
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
	 * Update the task row and set it's DONE field to 1, 
	 * it also set the userLocation as the one passed, and then
	 * DoneTime to MySQL NOW()
	 *  
	 * @param taskID the task id into the database
	 * @param userPosition the current user position is needed to know where the task has been set to DONE
	 * @return false if update fails, true otherwise 
	 */	
	public static boolean markTaskAsDone(String taskID,GPSLocation userPosition) {
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		log.info("markTaskAsDone taskID="+taskID+
				"location("+userPosition.latitude+
				"-"+userPosition.longitude+
				" - start");
		
		String setTaskDoneQuery="Update Task " +
				" set " +
				"Done=1," +
				"DoneLatitude="+userPosition.latitude+ "," +
				"DoneLongitude="+userPosition.longitude+","+
				"DoneTime=NOW()"+
				"where id="+taskID;
		
		QueryStatus qs=dbManager.customQuery(conn, setTaskDoneQuery);
		
		log.info(setTaskDoneQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during setting task to DONE operation.. aborting operation");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		dbManager.dbDisconnect(conn);
		log.info("markTaskAsDone - OK");
		return true;
		
	}
	
	
	public static String now(String dateFormat) {
	   Calendar cal = Calendar.getInstance();
	   SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	   return sdf.format(cal.getTime());
	}

	
	/**
	 * Update the specific task
	 * @param taskID unique UUID of the task
	 * @param task the new task
	 * @return false if update unsuccessful , true if update is successful
	 */
	public static boolean updateTask(final String taskID, final SingleTask task){

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
			log.error("Error during transaction starting... Task not updated");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		String updateQuery="UPDATE Task set ";

		updateQuery+="dueDate='"+task.dueDate+"',";
		updateQuery+="notifyTimeEnd='"+task.notifyTimeEnd+"',";
		updateQuery+="UserGroup='"+task.groupId+"',";
		updateQuery+="notifyTimeStart='"+task.notifyTimeStart+"'";
		
		updateQuery+=" where id="+taskID+"";
		
		updateQuery+=";";
		
		qs=dbManager.customQuery(conn, updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			dbManager.rollbackTransaction(conn);
			log.error("Error during Task update");
			dbManager.dbDisconnect(conn);
			return false;
		}
		
		
		updateQuery="UPDATE Reminder SET ";
		updateQuery+="description='"+task.description+"',";
		updateQuery+="title='"+task.title+"',";

		updateQuery+="latitude='"+task.gpscoordinate.latitude+"',";
		updateQuery+="longitude='"+task.gpscoordinate.longitude+"',";

		updateQuery+="priority='"+task.priority+"',";
		updateQuery+="title='"+task.title+"',";
		updateQuery+="type="+task.type+"";
		
		updateQuery+=" where id=(select ReminderId from Task where Task.id="+taskID+");";
		
		
		qs=dbManager.customQuery(conn, updateQuery);
		System.out.println(updateQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			counter=0;
			do{
				counter++;
			}while(dbManager.rollbackTransaction(conn).execError && counter<100);
			log.error("Error during Task update");
			dbManager.dbDisconnect(conn);
			return false;
		}else{
			counter=0;
			do{
				counter++;
			}while(dbManager.commitTransaction(conn).execError && counter<100);			
		}

		dbManager.dbDisconnect(conn);	
		return true;
	}
		

	//Methods for testing
	public static String addTaskQuery(String userID, String title,
			String notifyTimeStart, String notifyTimeEnd, String dueDate,
			String description, int priority,String userGroup) {
		
		String queryToReturn="Insert into Reminder (title,description,priority,type) values ('"+title+"','"+description+"','"+priority+"',2);";
		
		queryToReturn+="Insert into Task (User,dueDate,notifyTimeStart,notifyTimeEnd,ReminderId,UserGroup) values ('"+userID+"','"+dueDate+"','"+notifyTimeStart+"','"+notifyTimeEnd+"',LAST_INSERT_ID(),'"+userGroup+"');";
		
		return queryToReturn;
	}
	

	
	public static void main(String[] args){

		String dueDate="2011-12-02T17:15:31.125+01:00";
		String userGroup="";
		String notifyTimeStart="06:00:31.126+01:00";
		String notifyTimeEnd="21:00:31.126+01:00";
		String description="test task";
		
		String[] userIDs=new String[4];
		userIDs[0]="PreTest-GE-01";
		userIDs[1]="PreTest-GE-02";
		userIDs[2]="PreTest-GE-03";
		userIDs[3]="PreTest-GE-04";
		
		/*userIDs[0]="GE-01";
		userIDs[1]="GE-02";
		userIDs[2]="GE-03";
		userIDs[3]="GE-04";
		userIDs[4]="GE-05";
		userIDs[5]="GE-06";
		userIDs[6]="PD-01";
		userIDs[7]="PD-02";
		userIDs[8]="PD-03";
		userIDs[9]="PD-04";
		userIDs[10]="PD-05";
		userIDs[11]="PD-06";
		*/

		String[] taskDescriptions=new String[10];
		
		taskDescriptions[0]="comprare il latte";
		taskDescriptions[1]="andare alle poste";
		taskDescriptions[2]="comprare il pane";
		taskDescriptions[3]="fare benzina";
		taskDescriptions[4]="andare a tatro per prenotare i biglietti dello spettacolo Il commesso viaggiatore per domani";
		taskDescriptions[5]="comprare olio motore";
		taskDescriptions[6]="comprare il giornale";
		
		//Ge
		taskDescriptions[7]="andare a prenotare il ristorante Ostaja San Vincenzo per domani";
		//GE
		taskDescriptions[8]="andare a vedere la vetrina del negozio ricordi mediastore";
		//GE
		taskDescriptions[9]="andare a visitare la Casa di Colombo";		
		
		String[] taskTitles=new String[10];
		
		taskTitles[0]="latte";
		taskTitles[1]="poste";
		taskTitles[2]="pane";
		taskTitles[3]="benzina";
		taskTitles[4]="tatro";
		taskTitles[5]="olio";
		taskTitles[6]="giornale";
		
		//Ge
		taskTitles[7]="ristorante";
		//GE
		taskTitles[8]="negozio";
		//GE
		taskTitles[9]="monumento";
		
		
		for(int j=0;j<userIDs.length;j++){
			
			for(int i=0;i<taskTitles.length;i++){
				System.out.println(addTaskQuery(
						userIDs[j],taskTitles[i],notifyTimeStart,notifyTimeEnd,dueDate,taskDescriptions[i],3,userGroup
						));
			}
		
		}
		/*	
			String userID="guido";	 ts
			List<SingleTask> taskList=TaskDatabase.instance.getAllTask(userID);
			SingleTask t=taskList.get(0);
			
			GPSLocation location=new GPSLocation();
			location.latitude=1.0f;
			location.latitude=2.0f;
			TaskDatabase.instance.markTaskAsDone(t.taskID,location);
		*/
			/*
			System.out.println("da:"+t.description);
			t.description="nuova descrizione!?!?!";
			System.out.println("a:"+t.description);
			System.out.println("ID:"+t.taskID);
			if(!TaskDatabase.instance.updateTask(t.taskID,t)){
				System.out.println("Errore");
			}
			*/
	}
}