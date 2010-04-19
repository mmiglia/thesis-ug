package businessobject;

import java.util.Properties;

import businessobject.EventPublisher;
/**
 * This class provide general abstractions to be implemented by 
 * concrete class that can handle event management (e.g. Google Calendar)
*/
public abstract class EventSubscriber implements EventManager {
	/**
	 * Constructor for this class, subscribe itself to EventPublisher	 
	 */
	protected static final Properties CONSTANTS = Configuration.getInstance().constants;
	protected EventSubscriber() {
		EventPublisher.getInstance().subscribe(this);
	}	
}
