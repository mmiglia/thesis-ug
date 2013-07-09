package businessobject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;

import dao.CachingDatabase;
/* Manager per salvare, recuperare ed eliminare i dati in cache
 * @author Anuska
 */

public class CachingManager {
	
			private final static Logger log = LoggerFactory.
			getLogger(OntologyManager.class);
			
			
			private static class InstanceHolder 
			{
				private static final CachingManager INSTANCE = new CachingManager();
			}

			public static CachingManager getInstance() 
			{
				return InstanceHolder.INSTANCE;
			}
			
			public static void cachingListHint(String user, String sentence, float latitude, float longitude,int distance,List<Hint> list)
			{
				System.out.println("Siamo in CachingManager");
				CachingDatabase.istance.cachingListHint(user,sentence,latitude,longitude,distance,list);
			}
			
			public static List<Hint> searchLocalBusinessDB(float latitude, float longitude,String query,int distance)
			{
				System.out.println("Siamo in CachingManager");
				return CachingDatabase.istance.searchLocalBusinessDB(latitude,longitude,query,distance);
				
			}
			//
			public static void cachingDelete()
			{
				System.out.println("Siamo in CachingManager-cachingDelete");
				CachingDatabase.istance.cachingDelete();
			}

			public static boolean isAlreadyDeleteCacheToday()
			{
				System.out.println("Siamo in CachingManager-isAlreadyDeleteCacheToday");
				return CachingDatabase.istance.isAlreadyDeleteCacheToday();
			}
			
}
