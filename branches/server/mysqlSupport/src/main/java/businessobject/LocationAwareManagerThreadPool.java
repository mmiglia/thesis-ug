package businessobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.Item;
import valueobject.SingleTask;
import web.AppServletContextListener;

import businessobject.CachingManager;
import businessobject.DateUtilsNoTime;

/**
 * Check if, in the current place, there are task that can be completed. 
 * To understand the correct word to use in the search query it uses OntologyReasoner class
 * by passing to it all the words in the title of the task
*/
public class LocationAwareManagerThreadPool {
	
	
	private final static Logger log = LoggerFactory
			.getLogger(LocationAwareManager.class);
	
	//utili per vedere se è stata cancellata la cache
	public static DateUtilsNoTime date = new DateUtilsNoTime();
	public static String nowDateCanc = date.now();
	public static boolean flagCanc = false;
	
	/* 30-08-2011
	 * Il threadpool viene creato al momento dell'avvio di Tomcat 
	 * nella classe AppServletContextListener,(vedere web.xml <listener>). 
	 * Tramite il metodo AppServletContextListener.executeThread(Runnable runn)
	 * il thread da eseguire in parallelo viene sottoposto al
	 * threadpool
	 * 
	 */
	
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
		String nowDate = date.now();
		if (!nowDate.equalsIgnoreCase(nowDateCanc) || !flagCanc)
		{	System.out.println("cancello cache");
			CachingManager.cachingDelete();
		    nowDateCanc = nowDate;
		    flagCanc = true;
		}
		
    	List<String> queryList = new ArrayList<String>(); // list of inferred search query string
		int dist;
		System.out.println("Sono in LocationAwareManager checkLocationSingleThread ");
		log.info("LocationAwareManagerThreadPool.checkLocationSingle for user:"+userid+"with sentence= "+sentence);
		queryList.addAll(findLocationForSentence(userid,sentence));
				
		List<Hint> toReturn= new LinkedList<Hint>();
		List<Hint> toReturn1= new LinkedList<Hint>();
		List<Hint> toReturn2= new LinkedList<Hint>();
		
		
		for (String query : queryList) 
		{
			List<Hint> result1 = new LinkedList<Hint>(); // list of search result IN CACHE
			
			//aggiungo eventuali luoghi privati
			result1.addAll(PlacesManager.searchPrivatePlacesDB(userid,latitude,longitude,query));
			//aggiungo eventuali luoghi pubblici votati dall'utente
			result1.addAll(PlacesManager.searchPublicPlacesDB(userid,latitude,longitude,query));
			
			
			//aggiungo eventuali luoghi presenti nella cache dei risultati di Google
			result1.addAll(CachingManager.searchLocalBusinessDB(
							latitude, longitude, query,distance));
			
			toReturn1 = new HintManager().filterLocation(distance, latitude, longitude, result1);
			System.out.println("DISTANCE:"+distance+" Risultato ricerca in cache:"+ toReturn1);
			log.info("Risultato ricerca in cache for user:"+userid+" distance="+distance);
		
			toReturn.addAll(toReturn1);
		}
		
		int nRisultati=0 ;
		for (Hint tr : toReturn )
		{
			nRisultati ++; //conto il numero di risultati che ho ottenuto
		}
		System.out.println("In db ho ottenuto N= "+nRisultati+" risultati");
		
