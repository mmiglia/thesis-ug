package test;

import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import businessobject.Configuration;
import businessobject.google.MapsClient;

public class ClientTest extends TestCase {

	static {
		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SLF4JLog");
		System.setProperty("log4j.configuration", "log4j.conf");
		final Properties CONSTANTS = Configuration.getInstance().constants;
		if (CONSTANTS.containsKey("http.proxyHost")) System.setProperty("http.proxyHost", CONSTANTS.getProperty("HTTP_PROXY"));
		if (CONSTANTS.containsKey("http.proxyPort")) System.setProperty("http.proxyPort", CONSTANTS.getProperty("HTTP_PORT"));
		if (CONSTANTS.containsKey("https.proxyHost")) System.setProperty("https.proxyHost", CONSTANTS.getProperty("HTTPS_PROXY"));
		if (CONSTANTS.containsKey("https.proxyPort")) System.setProperty("https.proxyPort", CONSTANTS.getProperty("HTTPS_PORT"));
	}

	/*
	 * public void testLogin() throws Exception { Logger log =
	 * LoggerFactory.getLogger(ClientTest.class);
	 * log.info("hallo hallo Bandung"); LoginManager.login("gw", "sendiri");
	 * //RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG",
	 * "checkthesisUG"); }
	 * 
	 * public void testaddUser() throws Exception { Logger log =
	 * LoggerFactory.getLogger(ClientTest.class);
	 * log.info("hallo hallo Bandung"); RegisteredUsers.instance.addUsers("ada",
	 * "hanya"); //RegisteredUsers.instance.addUsers("pras", "hanyakamu",
	 * "thesisUG", "checkthesisUG"); }
	 */

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

	public void testMaps() throws Exception {
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		//MapsClient coba = new MapsClient();
		float lat = 45.521694f;
		float lon = -122.691806f;
		log.info("lat = " + lat + " long = " + lon);
		//List<Hint> results = coba.searchLocalBusiness(lat, lon, "coffee");
		assertNotNull(lon);
	}
	/**
	public void testTimes() throws Exception{
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		Calendar hallo = Converter.toJavaTime("09:30:10-06:00");
		log.info(hallo.toString());
	}*/

/*	private void assertValidResults(List<Hint> results) {
		assertNotNull(results);
		assertTrue(results.size() > 0);
		for (Hint o : results)
			assertValid(o);
		// for (Result r : results) log.info(r.phoneNumbers.get(0).number);
	}

	private void assertValid(Hint r) {
		System.out
				.println("--------------------------------------------------------");
		System.out.println(r);
	}*/
}
