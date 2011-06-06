package businessobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.Item;
import valueobject.SingleTask;

import businessobject.CachingManager;

/**
 * Check if, in the current place, there are task that can be completed. 
 * To understand the correct word to use in the search query it uses OntologyReasoner class
 * by passing to it all the words in the title of the task
*/
public class LocationAwareManager {
	private final static Logger log = LoggerFactory
			.getLogger(LocationAwareManager.class);

	/**
	 * Check for a task that can be completed near given location 
	 * between all the current task in TaskManager.
	 * 
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
		log.info("Retreived " + tasks.size()+" task for user "+ userid);
		
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
		System.out.println("needs are "+ needs.size()+" : "+needs.toString());
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		
		
		
		/*23-5-2011
		 * Aggiunto il controllo delle location nel db(ritorna quelle votate dall'utente)
		 * @author Anuska
		 */
		//for (String o : needs) queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
		for (String o : needs) 
		{	System.out.println("Sono in LocationAwareManager checkLocationAll for needs:"+o);
			queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
			queryList.addAll(OntologyManager.getInstance().viewLocationForItemVoted(userid,o));
			queryList.addAll(OntologyManager.getInstance().viewLocationForActionVoted(userid,o));
		}
		log.info("querylist are "+ queryList.size()+" : "+queryList.toString());
		System.out.println("querylist are "+ queryList.size()+" : "+queryList.toString());
		/* Anuska
		 * se non trovo niente nell'ontologia o nel db allora cerco con 
		 * la query ricevuta
		 */
		if (queryList.isEmpty())
		{	
			System.out.println("Nessuna corrispondenza:mando direttamente la query dei bisogni");
			queryList.addAll(needs);
		}
		
		/*
		List<Hint> result = new LinkedList<Hint>(); // list of search result
		
		// Anuska - modificato in modo che salvi i risultati in cache con la query relativa
		for (String query : queryList) 
		{
			List<Hint> listToAdd = MapManager.getInstance().searchLocalBusiness(
					latitude, longitude, query);
			result.addAll(listToAdd);
			System.out.println("for string query:"+query);
			CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
			System.out.println("inserito nel db");
		}
		log.info("result are "+result.size());
		// filter the result
		List<Hint> toReturn = new HintManager().filterLocation(distance, latitude, longitude, result);
		return toReturn;*/
		log.info("querylist are "+ queryList.size()+" : "+queryList.toString());
		
		List<Hint> result = new LinkedList<Hint>();
		List<Hint> toReturn= new LinkedList<Hint>();
		List<Hint> toReturn1= new LinkedList<Hint>();
		List<Hint> toReturn2= new LinkedList<Hint>();
		
		for (String query : queryList) 
		{
			List<Hint> result1 = new LinkedList<Hint>(); // list of search result IN CACHE
			result1.addAll(CachingManager.searchLocalBusinessDB(
							latitude, longitude, query,distance));
			if (distance==0)
			{	//significa che non ho vincoli sulla distanza, io li pongo a 50 Km
				toReturn1 = new HintManager().filterLocation(50000, latitude, longitude, result1);
				System.out.println("distance =0 Risultato ricerca in cache:"+ toReturn1);
			}
			else
			{
				toReturn1 = new HintManager().filterLocation(distance, latitude, longitude, result1);
				System.out.println("distance <>0 Risultato ricerca in cache:"+ toReturn1);
				
			}
			
			
			//Se non trovo niente allora interrogo Google
			if (toReturn1.isEmpty())
			{	//cerco in Google e salvo i risultati in cache
				System.out.println("Interrogo google per query:"+query);
					List<Hint> result2 = new LinkedList<Hint>(); // list of search result IN GOOGLE
					List<Hint> listToAdd = new LinkedList<Hint>();
					listToAdd = MapManager.getInstance().searchLocalBusiness(
							latitude, longitude, query);
					System.out.println("for string query:"+query);
					CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
					System.out.println("inserito nel db");
			
					//filter the result
					toReturn2 = new HintManager().filterLocation(distance, latitude, longitude, listToAdd);
					System.out.println("Risultato ricerca in Google:"+ listToAdd);
					toReturn.addAll(toReturn2);
			}else toReturn.addAll(toReturn1);
		}

