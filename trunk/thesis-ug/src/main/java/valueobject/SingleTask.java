package valueobject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the basic task object that will be used for communications to client
 */
@XmlRootElement
public class SingleTask extends Reminder implements Comparable<SingleTask>{
	/**
	 * deadline for the execution of the task
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

	/**
	 * This method overwrite Comparable interface compareTo method to enable sorting.
	 * It will first sort based on task duedate, earlier first.
	 * If it's equal it will compare its priority, greater priority first.
	 */
	@Override
	public int compareTo(SingleTask compare) {
		Date datecompare = convertToJavaDate(compare.dueDate);
		Date thisobject = convertToJavaDate(this.dueDate);
		if (!(thisobject.after(datecompare) || thisobject.before(datecompare))) {
			if (this.priority == compare.priority)
				return 0;
			else
				return (this.priority > compare.priority) ? -1 : 1;
		} else
			return (thisobject.after(datecompare)) ? 1 : -1;
	}
	
	private static Date convertToJavaDate (String toParse){
		String xsDateTime = new String(toParse);
		int stringLength = xsDateTime.length();
		// removes the colon ':' at the 3rd position from the end to match ISO 8601
		xsDateTime=xsDateTime.substring(0, stringLength-3)+xsDateTime.substring(stringLength-2,stringLength);
		SimpleDateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
		try {
			return ISO_8601.parse(xsDateTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
