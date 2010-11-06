package businessobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.parser.Language;
import businessobject.parser.Parser;
import businessobject.parser.command.AddEvent;
import businessobject.parser.command.AddTask;

public class ParserManager {
	private final static Logger log = LoggerFactory.getLogger(ParserManager.class);
	private final static String LANGUAGE_FILE = "en.lang";
	private Parser mainparser;
	private boolean initialized = false;
	
	public boolean inputQuery(String userid, String command){
		initialize();
		return mainparser.startParse(userid, command);
	}
	
	private void initialize(){
		log.info("initializing");
		if (initialized) return;
		mainparser = new Parser();
		log.info(LANGUAGE_FILE);
		mainparser.setLanguage(new Language(LANGUAGE_FILE));
		mainparser.createCommand(new AddTask());
		mainparser.createCommand(new AddEvent());
		initialized = true;
	}
}
