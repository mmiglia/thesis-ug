package businessobject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration{
	private final static Logger log = LoggerFactory.getLogger(Configuration.class);
	public static final Properties constants = new Properties();
	
	private Configuration(){
		try {
			constants.load(this.getClass().getClassLoader().getResourceAsStream("system.conf"));
		} catch (IOException e) {
			log.error("Error loading system configuration file");
			e.printStackTrace();
		}
	}
	
	public static Configuration getInstance(){
		return InstanceHolder.INSTANCE;
	}
	
	private static class InstanceHolder { 
	     private static final Configuration INSTANCE = new Configuration();
	}
}
