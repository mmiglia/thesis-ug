package businessobject;

import java.util.LinkedList;
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
	 * 
	 * @param latitude
	 *            GPS latitude coordinate
	 * @param longitude
	 *            GPS longitude coordinate
	 * @return list of hints
	 */
	public List<Hint> checkLocation(float latitude, float longitude) {
		// get all the 'need' string from task (Parser job)
		List<String> queryList = OntologyReasoner.getSearchQuery("need");

		List<Hint> result = new LinkedList<Hint>();
		for (String query : queryList) {
			result.addAll(MapManager.getInstance().searchLocalBusiness(
					latitude, longitude, query));
		}
		result = HintManager.filterLocation(50, latitude, longitude, result);
		return result;
	}
}
