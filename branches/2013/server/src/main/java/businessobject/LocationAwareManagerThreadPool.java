package businessobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.SingleTask;
import web.AppServletContextListener;

import businessobject.CachingManager;
import businessobject.DateUtilsNoTime;

/**
 * Check if, in the current place, there are task that can be completed. 
 * To understand the correct word to use in the search query it uses OntologyReasoner class
 * by passing to it all the words in the title of the task
*/
public class LocationAwareManagerThreadPool 
{
	
	
	private final static Logger log = LoggerFactory
			.getLogger(LocationAwareManager.class);
	
	//utili per vedere se è stata cancellata la cache
	public static DateUtilsNoTime date = new DateUtilsNoTime();
	public static String nowDateCanc = DateUtilsNoTime.now();
	public static boolean flagCanc = false;
	private static final int PRECACHINGAREA = 6; //Side of caching square area in miles. 6 miles = 10 km 
	private static final double PRECACHINGDST = 1.0/6371.0; //Distance between centres of each caching area 
	//It's unnecessary to memorize cachedQueries on Db because 
	//cache is deleted when server restart 
	//private static List<String> cachingQueries = new ArrayList<String>();
	private static HashMap<String,List<String>> cachingQueries = new HashMap<String,List<String>>();
	/* 30-08-2011
	 * Il threadpool viene creato al momento dell'avvio di Tomcat 
	 * nella classe AppServletContextListener,(vedere web.xml <listener>). 
	 * Tramite il metodo AppServletContextListener.executeThread(Runnable runn)
	 * il thread da eseguire in parallelo viene sottoposto al
	 * threadpool
	 * 
	 */
	
