package test;

import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.LoginReply;
import valueobject.SingleEvent;
import valueobject.SingleTask;
import businessobject.Configuration;
import businessobject.Converter;
import businessobject.EventManager;
import businessobject.LoginManager;
import businessobject.TaskManager;
import businessobject.google.MapsClient;
import dao.RegisteredUsers;

/**
 * Contrary to the class name, this class is not meant
 * for Offline Testing part in the thesis.
 * It is to test the server methods offline, by calling directly business logics.
 * To test the server method by calling the REST interfaces, we use Online Testing
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

	private String first="FirstName";
	private String last="LastName";
	private String mail="user@dummy.com";
	private String user="user";
	private String pass="dummy";
	private String ver_code="verified_test";
	
	@Test
	public void testaddUser() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("TESTADDUSER");
		log.info("Adding dummy account: first name="+first+" last name="+last+" email="+mail+" username="+user+", password="+pass);
		RegisteredUsers.instance.addUsers(first, last, mail, user, pass, ver_code);
		String id=RegisteredUsers.instance.checkMatch(user, pass);
		log.info("ID per " +user + " " + pass+ id);
		assertTrue(true);
	}
	
	@Test
	public void testLogin() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Trying to login with username = "+user+", password ="+pass+"");
		LoginReply result = LoginManager.login(user, pass);
		
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

		boolean result = manager.createEvent(user,"2010-05-18T08:00:00-08:00",
				"2010-05-18T08:00:00-08:00", "2010-05-18T09:00:00-10:00",
				"her apartment","Liz Birthday", "prepare some presents");
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

		String userId=RegisteredUsers.instance.checkMatch(user, pass);
		
		TaskManager manager = TaskManager.getInstance();
		boolean result = manager.createTask(userId, "Comprare il biglietto",
				"09:30:10-06:00", "14:30:10-06:00",
				"2010-05-18T17:30:00-08:00", "Stazione Brignole", 2,"1");
		assertTrue(true);
	}

	@Test
	public void testgetFirstTasks() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		log.info("Getting most important tasks");
		String userId=RegisteredUsers.instance.checkMatch(user, pass);
		TaskManager manager = TaskManager.getInstance();
		List<SingleTask> result = manager.getFirstTasks(userId);
		log.info("Found "+ result.size() + " task for the user " + user);
		for (SingleTask o : result) log.info (o.title);
		assertNotNull (result);
	}
	

	public void testMaps() throws Exception {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		MapsClient coba = new MapsClient();
		float lat = 45.521694f;
		float lon = -122.691806f;
		log.info("lat = " + lat + " long = " + lon);
		//List<Hint> results = coba.searchLocalBusiness(lat, lon, "coffee");
		assertNotNull(lon);
	}

	public void testTimes() {
		Logger log = LoggerFactory.getLogger(OfflineTest.class);
		Calendar now = Converter.toJavaTime("00:00:08-06:00");
		log.info(Converter.CalendarTimetoString(now));
		Calendar future = Converter.toJavaDate("2010-04-19T00:00:08-06:00");
		log.info (Converter.CalendarDatetoString(future));
	}

}
