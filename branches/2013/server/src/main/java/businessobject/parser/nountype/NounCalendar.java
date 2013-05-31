package businessobject.parser.nountype;


import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import businessobject.parser.Suggestion;

import com.clutch.dates.StringToTime;
import com.clutch.dates.StringToTimeException;

public class NounCalendar implements Noun{
	@Override
	public List<Suggestion> getSuggestion(String text) {
		List<Suggestion> result = new LinkedList<Suggestion>();
		Date parsedDate = null;
		try {
			parsedDate = new StringToTime(text);
		}
		catch (StringToTimeException e){
			System.out.println("Error when parsing Calendar stringtotime");
		}
		if (parsedDate == null) {
			// at least return one suggestion
			Calendar now = Calendar.getInstance();
			Suggestion today = new Suggestion(now.toString(), now);
			result.add(today); 
		} else{
			Suggestion parsed = new Suggestion(parsedDate.toString(), parsedDate);
			result.add(parsed);
		}
		return result;
	}

	@Override
	public NounType getType() {
		return NounType.CALENDAR;
	}
}