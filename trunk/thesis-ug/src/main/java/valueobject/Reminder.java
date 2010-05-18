package valueobject;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * This is the parent class for event and task that has common field for them.
 */
@XmlRootElement
public abstract class Reminder {
	/**
	 * unique ID of the task. It is implemented as an automatically generated
	 * UUID, which will be converted to a string.
	 */
	public String ID;
	/**
	 * task priority. Priority is between 1 to 5 , with 3 being the default priority.
	 */
	public int priority = 3;
	/**
	 * short description of a task
	 */
	public String description;
	/**
	 * the title of reminder
	 */
	public String title;
	/**
	 * distinguish between event or task. 1 is event, 2 is task.
	 */
	public int type;
	/**
	 * location of the event, can be defined
	 */
	public GPSLocation gpscoordinate;
	
	protected Reminder(){		
		gpscoordinate = new GPSLocation();
	}
	/**
	 * Constructor for this class
	 * @param ID unique ID of the event
	 * @param title title of the event
	 * @param priority priority of the event
	 * @param description description of the event
	 * @param type the type of reminder, 1 for event 2 for task
	 */
	public Reminder(String ID, String title, int priority, String description,
			int type) {
		this.ID = ID;
		this.title = title;
		this.priority = priority;
		this.description = description;
		this.type = type;
		gpscoordinate = new GPSLocation();
	}
	
	/**
	 * Set the GPS coordinate for fulfillment
	 * @param longitude GPS longitude coordinate
	 * @param latitude GPS latitude coordinate 
	 */
	public void setGPS(float longitude, float latitude){
		this.gpscoordinate.latitude= latitude;
		this.gpscoordinate.longitude= longitude;
	}
	
	/**
	 * Set the GPS coordinate for fulfillment
	 * @param location location object specifying latitude and longitude
	 */
	public void setGPS(GPSLocation location){
		this.gpscoordinate = location;
	}
	
	/**
	 * the class specifying latitude and longitude position
	 *
	 */
	public static class GPSLocation{
		public float latitude = 0.0f;
		public float longitude = 0.0f;
	}
	
	@Override
	public boolean equals(Object other) {
		return (this.ID == ((Reminder) other).ID);
	}

	@Override
	public int hashCode() {
		return ID.hashCode();
	}
}
