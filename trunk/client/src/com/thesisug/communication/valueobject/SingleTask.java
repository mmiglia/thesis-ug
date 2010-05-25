package com.thesisug.communication.valueobject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
/**
 * This is the basic task object that will be used for communications to client
 */
public class SingleTask extends Reminder {
	/**
	 * deadline for the execution of the task (xs:dateTime format) <code>2006-04-17T15:00:00-08:00</code>
	 */
	public String dueDate;
	/**
	 * time to start notifying user to complete this task (xs:Time format, e.g.
	 * "09:30:10-06:00"). Default value is "00:00:00"
	 */
	public String notifyTimeStart;
	/**
	 * time to end notifying user to complete this task (xs:Time format, e.g.
	 * "09:30:10-06:00"). Default value is "23:59:59"
	 */
	public String notifyTimeEnd;

	public SingleTask(){
		super();
	}
	
	/**
	 * Basic constructor for the class
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
	 * @param title
	 *            title of the task
	 * @param dueDate
	 *            the deadline for task completion
	 * @param description
	 *            brief description of the task
	 */
	public SingleTask(String title, String dueDate, String description) {
		super(UUID.randomUUID().toString(), title, 3, description, 2);
		Date now = new Date();
		SimpleDateFormat timezone = new SimpleDateFormat("Z");
		this.dueDate = dueDate;
		this.notifyTimeEnd = "00:00:00" + timezone.format(now);
		this.notifyTimeEnd = "23:59:59" + timezone.format(now);
	}
	
	public SingleTask copy(){
		SingleTask newcopy = new SingleTask();
		newcopy.ID = ID;
		newcopy.priority = priority;
		newcopy.description = description;
		newcopy.title = title;
		newcopy.type = type;
		newcopy.dueDate = dueDate;
		newcopy.notifyTimeStart = notifyTimeStart;
		newcopy.notifyTimeEnd = notifyTimeEnd;
		newcopy.gpscoordinate.latitude = gpscoordinate.latitude;
		newcopy.gpscoordinate.longitude = gpscoordinate.longitude;
		return newcopy;
	}
}
