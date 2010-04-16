package BusinessObject;

/**
*/
public class EventPublisher extends Publisher implements EventManager{
/**
*/
private List<EventSubscriber> subscriberlist;
/**
 * @param Return 
 * @param subscriber 
*/
public void subscribe<EventSubscriber>(EventSubscriber subscriber) {
}
@Override
public void Authenticate(String username, String password) {
	// TODO Auto-generated method stub
	
}
@Override
public void createEvents(String username, String title, String startDate,
		String endDate) {
	// TODO Auto-generated method stub
	
}
@Override
public void quickAdd(String toParse) {
	// TODO Auto-generated method stub
	
}
@Override
public void retrieveAllEvents(String username) {
	// TODO Auto-generated method stub
	
}
@Override
public void retrieveEventsbyDate(String username, String startDate,
		String endDate) {
	// TODO Auto-generated method stub
	
}
@Override
public void updateEvent() {
	// TODO Auto-generated method stub
	
}
}

