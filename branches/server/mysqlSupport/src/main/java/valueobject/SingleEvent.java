package valueobject;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gdata.data.TextContent;

/**
 * This is the basic event object that will be used for communication
 * to clients, fields are:
 * startTime,endTime,location. All of type String
*/
@XmlRootElement
public class SingleEvent extends Reminder {
	
	/**
	 * The id of the event into the database
	 */
	public String eventID;
	
	/**
	 * start time of event execution (xs:dateTime format) <code>2006-04-17T15:00:00-08:00</code>
	 * */
	public String startTime;
	/**
	 * end time of event execution(xs:dateTime format) <code>2006-04-17T15:00:00-08:00</code>
	 * */
	public String endTime;
	/**
	 * where this event is held
	 * */
	public String location;
	
	/**
	 * deadline for event execution
	 */
	public String dueDate;

	private SingleEvent(){
		super();
	}
	/**
	 * Constructor for this class	 
	 * @param title the main title for the event
	 * @param startTime start time of event execution
	 * @param endTime end time of event execution
	 * @param location where this event is held
	 * @param description brief descriptions regarding the event
	 */
	public SingleEvent(String eventID,String title, String startTime,
			String endTime,String dueDate, String location, String description,String reminderID) {
		super(reminderID, title, 3, description, 1); // default priority is 3;
		this.eventID=eventID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.dueDate=dueDate;
		this.location = location;
	}
	
	/**
	 * Constructor for this class by also specifying priority
	 * @param title the main title for the event
	 * @param startTime start time of event execution
	 * @param endTime end time of event execution
	 * @param location where this event is held
	 * @param description brief descriptions regarding the event
	 * @param priority priority of the event
	 */
	public SingleEvent(String eventID,String title, String startTime,
			String endTime, String location, String description, int priority,String reminderID)  {
		super(reminderID, title, priority, description, 1);
		this.eventID=eventID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.priority = priority;
	}
	
	public SingleEvent(String plainText, String string, String string2,
			String valueString, String plainText2) {
		/*e.getTitle().getPlainText(),
				e.getTimes().get(0).getStartTime().toString(),
				e.getTimes().get(0).getEndTime().toString(),
				e.getLocations().get(0).getValueString(),
				((TextContent)e.getContent()).getContent().getPlainText()*/
		// TODO Auto-generated constructor stub
	}
	public void setLocation(float longitude, float latitude){
		super.setGPS(longitude, latitude);
	}	
}
