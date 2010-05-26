package businessobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.SingleTask;

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
	 * @param distance
	 * 			  distance in meter
	 * @return list of hints
	 */
	public static List<Hint> checkLocationAll(String userid, float latitude, float longitude, int distance) {
		// get all user tasks
		List<SingleTask> tasks= TaskManager.getInstance().retrieveAllTask(userid);
		List<String> needs = new ArrayList<String>(); // list of user needs
		/* current parser implementation is just splitting tasks-title into words
		*  future improvement such as the use of keyword extraction is strongly encouraged*/
		for (SingleTask o : tasks){
			String[] words = o.title.split(" ");
			needs.addAll(Arrays.asList(words));
		}
		// remove duplicates by using HashSet
		HashSet<String> needsfilter = new HashSet<String>(needs);
		needs.clear();
		needs.addAll(needsfilter);
		log.info("needs are "+ needs.size()+" : "+needs.toString());
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		for (String o : needs) queryList.addAll(OntologyReasoner.getSearchQuery(o));
		log.info("querylist are "+ queryList.size()+" : "+queryList.toString());
		List<Hint> result = new LinkedList<Hint>(); // list of search result
		for (String query : queryList) 
			result.addAll(MapManager.getInstance().searchLocalBusiness(
					latitude, longitude, query));
		log.info("result are "+result.size());
		// filter the result
		List<Hint> toReturn = HintManager.filterLocation(distance, latitude, longitude, result);
		log.info("filtered result are "+toReturn.size()+toReturn.toString());
		return toReturn;
	}
	
	/**
	 * Check for a task that can be completed near given location
	 * @param userid
	 * 			  user id of the user
	 * @param sentence
	 * 			  task description or title
	 * @param latitude
	 *            GPS latitude coordinate
	 * @param longitude
	 *            GPS longitude coordinate
	 * @param distance
	 * 			  distance in meter
	 * @return list of hints
	 */
	public static List<Hint> checkLocationSingle(String userid, String sentence, float latitude, float longitude, int distance) {
		List<String> needs = new ArrayList<String>(); // list of user needs

		/* current parser implementation is just splitting tasks-title into words
		 *  future improvement such as the use of keyword extraction is strongly encouraged*/
		needs.addAll(Arrays.asList(sentence.split(" ")));
		
		// remove duplicates by using HashSet
		HashSet<String> needsfilter = new HashSet<String>(needs);
		needs.clear();
		needs.addAll(needsfilter);

		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		for (String o : needs) queryList.addAll(OntologyReasoner.getSearchQuery(o));
		log.info("querylist are "+ queryList.size()+" : "+queryList.toString());
		List<Hint> result = new LinkedList<Hint>(); // list of search result
		for (String query : queryList) 
			result.addAll(MapManager.getInstance().searchLocalBusiness(
					latitude, longitude, query));
		// filter the result
		List<Hint> toReturn = HintManager.filterLocation(distance, latitude, longitude, result);
		log.info("filtered result are "+toReturn.size()+toReturn.toString());
		return toReturn;
	}
}
