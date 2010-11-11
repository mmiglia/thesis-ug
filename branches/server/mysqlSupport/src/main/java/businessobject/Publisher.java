package businessobject;

import java.util.LinkedList;
import java.util.List;

/**
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
