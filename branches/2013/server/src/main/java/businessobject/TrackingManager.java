package businessobject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackingManager 
{
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.ITALY);;
	private static Calendar calendar=Calendar.getInstance();
	private static String trackFolder = "/var/lib/tomcat7/webapps/ephemere/tracking/";
	private final static Logger log = LoggerFactory
			.getLogger(TaskManager.class);
	public static void saveActionsToFile(String userid, String fileContent)
	{
		
		 try
		 {
			 new File(trackFolder+userid+"/").mkdir();
			 new File(trackFolder+userid+"/actions/").mkdir();
			 // Create file 
			 File file = new File(trackFolder+userid+"/actions/tracking_"+dateFormat.format(calendar.getTime())+".txt");
			 file.createNewFile();
			 
			 FileWriter fstream = new FileWriter(file);
			 BufferedWriter out = new BufferedWriter(fstream);
			 out.write(fileContent);
			 //Close the output stream
			 out.close();
			 System.out.println("uploadAction done.");
	 	}
		 catch (Exception e)
		 {
			  log.error("uploadAction failed: " + e.getMessage());
			  System.err.println("Error: " + e.getMessage());
		 }
	}
	
	public static void savePathToFile(String userid, String fileName, String fileContent)
	{
		 try
		  {
			  new File(trackFolder+userid+"/").mkdir();
			  new File(trackFolder+userid+"/paths").mkdir();
			  
			  File file = new File(trackFolder+userid+"/paths/"+fileName);
			  file.createNewFile();
			  FileWriter fstream = new FileWriter(file);
			  BufferedWriter out = new BufferedWriter(fstream);
			  out.write(fileContent);
			  //Close the output stream
			  out.close();
			  System.out.println("uploadPath done.");
		  }
		  catch (Exception e)
		  {

			  log.error("uploadPath failed: " + e.getMessage());
			  System.err.println("Error: " + e.getMessage());
		  }
	}
}