		// se ho trovato qualcosa in luoghi privati,pubblici o cache restituisco al client
		if (!toReturn.isEmpty())
		{	System.out.println("ho trovato qualcosa in cache");
			
			log.info("LocationAwareManagerThreadPool.checkLocationSingle for user:"+userid+" -> trovato risultati in cache");
			final List<String> queryListFinal = queryList;
			
			if (distance==0) 
				dist = 1000000; //1000 Km
			else
				dist = distance;
			//se ho meno di 5 risultati interrogo Google
			//e il raggio è maggiore di x metri, perchè se è inferiore
			//a x metri è probabile che ci siano pochi hint possibili
			//così facendo diminuisco le richieste
			if ((nRisultati < 5) && (dist>1000))
			{	
				//esegue il thread nel threadPool instanziato all'avvio di Tomcat
				AppServletContextListener.executeThread(new Runnable()
				{ public void run()
					{
						System.out.println("ho avviato il thread per salvare solo nel db");
						log.info("LocationAwareManagerThreadPool.checkLocationSingle for user:"+userid+"-> AVVIATO THREAD del threadpool che interroga Google");
						
						for (String q : queryListFinal) 
						{	
							List<Hint> listToAdd = new LinkedList<Hint>();
							
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
				listToAdd = MapManager.getInstance().searchLocalBusiness(
						latitude, longitude, query);
				System.out.println("for string query:"+query);
				CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
				System.out.println("inserito nel db");
		
				//filter the result
				toReturn2 = new HintManager().filterLocation(distance, latitude, longitude, listToAdd);
				System.out.println("Risultato ricerca in Google:"+ listToAdd);
				toReturn.addAll(toReturn2);
			}
			return toReturn;
		}
		
	}
	
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
		
		String nowDate = date.now();
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
		
		for (String query : queryList) 
		{
			List<Hint> result1 = new LinkedList<Hint>(); // list of search result IN CACHE
			
			//aggiungo eventuali luoghi privati
			result1.addAll(PlacesManager.searchPrivatePlacesDB(userid,latitude,longitude,query));
			//aggiungo eventuali luoghi pubblici votati dall'utente
			result1.addAll(PlacesManager.searchPublicPlacesDB(userid,latitude,longitude,query));
			
			//aggiungo eventuali luoghi presenti nella cache dei risultati di Google
	    	result1.addAll(CachingManager.searchLocalBusinessDB(
							latitude, longitude, query,distance));
	    
			toReturn1 = new HintManager().filterLocation(distance, latitude, longitude, result1);
			System.out.println("DISTANCE:"+distance+" Risultato ricerca in cache:"+ toReturn1);
			log.info("Risultato ricerca in cache for user:"+userid+" distance="+distance);
			toReturn.addAll(toReturn1);
		}
		
		int nRisultati=0 ;
		for (Hint tr : toReturn )
		{
			nRisultati ++; //conto il numero di risultati che ho ottenuto
		}
		System.out.println("In db ho ottenuto N= "+nRisultati+" risultati");
		
		// se ho trovato qualcosa in cache restituisco al client
		if (!toReturn.isEmpty())
		{	System.out.println("ho trovato qualcosa in cache");
			final List<String> queryListFinal = queryList;
			//tpe.execute(new Runnable()
			
			if (distance==0) 
				dist = 1000000; //1000 Km
			else
				dist = distance;
			//se ho meno di 5 risultati interrogo Google
			//e il raggio è maggiore di x metri, perchè se è inferiore
			//a x metri è probabile che ci siano pochi hint possibili
			//così facendo diminuisco le richieste
			if ((nRisultati < 5) && (distance>1000))
			{	
				//esegue il thread nel threadPool instanziato all'avvio di Tomcat
				AppServletContextListener.executeThread(new Runnable()
				{ public void run()
					{
						System.out.println("ho avviato il thread per salvare solo nel db");
					
						for (String q : queryListFinal) 
						{	
							List<Hint> listToAdd = new LinkedList<Hint>();
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
				listToAdd = MapManager.getInstance().searchLocalBusiness(
						latitude, longitude, query);
				System.out.println("for string query:"+query);
				CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
				System.out.println("inserito nel db");
		
				//filter the result
				toReturn2 = new HintManager().filterLocation(distance, latitude, longitude, listToAdd);
				System.out.println("Risultato ricerca in Google:"+ listToAdd);
				toReturn.addAll(toReturn2);
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
