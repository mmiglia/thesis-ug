package businessobject;

import java.util.Properties;

/**
*/
public abstract class MapSubscriber implements MapInterface{
	/**
	 * Constructor for this class, subscribe itself to EventPublisher	 
	 */
	protected static final Properties CONSTANTS = Configuration.getInstance().constants;
	protected MapSubscriber() {
		MapManager.getInstance().subscribe(this);
	}
}