		return toReturn;
		
	}
	
	/**
	 * Starting from a sentence, checks if the task can be completed near given location
	 * by searching businness based on the needs actually determined by splitting the 
	 * sentence using spaces.
	 *   
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
		System.out.println("Sono in LocationAwareManager checkLocationSingle ");
		
		/* 30-5-2011
		 * Controllo se nel db Location c'è il title del task, se si interrogo 
		 * Google con la location relativa
		 */
		String location = OntologyManager.getInstance().findLocation(userid,sentence.toLowerCase());
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		if (!location.equalsIgnoreCase(""))
		{	
			queryList.add(location);
		}
		else
		{
		
			/* current parser implementation is just splitting tasks-title into words
			 *  future improvement such as the use of keyword extraction is strongly encouraged*/
			
			needs.addAll(Arrays.asList(sentence.split(" ")));
		
			// remove duplicates by using HashSet
			HashSet<String> needsfilter = new HashSet<String>(needs);
			needs.clear();
			needs.addAll(needsfilter);
		
			/*20-5-2011
			 * Aggiunto il controllo delle location nel db(ritorna quelle votate dall'utente)
			 * @author Anuska
			 */
		
			for (String o : needs) 
			{	System.out.println("Sono in LocationAwareManager checkLocationSingle for needs:"+o);
				queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
				queryList.addAll(OntologyManager.getInstance().viewLocationForItemVoted(userid,o));
				queryList.addAll(OntologyManager.getInstance().viewLocationForActionVoted(userid,o));
			}
			System.out.println("posso trovarlo in: "+queryList);
		}
		
		/* Anuska
		 * se non trovo niente nell'ontologia o nel db allora cerco con
		 * la query ricevuta
		 */
		if (queryList.isEmpty())
		{	
			System.out.println("Nessuna corrispondenza:mando direttamente la query");
			//queryList.add(sentence.replaceAll(" ", "%20"));
			queryList.addAll(needs);
		}
		
		log.info("querylist are "+ queryList.size()+" : "+queryList.toString());
		
		List<Hint> result = new LinkedList<Hint>();
		/* Anuska - prima cerca nella cache*/
		/*
		for (String query : queryList) 
		{
			result1.addAll(CachingManager.searchLocalBusinessDB(
							latitude, longitude, query,distance));
		}
		List<Hint> toReturn = new HintManager().filterLocation(distance, latitude, longitude, result1);
		System.out.println("Risultato ricerca in cache:"+ toReturn);
		//Se non trovo niente allora interrogo Google
		if (toReturn.isEmpty())
		{	//cerco in Google e salvo i risultati in cache
			for (String query : queryList) 
			{	// Anuska - modificato in modo che salvi i risultati in cache con la query relativa
				
				List<Hint> listToAdd = MapManager.getInstance().searchLocalBusiness(
						latitude, longitude, query);
				result2.addAll(listToAdd);
				System.out.println("for string query:"+query);
				CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
				System.out.println("inserito nel db");
			}
			//filter the result
			toReturn = new HintManager().filterLocation(distance, latitude, longitude, result2);
			System.out.println("Risultato ricerca in Google:"+ result1);
		}
		*/
		List<Hint> toReturn= new LinkedList<Hint>();
		List<Hint> toReturn1= new LinkedList<Hint>();
		List<Hint> toReturn2= new LinkedList<Hint>();
		
		for (String query : queryList) 
		{
			List<Hint> result1 = new LinkedList<Hint>(); // list of search result IN CACHE
			result1.addAll(CachingManager.searchLocalBusinessDB(
							latitude, longitude, query,distance));
			if (distance==0)
			{	//significa che non ho vincoli sulla distanza, io li pongo a 50 Km
				toReturn1 = new HintManager().filterLocation(50000, latitude, longitude, result1);
				System.out.println("distance =0 Risultato ricerca in cache:"+ toReturn1);
			}
			else
			{
				toReturn1 = new HintManager().filterLocation(distance, latitude, longitude, result1);
				System.out.println("distance <>0 Risultato ricerca in cache:"+ toReturn1);
				
			}
			
			
			//Se non trovo niente allora interrogo Google
			if (toReturn1.isEmpty())
			{	//cerco in Google e salvo i risultati in cache
				System.out.println("Interrogo google per query:"+query);
					List<Hint> result2 = new LinkedList<Hint>(); // list of search result IN GOOGLE
					List<Hint> listToAdd = new LinkedList<Hint>();
					listToAdd = MapManager.getInstance().searchLocalBusiness(
							latitude, longitude, query);
					System.out.println("for string query:"+query);
					CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
					System.out.println("inserito nel db");
			
					//filter the result
					toReturn2 = new HintManager().filterLocation(distance, latitude, longitude, listToAdd);
					System.out.println("Risultato ricerca in Google:"+ listToAdd);
					toReturn.addAll(toReturn2);
			}else toReturn.addAll(toReturn1);
		}

		return toReturn;
	}
	
	
	/* 1-6-2011 thread
	 * 
	 */
	
	
	public static List<Hint> checkLocationSingleThread(String userid, String sentence, float latitude, float longitude, int distance) {
		List<String> needs = new ArrayList<String>(); // list of user needs
		System.out.println("Sono in LocationAwareManager checkLocationSingleThread ");
		
		//mi trovo le location in cui posso soddisfare i miei needs
		String location = OntologyManager.getInstance().findLocation(userid,sentence.toLowerCase());
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		if (!location.equalsIgnoreCase(""))
		{	
			queryList.add(location);
		}
		else
		{
		
			/* current parser implementation is just splitting tasks-title into words
			 *  future improvement such as the use of keyword extraction is strongly encouraged*/
			
			needs.addAll(Arrays.asList(sentence.split(" ")));
		
			// remove duplicates by using HashSet
			HashSet<String> needsfilter = new HashSet<String>(needs);
			needs.clear();
			needs.addAll(needsfilter);
		
			/*20-5-2011
			 * Aggiunto il controllo delle location nel db(ritorna quelle votate dall'utente)
			 * @author Anuska
			 */
		
			for (String o : needs) 
			{	System.out.println("Sono in LocationAwareManager checkLocationSingle for needs:"+o);
				queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
				queryList.addAll(OntologyManager.getInstance().viewLocationForItemVoted(userid,o));
				queryList.addAll(OntologyManager.getInstance().viewLocationForActionVoted(userid,o));
			}
			System.out.println("posso trovarlo in: "+queryList);
		}
		/* Anuska
		 * se non trovo niente nell'ontologia o nel db allora cerco con
		 * la query ricevuta
		 */
		if (queryList.isEmpty())
		{	
			System.out.println("Nessuna corrispondenza:mando direttamente la query");
			//queryList.add(sentence.replaceAll(" ", "%20"));
			queryList.addAll(needs);
		}
		
		log.info("querylist are "+ queryList.size()+" : "+queryList.toString());
		
		List<Hint> result = new LinkedList<Hint>();
		
		List<Hint> toReturn= new LinkedList<Hint>();
		List<Hint> toReturn1= new LinkedList<Hint>();
		List<Hint> toReturn2= new LinkedList<Hint>();
		
		
		for (String query : queryList) 
		{
			List<Hint> result1 = new LinkedList<Hint>(); // list of search result IN CACHE
			result1.addAll(CachingManager.searchLocalBusinessDB(
							latitude, longitude, query,distance));
			if (distance==0)
			{	//significa che non ho vincoli sulla distanza, io li pongo a 50 Km
				toReturn1 = new HintManager().filterLocation(50000, latitude, longitude, result1);
				System.out.println("distance =0 Risultato ricerca in cache:"+ toReturn1);
			}
			else
			{
				toReturn1 = new HintManager().filterLocation(distance, latitude, longitude, result1);
				System.out.println("distance <>0 Risultato ricerca in cache:"+ toReturn1);
				
			}
		}	
			
		/*	//Se non trovo niente allora interrogo Google
			if (toReturn1.isEmpty())
			{	//cerco in Google e salvo i risultati in cache
				System.out.println("Interrogo google per query:"+query);
					List<Hint> result2 = new LinkedList<Hint>(); // list of search result IN GOOGLE
					List<Hint> listToAdd = new LinkedList<Hint>();
					listToAdd = MapManager.getInstance().searchLocalBusiness(
							latitude, longitude, query);
					System.out.println("for string query:"+query);
					CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
					System.out.println("inserito nel db");
			
					//filter the result
					toReturn2 = new HintManager().filterLocation(distance, latitude, longitude, listToAdd);
					System.out.println("Risultato ricerca in Google:"+ listToAdd);
					toReturn.addAll(toReturn2);
			}else toReturn.addAll(toReturn1);
		}
*/
		return toReturn;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * 13-05-2011
	 * @author Anuska
	 */
	public static List<Hint> checkLocationSingleDB(String userid, String sentence, float latitude, float longitude, int distance) {
		List<String> needs = new ArrayList<String>(); // list of user needs

		/* current parser implementation is just splitting tasks-title into words
		 *  future improvement such as the use of keyword extraction is strongly encouraged*/
		needs.addAll(Arrays.asList(sentence.split(" ")));
		
		// remove duplicates by using HashSet
		HashSet<String> needsfilter = new HashSet<String>(needs);
		needs.clear();
		needs.addAll(needsfilter);

		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		for (String o : needs) queryList.addAll(OntologyReasoner.getInstance().getSearchQuery(o));
		log.info("querylist are "+ queryList.size()+" : "+queryList.toString());
		List<Hint> result = new LinkedList<Hint>(); // list of search result
		
		//Interrogazione Database
		for (String query : queryList) {
	//		result.addAll(CachingManager.searchLocalBusinessDB(
	//				latitude, longitude, query,distance));
			result.addAll(CachingManager.searchLocalBusinessDB(
					latitude, longitude, sentence,distance));
			CachingManager.cachingListHint(userid, query, latitude, longitude, distance,result);
		}
		System.out.println("LocationAwareManager result.addAll");
		/*//Interrogazione a Google
		for (String query : queryList) 
			result.addAll(MapManager.getInstance().searchLocalBusiness(
					latitude, longitude, query));
		// filter the result
		 * 
		 */
		List<Hint> toReturn = new HintManager().filterLocation(distance, latitude, longitude, result);
		//http://localhost:8080/ephemere/anuska/location/singleDB?q=pane&lat=45.69553&lon=11.830902&dist=0
		System.out.println("LocationAwareManager filterLocation");
		// salvare nel db: FUNZIONA
		
		return toReturn;
	}
	
	public static void cachingDelete()
	{
		CachingManager.cachingDelete();
		
	}
	
	
	
	public static List<Hint> checkLocationSingleProva(float latitude, float longitude, int distance) {
		List<String> needs = new ArrayList<String>(); // list of user needs

		/* current parser implementation is just splitting tasks-title into words
		 *  future improvement such as the use of keyword extraction is strongly encouraged*/
		//needs.addAll(Arrays.asList(sentence.split(" ")));
		needs.add("pasta");
		log.info("querylist  are checkLocationSingleProva"+ needs.size()+" : "+needs.toString());
		List<Hint> result = new LinkedList<Hint>(); // list of search result
		for (String query : needs) 
			result.addAll(MapManager.getInstance().searchLocalBusiness(
					latitude, longitude, query));
		// filter the result
		List<Hint> toReturn = new HintManager().filterLocation(distance, latitude, longitude, result);
		return toReturn;
	}
	
}
