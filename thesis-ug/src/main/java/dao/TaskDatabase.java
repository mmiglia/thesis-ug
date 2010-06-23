package dao;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleTask;
import businessobject.Configuration;

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

/**
 * Singleton class that acts as a database that will save all the tasks
*/
public enum TaskDatabase {
	instance;
	private static final String DATABASE_NAME = Configuration.getInstance().constants.getProperty("DATABASE_FOLDER")+"/TaskDatabase";
	private final static Logger log = LoggerFactory.getLogger(TaskDatabase.class);
	private static ObjectServer server ; //db4o server
	private static boolean databaseOpen = false; // true means database server is initialized
	private static final Object lock = new Object(); // mutex lock
	
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
	 * Get all tasks from a specific userID
	 * @param userID unique UUID of the user
	 * @return list containing all tasks from the user
	 */
	public static List<SingleTask> getAllTask(final String userID) {
		ObjectContainer db = openDatabase();
		List<SingleTask> result = new LinkedList<SingleTask>();
		try {
			Query query = db.query();
			query.constrain(TaskTuple.class);
			query.descend("userID").constrain(userID);
			ObjectSet<TaskTuple> queryResult = query.execute();
			if (queryResult.isEmpty()) {
				log.warn("Cannot find user ID " + userID);
				return result;
			}
			for (TaskTuple o : queryResult){
				o.activateRead();
				result.add(o.task);
			}
			return result;
		} finally {
			db.close();
		}
	}
	
	/**
	 * Delete the specific task from database
	 * @param userID unique UUID of the user
	 * @param taskID unique UUID of the task
	 * @return false if deletion fails 
	 */	
	public static boolean deleteTask(final String userID, final String taskID) {
		ObjectContainer db = openDatabase();
		try {
			Query query = db.query();
			query.constrain(TaskTuple.class);
			Constraint constr=query.descend("taskID").constrain(taskID);
			query.descend("userID").constrain(userID).and(constr);
			ObjectSet<TaskTuple> result = query.execute();
			if (result.isEmpty()) return false;
			TaskTuple toDelete = result.get(0);
			db.delete(toDelete);
			return true;
		} finally {
			db.close();
		}
	}
	
	/**
	 * Update the specific task
	 * @param taskID unique UUID of the task
	 * @param task the new task
	 * @return false if update unsuccessful , true if update is successful
	 */
	public static boolean updateTask(final String taskID, final SingleTask task){
		ObjectContainer db = openDatabase();
		try {
			Query query = db.query();
			query.constrain(TaskTuple.class);
			query.descend("taskID").constrain(taskID);
			ObjectSet<TaskTuple> result = query.execute();
			if (result.isEmpty()) {
				log.warn ("Cannot find task with ID "+taskID);
				return false;
			}
			TaskTuple toChange = result.get(0);
			toChange.activateWrite();
			task.ID = taskID; //make sure ID is the same
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
				current.activateRead();
		        return current.task.equals(task);
		    }
		});
		return !result.isEmpty();
		}finally{
			db.close();
		}		
	}
	
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
	 * The basic class to be saved in TaskDatabase
	 *
	 */
	private class TaskTuple implements Activatable {
		private transient Activator _activator;
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