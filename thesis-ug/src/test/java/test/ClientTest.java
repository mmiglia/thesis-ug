package test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleTask;

import junit.framework.TestCase;
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
		CalendarClient haha = new CalendarClient();
		//RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG", "checkthesisUG");
	}	

}
