package businessobject;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleTask;
import dao.TaskDatabase;

/**
 * This SINGLETON class is the only manager/publisher for event. All implemented
 * methods are just doing the operation in local database, and then calling all
 * subsequent methods in the subscriber (3rd party database)
 */
public class TaskManager extends Publisher<TaskSubscriber> {
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
		List<SingleTask> result = TaskDatabase.instance.getAllTask(userID);
		Calendar now = Calendar.getInstance();
		Calendar compare;
		for (SingleTask o:result){
			compare = Converter.toJavaDate(o.dueDate);
			// delete if task's deadline is already passed
			if(now.after(compare)) result.remove(o);			
		}
		Collections.sort(result); // sort based on compareTo method
		return (result.size()<=5)? result : result.subList(0, 4); // trim the result
	}	

	/**
	 * This method create a new tasks
	 * 
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
			String description, int priority) {
		SingleTask toAdd = new SingleTask(title, notifyTimeStart,
				notifyTimeEnd, dueDate, description, priority);
		for (TaskSubscriber o : subscriberlist) {
			if (o.createTasks(userID, toAdd))
				continue;
			else
				return false;
		}
		TaskDatabase.instance.addTask(userID, toAdd);
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
	public boolean updateTask(String userid, SingleTask oldTask,
			SingleTask newTask) {		
		for (TaskSubscriber o : subscriberlist) {
			if (o.updateTask(userid, oldTask, newTask)) continue;
			else return false ;
		}
		TaskDatabase.instance.updateTask(oldTask.ID, newTask);
		return true;
	}
	

	/**
	 * Remove an task from a database
	 * @param userid unique UUID of the user
	 * @param taskID taskID to be removed
	 * @return false if unsuccessful, 1 if successful
	 */
	public boolean removeTask(String userid, String taskID) {		
		for (TaskSubscriber o : subscriberlist) {
			if (o.removeTask(userid, taskID)) continue;
			else return false;
		}
		TaskDatabase.instance.deleteTask(userid, taskID);
		return true;
	}
}
