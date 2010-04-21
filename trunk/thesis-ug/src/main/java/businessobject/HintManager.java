package businessobject;

/**
 * Hint Manager is a pipe (filter) that accepts the result from LocationManager
 * and filter it based on user preferences.
 */
public class HintManager {
	/**
	 * filter the result from MapManager according to distance from current
	 * location
	 * 
	 * @param PotentialSolutions
	 * @param Return
	 * @param userDistance
	 */
	public void filterLocation(int userDistance, int PotentialSolutions) {
	}

	/**
	 * @param limit
	 *            give the upper limit for priority
	 * @param Return
	 */
	public void filterPriority(int limit) {
	}

	/**
	 * @param Return
	 */
	public void filterTime() {
	}

	/**
	 * @param Return
	 */
	public void filterAll() {
	}
}
