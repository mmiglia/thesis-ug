package businessobject;

import java.util.Properties;

import businessobject.EventManager;
/**
 * This class provide general abstractions to be implemented by 
 * concrete class that can handle event management (e.g. Google Calendar)
*/
public abstract class EventSubscriber implements EventInterface {
	/**
	 * Constructor for this class, subscribe itself to EventPublisher	 
	 */
	protected static final Properties CONSTANTS = Configuration.getInstance().constants;
	protected EventSubscriber() {
		EventManager.getInstance().subscribe(this);
	}
}
