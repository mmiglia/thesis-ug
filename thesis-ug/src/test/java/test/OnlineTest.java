package test;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.jboss.resteasy.client.ProxyFactory;
import org.slf4j.Logger;

import valueobject.Hint;
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

	/*@Ignore
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
		SingleEvent haha = new SingleEvent("Drinking Tea", "2010-05-10T17:00:00-08:00", "2010-04-12T17:30:00-08:00",
				"garden", "only black tea", 2);
		client.createEvent("user", "supposedtobeCookie", haha);
	}
	 */	
	
	
/*	@Test
	public void testGetAllEvent() {
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
				"http://localhost:8080/ephemere");
		List<SingleEvent> result = client.getAllEvents("user", "cookie");
		for (SingleEvent o : result) {
			log.info(o.title + " " + o.description + " " + o.startTime);
		}
		assertNotNull(result);
	}
*/
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
	/* Test of between range of dates
	 * http://localhost:8080/ephemere/user/event/between/?s=2006-04-17T15:00:00-08:00&e=2012-04-17T15:00:00-08:00
	 */
	/*public void testURL(){
		log = LoggerFactory.getLogger(OnlineTest.class);
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("s", "564474"));
		params.add(new BasicNameValuePair("e", "DateTo"));
		log.info( URLEncodedUtils.format(params, "UTF-8"));
	}*/
	
	/*public void testUpdateEvent() {
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
				"http://localhost:8080/ephemere");
		SingleEvent newEvent = new SingleEvent("Thank you Jesus", "2010-01-03T08:00:00-08:00", "2010-05-22T09:00:00-08:00", "her apartment", "niente");
		newEvent.ID="165804e0-6972-41e6-a87a-1154b307d19c";
		client.updateEvent("user", "cookie", newEvent);
		
	}*/
	
	/*public void testLocationManager() {
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
				"http://localhost:8080/ephemere");
		float latitude = 40.759011f;
		float longitude = -73.9844722f;
		List<Hint> result = client.checkLocation(latitude, longitude, 100, "user", "dummy");
		assertNotNull(result);
	}*/
	
	public void testInputString(){
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
		"http://localhost:8080/ephemere");
		client.input("user", "dummy", "add event jogging at wednesday");
	}
}
