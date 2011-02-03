package businessobject;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Reminder.GPSLocation;
import valueobject.SingleTask;
import dao.TaskDatabase;

/**
 * This SINGLETON class is the only manager/publisher for event. All implemented
 * methods are just doing the operation in local database, and then calling all
 * subsequent methods in the subscriber (3rd party database).
 * Here all tasks that are created are stored in each of the TaskSubscriber (like GoogleCalendar)
 * but in the future may be that we have to choose only one of them or at least let the user
 * choose on wich TaskSubscriber save the tasks
 */
public class TaskManager extends Publisher<TaskSubscriber> implements TaskInterface{
	private final static Logger log = LoggerFactory
			.getLogger(TaskManager.class);

	private TaskManager() {
		super();
	}

	private static class InstanceHolder {
		private static final TaskManager INSTANCE = new TaskManager();
	}

	public static TaskManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * Authenticate to the backend service that manage tasks
	 * 
	 * @param username
	 *            username to the service provider
	 * @param password
	 *            password to the service provider
	 * @return 0 if unsuccessful, 1 if successful
	 */
	public boolean Authenticate(String username, String password) {
		for (TaskSubscriber o : subscriberlist) {
			if (o.Authenticate(username, password)) continue;
			else return false;
		}
		return true;
	}
	
	/**
	 * Get all task from a given userid
	 * 
	 * @param userID
	 *            unique UUID of the user
	 * @return list containing all tasks from the user
	 * 
	 */
	public List<SingleTask> retrieveAllTask(String userID) {
		return TaskDatabase.instance.getAllTask(userID);
	}

	/**
	 * get only first few near deadline task (according to the date in server), 
	 * if deadline is the same, then sort based on priority.
	 * 
	 * @param userID unique UUID of the user
	 * @return 5 most prioritized tasks
	 */
	public List<SingleTask> getFirstTasks(String userID) {
		int firstTaskListLength=11;
		List<SingleTask> result = TaskDatabase.instance.getAllTask(userID);
		log.debug("Found "+result.size() + " tasks for user" );
		Calendar now = Calendar.getInstance();
		Calendar compare;
		Iterator<SingleTask> iterator = result.iterator();
		while (iterator.hasNext()){
			SingleTask o = iterator.next();
			compare = Converter.toJavaDate(o.dueDate);
			// delete if task's deadline is already passed
			if(now.after(compare)) iterator.remove();			
		}
		log.debug("Found "+result.size() + " tasks for user that have a dueDate<deadline" );
		Collections.sort(result); // sort based on compareTo method
		return (result.size()<=firstTaskListLength)? result : result.subList(0, 4); // trim the result
	}	

	/**
	 * This method create a new tasks
	 * 
	 * @param userID unique UUID of the user
	 * @param title
	 *            title of the task
	 * @param notifyTimeStart
	 *            time to start notifying user
	 * @param notifyTimeEnd
	 *            time to end notifying user
	 * @param dueDate
	 *            the deadline for task completion
	 * @param description
	 *            brief description of the task
	 * @param priority
	 *            task priority
	 * @return true if task is successfully created , false otherwise
	 */
	public boolean createTask(String userID, String title,
			String notifyTimeStart, String notifyTimeEnd, String dueDate,
			String description, int priority,String groupId) {
		System.out.println("TaskManager - addTask: '"+userID+"', '"+title+"', '"+notifyTimeStart+"', '"+notifyTimeEnd+"', '"+dueDate+"', '"+description+"', '"+priority+"', '"+groupId+"'");
		SingleTask toAdd = TaskDatabase.instance.addTask(userID, title,notifyTimeStart, notifyTimeEnd, dueDate,
				description, priority,groupId);
		
		for (TaskSubscriber o : subscriberlist) o.createTask(userID, toAdd);		
		
		
		return true;
	}
	
	/**
	 * Create new task by supplying SingleTask instance
	 * @param userID unique UUID of the user
	 * @param toAdd SingleTask instance
	 * @return true if task is successfully added to database, false otherwise
	 * 
	 */
	public boolean createTask(String userID, SingleTask taskObj){
		taskObj=setDefaultIfEmpty(taskObj);
		log.debug("GROUPID:"+taskObj.groupId);
		SingleTask toAdd = TaskDatabase.instance.addTask(userID,taskObj.title,taskObj.notifyTimeStart, taskObj.notifyTimeEnd, taskObj.dueDate,
				taskObj.description, taskObj.priority,taskObj.groupId);
		
		for (TaskSubscriber o : subscriberlist) o.createTask(userID, toAdd);
		//TaskDatabase.instance.addTask(userID, toAdd);
		return true;
	}
	

	/**
	 * Update the task by a given UUID to a new one
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param oldTask
	 *            old task object to be replaced
	 * @param newTask
	 *            the new task object
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean updateTask(String userid, SingleTask newTask) {		
		for (TaskSubscriber o : subscriberlist) o.updateTask(userid, newTask);
		TaskDatabase.instance.updateTask(newTask.taskID, newTask);
		return true;
	}	

	/**
	 * Remove an task from a database
	 * @param userid unique UUID of the user
	 * @param taskID taskID to be removed
	 * @return false if unsuccessful, 1 if successful
	 * 
	 */
	public boolean removeTask(String userid, String taskID) {		
		for (TaskSubscriber o : subscriberlist) {
			if (o.removeTask(userid, taskID)) continue;
			else return false;
		}
		TaskDatabase.instance.deleteTask(taskID);
		return true;
	}

	@Override
	public boolean quickAdd(String userid, String toParse) {
		for (TaskSubscriber o : subscriberlist) {
			if (o.quickAdd(userid, toParse)) continue;
			else return false;
		}
		return true;
	}
	
	private SingleTask setDefaultIfEmpty(SingleTask task){
	
		if(task.groupId.equals("")){
			//Default = personal task
			task.groupId="0";
		}
		
		return task;
		
	}

	@Override
	public boolean markTaskAsDone(String taskID, GPSLocation userLocation) {
		return TaskDatabase.markTaskAsDone(taskID, userLocation);
	}
	
}
