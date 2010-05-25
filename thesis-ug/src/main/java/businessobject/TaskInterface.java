package businessobject;

import java.util.List;

import valueobject.SingleTask;

/**
 * This class provides abstraction of all the methods that needs to be
 * implemented in the task database service
 */
public interface TaskInterface {
	/**
	 * this method will create directly an tasks from a specified date, and
	 * title.
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param task
	 *            SingleTask object from local database
	 * @return boolean 0 if unsuccessful, 1 if successful
	 */
	public boolean createTasks(String userid, SingleTask task);

	/**
	 * this method will remove the specific task
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param taskID
	 *            unique UUID of the task
	 * @return boolean 0 if unsuccessful, 1 if successful
	 */
	public boolean removeTask(String userid, String taskID);

	/**
	 * this method will particularly use quick add features in Google Calendar,
	 * which can parse directly any type of string.
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @param toParse
	 *            text to be parsed
	 * @return boolean 0 if unsuccessful, 1 if successful
	 */
	public boolean quickAdd(String userid, String toParse);

	/**
	 * Authenticate to the backend service (e.g. Google Calendar)
	 * 
	 * @param username
	 *            username to the service provider
	 * @param password
	 *            password to the service provider
	 * @return 0 if unsuccessful, 1 if successful
	 */
	public boolean Authenticate(String username, String password);

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
	public boolean updateTask(String userid, SingleTask newTask);

	/**
	 * retrieve all tasks from this user Calendar
	 * 
	 * @param userid
	 *            unique UUID of the user
	 * @return list that contains all tasks from the user
	 */
	public List<SingleTask> retrieveAllTasks(String userid);
}
