package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleTask;

import dao.management.mysql.MySQLDBManager;
import dao.management.QueryStatus;

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
		String selectQuery="Select * from Task join Reminder on Task.ReminderId=Reminder.id where Task.User='"+userID+"' ";
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
	 * Delete the specific task from database
	 * @param userID unique UUID of the user
	 * @param taskID unique UUID of the task
	 * @return false if deletion fails 
	 */	
	public static boolean deleteTask(String taskID) {
		
		Connection conn= (Connection) dbManager.dbConnect();
		
		String deleteQuery="delete from Task where id="+taskID;
		QueryStatus qs=dbManager.customQuery(conn, deleteQuery);
		
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
		

	public static void main(String[] args){
		String userID="1";	

			
			List<SingleTask> taskList=TaskDatabase.instance.getAllTask(userID);
			SingleTask t=taskList.get(1);
			
			System.out.println("da:"+t.description);
			t.description="nuova descrizione!?!?!";
			System.out.println("a:"+t.description);
			System.out.println("ID:"+t.taskID);
			if(!TaskDatabase.instance.updateTask(t.taskID,t)){
				System.out.println("Errore");
			}
	}
}