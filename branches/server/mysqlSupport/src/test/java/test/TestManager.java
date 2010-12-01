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
	
	private String user="user";
	private String passwd="dummy";
	private EventManager emanager = EventManager.getInstance();
	private TaskManager tmanager = TaskManager.getInstance();
	private String testName = "";
	
	private ArrayList<String> taskToDelete=new ArrayList<String>();
	private ArrayList<String> eventToDelete=new ArrayList<String>();
	
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	@BeforeClass
	public void initClass() {
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
	public void testLogin() {
		/*
		 * input parameters: username, password
		 * return: Loginreply object: status, session
		 */
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
		/*
		 * input parameters: userID, dueDate, startTime, endTime, location,
		 * 		title, description
		 * return: boolean
		 */
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
	public void testCreateTask() {
		/*
		 * input parameters: userID, title, notifyTimeStart, notifyTimeEnd,
		 * 		dueDate, description, priority, groupId
		 * return: boolean
		 */
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
	public void testDeleteEvent() {
		/*
		 * input parameters: userid, taskID
		 * return: boolean
		 * 
		 * the sql command is: "delete from Task where id="+taskID
		 * so first create manually a line in Task table using:
		 * INSERT INTO thesisug.Event VALUES (999998, 'user', '2010-12-01T08:00:00-08:00', '2010-12-01T08:00:00-08:00', '2010-12-01T09:00:00-10:00', '99999998');
		 */
		String eventid = "999998";
		
		log.info("Trying to delete event from the system");
		
		EventManager manager = EventManager.getInstance();
		manager.Authenticate(user, passwd);
		
		boolean result = manager.removeEvent(user, eventid);
		
		assertEquals(true, result);
	}
	
	@Test
	public void testDeleteTask() {
		/*
		 * input parameters: userid, taskID
		 * return: boolean
		 * 
		 * the sql command is: "delete from Task where id="+taskID
		 * so first create manually a line in Task table using:
		 * INSERT INTO thesisug.Task VALUES (999999, 'user', '2010-12-01T12:41:16.039Z', '06:00:16.040Z', '21:00:16.040Z', '99999999');
		 */
		String taskid = "999999";
		
		log.info("Trying to delete task from the system");
		
		TaskManager manager = TaskManager.getInstance();
		manager.Authenticate(user, passwd);
		
		boolean result = manager.removeTask(user, taskid);
		
		assertEquals(true, result);
	}
	
	@Test
	public void testupdateEvent() {
		/*
		 * input parameters: userid, SingleEvent object
		 * return: boolean
		 * 
		 * so first create manually a line in Task table using:
		 * INSERT INTO thesisug.Event VALUES (999996, 'user', '2010-12-01T08:00:00-08:00', '2010-12-01T08:00:00-08:00', '2010-12-01T09:00:00-10:00', '99999998');
		 */
		String eventid = "999996";
		
		log.info("Trying to update event in the system");
		
		EventManager manager = EventManager.getInstance();
		manager.Authenticate(user, passwd);
		
		SingleEvent newEvent = new SingleEvent(eventid, "Title after update", "2010-12-01T08:00:00-08:00", "2010-12-01T09:00:00-10:00", "2010-12-01T08:00:00-08:00", "Location after update", "Description after update", "99999996");
								
		boolean result = manager.updateEvent(user, newEvent);
		
		assertEquals(true, result);
	}
	
	@Test
	public void testupdateTask() {
		/*
		 * input parameters: userid, SingleTask object
		 * return: boolean
		 * 
		 * so first create manually a line in Task table using:
		 * INSERT INTO thesisug.Event VALUES (999996, 'user', '2010-12-01T08:00:00-08:00', '2010-12-01T08:00:00-08:00', '2010-12-01T09:00:00-10:00', '99999998');
		 */
		String taskid = "999997";
		
		log.info("Trying to update task in the system");
		
		TaskManager manager = TaskManager.getInstance();
		manager.Authenticate(user, passwd);
		//INSERT INTO thesisug.Task VALUES (999999, 'user', '2010-12-01T12:41:16.039Z', '06:00:16.040Z', '21:00:16.040Z', '99999999', '2');
		SingleTask newTask = new SingleTask(taskid, "Title after update", "06:00:16.040Z", "21:00:16.040Z", "2010-12-01T12:41:16.039Z", "Description after update", 5, "99999997", "28");
						
		boolean result = manager.updateTask(user, newTask);
		
		assertEquals(true, result);
	}

}
