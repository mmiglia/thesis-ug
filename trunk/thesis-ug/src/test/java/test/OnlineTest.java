package test;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.LoginReply;
import valueobject.SingleEvent;
import businessobject.Configuration;

/**
 * Tests the server method online
 */
public class OnlineTest extends TestCase {
	private static final Logger log = LoggerFactory.getLogger(OnlineTest.class);
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

	@Test
	@Ignore
	public void testLogin() {
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		int i = 0;
		while (i < 100) {
			createThread().start();
			i++;
		}
		//SimpleClient client = ProxyFactory.create(SimpleClient.class,
		//		"http://localhost:8080/ephemere");

		//LoginReply result = client.Authenticate("user2", "dummy");
		//log.info(Integer.toString(result.status));
		//assertNotNull(result);
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

	@Ignore("not ready yet")
	@Test
	public void testEvent() {
		SimpleClient client = ProxyFactory.create(SimpleClient.class,
				"http://localhost:8080/ephemere");
		List<SingleEvent> result = client.getAllEvents("hell", "cookie");
		for (SingleEvent o : result) {
			log.info(o.title + " " + o.description + " " + o.startTime);
		}
		assertNotNull(result);
	}

}
