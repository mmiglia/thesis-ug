package businessobject;

import java.util.Properties;

import businessobject.EventManager;
/**
 * This class provide general abstractions to be implemented by 
 * concrete class that can handle event management (e.g. Google Calendar).
 * All the class that extend EventSubscriber are automatically added to the 
 * subscribedlist of the class that can manage events. So we can have multiple elements
 * that manage events, like Google Calendar and others for wich we have to create a client
 * that have to exted this class to be subscribed into the list of the ones that are used from
 * the EventManager. Furthermore the client have to implement all the method defined into the
 * interface.
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
