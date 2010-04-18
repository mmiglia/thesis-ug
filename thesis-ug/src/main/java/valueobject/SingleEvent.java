package valueobject;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the basic event object that will be used for communication
 * to clients
*/
@XmlRootElement
public class SingleEvent extends Reminder {
	/**
	 * start time of event execution. In the format of "Tue Nov 04 20:14:11 EST 2003"
	 * */
	public String startTime;
	/**
	 * end time of event execution. In the format of "Tue Nov 04 20:14:11 EST 2003"
	 * */
	public String endTime;
	/**
	 * where this event is held
	 * */
	public String location;

	/**
	 * Constructor for this class	 
	 * @param title the main title for the event
	 * @param startTime start time of event execution
	 * @param endTime end time of event execution
	 * @param location where this event is held
	 * @param description brief descriptions regarding the event
	 */
	public SingleEvent(String title, String startTime,
			String endTime, String location, String description) {
		super(UUID.randomUUID().toString(), title, 3, description, 1); // default priority is 3;
		this.startTime = startTime;
		this.endTime = endTime;
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
	public SingleEvent(String title, String startTime,
			String endTime, String location, String description, int priority) {
		super(UUID.randomUUID().toString(), title, priority, description, 1);
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.priority = priority;
	}
	
	public void setLocation(float longitude, float latitude){
		super.setGPS(longitude, latitude);
	}
	
	public boolean equals(Object other){
		return (other instanceof SingleEvent && ID == ((SingleEvent)other).ID);		
	}
	
	public int hashCode(){
		return ID.hashCode();
	}
}
