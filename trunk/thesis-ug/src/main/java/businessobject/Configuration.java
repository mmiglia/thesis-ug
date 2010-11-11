package businessobject;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class that holds all the configurations for the system.
 * All the configuration variables and values are taken from the file system.conf 
 * 
 */
public class Configuration{
	private static final Logger log = LoggerFactory.getLogger(Configuration.class);
	public static final Properties constants = new Properties();
	private static final Configuration INSTANCE = new Configuration();
	
	/**
	 * Private constructor, accessed once by singleton instance
	 */
	private Configuration(){
		try {
			constants.load(this.getClass().getClassLoader().getResourceAsStream("system.conf"));
		} catch (IOException e) {
			log.error("Error loading system configuration file");
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieval of singleton instance
	 * @return single static instance of this class
	 */
	public static Configuration getInstance(){
		return INSTANCE;
	}
}