	private static void preCacheQueries(final List<String> queryList, final List<Hint> listToAdd,final String userid,final float latitude,final float longitude,final int distance)
	{
		List<String> nonCachingQueries = new ArrayList<String>();
		//Add all queries to cachingQueries list. In this way, I keep trace 
		//of queries I am already caching, to avoid caching two times the same
		//query
		System.out.println(queryList.size() + " QUERIES");
		for(String q:queryList)
		{

			synchronized(cachingQueries)
			{
				List<String> userCachingQueries;
				//Check if some other thread is already caching some queries for this user
				if(cachingQueries.containsKey(userid))
				{
					userCachingQueries = cachingQueries.get(userid);
				}
				else
				{
					userCachingQueries = new ArrayList<String>();
					cachingQueries.put(userid, userCachingQueries);
				}
				//Check wich queries are already being cached for this user
				if(userCachingQueries.contains(q))
				{
					System.out.println("ALREADY CACHING THIS QUERY FOR " + userid);
					continue;
				}
				//Add this query to queries I need to cache
				nonCachingQueries.add(q);
				//Set this query as caching in progress so other thread won't start caching it again.
				userCachingQueries.add(q);
			}
		}
		final List<String> nonCachingQueriesFinal = nonCachingQueries;
		if(nonCachingQueriesFinal.size()>0)
		AppServletContextListener.executeThread(new Runnable()
		{ 
			public void run()
			{
			System.out.println("PreCachingThread started");
				for(String q:nonCachingQueriesFinal)
				{
					System.out.println("Precaching query: " + q);
					float newlatitude = latitude;
					float newlongitude = longitude;
					MapManager.getInstance().searchLocalBusiness(
							latitude, longitude, q);
					for(int i = 0; i < PRECACHINGAREA; i++)
					{
						
						double dst = PRECACHINGDST;
						double brng = Math.toRadians(90);
						double lat1 = Math.toRadians(newlatitude);
						double lon1 = Math.toRadians(newlongitude);
	
						double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dst) + Math.cos(lat1)*Math.sin(dst)*Math.cos(brng) );
						double a = Math.atan2(Math.sin(brng)*Math.sin(dst)*Math.cos(lat1), Math.cos(dst)-Math.sin(lat1)*Math.sin(lat2));
						//System.out.println("a = " +  a);
						double lon2 = lon1 + a;
	
						//lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
						newlatitude=(float) Math.toDegrees(lat2);
						newlongitude=(float)Math.toDegrees(lon2);
						//System.out.println("Latitude = "+Math.toDegrees(lat2)+"\nLongitude = "+Math.toDegrees(lon2));
						listToAdd.addAll(MapManager.getInstance().searchLocalBusiness(
								newlatitude, newlongitude, q));
						float templat = newlatitude;
						float templng = newlongitude;
						for(int j = 0; j < PRECACHINGAREA; j++)
						{
							double dstv = PRECACHINGDST;
							//System.out.println(dstv);
							double brngv = Math.toRadians(0);
							double lat1v = Math.toRadians(templat);
							double lon1v = Math.toRadians(templng);
	
							double lat2v = Math.asin( Math.sin(lat1v)*Math.cos(dstv) + Math.cos(lat1v)*Math.sin(dstv)*Math.cos(brngv) );
							double av = Math.atan2(Math.sin(brngv)*Math.sin(dstv)*Math.cos(lat1v), Math.cos(dstv)-Math.sin(lat1v)*Math.sin(lat2v));
							//System.out.println("a = " +  av);
							double lon2v = lon1v + av;
	
							//lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
							templat=(float) Math.toDegrees(lat2v);
							templng=(float)Math.toDegrees(lon2v);
							//System.out.println("Latitude = "+Math.toDegrees(lat2v)+"\nLongitude = "+Math.toDegrees(lon2v));
							listToAdd.addAll(MapManager.getInstance().searchLocalBusiness(
									templat, templng, q));
						}
						templat = newlatitude;
						templng = newlongitude;
						for(int j = 0; j < PRECACHINGAREA; j++)
						{
							double dstv = PRECACHINGDST;
							//System.out.println(dstv);
							double brngv = Math.toRadians(360);
							double lat1v = Math.toRadians(templat);
							double lon1v = Math.toRadians(templng);
	
							double lat2v = Math.asin( Math.sin(lat1v)*Math.cos(dstv) + Math.cos(lat1v)*Math.sin(dstv)*Math.cos(brngv) );
							double av = Math.atan2(Math.sin(brngv)*Math.sin(dstv)*Math.cos(lat1v), Math.cos(dstv)-Math.sin(lat1v)*Math.sin(lat2v));
							//System.out.println("a = " +  av);
							double lon2v = lon1v + av;
	
							//lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
							templat=(float) Math.toDegrees(lat2v);
							templng=(float)Math.toDegrees(lon2v);
							//System.out.println("Latitude = "+Math.toDegrees(lat2v)+"\nLongitude = "+Math.toDegrees(lon2v));
							listToAdd.addAll(MapManager.getInstance().searchLocalBusiness(
									templat, templng, q));
						}
						
					}
					newlatitude = latitude;
					newlongitude = longitude;
					for(int i = 0; i < PRECACHINGAREA; i++)
					{
						
						double dst = PRECACHINGDST;
						//System.out.println(dst);
						double brng = Math.toRadians(270);
						double lat1 = Math.toRadians(newlatitude);
						double lon1 = Math.toRadians(newlongitude);
	
						double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dst) + Math.cos(lat1)*Math.sin(dst)*Math.cos(brng) );
						double a = Math.atan2(Math.sin(brng)*Math.sin(dst)*Math.cos(lat1), Math.cos(dst)-Math.sin(lat1)*Math.sin(lat2));
						//System.out.println("a = " +  a);
						double lon2 = lon1 + a;
	
						//lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
						newlatitude=(float) Math.toDegrees(lat2);
						newlongitude=(float)Math.toDegrees(lon2);
						//System.out.println("Latitude = "+Math.toDegrees(lat2)+"\nLongitude = "+Math.toDegrees(lon2));
						listToAdd.addAll(MapManager.getInstance().searchLocalBusiness(
								newlatitude, newlongitude, q));
						
						float templat = newlatitude;
						float templng = newlongitude;
						for(int j = 0; j < PRECACHINGAREA; j++)
						{
							double dstv = PRECACHINGDST;
							//System.out.println(dstv);
							double brngv = Math.toRadians(0);
							double lat1v = Math.toRadians(templat);
							double lon1v = Math.toRadians(templng);
	
							double lat2v = Math.asin( Math.sin(lat1v)*Math.cos(dstv) + Math.cos(lat1v)*Math.sin(dstv)*Math.cos(brngv) );
							double av = Math.atan2(Math.sin(brngv)*Math.sin(dstv)*Math.cos(lat1v), Math.cos(dstv)-Math.sin(lat1v)*Math.sin(lat2v));
							//System.out.println("a = " +  av);
							double lon2v = lon1v + av;
	
							//lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
							templat=(float) Math.toDegrees(lat2v);
							templng=(float)Math.toDegrees(lon2v);
							//System.out.println("Latitude = "+Math.toDegrees(lat2v)+"\nLongitude = "+Math.toDegrees(lon2v));
							listToAdd.addAll(MapManager.getInstance().searchLocalBusiness(
									templat, templng, q));
						}
						templat = newlatitude;
						templng = newlongitude;
						for(int j = 0; j < PRECACHINGAREA; j++)
						{
							double dstv = PRECACHINGDST;
							//System.out.println(dstv);
							double brngv = Math.toRadians(360);
							double lat1v = Math.toRadians(templat);
							double lon1v = Math.toRadians(templng);
	
							double lat2v = Math.asin( Math.sin(lat1v)*Math.cos(dstv) + Math.cos(lat1v)*Math.sin(dstv)*Math.cos(brngv) );
							double av = Math.atan2(Math.sin(brngv)*Math.sin(dstv)*Math.cos(lat1v), Math.cos(dstv)-Math.sin(lat1v)*Math.sin(lat2v));
							//System.out.println("a = " +  av);
							double lon2v = lon1v + av;
	
							//lon2 = (lon2+ 3*Math.PI) % (2*Math.PI) - Math.PI;
							templat=(float) Math.toDegrees(lat2v);
							templng=(float)Math.toDegrees(lon2v);
							//System.out.println("Latitude = "+Math.toDegrees(lat2v)+"\nLongitude = "+Math.toDegrees(lon2v));
							listToAdd.addAll(MapManager.getInstance().searchLocalBusiness(
									templat, templng, q));
						}
					}
					System.out.println("for string query:"+q);
					CachingManager.cachingListHint(userid, q, latitude, longitude, distance,listToAdd);
					
					System.out.println("inserito nel db");
					
					
					System.out.println("Finshed precaching query: " + q);
				}
				//Remove all caching queries together 
				for(String q:nonCachingQueriesFinal)
				{
					synchronized(cachingQueries)
					{
						//Caching of this query has finished
						cachingQueries.remove(q);
					}
				}
			}
		});
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
	public static List<Hint> checkLocationSingle(final String userid,final String sentence,final float latitude,final float longitude,final int distance)
	{	
		String nowDate = DateUtilsNoTime.now();
		if (!nowDate.equalsIgnoreCase(nowDateCanc) || !flagCanc)
		{	System.out.println("cancello cache");
			CachingManager.cachingDelete();
		    nowDateCanc = nowDate;
		    flagCanc = true;
		}
		
		
		
    	List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		List<String> queryToCache = new ArrayList<String>();
    	int dist;
		System.out.println("Sono in LocationAwareManager checkLocationSingleThread ");
		log.info("LocationAwareManagerThreadPool.checkLocationSingle for user:"+userid+"with sentence= "+sentence);
		queryList.addAll(findLocationForSentence(userid,sentence));
	
		List<Hint> toReturn= new LinkedList<Hint>();
		List<Hint> toReturn2= new LinkedList<Hint>();
		
		List<Hint> result1 = new LinkedList<Hint>(); // list of search result in CACHE and in DB
		for (String query : queryList) 
		{
			//aggiungo eventuali luoghi privati
			result1.addAll(PlacesManager.searchPrivatePlacesDB(userid,latitude,longitude,query));
			//aggiungo eventuali luoghi pubblici votati dall'utente
			result1.addAll(PlacesManager.searchPublicPlacesDB(userid,latitude,longitude,query));
			//aggiungo eventuali luoghi presenti nella cache dei risultati di Google
			List<Hint> resultsInDB = CachingManager.searchLocalBusinessDB(latitude, longitude, query,distance);
			if(resultsInDB.size() == 0 )
				//This query has not been already cached
				queryToCache.add(query);
			else
				result1.addAll(resultsInDB);
		}
		
		//filter the result by distance
		if (!result1.isEmpty())
		{
			toReturn.addAll(new HintManager().filterLocation(distance, latitude, longitude, result1));
		}	
		
		//count the number of result that i have found
		int nRisultati=toReturn.size();
		/*for (Hint tr : toReturn )
		{
			nRisultati ++; //conto il numero di risultati che ho ottenuto
		}*/
		System.out.println("In db ho ottenuto N= "+nRisultati+" risultati");
		
		// se ho trovato qualcosa in luoghi privati,pubblici o cache restituisco al client
		if (nRisultati>0)
		{	
			System.out.println("ho trovato qualcosa in cache");
			
			log.info("LocationAwareManagerThreadPool.checkLocationSingle for user:"+userid+" -> trovato risultati in cache");
				
			if (distance==0) 
				dist = 1000000; //1000 Km
			else
				dist = distance;
			//Se la cache nn è stata riempita con tutte le query di interesse
			//ritorno una risposta temporanea
			if(!queryToCache.isEmpty())
			{
				System.out.println("Ci sono delle query ancora da cashare!!");
				for (String query : queryList) 
				{
					List<Hint> listToAdd = new LinkedList<Hint>();
					//creo qui dentro la lista dato che devo salvare i risultati in cache,
					//e non devo risalvare i risultati dell'iterazione precedente
					
					listToAdd = MapManager.getInstance().searchLocalBusiness(
							latitude, longitude, query);
					//Se sto facendo il pre-caching non metto questa risposta temporanea in cache.
					toReturn2.addAll(listToAdd);
				}
				
				int filterDistance = 0;
				if(distance > 150)
					filterDistance = 150;
				else
					filterDistance = distance;
				
				//filter the result by distance
				if (!toReturn2.isEmpty())
				{
					
					toReturn.addAll(new HintManager().filterLocation(filterDistance, latitude, longitude, toReturn2));
						
				}	
				//If area is resized respect client request, add a dummy hint at the end of the list
				//indicating the effective size of the response
				Hint dummyHint = new Hint();
				dummyHint.title="searchRadius";
				dummyHint.searchRadius = Integer.toString(filterDistance);
				toReturn.add(dummyHint);
				
				//Faccio partire il precaching delle query mancanti
				preCacheQueries(queryToCache, new ArrayList<Hint>(),userid,latitude,longitude,distance);
				
				return toReturn;
			}
		
			//se ho meno di 5 risultati e il raggio di ricerca è maggiore di 500 metri
			//Probabilmente la cache non è completa perché ci si aspetta che ci siano più hint
			//in un area così vasta.
			//Allora ritorno un risultato temporaneo e faccio un altro pre-caching
			if (((nRisultati < 5) && (dist>500)))
			{	
				
				preCacheQueries(queryList, new ArrayList<Hint>(),userid,latitude,longitude,distance);
				Hint dummyHint = new Hint();
				dummyHint.title="searchRadius";
				dummyHint.searchRadius="1";//In this way I'm sure that client will ask server again
				toReturn.add(dummyHint);
				return toReturn;
			}
			else
			{
				//At the moment max precaching area is 10 km
				if(distance>10000)
				{
					Hint dummyHint = new Hint();
					dummyHint.title="searchRadius";
					dummyHint.searchRadius="10000";
					toReturn.add(dummyHint);
				}
				return toReturn;
			}
			
		}
		else
		{	
			System.out.println("non ho trovato niente in cache");
			//Se non trovo niente allora devo fare precaching
			//Ritorno intanto una risposta temporanea
			for (String query : queryList) 
			{
				List<Hint> listToAdd = new LinkedList<Hint>();
				//creo qui dentro la lista dato che devo salvare i risultati in cache,
				//e non devo risalvare i risultati dell'iterazione precedente
				
				listToAdd = MapManager.getInstance().searchLocalBusiness(
						latitude, longitude, query);
				//Se sto facendo il pre-caching non metto questa risposta temporanea in cache.
				toReturn2.addAll(listToAdd);
			}
			
			int filterDistance = 0;
			if(distance > 150)
				filterDistance = 150;
			else
				filterDistance = distance;
			
			//filter the result by distance
			if (!toReturn2.isEmpty())
			{
				
				toReturn.addAll(new HintManager().filterLocation(filterDistance, latitude, longitude, toReturn2));
					
			}	
			//If area is resized respect client request, add a dummy hint at the end of the list
			//indicating the effective size of the response
			Hint dummyHint = new Hint();
			dummyHint.title="searchRadius";
			dummyHint.searchRadius = Integer.toString(filterDistance);
			toReturn.add(dummyHint);
			
			//Faccio partire il precaching
			preCacheQueries(queryList, new ArrayList<Hint>(),userid,latitude,longitude,distance);
			
			return toReturn;
		}
		
	}
	
	/*final float newlatitudefinal = newlatitude;
	final float newlongitudefinal = newlongitude;
	final String q = query;
	
	
	AppServletContextListener.executeThread(new Runnable()
	{ public void run()
		{
			System.out.println("ho avviato il thread per salvare solo nel db");
			log.info("LocationAwareManagerThreadPool.checkLocationSingle for user:"+userid+"-> AVVIATO THREAD del threadpool che interroga Google");
			
				List<Hint> listToAdd = new LinkedList<Hint>();
				//creo qui dentro la lista dato che devo salvare i risultati in cache,
				//e non devo risalvare i risultati dell'iterazione precedente
				
				listToAdd = MapManager.getInstance().searchLocalBusiness(
						newlatitudefinal, newlongitudefinal, q);
				System.out.println("for string query:"+q);
				CachingManager.cachingListHint(userid, q, latitude, longitude, distance,listToAdd);
				System.out.println("inserito nel db");
			
		}	
	});
	*/
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
	public static List<Hint> checkLocationAll(final String userid,final float latitude,final float longitude,final int distance)
	{// get all user tasks
		
		String nowDate = DateUtilsNoTime.now();
		if (!nowDate.equalsIgnoreCase(nowDateCanc) || !flagCanc)
		{	System.out.println("cancello cache");
			CachingManager.cachingDelete();
		    nowDateCanc = nowDate;
		    flagCanc = true;
		}
		log.info("LocationAwareManagerThreadPool.checkLocationAll for user:"+userid);
		
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		int dist;
		queryList.addAll(findLocationForSentenceAll(userid));
		
		List<Hint> toReturn= new LinkedList<Hint>();
		List<Hint> toReturn1= new LinkedList<Hint>();
		List<Hint> toReturn2= new LinkedList<Hint>();
		
		List<Hint> result1 = new LinkedList<Hint>(); // list of search result IN CACHE
		for (String query : queryList) 
		{
			//aggiungo eventuali luoghi privati
			result1.addAll(PlacesManager.searchPrivatePlacesDB(userid,latitude,longitude,query));
			//aggiungo eventuali luoghi pubblici votati dall'utente
			result1.addAll(PlacesManager.searchPublicPlacesDB(userid,latitude,longitude,query));
			
			//aggiungo eventuali luoghi presenti nella cache dei risultati di Google
	    	result1.addAll(CachingManager.searchLocalBusinessDB(
							latitude, longitude, query,distance));
	   	}
		
		//filter the result by distance
		if (!result1.isEmpty())
		{
			toReturn.addAll(new HintManager().filterLocation(distance, latitude, longitude, result1));
		}
		//count the number of resulta that i have found
		int nRisultati=toReturn.size();
		/*for (Hint tr : toReturn )
		{
			nRisultati ++; //conto il numero di risultati che ho ottenuto
		}*/
		System.out.println("In db ho ottenuto N= "+nRisultati+" risultati");
		
		// se ho trovato qualcosa in cache restituisco al client
		if (!toReturn.isEmpty())
		{	
			System.out.println("ho trovato qualcosa in cache");
			final List<String> queryListFinal = queryList;
			
			if (distance==0) 
				dist = 1000000; //1000 Km
			else
				dist = distance;
			
			//se ho meno di 5 risultati interrogo Google
			//e il raggio è maggiore di x metri, perchè se è inferiore
			//a x metri è probabile che ci siano pochi hint possibili
			//così facendo diminuisco le richieste
			if ( ((nRisultati < 5) && (dist>500)) )
			{	
				//esegue il thread nel threadPool instanziato all'avvio di Tomcat
				AppServletContextListener.executeThread(new Runnable()
				{ 
					public void run()
					{
						System.out.println("ho avviato il thread per salvare solo nel db");
					
						for (String q : queryListFinal) 
						{	
							List<Hint> listToAdd = new LinkedList<Hint>();
							//creo qui dentro la lista dato che devo salvare i risultati in cache,
							//e non devo risalvare i risultati dell'iterazione precedente
							
							listToAdd = MapManager.getInstance().searchLocalBusiness(
									latitude, longitude, q);
							System.out.println("for string query:"+q);
							CachingManager.cachingListHint(userid, q, latitude, longitude, distance,listToAdd);
							System.out.println("inserito nel db");
						}
					}	
				});
			}
		return toReturn;
		}
		else
		{	//Se non trovo niente allora interrogo Google
			System.out.println("non ho trovato niente in cache");
			for (String query : queryList) 
			{
				List<Hint> listToAdd = new LinkedList<Hint>();
				//creo qui dentro la lista dato che devo salvare i risultati in cache,
				//e non devo risalvare i risultati dell'iterazione precedente
				
				listToAdd = MapManager.getInstance().searchLocalBusiness(
						latitude, longitude, query);
				
				System.out.println("for string query:"+query);
				CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
				System.out.println("inserito nel db");
		
				toReturn2.addAll(listToAdd);
			}
			
			//filter the result by distance
			if (!toReturn2.isEmpty())
			{
				toReturn.addAll(new HintManager().filterLocation(distance, latitude, longitude, toReturn2));
			}	
			return toReturn;
		}
		
		
	}

	public static List<String> findLocationForSentence(String userid,String sentence)
	{
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		
		List<String> needs = new ArrayList<String>(); // list of user needs
		//mi trovo le location in cui posso soddisfare i miei needs
		String location = OntologyManager.getInstance().findLocation(userid,sentence.toLowerCase());
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
			queryList.addAll(needs);
			//queryList.add(sentence);
		}
		// remove duplicates by using HashSet
		HashSet<String> queryListfilter = new HashSet<String>(queryList);
		queryList.clear();
		queryList.addAll(queryListfilter);
		return queryList;
	}
	
	public static List<String> findLocationForSentenceAll(String userid)
	{
		List<SingleTask> tasks= TaskManager.getInstance().retrieveAllTask(userid);
		List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		
		for (SingleTask sentence : tasks)
		{
			List<String> needs = new ArrayList<String>(); // list of user needs
			//mi trovo le location in cui posso soddisfare i miei needs
			String location = OntologyManager.getInstance().findLocation(userid,sentence.title.toLowerCase());
			if (!location.equalsIgnoreCase(""))
			{	
				queryList.add(location);
			}
			else
			{
		
				/* current parser implementation is just splitting tasks-title into words
				 *  future improvement such as the use of keyword extraction is strongly encouraged*/
			
				needs.addAll(Arrays.asList(sentence.title.split(" ")));
		
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
				queryList.addAll(needs);
				//queryList.add(sentence.title);
			}
		}
		// remove duplicates by using HashSet
		HashSet<String> queryListfilter = new HashSet<String>(queryList);
		queryList.clear();
		queryList.addAll(queryListfilter);
		return queryList;
	}
	
}
