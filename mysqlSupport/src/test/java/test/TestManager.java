package test;

import java.sql.Connection;
import java.util.ArrayList;
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
import dao.EventDatabase;
import dao.RegisteredUsers;
import dao.TaskDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

public class TestManager extends TestCase {
	
	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 * 
	 * This class provides a series of test for the Manager classes
	 * Test cases:
	 * TaskManager: Creation, Update, Delete
	 * EventManager: Creation, Update, Delete
	 * LoginManager: Login
	 * 
	 */
	
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
	
	private Logger log = LoggerFactory.getLogger(TestManager.class);
	
	private String user="test-user";
	private String passwd="test-passwd";
	private String testName = "";
	
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	@BeforeClass
	public void initClass() {
		/*
		 * this method provide to create 4 rows in the MySQL DB
		 * in order to fulfill the following remove and update tests
		 */
		Connection conn= (Connection) dbManager.dbConnect();
		QueryStatus qs=dbManager.startTransaction(conn);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... Event not added");
			dbManager.dbDisconnect(conn);
		}
		else {
			String insertQuery="INSERT INTO thesisug.Task VALUES (999999, 'user', '2010-12-01T12:41:16.039Z', '06:00:16.040Z', '21:00:16.040Z', '99999999', '2'); " +
				"INSERT INTO thesisug.Task VALUES (999997, 'user', '2010-12-01T12:41:16.039Z', '06:00:16.040Z', '21:00:16.040Z', '99999997', '3'); " +
				"INSERT INTO thesisug.Event VALUES (999998, 'user', '2010-12-01T08:00:00-08:00', '2010-12-01T08:00:00-08:00', '2010-12-01T09:00:00-10:00', 'Location', '99999998'); " +
				"INSERT INTO thesisug.Event VALUES (999996, 'user', '2010-12-01T08:00:00-08:00', '2010-12-01T08:00:00-08:00', '2010-12-01T09:00:00-10:00', 'Location', '99999996');";
			qs=dbManager.customQuery(conn, insertQuery);
			if(qs.execError){
				log.error(qs.explainError());
				qs.occourtedErrorException.printStackTrace();
				dbManager.rollbackTransaction(conn);
				log.error("Error during remider adding... Event not added");
				dbManager.dbDisconnect(conn);
			}
		}
		dbManager.dbDisconnect(conn);
	}
	
	@Before
	public void setUp() throws Exception {
		//EventManager manager = EventManager.getInstance();
		System.out.println("Starting "+testName+"...");
		log.info("Starting "+testName+"...");
		//emanager.Authenticate(user, passwd);
	}
	
	@After
	public void tearDown() throws Exception {
		System.out.println("End "+testName);
		log.info("End "+testName);
	}
	
	@Test
	public void testAddUser() {
		testName = "Test Add User";		
		log.info("Adding dummy account username = "+user+", password ="+passwd);
		RegisteredUsers.instance.addUsers(user, passwd);
		String id=RegisteredUsers.instance.checkMatch(user, passwd);
		log.info("Find ID for: "+user+", identified by: "+passwd+", with ID: "+id);		
		if (id.equals(null))
			assertFalse(false);
		else
			assertTrue(true);
	}	
	
	@Test
	public void testLogin() {
		testName = "Test login";		
		log.info("Trying to login with username = "+user+", password ="+passwd+"");
		LoginReply result = LoginManager.login(user, passwd);		
		log.info("Login status is " + result.status + ", session id = "
				+ result.session);		
		boolean condition=false;
		if (result.status==1 && !result.session.equals(""))
			condition = true;		
		assertTrue(condition);
	}
	
	@Test
	public void testCreateEvent() {
		testName = "Test Create Event";		
		log.info("Trying to create event in the system");		
		EventManager manager = EventManager.getInstance();
		manager.Authenticate(user, passwd);
		boolean result = manager.createEvent(user,"2010-12-01T08:00:00-08:00",
				"2010-12-01T08:00:00-08:00", "2010-12-01T09:00:00-10:00",
				"Event Location - test","Event Title - test", 
				"Event Description - test");		
		assertEquals(true, result);
	}
	
	@Test
	public void testupdateEvent() {
		String eventid = "999996";		
		testName = "Test Update Event";		
		log.info("Trying to update event in the system");		
		EventManager manager = EventManager.getInstance();
		manager.Authenticate(user, passwd);		
		SingleEvent newEvent = new SingleEvent(eventid, "Title after update", 
				"2010-12-01T08:00:00-08:00", "2010-12-01T09:00:00-10:00", 
				"2010-12-01T08:00:00-08:00", "Location after update", 
				"Description after update", "99999996");								
		boolean result = manager.updateEvent(user, newEvent);		
		assertEquals(true, result);
	}
	
	@Test
	public void testDeleteEvent() {
		String eventid = "999998";		
		testName = "Test Delete Event";		
		log.info("Trying to delete event from the system");		
		EventManager manager = EventManager.getInstance();
		manager.Authenticate(user, passwd);		
		boolean result = manager.removeEvent(user, eventid);		
		assertEquals(true, result);
	}
	
	@Test
	public void testRetrieveAllEvents() {
		testName = "Test Retrieve All Event";		
		log.info("Trying to retrieve all events from the system");		
		EventManager manager = EventManager.getInstance();
		manager.Authenticate(user, passwd);		
		List<SingleEvent> result = manager.retrieveAllEvents(user);
		log.info("Found "+ result.size() + " event for the user " + user);
		int count = 1;
		for (SingleEvent o : result) {
			log.info("Event #"+count+": "+o.title);
			count++;
		}
		if(result!=null){
			assertTrue(result.size()>0);		
		}
	}
	
	@Test
	public void testRetrieveEventToday() {
		testName = "Test Retrieve Event Today";
		log.info("Trying to retrieve events that occured today");
		EventManager manager = EventManager.getInstance();
		manager.Authenticate(user, passwd);
		List<SingleEvent> result = manager.retrieveEventToday(user);
		log.info("Found "+ result.size() + " event for the user " + user);
		int count = 1;
		for (SingleEvent o : result) {
			log.info("Event #"+count+": "+o.title);
			count++;
		}
		if(result!=null){
			assertTrue(result.size()>0);
		}
	}
	
	@Test
	public void testCreateTask() {
		testName = "Tast Create Task";		
		log.info("Trying to create Task in the system");		
		TaskManager manager = TaskManager.getInstance();
		manager.Authenticate(user, passwd);
		boolean result = manager.createTask(user,"Task Title - test",
				"08:00:00-08:00", "09:30:00-06:00", "2010-12-01T08:00:00-08:00", 
				"Task Description - test", 4, "1");		
		assertEquals(true, result);
	}
	
	@Test
	public void testUpdateTask() {
		String taskid = "999997";		
		testName = "Test Update Task";		
		log.info("Trying to update task in the system");		
		TaskManager manager = TaskManager.getInstance();
		manager.Authenticate(user, passwd);		
		SingleTask newTask = new SingleTask(taskid, "Title after update", 
				"06:00:16.040Z", "21:00:16.040Z", "2010-12-01T12:41:16.039Z", 
				"Description after update", 5, "99999997", "28");						
		boolean result = manager.updateTask(user, newTask);		
		assertEquals(true, result);
	}	
	
	@Test
	public void testDeleteTask() {
		testName = "Test Delete Task";
		String taskid = "999999";		
		testName = "Test Delete Task";		
		log.info("Trying to delete task from the system");		
		TaskManager manager = TaskManager.getInstance();
		manager.Authenticate(user, passwd);		
		boolean result = manager.removeTask(user, taskid);		
		assertEquals(true, result);
	}
	
	@Test
	public void testRetrieveAllTask () {
		testName = "Test Retrieve All Task";
		log.info("Trying to retrieve all tasks from the system");
		TaskManager manager = TaskManager.getInstance();
		List<SingleTask> result = manager.retrieveAllTask(user);
		log.info("Found "+result.size()+" task for the user "+user);
		int count = 1;
		for (SingleTask o : result) {
			log.info("Task #"+count+": "+o.title);
			count++;
		}
		if(result!=null){
			assertTrue(result.size()>0);
		}
		
	}
	
	@Test
	public void testGetFirstTasks() {
		testName = "Test Get First Task";
		log.info("Trying to get most important tasks");
		TaskManager manager = TaskManager.getInstance();
		List<SingleTask> result = manager.getFirstTasks(user);
		log.info("Found "+ result.size() + " task for the user " + user);
		int count = 1;
		for (SingleTask o : result){
			log.info("Task #"+count+": "+o.title);
			count++;
		}
		if(result!=null){
			assertTrue(result.size()>0);
		}
		
	}	

}
