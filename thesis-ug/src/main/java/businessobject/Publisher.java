package businessobject;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used to specify the list of the subscriber to use.
 * All subscriber are stored into a List.
 * If, for example, we have more than one geolocation system, in the MapManager we put it
 * into the list and then every time that we use the MapManager, all the elements in this list 
 * are queryed. May be that this class can contain other elements such as a description for
 * each of the subscribers
 * 
*/
public abstract class Publisher<E> {
	/**
	 * list of subscriber
	 */
	protected List<E> subscriberlist;

	public Publisher(){
		subscriberlist = new LinkedList<E>();
	}
	/**
	 * this method will put the subscriber to the subscriber list
	 * 
	 * @param subscriber the subscriber
	 */
	public void subscribe(E subscriber) {
		subscriberlist.add(subscriber);
	}
	/**
	 * this method will delete the subscriber from the subscriber list
	 * 
	 * @param subscriber the subscriber
	 */
	public void unsubscribe(E subscriber) {
		subscriberlist.remove(subscriber);
	}
}
