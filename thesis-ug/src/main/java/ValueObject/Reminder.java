package ValueObject;
import java.lang.*;
/**
*/
public abstract class Reminder{
/**
 * unique ID of the task. It is implemented as an automatically generated UUID, which will be converted to a string.
*/
public String ID;
/**
 * task priority
*/
public int priority;
/**
 * short description of a task
*/
public String description;
/**
*/
public String title;
/**
 * distinguish between event or task. 1 is event, 2 is task.
*/
public int type;
/**
 * @param title 
 * @param priority 
 * @param ID 
 * @param dueDate 
*/
public Reminder(String ID, String title, String dueDate, int priority) {
}
}

