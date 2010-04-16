package BusinessObject;
import ValueObject.*;
import java.lang.*;
/**
*/
public interface TaskManager{
/**
 * get all task from a given username.
 * @param username 
 * @param Return username of the user
*/
public void getAllTask(String username) {
}
/**
 * get only first few near deadline task, if deadline is the same, then sort based on priority.
 * @param username 
 * @param Return username of the user
*/
public void getFirstTask(String username) {
}
/**
 * @param username 
 * @param description 
 * @param Return 
 * @param dueDate 
*/
public void createTask(String username, String description, String dueDate) {
}
/**
 * @param username 
 * @param task 
 * @param Return 
 * @param taskID 
*/
public void updateTask(String username, String taskID, Reminder task) {
}
/**
 * @param username 
 * @param Return 
 * @param taskID 
*/
public void retrieveTaskbyID(String username, String taskID) {
}
}

