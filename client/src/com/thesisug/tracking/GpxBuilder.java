package com.thesisug.tracking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.thesisug.communication.TrackingResource;
/**
 * 
 * Implements methods to handle construction of .gpx xml files for path tracking.
 * 
 * @author Alberto Servetti
 * @
 */
public class GpxBuilder 
{
	private static final String TAG = "thesisug - Gpx File Builder";
	private Context context;
	private String gpxString;
	private Date lastGpxFile;
	private Calendar calendar;
	private Handler handler = new Handler();
	public GpxBuilder(Date data,Context context)
	{
		Log.i(TAG,"New GpxBuilder");
		
		this.context = context;
		calendar = Calendar.getInstance();
		gpxString="";
		lastGpxFile = new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).getTime();
		
		File file = context.getFileStreamPath(TrackUtilities.dateFormat.format(data)+".gpx");
		
		if(file.exists()) //If "today" track exists, I check for previous unclosed files.
			checkUnclosedFiles();
	}
	/**
	 * Checks for unclosed .gpx files. Useful when phone is turned on. 
	 *
	 */
	private void checkUnclosedFiles()
	{
		Log.d(TAG,"Checking unclosed gpx files.");
		File directory = context.getFilesDir();
		String[] files = directory.list();
		for(int i=0;i<files.length;i++)
		{
			if(files[i].contains(".gpx"))
			{
				Log.d(TAG,files[i]+" was not closed.");
				Date today = new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).getTime();
				if(!files[i].equals(TrackUtilities.dateFormat.format(today)+".gpx"))
				{
					String tmp="</trkseg></trk></gpx>";
					
					if(TrackUtilities.writeToFile(files[i], tmp, context))
					{
						Log.d(TAG,files[i] +" closed. Starting upload.");
						//Start a thread to upload track to server
						TrackingResource.uploadTrack(1, files[i], handler, context);
					}
					else
					{
						Log.e(TAG,"Failed to close " + files[i]);
					}
					
				}
			}
		}
	}
	
	/**
	 * Closes previous day .gpx path opened tags.
	 * 
	 * @param date Date of the gpx file to close.
	 */
	public void closeFile(Date date)
	{
		String FILENAME;
		//if date is null close last gpx file
				
		FILENAME = TrackUtilities.dateFormat.format(date) + ".gpx";
		
		File file = context.getFileStreamPath(FILENAME);
		if(file.exists())
		{
			
			gpxString="</trkseg></trk></gpx>";
			
			if(TrackUtilities.writeToFile(FILENAME, gpxString, context))
			{
				Log.d(TAG,FILENAME +" closed. Starting upload.");
				//Start a thread to upload track to server
				TrackingResource.uploadTrack(1, FILENAME, handler, context);
			}
			else
			{
				Log.e(TAG,"Failed to close " + FILENAME);
			}
			
		}
	}
	
	/**
	 * Start a new track segment. Called when provider changes. 
	 */
	public void newtrkSeg(Date date)
	{
		String FILENAME = TrackUtilities.dateFormat.format(date) + ".gpx";
		
		File file = context.getFileStreamPath(FILENAME);
		
		if(file.exists())
		{
			
			gpxString="</trkseg><trkseg>";
			
			if(TrackUtilities.writeToFile(FILENAME, gpxString, context))
			{
				Log.d(TAG,"newtrkSeg success.");
			}
			else
			{
				Log.e(TAG,"newtrkSeg failure.");
			}
			
		}	
	}
	
	/**
	 * Append a new point to .gpx track.
	 * 
	 * @param newLocation	the new Location to be tracked.
	 * @param newLocationDate	Date of track.
	 * @param newTrkSeg should be true when a new track segment starts.
	 */
	public void append(Location newLocation,Date newLocationDate)
	{
		String creationData = TrackUtilities.dateFormat.format(newLocationDate);
		String creationTime = TrackUtilities.timeFormat.format(newLocationDate);
		//Obtain FILENAME from newLocationDate
		String FILENAME = creationData + ".gpx";
		
		//File Output Stream mode.
		int mode;	
		
		File file = context.getFileStreamPath(FILENAME);
		
		//Check if "today" .gpx file already exists.
		if(!file.exists())
		{
			//check if a new day started to close last gpx file
    		Date today = new GregorianCalendar(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).getTime();
    		if(today.after(lastGpxFile))
    		{
    			closeFile(lastGpxFile);
    			lastGpxFile=today;
    		}
			
			Log.i(TAG,"Gpxfile: "+FILENAME+ "does not exist.");
			
			
			gpxString="<?xml version=\"1.0\"?>"+
						"<gpx "+
						"version=\"1.0\" "+
						"creator=\"thesisug\" "+
						"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
						"xmlns=\"http://www.topografix.com/GPX/1/0\" "+
						"xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\" >"+
						"<metadata>" +
						"<name>"+"thesusug-"+creationData+"</name>"+
						"<desc>"+"Path followed by user during the day."+"</desc>"+
						"<time>"+creationData+" "+creationTime+"</time>" +
						"</metadata>"+
						"<trk>"+
						"<name>"+"trk-"+creationData+"</name>"
						+"<trkseg>"; 
			mode = Context.MODE_PRIVATE;
		}
		else
		{			
			mode = Context.MODE_APPEND;
		}
		
		Date locDate = new Date(newLocation.getTime()); 
		
		gpxString+=
					"<trkpt lat=\""+
					Double.toString(newLocation.getLatitude())+"\" "+
					"lon=\""+Double.toString(newLocation.getLongitude())+"\">"+
					"<ele>"+Double.toString(newLocation.getAltitude())+"</ele>"+
					"<time>"+TrackUtilities.timeFormat.format(locDate)+"</time>"+
					"<name>Point-"+Integer.toString(newLocation.hashCode())+"</name>"+
					"<desc>"+"Provider: "+newLocation.getProvider()+"</desc>"+
					"<pdop>"+Float.toString(newLocation.getAccuracy())+"</pdop>"+
					"</trkpt>";
				
			if(TrackUtilities.writeToFile(FILENAME, gpxString, context))
			{
				Log.i(TAG,"Point: \n"+ gpxString + " \n added to " + FILENAME+".");
			}
			else
			{
				Log.e(TAG,"Point: \n"+ gpxString + " \n was not to " + FILENAME+".");
			}
		}
	
	public String getGpxString(Date gpxFileDate)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.ITALY);
		String date = dateFormat.format(gpxFileDate);
		String FILENAME = date + ".gpx";
		File file = context.getFileStreamPath(FILENAME);
		String ret = null;
		if(file.exists())
		{
			try 
			{
			
				FileInputStream fis = context.openFileInput(FILENAME);
				StringBuffer fileContent = new StringBuffer("");

				byte[] buffer = new byte[1024];

				while (fis.read(buffer) != -1) 
				{
				    fileContent.append(new String(buffer));
				}
				ret=fileContent.toString();
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
		}
		Log.i(TAG,ret);
		return ret;
	}
	
	public static void finishSave(String fileName,boolean result,Context context)
	{
		if(result)
		{
			Log.d(TAG, fileName +" was successfully uploaded. Now it's going to be deleted.");
			if(context.getFileStreamPath(fileName).delete())
				Log.d(TAG, fileName + " successfully deleted.");
			else
				Log.e(TAG,fileName +" deleting failure.");
		}
		
	}
}
