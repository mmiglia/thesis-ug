package businessobject.parser.command;
import java.util.List;

import businessobject.parser.Arguments;


/**
 * Interface to any command implementation
 *
 */
public interface Verb {
	public String getName();
	public List<String> getVerbs();
	public List<Arguments> getArguments();
	public boolean execute(String userid, List<Arguments> args);
}
