package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import businessobject.LocationAwareAllThread;
import businessobject.LocationAwareManager;
import businessobject.LocationAwareThread;
import businessobject.LocationAwareManagerThreadPool;
import businessobject.OntologyManager;


/**
 * Responsible for getting the right task/event near user location/time.
 * This class uses mainly method of LocationAwareManager class
 */
@Path("/{username}/location")
public class ContextListener {
	private static Logger log = LoggerFactory.getLogger(ContextListener.class);

	/**
	 * 
	 * This method will report those task that can be completed near the
	 * location for ALL user tasks
	 * 
	 * @param latitude
	 *            latitude location from GPS sensor
	 * @param longitude
	 *            longitude location from GPS sensor
	 * @param userid
	 *            user id of the user
	 * @param sessionid
	 *            session token acquired by login
	 * @return list of tasks that can be completed nearby
	 */
	@GET
	@Path("/all")
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationAll(@QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Receive ALL context from user " + userid + " from location "
				+ latitude + ":" + longitude);
		//LocationAwareAllThread locT= new LocationAwareAllThread(userid,  latitude, longitude, distance);	
		//return locT.checkLocationAllThread(userid, latitude, longitude, distance);
		
		//return LocationAwareManager.checkLocationAll(userid, latitude, longitude, distance);
	
		return LocationAwareManagerThreadPool.checkLocationAll(userid, latitude, longitude, distance);
		
	}

	/**
	 * 
	 * This method will report those task that can be completed near the
	 * location for specific task string
	 * 
	 * @param latitude
	 *            latitude location from GPS sensor
	 * @param longitude
	 *            longitude location from GPS sensor
	 * @param userid
	 *            user id of the user
	 * @param sessionid
	 *            session token acquired by login
	 * @return list of tasks that can be completed nearby
	 */
	@GET
	@Path("/single")
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationSingle(@QueryParam("q")String sentence, @QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request single context from user " + userid + " sentence "+sentence+" from location "
				+ latitude + ":" + longitude);
		//LocationAwareThread locT= new LocationAwareThread(userid, sentence, latitude, longitude, distance);	
		//return locT.checkLocationSingleThread(userid, sentence, latitude, longitude, distance);
		
		//return LocationAwareManager.checkLocationSingle(userid, sentence, latitude, longitude, distance);
		return LocationAwareManagerThreadPool.checkLocationSingle(userid, sentence, latitude, longitude, distance);
	}	
	
	@GET
	@Path("/cachingDelete")	
	public  static void  cachingDelete(@PathParam("username") String userID)
	{
		LocationAwareManager.cachingDelete();
	
	
	}
	@GET
	@Path("/singleDB")
	//@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationSingleDB(@QueryParam("q")String sentence, @QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request single context from user " + userid + " sentence "+sentence+" from location "
				+ latitude + ":" + longitude);
		// recupero i dati dal DB: FUNZIONA
		//List<Hint> list= LocationAwareManager.checkLocationSingleDB(userid, sentence, latitude, longitude, distance);
		
		//List<Hint> list= LocationAwareManager.checkLocationSingle(userid, sentence, latitude, longitude, distance);
		
		// salvare nel db: FUNZIONA
		//CachingManager.cachingListHint(userid, sentence, latitude, longitude, distance,list);
		//return list ;
		return LocationAwareManager.checkLocationSingle(userid, sentence, latitude, longitude, distance);
	}
	
	@GET
	@Path("/singleT")
	//@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationSingleThread(@QueryParam("q")String sentence, @QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request single context from user " + userid + " sentence "+sentence+" from location "
				+ latitude + ":" + longitude);
		LocationAwareThread locT= new LocationAwareThread(userid, sentence, latitude, longitude, distance);	
		return locT.checkLocationSingleThread(userid, sentence, latitude, longitude, distance);
		//return LocationAwareManager.checkLocationSingle(userid, sentence, latitude, longitude, distance);
	}
	
	
	
	//(Mirco)creo un'altra risorsa per vedere cosa restituise google maps con "pasta"
	@GET
	@Path("/singleProva")
	@Produces("application/xml")
	public List<Hint> checkLocationSingle(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		//log.info("Request single context from user " + userid + " sentence "+sentence+" from location "
				//+ latitude + ":" + longitude);
		float latitude=4578405;
		float longitude=1187243;
		int distance=150;
		
		return LocationAwareManager.checkLocationSingleProva(latitude, longitude, distance);
	}	
	
	//prova threadpool
	@GET
	@Path("/singleThreadPool")
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> checkLocationSingleThreadPool(@QueryParam("q")String sentence, @QueryParam("lat") float latitude, @QueryParam("lon") float longitude, @QueryParam("dist") int distance,
			@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request single context from user " + userid + " sentence "+sentence+" from location "
				+ latitude + ":" + longitude);
		//LocationAwareThread locT= new LocationAwareThread(userid, sentence, latitude, longitude, distance);	
		//return locT.checkLocationSingleThread(userid, sentence, latitude, longitude, distance);
		return LocationAwareManager.checkLocationSingleTP(userid, sentence, latitude, longitude, distance);
		}	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
