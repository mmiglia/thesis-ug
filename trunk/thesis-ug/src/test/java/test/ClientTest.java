package test;

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
	
	public void testDate() throws Exception
	{
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		CalendarClient haha = new CalendarClient("thesisUG", "checkthesisUG");
		haha.Authenticate();
		//EventManager.getInstance().createEvents("dummy", "ada2", "2010-04-19T17:00:00-08:00", "2010-04-19T17:30:00-08:00", "School", "haiya");
		EventManager.getInstance().removeEvent("dummy", "5fde09a9-f3a3-4a0a-a5fd-4ab82d568375");		
		//RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG", "checkthesisUG");
	}	

}
