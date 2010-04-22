package businessobject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.SingleTask;

/**
 * Hint Manager is a pipe (filter) that accepts the result from MapManager and
 * filter it based on user preferences.
 */
public class HintManager {
	private final static Logger log = LoggerFactory
			.getLogger(HintManager.class);

	/**
	 * filter the result from MapManager according to distance from current
	 * location
	 * 
	 * @param userDistance
	 *            farthest distance from user location (in meter)
	 * @param currentLat
	 *            current user latitude
	 * @param currentLong
	 *            current user longitude
	 * @param solution
	 *            solution list of hints to be filtered
	 * @return list of hints that is inside userDistance radius
	 */
	public static List<Hint> filterLocation(int userDistance, float currentLat,
			float currentLong, List<Hint> solution) {
		log.info("Filtering hints result based on locations distance");
		for (Hint o : solution) {
			if (userDistance < (calculateDistance(currentLat, currentLong,
					Float.parseFloat(o.lat), Float.parseFloat(o.lng))))
				solution.remove(o);
		}
		return solution;
	}

	/**
	 * Filter the list of tasks based on the limit on priority
	 * 
	 * @param upperlimit
	 *            upper limit on the priority
	 * @param solution
	 *            list of tasks to be filtered
	 * @return list of tasks that has lower priority than limit
	 */
	public static List<SingleTask> filterPriority(int upperlimit,
			List<SingleTask> solution) {
		log.info("Removing tasks higher than priority " + upperlimit);
		for (SingleTask o : solution) {
			if (o.priority > upperlimit)
				solution.remove(o);
		}
		return solution;
	}

	/**
	 * Filter the task based on proper time to notify the user
	 * 
	 * @param solution
	 *            list of tasks to be filtered
	 * @return list of tasks that can be executed NOW (current time in server)
	 */
	public static List<SingleTask> filterTime(List<SingleTask> solution) {
		Calendar now = Calendar.getInstance();
		log.info("Removing tasks outside notify period");
		log.info("Current time on server : "
				+ new SimpleDateFormat().format(now));
		for (SingleTask o : solution) {
			if (!(now.after(Converter.toJavaDate(o.notifyTimeStart)) && now
					.before(Converter.toJavaTime(o.notifyTimeEnd))))
				solution.remove(o);
		}
		return solution;
	}

	/**
	 * This method filter the task list based on user priority limit, and
	 * allowed notification time
	 * 
	 * @param upperlimit
	 *            upper limit on the priority
	 * @param solution
	 *            list of tasks to be filtered
	 * @return list of tasks that can be executed
	 */
	public static List<SingleTask> filterAll(int upperlimit,
			List<SingleTask> solution) {
		solution = filterPriority(upperlimit, solution);
		solution = filterTime(solution);
		return solution;
	}

	/**
	 * Returns the distance in meter between two GPS location
	 * 
	 * @param latA
	 *            latitude of point A
	 * @param longA
	 *            longitude of point B
	 * @param latB
	 *            latitude of point B
	 * @param longB
	 *            longitude of point B
	 * @return distance in meter
	 */
	private static double calculateDistance(float latA, float longA,
			float latB, float longB) {
		final double EARTH_RADIUS = 6378.14; // in kilometer, according to
		// WolframAlpha
		double cosaob = Math.cos((double) latA) * Math.cos((double) latB)
				* Math.cos((double) longB - (double) longA)
				+ Math.sin((double) latA) * Math.sin((double) latB);
		return Math.toRadians(Math.acos(cosaob)) * EARTH_RADIUS * 1000;
	}
}
