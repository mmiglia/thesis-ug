package BusinessObject.google;

import java.util.List;

import ValueObject.SingleEvent;
import BusinessObject.EventSubscriber;
import com.google.gdata.client.calendar.*;

public class CalendarClient extends EventSubscriber{

	@Override
	public boolean Authenticate(String userid, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createEvents(String userid, String title, String startTime,
			String endTime, String location, String description) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean quickAdd(String userid, String toParse) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<SingleEvent> retrieveAllEvents(String userid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SingleEvent> retrieveEventsbyDate(String userid,
			String startTime, String endTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateEvent(String userid, String eventID,
			SingleEvent newEvent) {
		// TODO Auto-generated method stub
		return false;
	}

}
