package com.thesisug.communication;

import java.util.List;

import com.thesis.communication.valueobject.SingleEvent;

public interface EventResource {	
		
	public void createEvent(String userid,  String sessionid, SingleEvent toAdd) ;
	public List<SingleEvent> getAllEvents(String userid, String sessionid) ;	
	public List<SingleEvent> getEventToday(String userid, String sessionid) ;	
	public List<SingleEvent> getEvent(String DateFrom, String DateTo, String userid, String sessionid) ;	
	public void updateEvent(String userid, String sessionid, SingleEvent oldEvent, SingleEvent newEvent) ;	
	public void removeEvent(String eventID,	String userid,  String sessionid) ;

}
