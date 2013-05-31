package test;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.LoginReply;
import valueobject.SingleEvent;
import valueobject.SingleTask;
import businessobject.Configuration;

/**
 * Tests the server method online
 */
public class OnlineTest extends TestCase {
	private static Logger log;
	int clientnumber = 1;

	static {
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SLF4JLog");
		System.setProperty("log4j.configuration", "log4j.conf");
		log = LoggerFactory.getLogger(OnlineTest.class);
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
	private String ServerURL="http://localhost:8080/ephemere-0.0.1";
	private String username="user";
	private String password="dummy";

	@Test(timeout = 20000)
	public void testLogin() {
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					System.out.println("Test Login");
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					LoginReply result = client.Authenticate(username, password);
					log.info(Integer.toString(result.status));

				};
			};
			clientthread[i].start();
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);
	}

	private Thread createThread() {
		Thread result = new Thread() {
			public void run() {
				SimpleClient client = ProxyFactory.create(SimpleClient.class,
						ServerURL);

				LoginReply haha = client.Authenticate(username, password);
				log.info(Integer.toString(haha.status));
			};
		};
		return result;
	}

	@Test()
	public void testcreateEvent() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					SingleEvent haha = new SingleEvent("10","permesso",
							"2010-05-10T17:00:00-08:00",
							"2010-04-12T17:30:00-08:00", "brignole",
							"bring passport", 2, "");
					client.createEvent(username, "cookie", haha);
				};
			};
			clientthread[i].start();
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);
	}

	@Test
	public void testcreateTask() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					//This task is not a group task
					SingleTask haha = new SingleTask("buy cigarette",
							"2010-08-10T17:00:00-08:00", "in supermarket","5","");
					client.createTask(username, "cookie", haha);
				};
			};
			clientthread[i].start();
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);

	}

	@Test
	public void testGetAllEvent() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					List<SingleEvent> result = client.getAllEvents(username,
							"cookie");
					log.info("Get All Event results :");
					for (SingleEvent o : result) {
						log.info(o.title + " " + o.description + " "
								+ o.startTime+" "+o.eventID);
					}
				};
			};
			clientthread[i].start();
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);
	}

	@Test
	public void testGetAllTasks() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					List<SingleTask> result = client.getAllTasks(username,
							"cookie");
					log.info("Get All Task results :");
					for (SingleTask o : result) {
						log.info(o.title + " " + o.description + " "
								+ o.dueDate+" "+o.taskID);
					}
				};
			};
			clientthread[i].start();
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);
	}
	/* * Test of between range of dates
	 * http://localhost:8080/ephemere/user/event/between
	 * /?s=2006-04-17T15:00:00-08:00&e=2012-04-17T15:00:00-08:00
	 */

	@Test
	public void testUpdateEvent() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					SingleEvent newEvent = new SingleEvent("new event",
							"2010-01-03T08:00:00-08:00",
							"2010-05-22T09:00:00-08:00", "some place",
							"niente");
					newEvent.eventID = "df53a6ed-d913-400e-908a-3035f3007a53";
					client.updateEvent(username, "cookie", newEvent);

				};
			};
			clientthread[i].start();
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);
	}

	@Test
	public void testLocationManager() {
		System.out.println("[INIZIO testLocationManager]");
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					float latitude = 40.759011f;
					float longitude = -73.9844722f;
					try {
						sleep(Math.round(Math.random()*5000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<Hint> result = client.checkLocation(latitude,
							longitude, 100, username, password);
					
				};
			};
			clientthread[i].start();
			
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);
		System.out.println("[FINE testLocationManager]");
	}

	@Test
	public void testInputString() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							ServerURL);
					client.input(username, password,
							"remind me to buy milk before friday");
				};
			};
			clientthread[i].start();
			i++;
		}
		for (Thread o : clientthread)
			try {
				o.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		assertTrue(true);
	}
}
