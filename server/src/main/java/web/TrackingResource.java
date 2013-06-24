package web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.TrackingManager;

import valueobject.SingleTask;

/**
 * 
 * Methods to handle client requests for uploading tracking files.
 * 
 * @author Alberto Servetti
 *
 */

@Path("/{username}/tracking")
public class TrackingResource 
{
	@Context HttpHeaders requestHeaders;
	
	
	private static Logger log = LoggerFactory.getLogger(TrackingResource.class);
	
	/**
	 * 
	 * Handles request for uploading an action tracking xml file.
	 * 
	 * @param userid		User ID.
	 * @param sessionid		Session ID.
	 * @param fileName		Name of the xml file.
	 * @param fileContent	Content of xml file.
	 */
	@POST
	@Path("/action")	
	@Consumes("binary/octet-stream")
	public void uploadAction(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid,@HeaderParam(value = "filename") String fileName,  String fileContent)
	{
		log.info("uploadAction request");
		
		 TrackingManager.saveActionsToFile(userid, fileContent);
		
	}
	
	/**
	 * 
	 * Handles request for uploading path tracking gpx file.
	 * 
	 * @param userid		User ID.
	 * @param sessionid		Session ID.
	 * @param fileName		Name of the gpx file.
	 * @param fileContent	Content of gpx file.
	 */
	
	@POST
	@Path("/path")	
	@Consumes("binary/octet-stream")
	public void uploadPath(@PathParam("username") String userid,
			@CookieParam("sessionid") String sessionid,@HeaderParam(value = "filename") String fileName, String fileContent)
	{
		log.info("uploadPath request");
		 
		TrackingManager.savePathToFile(userid, fileName, fileContent);
	}
	
}
