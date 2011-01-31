package businessobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.parser.Language;
import businessobject.parser.Parser;
import businessobject.parser.command.AddEvent;
import businessobject.parser.command.AddTask;

/**
 * This class is used to call the underlying parser system.
 * All classes that compose the parser system are in businessobject.parser package 
 * 
 *
 */


public class ParserManager {
	private final static Logger log = LoggerFactory.getLogger(ParserManager.class);
	private final static String LANGUAGE_FILE = "en.lang";
	private Parser mainparser;
	private boolean initialized = false;
	private Language lang;
	
	//public boolean inputQuery(String userid, String command){
	public boolean inputQuery(String userid, String command, String language){
		//initialize();
		initialize(language);
		return mainparser.startParse(userid, command, lang);
	}
	
	//private void initialize(){
	private void initialize(String language){
		log.info("initializing ParserManager");
		if (initialized) return;
		mainparser = new Parser();
		//log.info(LANGUAGE_FILE);		
		log.info("Language file: "+language);
		//mainparser.setLanguage(new Language(LANGUAGE_FILE));
		lang = new Language(language);
		mainparser.setLanguage(lang);
		mainparser.createCommand(new AddTask());
		mainparser.createCommand(new AddEvent());
		initialized = true;
	}
	
	/*
		public static void main(String[] args){
		 
		
			ParserManager parser = new ParserManager();
			
			String inputString="now add task buy milk before 22:50";		
			
			System.out.println("END:"+parser.inputQuery("guido",inputString));
		}
	*/
}
