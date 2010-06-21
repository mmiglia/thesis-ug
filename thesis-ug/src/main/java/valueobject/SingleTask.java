package valueobject;

import java.util.Calendar;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import businessobject.Converter;

import com.db4o.activation.ActivationPurpose;
import com.db4o.activation.Activator;
import com.db4o.ta.Activatable;

/**
 * This is the basic task object that will be used for communications to client
 */
@XmlRootElement
public class SingleTask extends Reminder implements Comparable<SingleTask>, Activatable{
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
	
	private SingleTask(){
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
		this.dueDate = dueDate;
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		this.notifyTimeStart = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		this.notifyTimeEnd = Converter.CalendarTimetoString(now);
	}

	/**
	 * This method overwrite Comparable interface compareTo method to enable sorting.
	 * It will first sort based on task duedate, earlier first.
	 * If it's equal it will compare its priority, greater priority first.
	 */
	@Override
	public int compareTo(SingleTask compare) {
		Calendar datecompare = Converter.toJavaDate(compare.dueDate);
		Calendar thisobject = Converter.toJavaDate(this.dueDate);
		if (!(thisobject.after(datecompare) || thisobject.before(datecompare))) {
			if (this.priority == compare.priority)
				return 0;
			else
				return (this.priority > compare.priority) ? -1 : 1;
		} else
			return (thisobject.after(datecompare)) ? 1 : -1;
	}

	@Override
	public void activate(ActivationPurpose arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bind(Activator arg0) {
		// TODO Auto-generated method stub
		
	}
}
