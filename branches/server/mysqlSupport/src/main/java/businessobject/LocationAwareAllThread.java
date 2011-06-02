package businessobject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import valueobject.Hint;
import valueobject.SingleTask;
import businessobject.CachingManager;
import businessobject.HintManager;
import businessobject.MapManager;
import businessobject.OntologyManager;
import businessobject.OntologyReasoner;
import businessobject.TaskManager;

public class LocationAwareAllThread implements Runnable{
	List<String> queryList = new ArrayList<String>(); // list of inferred search query string
	String userid;
	float latitude;
	float longitude;
	int distance;
	
	public LocationAwareAllThread(String userid, float latitude, float longitude, int distance)
	{
		this.userid=userid;
		this.latitude=latitude;
		this.longitude=longitude;
		this.distance=distance;
		System.out.println("Sono nel costruttore del Thread");
		//checkLocationSingleThread(userid, sentence, latitude, longitude, distance);
	}
	
	public List<Hint> checkLocationSingleThread(String userid, float latitude, float longitude, int distance) {
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
		System.out.println("needs are "+ needs.size()+" : "+needs.toString());
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
		
		
		List<Hint> result = new LinkedList<Hint>();
		List<Hint> toReturn= new LinkedList<Hint>();
		List<Hint> toReturn1= new LinkedList<Hint>();
		List<Hint> toReturn2= new LinkedList<Hint>();
		
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
			toReturn.addAll(toReturn1);
		}
		// se ho trovato qualcosa in cache restituisco al client
		if (!toReturn.isEmpty())
		{	System.out.println("ho trovato qualcosa in cache");
			Thread t=new Thread(this,"InterrogaGoogle");
			t.start();
			return toReturn;
		}
		else
		{	//Se non trovo niente allora interrogo Google
			System.out.println("non ho trovato niente in cache");
			for (String query : queryList) 
			{
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
			}
			return toReturn;
		}
		
	}
	
	
	
	public void run()
	{
		for (String query : queryList) 
		{
			System.out.println("ho avviato il thread per salvare solo nel db");
			List<Hint> result2 = new LinkedList<Hint>(); // list of search result IN GOOGLE
			List<Hint> listToAdd = new LinkedList<Hint>();
			listToAdd = MapManager.getInstance().searchLocalBusiness(
					latitude, longitude, query);
			System.out.println("for string query:"+query);
			CachingManager.cachingListHint(userid, query, latitude, longitude, distance,listToAdd);
			System.out.println("inserito nel db");
	
		}
		
	}

}
