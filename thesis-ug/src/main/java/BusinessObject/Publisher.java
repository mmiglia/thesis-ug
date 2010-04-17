package BusinessObject;

import java.util.List;

/**
*/
public abstract class Publisher<E> {
	/**
*/
	private List<E> subscriberlist;

	/**
	 * this method will put the subscriber to the subscriber list
	 * 
	 * @param Return
	 * @param subscriber
	 */
	public <E> void subscribe(E subscriber) {
	}
}
