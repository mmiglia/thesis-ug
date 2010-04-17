package DAO;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.query.Predicate;

import ValueObject.SingleTask;

/**
 * Singleton class that acts as a database that will save all the tasks
*/
public enum TaskDatabase {
	instance;
	private static final String DATABASE_NAME = "src/main/resources/TaskDatabase";
	private final static Logger log = LoggerFactory.getLogger(TaskDatabase.class);
	/**
	 * Add new task to the database
	 * @param userID unique UUID of the user
	 * @param task task object to be saved
	 */
	public static void addTask(String userID, SingleTask task) {
		if (taskExist(task)) {//check for redundant entry, because db40 saves redundant object
			log.warn ("Task "+task.ID+" already exist");
			return; 
		}
		TaskTuple toAdd = instance.new TaskTuple(userID, task.ID, task);
		ObjectContainer db = openDatabase();
		try {			
			db.store(toAdd);
		} finally{
			db.close();			
		}
	}
	
	/**
	 * Add new task to the database
	 * @param userID unique UUID of the user
	 * @param taskID unique UUID of the task, must be the same from the one inside task object	 
	 * @param task task object to be saved
	 */
	public static void addTask(String userID, String taskID, SingleTask task) {
		if (taskExist(task)) {//check for redundant entry, because db40 saves redundant object
			log.warn("Task "+task.ID+" already exist");
			return; 
		}
		TaskTuple toAdd = instance.new TaskTuple(userID, taskID, task);
		ObjectContainer db = openDatabase();
		try {			
			db.store(toAdd);
		} finally{
			db.close();			
		}
	}
	
	/**
	 * Delete the specific task from database
	 * @param userID unique UUID of the user
	 * @param taskID unique UUID of the task	 
	 */	
	public static void deleteTask(final String userID, final String taskID) {
		ObjectContainer db = openDatabase();
		try {
			List<TaskTuple> result = db.query(new Predicate<TaskTuple>() {
				public boolean match(TaskTuple current) {
					return current.userID.equals(userID) && current.taskID.equals(taskID);
				}
			});
			if (result.isEmpty()) return;
			TaskTuple toDelete = result.get(0);
			db.delete(toDelete);
		} finally {
			db.close();
		}
	}
	
	/**
	 * Update the specific task
	 * @param taskID unique UUID of the task
	 * @param task the new task, must have the same UUID
	 * @return false if update unsuccessful , true if update is successful
	 */
	public static boolean updateTask(final String taskID, final SingleTask task){
		ObjectContainer db = openDatabase();
		try {
			List<TaskTuple> result = db.query(new Predicate<TaskTuple>() {
				public boolean match(TaskTuple current) {
					return (current.taskID.equals(taskID) && current.task.equals(task));
				}
			});
			if (result.isEmpty()) {
				log.warn ("Cannot find task with ID "+taskID);
				return false;
			}
			TaskTuple toChange = result.get(0);
			toChange.task = task;
			db.store(toChange);
			return true;
		} finally {
			db.close();
		}		
	}
	
	/**
	 * This method check if the task exist in the database
	 * @param task object to be checked against database
	 * @return 1 if event is exist, 0 otherwise
	 */
	private static boolean taskExist(final SingleTask task) {
		ObjectContainer db = openDatabase();
		try {
		List <TaskTuple> result = db.query(new Predicate<TaskTuple>() {
			public boolean match(TaskTuple current) {
		        return current.task.equals(task);
		    }
		});
		return !result.isEmpty();
		}finally{
			db.close();
		}		
	}
	
	private static ObjectContainer openDatabase() {
		ObjectContainer db = Db4oEmbedded.openFile(Db4oEmbedded
				.newConfiguration(), DATABASE_NAME);		
		return db;
	}

	/**
	 * The basic class to be saved in TaskDatabase
	 *
	 */
	private class TaskTuple {
		/**
		 * UUID of the user
		 */
		private String userID;
		/**
		 * the ID of the task 
		 */
		private String taskID;
		/**
		 * list of tasks that is saved in the database
		 */
		private SingleTask task;

		public TaskTuple (String userID, String taskID, SingleTask task){
			this.userID=userID;
			this.taskID=taskID;
			this.task=task;
		}
	}
}