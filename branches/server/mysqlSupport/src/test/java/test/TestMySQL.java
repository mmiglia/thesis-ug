package test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Properties;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.LoginReply;
import valueobject.SingleEvent;
import valueobject.SingleTask;

import businessobject.Configuration;
import businessobject.Converter;
import businessobject.LoginManager;
import dao.EventDatabase;
import dao.RegisteredUsers;
import dao.TaskDatabase;

import junit.framework.TestCase;

public class TestMySQL extends TestCase {
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
	
	private String testName="";
	
	private ArrayList<String> taskToDelete=new ArrayList<String>();
	private ArrayList<String> eventToDelete=new ArrayList<String>();
	
	/*
	 * This is used to get a task to be updated or removed (the id is needed)
	 */
	private String idTask="";

	/**
	 * Called at startup of test suite
	 */
    protected void setUp() {
      System.out.println("Starting "+testName+"..."); 

     }

    /**
     * Called at the end of the execution of test suite
     */
     protected void tearDown() {
    	 System.out.println("Ending "+testName);

    	 //Delete test tasks from the database
         if(!taskToDelete.isEmpty()){
       	  for(String idTaskToDelete : taskToDelete){
   		  	TaskDatabase.deleteTask(idTaskToDelete);
       	  }
         }
         taskToDelete.clear();
         
         if(!eventToDelete.isEmpty()){
          	  for(String idEventToDelete : eventToDelete){
      		  	EventDatabase.instance.deleteEvent(idEventToDelete);
          	  }
            }
         eventToDelete.clear();
         
    	 System.out.println("End "+testName);
     }	
	
    String first="testFirstName";
    String last="testLastName";
    String mail="test@test.com";
    String userId="testUser";
    String username="testUser";
    String password="testPassword";
    String ver_code="verified_test";
	
	public void testPersonalTaskCreation() {
		testName="testPersonalTaskCreation";
		String title="testTitle";
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeStart = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeEnd = Converter.CalendarTimetoString(now);
		
		String dueDate= notifyTimeEnd;
		String description="testDescription";
		int priority=3;
		String groupId="0";
		SingleTask addedTask=TaskDatabase.instance.addTask(userId, title, notifyTimeStart, notifyTimeEnd, dueDate, description, priority, groupId);
		
		idTask=addedTask.taskID;
		
		//Added the id of the task to delete
		taskToDelete.add(idTask);
		
		assertTrue(Integer.parseInt(idTask)>0);
	}	

	public void testGroupTaskCreation() {
		testName="testGroupTaskCreation";
		String title="testTitle";
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeStart = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeEnd = Converter.CalendarTimetoString(now);
		
		String dueDate= notifyTimeEnd;
		String description="testDescription";
		int priority=3;
		String groupId="1";
		SingleTask addedTask=TaskDatabase.instance.addTask(userId, title, notifyTimeStart, notifyTimeEnd, dueDate, description, priority, groupId);
		
		idTask=addedTask.taskID;
		
		//Added the id of the task to delete
		taskToDelete.add(idTask);
		
		
		assertTrue(Integer.parseInt(idTask)>0);
	}	
	 
	public void testTaskUpdate() {
		testName="testTaskUpdate";

		String title="testTitle";
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeStart = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeEnd = Converter.CalendarTimetoString(now);
		
		String dueDate= notifyTimeEnd;
		String description="testDescription";
		int priority=3;
		String groupId="1";
		SingleTask addedTask=TaskDatabase.instance.addTask(userId, title, notifyTimeStart, notifyTimeEnd, dueDate, description, priority, groupId);
		
		idTask=addedTask.taskID;
		
		//Added the id of the task to delete
		taskToDelete.add(idTask);
		
		addedTask.title="testTitle2";
		addedTask.description="testDescription2";
		addedTask.priority=addedTask.priority+1;
		addedTask.groupId="10";
		
		
		assertTrue(TaskDatabase.instance.updateTask(idTask, addedTask));
	}		
	
	public void testTaskDelete(){
		testName="testTaskDelete";
		
		
		String title="testTitle";
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeStart = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String notifyTimeEnd = Converter.CalendarTimetoString(now);
		
		String dueDate= notifyTimeEnd;
		String description="testDescription";
		int priority=3;
		String groupId="1";
		SingleTask addedTask=TaskDatabase.instance.addTask(userId, title, notifyTimeStart, notifyTimeEnd, dueDate, description, priority, groupId);
		
		assertTrue(TaskDatabase.instance.deleteTask(addedTask.taskID));
	}

	public void testAddUser(){
		testName="testAddUser";
		RegisteredUsers.instance.addUsers(first, last, mail, username, password, ver_code);
		assertTrue(RegisteredUsers.instance.usernameExist(username));
		
	}	
	
	public void testUserLogin(){
		testName="testUserLogin";
		assertTrue(RegisteredUsers.instance.checkMatch(username, password).equals(username));
	}
	
	public void testDeleteUser(){
		testName="testDeleteUser";
		assertTrue(RegisteredUsers.instance.deleteUsers(username));		
	}
	
	public void testAddEvent(){
		testName="testAddEvent";
		String title="testTitle";
		
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		
		String startTime = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String endTime = Converter.CalendarTimetoString(now);
		
		String dueDate= endTime;
		String description="testDescription";
		String location="testLocation";


		
		SingleEvent addedEvent=EventDatabase.instance.addEvent(username, dueDate, startTime, endTime, location, title, description);

		//Added the id of the event to delete
		eventToDelete.add(addedEvent.eventID);
		
		assertTrue(Integer.parseInt(addedEvent.eventID)>0);
		
	}
	
	public void testUpdateEvent(){
		String title="testTitle";
		
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		
		String startTime = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String endTime = Converter.CalendarTimetoString(now);
		
		String dueDate= endTime;
		String description="testDescription";
		String location="testLocation";

		
		SingleEvent addedEvent=EventDatabase.instance.addEvent(username, dueDate, startTime, endTime, location, title, description);
	
		//Added the id of the event to delete
		eventToDelete.add(addedEvent.eventID);
		
		addedEvent.description="newDescription";
		addedEvent.dueDate="";
		addedEvent.endTime="";
		addedEvent.location="";
		addedEvent.startTime="";
		addedEvent.priority=0;
		addedEvent.title="";
		
		assertTrue(EventDatabase.instance.updateEvent(addedEvent.eventID, addedEvent));
		
	}
	
	public void testDeleteEvent(){
		
		testName="testDeleteEvent";
		String title="testTitle";
		
		Calendar now = Calendar.getInstance();
		now.set(Calendar.HOUR_OF_DAY, 6);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		
		String startTime = Converter.CalendarTimetoString(now);
		now.set(Calendar.HOUR_OF_DAY, 21);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.SECOND, 0);
		String endTime = Converter.CalendarTimetoString(now);
		
		String dueDate= endTime;
		String description="testDescription";
		String location="testLocation";

		
		SingleEvent addedEvent=EventDatabase.instance.addEvent(username, dueDate, startTime, endTime, location, title, description);
	
		assertTrue(EventDatabase.instance.deleteEvent(addedEvent.eventID));
		
	}
	
	

}
