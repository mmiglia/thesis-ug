package businessobject;
import BusinessObject.*;
/**
*/
public class TaskPublisher extends Publisher implements TaskManager{
/**
*/
private List<TaskSubscriber> subscriberlist;
/**
 * @param Return 
 * @param subscriber 
*/
public void subscribe<TaskSubscriber>(TaskSubscriber subscriber) {
}
}

