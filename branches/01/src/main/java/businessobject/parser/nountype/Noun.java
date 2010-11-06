package businessobject.parser.nountype;
import java.util.List;

import businessobject.parser.Suggestion;


/**
 * Interface to implement 'NounType' in ubiquity
 *
 */
public interface Noun {
	public enum NounType {CALENDAR, LOCATION, TEXT};
	public List<Suggestion> getSuggestion(String text);
	public NounType getType();
	
}
