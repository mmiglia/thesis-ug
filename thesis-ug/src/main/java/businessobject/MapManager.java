package businessobject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;

/**
 * This SINGLETON class is the only manager/publisher for map/geolocation services. All implemented
 * methods are just doing the operation in local database, and then calling all
 * subsequent methods in the subscriber (3rd party database)
 * 
 */
public class MapManager extends Publisher<MapSubscriber> {
	private final static Logger log = LoggerFactory.getLogger(MapManager.class);

	private MapManager() {
		super();
	}

	private static class InstanceHolder {
		private static final MapManager INSTANCE = new MapManager();
	}

	public static MapManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	/**
	 * @param Return
	 * @return
	 */
	public boolean openConnection() {
		
		return false;
	}

	/**
	 * This method will search for any given 'keyword business' around the given
	 * location
	 * 
	 * @param Return
	 * @param longitude
	 *            longitude coordinate from GPS
	 * @param latitude
	 *            latitude coordinate from GPS
	 * @param business
	 *            keyword of the business
	 */
	public List<Hint> searchLocalBusiness(float latitude, float longitude,
			String business) {
		return null;
	}
}
