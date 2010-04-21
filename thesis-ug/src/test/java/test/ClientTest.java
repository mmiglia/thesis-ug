package test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleTask;

import junit.framework.TestCase;
import businessobject.EventManager;
import businessobject.EventSubscriber;
import businessobject.google.CalendarClient;
public class ClientTest extends TestCase
{

	static
	{
		System.getProperties().put("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "trace");
		System.getProperties().put("log4j.configuration", "log4j.conf");
		System.setProperty("http.proxyHost", "wifiproxy.unige.it");
		System.setProperty("http.proxyPort", "80");
		System.setProperty("https.proxyHost", "wifiproxy.unige.it");
		System.setProperty("https.proxyPort", "80");
		
	}
	
	/*
	public void testLogin() throws Exception
	{
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		log.info("hallo hallo Bandung");
		LoginManager.login("gw", "sendiri");
		//RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG", "checkthesisUG");
	}	
	
	public void testaddUser() throws Exception
	{
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		log.info("hallo hallo Bandung");
		RegisteredUsers.instance.addUsers("ada", "hanya");
		//RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG", "checkthesisUG");
	}*/
	
	/*public void testDate() throws Exception
	{
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		CalendarClient haha = new CalendarClient("thesisUG", "checkthesisUG");
		haha.Authenticate();
		EventManager.getInstance().createEvents("dummy", "gaha", "2010-04-19T17:00:00-08:00", "2010-04-19T17:30:00-08:00", "School", "haiya");
		//EventManager.getInstance().removeEvent("dummy", "5fde09a9-f3a3-4a0a-a5fd-4ab82d568375");		
		//RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG", "checkthesisUG");
	}*/	
	
	public void testDate() throws Exception
	{
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		String startDate = new String("2010-04-19T17:00:00-08:00");
		int stringlength = startDate.length();
		startDate=startDate.substring(0, stringlength-3)+startDate.substring(stringlength-2,stringlength);
		log.info(startDate);
		String endDate = new String("2010-04-20T17:30:00-08:00");		
		SimpleDateFormat pf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
		Date start = pf.parse(startDate);
		log.info("start "+start.toString());
	}

}
