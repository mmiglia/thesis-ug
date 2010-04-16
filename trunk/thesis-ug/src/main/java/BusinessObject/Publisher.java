package BusinessObject;
/**
*/
public abstract class Publisher{
/**
*/
private List<E> subscriberlist;
/**
 * this method will put the subscriber to the subscriber list
 * @param Return 
 * @param subscriber 
*/
public abstract void subscribe<E>(E subscriber) {
}
}

