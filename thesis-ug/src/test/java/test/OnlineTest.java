package test;

import java.util.Properties;

import junit.framework.TestCase;

import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.junit.Test;
import org.slf4j.Logger;

import businessobject.Configuration;

/**
 * Tests the server method online
 */
public class OnlineTest extends TestCase {
	private static Logger log;
	// final SimpleClient client = ProxyFactory.create(SimpleClient.class,
	// "http://localhost:8080/ephemere");

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

/*	@Ignore
	@Test
	public void testLogin() {
		log = LoggerFactory.getLogger(OnlineTest.class);
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		int i = 2;
		while (i < 1) {
			createThread().start();
			i++;
		}
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
				"http://localhost:8080/ephemere");
		
		LoginReply result = client.Authenticate("user", "dummy");
		log.info("Login status is " + result.status + ", session id = "
				+ result.session);
		assertNotNull(result);		
	}

	private Thread createThread() {
		Thread result = new Thread() {
			public void run() {
				SimpleClient client = ProxyFactory.create(SimpleClient.class,
						"http://localhost:8080/ephemere");

				LoginReply haha = client.Authenticate("user", "dummy");
				log.info(Integer.toString(haha.status));
				try {
					sleep(Math.round(Math.random() * 100));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		};
		return result;
	}
	
	@Test
	public void testcreateEvent() {	
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
		"http://localhost:8080/ephemere");
		SingleEvent haha = new SingleEvent("sariwangi", "2010-05-10T17:00:00-08:00", "2010-04-12T17:30:00-08:00",
				"School", "haiya", 3);
		client.createEvent("user", "supposedtobeCookie", haha);
	}
	
	
	@Test
	public void testGetAllEvent() {
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
				"http://localhost:8080/ephemere");
		List<SingleEvent> result = client.getAllEvents("user", "cookie");
		for (SingleEvent o : result) {
			log.info(o.title + " " + o.description + " " + o.startTime);
		}
		assertNotNull(result);
	}*/

	/*@Test
	public void testCoba() {
		ApacheHttpClient4Executor clientExecutor = new ApacheHttpClient4Executor(new DefaultHttpClient());
		   SimpleClient delegate = ProxyFactory.create(SimpleClient.class, "http://localhost:8080/ephemere", clientExecutor);
		   
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
				"http://localhost:8080/ephemere");
		try {
			delegate.coba("user","haha");
			log.info(Integer.toString(response.getResponseStatus()
					.getStatusCode()));
		} catch (ClientResponseFailure failure) {
			   log.info(Integer.toString(failure.getResponse().getStatus()));
		} catch (RuntimeException e) {
		} finally {
			   System.out.println("yippie");
		}
	}*/
}
