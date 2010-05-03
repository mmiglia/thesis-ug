package test;

import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.LoginReply;
import valueobject.SingleEvent;
import valueobject.SingleTask;
import businessobject.Configuration;
import businessobject.Converter;
import businessobject.EventManager;
import businessobject.LoginManager;
import businessobject.TaskManager;
import dao.RegisteredUsers;

/**
 * Test the server methods offline, by calling directly business logics.
 * 
 */
public class OfflineTest extends TestCase{
	static {
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SLF4JLog");
		System.setProperty("log4j.configuration", "log4j.conf");
		final Properties CONSTANTS = Configuration.getInstance().constants;
		if (CONSTANTS.containsKey("http.proxyHost"))
			System.setProperty("http.proxyHost", CONSTANTS
					.getProperty("HTTP_PROXY"));
		if (CONSTANTS.containsKey("http.proxyPort"))
			System.setProperty("http.proxyPort", CONSTANTS
					.getProperty("HTTP_PORT"));
		if (CONSTANTS.containsKey("https.proxyHost"))
			System.setProperty("https.proxyHost", CONSTANTS
					.getProperty("HTTPS_PROXY"));
		if (CONSTANTS.containsKey("https.proxyPort"))
			System.setProperty("https.proxyPort", CONSTANTS
					.getProperty("HTTPS_PORT"));
	}

	@Test
	public void testaddUser() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Adding dummy account username = 'user', password ='dummy'");
		RegisteredUsers.instance.addUsers("user", "dummy");
		assertTrue(true);
	}

	@Test
	public void testLogin() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Trying to login with username = 'user', password ='dummy'");
		LoginReply result = LoginManager.login("user", "dummy");
		log.info("Login status is " + result.status + ", session id = "
				+ result.session);
		assertNotNull(result);
	}

	@Test
	public void testaddEvent() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Trying to add event to the database");
		EventManager manager = EventManager.getInstance();
		manager.Authenticate("user", "dummy"); // need to call this
		boolean result = manager.createEvents("user", "dummy",
				"2010-04-19T17:00:00-08:00", "2010-04-19T17:30:00-08:00",
				"School", "haiya");
		assertEquals(true, result);
	}

	@Test
	public void testretrieveAllEvents() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Trying to retrieve all events from the database");
		EventManager manager = EventManager.getInstance();
		manager.Authenticate("user", "dummy"); // need to call this
		List<SingleEvent> result = manager.retrieveAllEvents("user");
		for (SingleEvent o : result)
			log.info(o.title);
		assertNotNull(result);
	}

	@Test
	public void testretrieveEventToday() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Trying to retrieve events that occured today");
		EventManager manager = EventManager.getInstance();
		manager.Authenticate("user", "dummy"); // need to call this
		List<SingleEvent> result = manager.retrieveEventToday("user");
		for (SingleEvent o : result)
			log.info(o.title);
		assertNotNull(result);
	}

	@Test
	public void testaddTasks(){
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Trying to add task to the database");
		TaskManager manager = TaskManager.getInstance();
		boolean result = manager.createTask("user", "jogging",
				"09:30:10-06:00", "14:30:10-06:00",
				"2010-05-19T17:30:00-08:00", "around Genoa", 5);// need to call
																// this
		assertNotNull(result);
	}

	@Test
	public void testgetFirstTasks() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Getting most important tasks");
		TaskManager manager = TaskManager.getInstance();
		List<SingleTask> result = manager.getFirstTasks("user");
		for (SingleTask o : result) log.info (o.title);
		assertNotNull (result);
	}

	/*
	 * public void testDate() throws Exception { Logger log =
	 * LoggerFactory.getLogger(ClientTest.class); CalendarClient haha = new
	 * CalendarClient("thesisUG", "checkthesisUG"); haha.Authenticate();
	 * EventManager.getInstance().createEvents("dummy", "gaha",
	 * "2010-04-19T17:00:00-08:00", "2010-04-19T17:30:00-08:00", "School",
	 * "haiya"); //EventManager.getInstance().removeEvent("dummy",
	 * "5fde09a9-f3a3-4a0a-a5fd-4ab82d568375");
	 * //RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG",
	 * "checkthesisUG"); }
	 */

	/*
	 * public void testDate() throws Exception { Logger log =
	 * LoggerFactory.getLogger(ClientTest.class); String startDate = new
	 * String("2010-04-19T17:00:00-08:00"); int stringlength =
	 * startDate.length(); startDate=startDate.substring(0,
	 * stringlength-3)+startDate.substring(stringlength-2,stringlength);
	 * log.info(startDate); String endDate = new
	 * String("2010-04-20T17:30:00-08:00"); SimpleDateFormat pf = new
	 * SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ"); Date start =
	 * pf.parse(startDate); log.info("start "+start.toString()); }
	 */

	/*public void testMaps() throws Exception {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		// MapsClient coba = new MapsClient();
		float lat = 45.521694f;
		float lon = -122.691806f;
		log.info("lat = " + lat + " long = " + lon);
		// List<Hint> results = coba.searchLocalBusiness(lat, lon, "coffee");
		assertNotNull(lon);
	}*/

	/*public void testTimes() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		Calendar hallo = Converter.toJavaTime("00:00:08-06:00");
		log.info("hallo "+Converter.CalendarTimetoString(hallo));
		Calendar kedua = Converter.toJavaDate("2010-04-19T00:00:08-06:00");
		log.info (Converter.CalendarDatetoString(kedua));
	}*/
	 

	/*
	 * private void assertValidResults(List<Hint> results) {
	 * assertNotNull(results); assertTrue(results.size() > 0); for (Hint o :
	 * results) assertValid(o); // for (Result r : results)
	 * log.info(r.phoneNumbers.get(0).number); }
	 * 
	 * private void assertValid(Hint r) { System.out
	 * .println("--------------------------------------------------------");
	 * System.out.println(r); }
	 */
}
