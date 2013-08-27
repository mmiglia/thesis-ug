package businessobject.parser.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;
import businessobject.Converter;
import businessobject.EventManager;
import businessobject.parser.Arguments;
import businessobject.parser.Language;
import businessobject.parser.SemanticRoles;
import businessobject.parser.nountype.ArbitraryObject;
import businessobject.parser.nountype.NounCalendar;

import com.clutch.dates.StringToTime;
import com.clutch.dates.StringToTimeException;

public class AddEvent implements Verb{
	private final static Logger log = LoggerFactory.getLogger(AddEvent.class);
	private static final Properties constants = new Properties();
	private String TAG = "AddEvent - ";
	
	@Override
	public String getName() {
		return "Add Event";
	}
	
	@Override
	public boolean execute(String userid, List<Arguments> args) {
		System.out.println(TAG+"Funzione execute");
		String title="";
		String whenString="";
		for (Arguments a:args){
			switch (a.role){
			case OBJECT: 
				title = a.content; 
				System.out.println(TAG+"oggetto OBJECT");
				break;
			case TIME: 
				whenString = a.content.replaceAll(" e ", ":");
				System.out.println(TAG+"oggetto TIME");
				break;
			}
		}
		log.info("executing add event for user "+userid+" with title "+title);
		try{
			StringToTime start = new StringToTime(whenString);
			Calendar startTime = Calendar.getInstance();
			Calendar endTime = Calendar.getInstance();
			 
			startTime.setTimeInMillis(start.getTime());
			endTime.add(Calendar.DAY_OF_MONTH, 0);
			endTime.setTimeInMillis(start.getTime());
			endTime.add(Calendar.DAY_OF_MONTH, 1); // add end time by one day
			EventManager.getInstance().createEvent(userid, "", Converter.CalendarDatetoString(startTime), Converter.CalendarDatetoString(endTime), "", title, "");
			return true;
			
		}
		catch (StringToTimeException e){
			log.info("Cannot understand user supplied date");
			return false;
		}
	}

	@Override
	public List<Arguments> getArguments() {
		List<Arguments> result = new LinkedList<Arguments>();
		result.add(new Arguments(SemanticRoles.RoleType.OBJECT, new ArbitraryObject() ));
		result.add(new Arguments(SemanticRoles.RoleType.TIME, new NounCalendar()));
		return result;
	}

	@Override
	public List<String> getVerbs(Language l) {
		String verbs_filename = l.filename.replace(".", "_verbs.");
		List<String> v = new LinkedList<String>();
		try {
			constants.load(this.getClass().getClassLoader().getResourceAsStream(verbs_filename));
			v = Arrays.asList(constants.getProperty("event_verbs").split(","));
		} catch (IOException e) {
			System.out.println("Cannot find the verbs file");
			e.printStackTrace();
		}
		return v;
		//return Arrays.asList("add event", "i have appointment", "add appointment");
		// aggiungere i verbi in italiano
		//return Arrays.asList("aggiungi evento", "ho un appuntamento", "aggiungi appuntamento", "ricordami appuntamento");
	}

}
