package businessobject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;

/**
*/
public class LocationAwareManager {
	private final static Logger log = LoggerFactory
	.getLogger(LocationAwareManager.class);
	
	/**
	 * Check for a task that can be completed near given location
	 * @param latitude GPS latitude coordinate
	 * @param longitude GPS longitude coordinate
	 * @return list of hints
	 */
	public List<Hint> checkLocation(float latitude, float longitude) {
		// Sample implementation
		// get the list of keyword from ontology 
		// for ontology
		List<Hint> result = MapManager.getInstance().searchLocalBusiness(latitude, longitude, "restaurant");
		result = HintManager.filterLocation(50,latitude, longitude, result);
		 
		return result;
	}
}
