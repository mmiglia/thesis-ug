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
	// final SimpleClient client = ProxyFactory.create(SimpleClient.class,
	// "http://localhost:8080/ephemere");

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

	@Test(timeout = 20000)
	public void testLogin() {
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							"http://localhost:8080/ephemere");

					LoginReply result = client.Authenticate("user", "dummy");
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

	/*private Thread createThread() {
		Thread result = new Thread() {
			public void run() {
				SimpleClient client = ProxyFactory.create(SimpleClient.class,
						"http://localhost:8080/ephemere");

				LoginReply haha = client.Authenticate("user", "dummy");
				log.info(Integer.toString(haha.status));
			};
		};
		return result;
	}*/

	@Test()
	public void testcreateEvent() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							"http://localhost:8080/ephemere");
					SingleEvent haha = new SingleEvent("permesso",
							"2010-05-10T17:00:00-08:00",
							"2010-04-12T17:30:00-08:00", "garden",
							"only black tea", 2);
					client.createEvent("user", "supposedtobeCookie", haha);
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
							"http://localhost:8080/ephemere");
					SingleTask haha = new SingleTask("buy cigarette",
							"2010-08-10T17:00:00-08:00", "in supermarket");
					client.createTask("user", "supposedtobeCookie", haha);
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
							"http://localhost:8080/ephemere");
					List<SingleEvent> result = client.getAllEvents("user",
							"cookie");
					log.info("Get All Event results :");
					for (SingleEvent o : result) {
						log.info(o.title + " " + o.description + " "
								+ o.startTime+" "+o.ID);
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
							"http://localhost:8080/ephemere");
					List<SingleTask> result = client.getAllTasks("user",
							"cookie");
					log.info("Get All Task results :");
					for (SingleTask o : result) {
						log.info(o.title + " " + o.description + " "
								+ o.dueDate+" "+o.ID);
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
							"http://localhost:8080/ephemere");
					SingleEvent newEvent = new SingleEvent("new event",
							"2010-01-03T08:00:00-08:00",
							"2010-05-22T09:00:00-08:00", "her apartment",
							"niente");
					newEvent.ID = "df53a6ed-d913-400e-908a-3035f3007a53";
					client.updateEvent("user", "cookie", newEvent);

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
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							"http://localhost:8080/ephemere");
					float latitude = 40.759011f;
					float longitude = -73.9844722f;
					try {
						sleep(Math.round(Math.random()*5000));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<Hint> result = client.checkLocation(latitude,
							longitude, 100, "user", "dummy");
					
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
	public void testInputString() {
		int i = 0;
		Thread[] clientthread = new Thread[clientnumber];
		while (i < clientnumber) {
			clientthread[i] = new Thread() {
				public void run() {
					SimpleClient client = ProxyFactory.create(
							SimpleClient.class,
							"http://localhost:8080/ephemere");
					client.input("user", "dummy",
							"remind me to have fun before friday");
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
