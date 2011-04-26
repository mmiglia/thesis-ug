package businessobject;

import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import valueobject.Reminder.GPSLocation;
import valueobject.SingleTask;
import dao.OntologyDatabase;

public class OntologyManager {
	
	private static class InstanceHolder {
		private static final OntologyManager INSTANCE = new OntologyManager();
	}

	public static OntologyManager getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public boolean addItemInLocation(String user, String item,
			String location) 
	{
		System.out.println("OntologyManager - addItemInLocation: '"+user+"', '"+item+"', '"+location+"'");
		OntologyDatabase.addItemInLocation(user, item,location);
		
		return true;
	}

}
