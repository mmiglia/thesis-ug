package test;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.TestCase;
import BusinessObject.LoginManager;
import DAO.RegisteredUsers;
public class ClientTest extends TestCase
{

	static
	{
		System.getProperties().put("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "trace");
		System.getProperties().put("log4j.configuration", "log4j.conf");
	}
	
	
	public void testNewsByZipCode() throws Exception
	{
		Logger log = LoggerFactory.getLogger(ClientTest.class);
		log.info("hallo hallo Bandung");
		LoginManager.login("gw", "sendiri");
		//RegisteredUsers.instance.addUsers("pras", "hanyakamu", "thesisUG", "checkthesisUG");
	}	

}
