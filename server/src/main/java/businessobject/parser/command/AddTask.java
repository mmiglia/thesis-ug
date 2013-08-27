package businessobject.parser.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.SingleTask;
import businessobject.Converter;
import businessobject.TaskManager;
import businessobject.parser.Arguments;
import businessobject.parser.Language;
import businessobject.parser.SemanticRoles;
import businessobject.parser.SemanticRoles.RoleType;
import businessobject.parser.nountype.ArbitraryObject;
import businessobject.parser.nountype.NounCalendar;

import com.clutch.dates.StringToTime;
import com.clutch.dates.StringToTimeException;


public class AddTask implements Verb{
	private final static Logger log = LoggerFactory.getLogger(AddTask.class);
	private static final Properties constants = new Properties();
	private String TAG = "AddTask - ";
	
	@Override
	public String getName() {
		return "Add Task";
	}
	
	@Override
	public boolean execute(String userID, List<Arguments> args) {
		String title="";
		String whenString="";
		System.out.println(TAG+"Argomenti passati:");
		for (Arguments ciccio : args) 
			System.out.print("Noun:"+ciccio.noun.toString()+" Content:"+ciccio.content+" Role:"+ciccio.role.toString());
		System.out.println("");
		for (Arguments a:args){
			if (a.role == SemanticRoles.RoleType.OBJECT) {
				title = a.content;
				System.out.println(TAG+"oggetto OBJECT: "+a.content);
				//break;
				continue;
			}
			if (a.role == SemanticRoles.RoleType.TIME) {
				whenString = a.content.replaceAll(" e ", ":");
				System.out.println(TAG+"oggetto TIME: "+a.content);
				//break;
				continue;
			}
//			switch (a.role){
//			case OBJECT: 
//				title = a.content;
//				System.out.println(TAG+"oggetto OBJECT: "+a.content);
//				break;
//			case TIME: 
//				whenString = a.content;
//				System.out.println(TAG+"oggetto OBJECT: "+a.content);
//				break;
//			}
		}
		System.out.println("executing add task fo user "+userID+" with title:"+title+" and time:"+whenString);
		log.info("executing add task fo user "+userID+" with title:"+title+" and time:"+whenString);
		//log.info("executing add task for user "+userID+" with title "+title);
		try{
			StringToTime start = new StringToTime(whenString);
			Calendar startTime = Calendar.getInstance();
			startTime.setTimeInMillis(start.getTime());
			
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR_OF_DAY, 6);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			String notifyTimeStart = Converter.CalendarTimetoString(now);
			now.set(Calendar.HOUR_OF_DAY, 21);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
			String notifyTimeEnd = Converter.CalendarTimetoString(now);
			String dueDate=Converter.CalendarDatetoString(startTime);
			String description="";
			int priority=2;
			String groupId="0";
//			System.out.println("createTask con user="+userID+" titolo="+title+" Tstart="+notifyTimeStart+" Tend="+notifyTimeEnd+" data="+dueDate+" descr="+description+" gruppo="+groupId);
			log.info("createTask con user="+userID+" titolo="+title+" Tstart="+notifyTimeStart+" Tend="+notifyTimeEnd+" data="+dueDate+" descr="+description+" gruppo="+groupId);
			TaskManager.getInstance().createTask(userID, title, notifyTimeStart, notifyTimeEnd, dueDate, description, priority,groupId);
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
		//System.out.println("AddTask - verbs_filename="+verbs_filename);
		List<String> v = new LinkedList<String>();
		try {
			constants.load(this.getClass().getClassLoader().getResourceAsStream(verbs_filename));
			v = Arrays.asList(constants.getProperty("task_verbs").split(","));
		} catch (IOException e) {
			System.out.println("Cannot find the verbs file");
			e.printStackTrace();
		}
		return v;
		//return Arrays.asList("i have", "remind me", "add task");
		// aggiungere i verbi in italiano
		//return Arrays.asList("devo", "ricordami", "ricorda", "aggiungi task");
	}
}
