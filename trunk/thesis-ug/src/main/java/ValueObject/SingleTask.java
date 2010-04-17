package ValueObject;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the basic task object that will be used for communications to client
 */
@XmlRootElement
public class SingleTask extends Reminder {
	/**
	 * deadline for the execution of the task
	 */
	public String dueDate;
	/**
	 * time to start notifying user to complete this task. Default value is
	 * "12:00:00 AM"
	 */
	public String notifyTimeStart = "12:00:00 AM";
	/**
	 * time to end notifying user to complete this task. Default value is
	 * "12:00:00 PM"
	 */
	public String notifyTimeEnd = "12:00:00 PM";

	/**
	 * Basic constructor for the class
	 * 
	 * @param title title of the task
	 * @param notifyTimeStart time to start notifying user
	 * @param notifyTimeEnd time to end notifying user
	 * @param dueDate the deadline for task completion
	 * @param description brief description of the task
	 * @param priority task priority
	 */
	public SingleTask(String title, String notifyTimeStart,
			String notifyTimeEnd, String dueDate, String description,
			int priority) {
		super(UUID.randomUUID().toString(), title, priority, description, 2);
		this.dueDate = dueDate;
		this.notifyTimeStart = notifyTimeStart;
		this.notifyTimeEnd = notifyTimeEnd;
	}

	/**
	 * Constructor without specifying priority
	 * 
	 * @param title title of the task
	 * @param notifyTimeStart time to start notifying user
	 * @param notifyTimeEnd time to end notifying user
	 * @param dueDate the deadline for task completion
	 * @param description brief description of the task
	 */
	public SingleTask(String title, String notifyTimeStart,
			String notifyTimeEnd, String dueDate, String description) {
		super(UUID.randomUUID().toString(), title, 3, description, 2);
		this.dueDate = dueDate;
		this.notifyTimeStart = notifyTimeStart;
		this.notifyTimeEnd = notifyTimeEnd;
	}

	/**
	 * Simple constructor with default value for priority and notify-time.
	 * 
	 * @param title title of the task
	 * @param dueDate the deadline for task completion
	 * @param description brief description of the task
	 */
	public SingleTask(String title, String dueDate, String description) {
		super(UUID.randomUUID().toString(), title, 3, description, 2);
		this.dueDate = dueDate;
	}
	
	public boolean equals(Object other){
		return (other instanceof SingleTask && ID == ((SingleTask)other).ID);		
	}
	
	public int hashCode(){
		return ID.hashCode();
	}
}
