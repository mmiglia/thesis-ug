package businessobject;
/**
*/
public class TaskPublisher extends Publisher<TaskSubscriber> implements TaskManager{
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


