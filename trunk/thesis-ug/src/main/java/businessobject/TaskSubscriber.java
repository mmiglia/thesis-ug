package businessobject;

import java.util.Properties;

/*
 * This class provide general abstractions to be implemented by
 * concrete class that can handle task management.
 */
public abstract class TaskSubscriber implements TaskInterface {
	protected static final Properties CONSTANTS = Configuration.getInstance().constants;

	protected TaskSubscriber() {
		TaskManager.getInstance().subscribe(this);
	}
}
