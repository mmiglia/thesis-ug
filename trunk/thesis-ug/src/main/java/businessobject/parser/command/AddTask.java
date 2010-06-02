package businessobject.parser.command;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleTask;
import businessobject.Converter;
import businessobject.TaskManager;
import businessobject.parser.Arguments;
import businessobject.parser.SemanticRoles;
import businessobject.parser.nountype.ArbitraryObject;
import businessobject.parser.nountype.NounCalendar;

import com.clutch.dates.StringToTime;
import com.clutch.dates.StringToTimeException;


public class AddTask implements Verb{
	private final static Logger log = LoggerFactory.getLogger(AddTask.class);
	@Override
	public String getName() {
		return "Add Task";
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
		log.info("executing add task for user "+userid+" with title "+title);
		try{
			StringToTime start = new StringToTime(whenString);
			Calendar startTime = Calendar.getInstance();
			startTime.setTimeInMillis(start.getTime());
			SingleTask toAdd = new SingleTask(title, Converter.CalendarDatetoString(startTime), "");
			TaskManager.getInstance().createTask(userid, toAdd);
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
		return Arrays.asList("i have", "remind me", "add task");
	}
}
