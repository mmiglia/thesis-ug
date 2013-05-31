package com.thesisug.communication.valueobject;


/**
 * This is the basic event object that will be used for communication
 * to clients
*/
public class SingleEvent extends Reminder {
	
	/**
	 * The id of the task into the database
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

	public SingleEvent(){
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
			String endTime, String location, String description, String reminderId) {
		super(reminderId, title, 3, description, 1); // default priority is 3;
		this.eventID=eventID;
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
	public SingleEvent(String eventID,String title, String startTime,
			String endTime, String location, String description,String reminderId, int priority) {
		super(reminderId, title, priority, description, 1);
		this.eventID=eventID;
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.priority = priority;
	}	
	
	/**
	 * Constructor for this class 
	 * @param title the main title for the event
	 * @param startTime start time of event execution
	 * @param endTime end time of event execution
	 * @param location where this event is held
	 * @param description brief descriptions regarding the event
	 * @param priority priority of the event
	 */
	public SingleEvent(String title, String startTime,
			String endTime, String location, String description,String reminderId) {
		super(reminderId, title, 3, description, 1);
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
			String endTime, String location, String description,String reminderId, int priority) {
		super(reminderId, title, priority, description, 1);
		this.startTime = startTime;
		this.endTime = endTime;
		this.location = location;
		this.priority = priority;
	}
	
	public void setLocation(float longitude, float latitude){
		super.setGPS(longitude, latitude);
	}	
	
	public SingleEvent copy(){
		SingleEvent newcopy = new SingleEvent();
		newcopy.eventID=eventID;
		newcopy.reminderID = reminderID;
		newcopy.priority = priority;
		newcopy.description = description;
		newcopy.title = title;
		newcopy.type = type;
		newcopy.startTime = startTime;
		newcopy.endTime = endTime;
		newcopy.location = location;
		newcopy.gpscoordinate.latitude = gpscoordinate.latitude;
		newcopy.gpscoordinate.longitude = gpscoordinate.longitude;
		return newcopy;
	}
}
