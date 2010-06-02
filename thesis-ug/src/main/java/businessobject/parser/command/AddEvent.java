package businessobject.parser.command;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleEvent;
import businessobject.Converter;
import businessobject.EventManager;
import businessobject.parser.Arguments;
import businessobject.parser.SemanticRoles;
import businessobject.parser.nountype.ArbitraryObject;
import businessobject.parser.nountype.NounCalendar;

import com.clutch.dates.StringToTime;
import com.clutch.dates.StringToTimeException;

public class AddEvent implements Verb{
	private final static Logger log = LoggerFactory.getLogger(AddEvent.class);
	@Override
	public String getName() {
		return "Add Event";
	}
	
	@Override
	public boolean execute(String userid, List<Arguments> args) {
		String title="";
		String whenString="";
		for (Arguments a:args){
			switch (a.role){
			case OBJECT: title = a.content; break;
			case TIME: whenString = a.content; break;
			}
		}
		log.info("executing add event for user "+userid+" with title "+title);
		try{
			StringToTime start = new StringToTime(whenString);
			Calendar startTime = Calendar.getInstance();
			Calendar endTime = Calendar.getInstance();
			startTime.setTimeInMillis(start.getTime());
			endTime.setTimeInMillis(start.getTime());
			endTime.add(Calendar.DAY_OF_MONTH, 1); // add end time by one day
			SingleEvent toAdd = new SingleEvent(title, Converter.CalendarDatetoString(startTime), Converter.CalendarDatetoString(endTime), "", "");
			EventManager.getInstance().createEvent(userid, toAdd);
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
	public List<String> getVerbs() {
		return Arrays.asList("add event", "i have appointment", "add appointment");
	}

}
