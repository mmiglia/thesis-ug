package businessobject.parser.nountype;

import java.util.LinkedList;
import java.util.List;

import businessobject.parser.Suggestion;


public class ArbitraryObject implements Noun{
	@Override
	public List<Suggestion> getSuggestion(String text) {
		List<Suggestion> result = new LinkedList<Suggestion>();
		result.add(new Suggestion(text, text));
		return result;
	}

	@Override
	public NounType getType() {
		return NounType.TEXT;
	}
}