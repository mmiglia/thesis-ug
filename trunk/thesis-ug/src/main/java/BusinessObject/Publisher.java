package BusinessObject;

import java.util.List;

/**
*/
public abstract class Publisher<E> {
	/**
*/
	protected List<E> subscriberlist;

	/**
	 * this method will put the subscriber to the subscriber list
	 * 
	 * @param Return
	 * @param subscriber
	 */
	public void subscribe(E subscriber) {
	}
}
