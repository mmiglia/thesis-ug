package com.thesisug.tracking;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.thesisug.communication.TrackingResource;

/**
 * Implements static methods to track to a file user actions.
 * 
 * @author Alberto Servetti
 *
 */
public class ActionTracker 
{
	private static final String TAG = "thesisug - Action tracker";
	private static final String FILENAME = "tracking.xml";
	private static final String OLDFILENAME ="tracking-completed.xml";
	private static final String trackOpenedApp ="APP-OPEN";
	private static final String trackClosedApp ="APP-CLOSED";
	private static final String trackAddedContent ="CONTENT-ADD";
	private static final String trackCompletedContent = "CONTENT-COMPL";
	private static final String trackNotificationSent = "NOTIFICATION-SENT";
	private static final String trackNotificationClicked ="NOTIFICATION-CLICK";
	private static final String trackNotificationViewed = "NOTIFICATION-VIEW";
	private static final String trackNotificationDismissed = "NOTIFICATION-DISMISS";
	private static final String trackNotificationSnooze="NOTIFICATION-SNOOZE";
	private static final String trackNotificationSnoozeDeleted="NOTIFICATION-SNOOZE-DELETE";
	private static final String trackHintChosen = "HINT-CHOSEN"; 
	private static final String trackForceHintSearch="FORCE-HINT";
	private static final String trackWifiRequest="WIFI-REQ";
	private static final String trackWifiResponse="WIFI-RESP";
	private static final String trackGpsRequest="GPS-REQ";
	private static final String trackGpsResponse="GPS-RESP";
	private static final long MAXFILESIZE = 102400; //100kb
	private static Handler handler =null;
	/**
	 * Initialization of ActionTracker file.
	 * @param creationData	Date of creation of the file.
	 * @param context		Application's context.
	 */
	public static void Init(Date creationDate,Context context)
	{
		Log.i(TAG,"ActionTracker init."); 
		File file = context.getFileStreamPath(FILENAME);
		if(handler==null)
			handler = new Handler();
		if(!file.exists())
		{
			String init = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
						  "<events>"+
						  "<creationdate>"+TrackUtilities.dateFormat.format(creationDate)+"</creationdate>"+
						  "<creationtime>"+TrackUtilities.timeFormat.format(creationDate)+"</creationtime>";
			
			if(TrackUtilities.writeToFile(FILENAME, init, context))
				Log.d(TAG,"ActionTracker init success.");
			else
				Log.e(TAG,"ActionTracker init failed");
		}
		else
		{
			Log.d(TAG,"ActionTracker already initiated. Size of file: "+ file.length()); 
			if(file.length()>MAXFILESIZE)
				sendToServer(context);
		}
	};
	
	/**
	 * Checks if file is too big; If it is, send it to the server and init a new one.
	 * 	 */
	private static void checkSize(Context context)
	{
		Log.i(TAG,"checkSize"); 
		File file = context.getFileStreamPath(FILENAME);
		if(file.exists() && file.length()>MAXFILESIZE)
		{
				sendToServer(context);
		}
	}
	
	/**
	 * Tracks when main Activity starts.
	 * 
	 * @param time		The moment in which Activity is opened.
	 * @param context 	Application's context.
	 */
	public static void appOpened(Date time,Context context)
	{
		Log.i(TAG,"Tracking appOpened.");
		
		checkSize(context);
		
		String track = 
				"<event>" +
				"<eventtype>"+trackOpenedApp+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos></infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked appOpened: " + track);
		else
			Log.e(TAG,"appOpened tracking failed");
	};
	
	/**
	 * Tracks when main Activity starts.
	 * 
	 * @param time		The moment in which Activity is opened.
	 * @param context 	Application's context.
	 */
	public static void appClosed(Date time,Context context)
	{
		Log.i(TAG,"Tracking appClosed.");
		
		checkSize(context);
		
		String track = 
				"<event>" +
				"<eventtype>"+trackClosedApp+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos></infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked appClosed: " + track);
		else
			Log.e(TAG,"appOpened tracking failed");
	};
	
	/**
	 * Tracks when a search for hints is forced.
	 * @param time		The moment in which search is forced.
	 * @param location 	Location of the request.
	 * @param context	Application's context.
	 */
	public static void forceHintSearch(Date time,Location location,Context context)
	{
		Log.i(TAG,"Tracking forceHintSearch.");
		
		checkSize(context);
		
		String track = 
				"<event>" +
				"<eventtype>"+trackForceHintSearch+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>";
		if(location!=null)
			track+=
			"<location " +
			"latitude=\""+Double.toString(location.getLatitude())+"\" "+
			"longitude=\""+Double.toString(location.getLongitude())+"\" " +
			"accuracy=\""+Float.toString(location.getAccuracy())+"\" "+"/>";
		track+="</infos></event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked forceHintSearch: " + track);
		else
			Log.e(TAG,"contentAdded tracking failed.");
		
	};
	
	/**
	 * Tracks when a new content is added to the to-do list.
	 * 
	 * @param time		The moment in which content is added.	
	 * @param title		The title of the content.
	 * @param context	Application's context.
	 * @param ty		Type of content - 0 task, 1 event.
	 */
	
	public static void contentAdded(Date time, String title,Context context,int ty)
	{
		Log.i(TAG,"Tracking contentAdded.");
		
		checkSize(context);
		
		
		String type;
		if(ty==0)
			type="Task";
		else
			type="Event";
		
		String track = 
				"<event>" +
				"<eventtype>"+trackAddedContent+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA[<![CDATA["+title+"]]></title>"+
				"</infos></event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked contentAdded: " + track);
		else
			Log.e(TAG,"contentAdded tracking failed.");
		
	};
	
	/**
	 * Tracks when a new content is done.
	 * 
	 * @param time		The moment in which content is done.
	 * @param title		The title of completed content.
	 * @param context	Application context.
	 * @param ty		Type of content - 0 task, 1 event.
	 */
	public static void contentCompleted(Date time, String title,Context context,int ty)
	{
		Log.i(TAG,"Tracking contentCompleted.");
		
		checkSize(context);
		
		String type;
		if(ty==0)
			type="Task";
		else
			type="Event";
		
		String track = 
				"<event>" +
				"<eventtype>"+trackCompletedContent+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA["+title+"]]></title>"+
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME,track,context))
			Log.i(TAG,"Tracked contentCompleted: " + track);
		else
			Log.e(TAG,"contentCompleted tracking failed.");
	};
	
	/**
	 * Tracks when a notification is sent.
	 * 
	 * @param time		The moment in which notification is sent.
	 * @param title		Title of the content notified.
	 * @param context	Application context.
	 * @param ty		Type of content - 0 task, 1 event.		
	 */
	public static void notificationSent(Date time, String title,Context context,int ty)
	{
		Log.i(TAG,"Tracking notificationSent.");
		
		checkSize(context);
		
		String type;
		if(ty==0)
			type="Task"; 
		else
			type="Event";
			
		
		String track = 
				"<event>" +
				"<eventtype>"+trackNotificationSent+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA["+title+"]]></title>"+
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked notificationSent: " + track);
		else
			Log.e(TAG,"notificationSent tracking failed.");
	};
	
	/**
	 * Tracks when a notification is viewed.
	 * 
	 * @param time		The moment in which notification is clicked.
	 * @param title		Title of the content whose notification is clicked.
	 * @param context	Application context.
	 * @param ty		Type of content - 0 task, 1 event.	
	 */
	public static void notificationViewed(Date time, String title,Context context,int ty)
	{
		Log.i(TAG,"Tracking notificationViewed.");

		checkSize(context);
		
		String type;
		if(ty==0)
			type="Task";
		else
			type="Event";
		
		String track = 
				"<event>" +
				"<eventtype>"+trackNotificationViewed+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA["+title+"]]></title>"+
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked notificationViewed: " + track);
		else
			Log.e(TAG,"notificationViewed tracking failed.");
	};
	
	/**
	 * Tracks when a notification is dismissed.
	 * 
	 * @param time		The moment in which notification is clicked.
	 * @param title		Title of the content whose notification is clicked.
	 * @param context	Application context.
	 * @param ty		Type of content - 0 task, 1 event.	
	 */
	public static void notificationDismissed(Date time, String title,Context context,int ty)
	{
		Log.i(TAG,"Tracking notificationDismissed.");

		checkSize(context);
		
		String type;
		if(ty==0)
			type="Task";
		else
			type="Event";
		
		String track = 
				"<event>" +
				"<eventtype>"+trackNotificationDismissed+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA["+title+"]]></title>"+
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked notificationDismissed: " + track);
		else
			Log.e(TAG,"notificationDismissed tracking failed.");
	};
	
	/**
	 * Tracks when a notification is clicked.
	 * 
	 * @param time		The moment in which notification is clicked.
	 * @param title		Title of the content whose notification is clicked.
	 * @param context	Application context.
	 * @param ty		Type of content - 0 task, 1 event.	
	 */
	public static void notificationClicked(Date time, String title,Context context,int ty)
	{
		Log.i(TAG,"Tracking notificationClicked.");
		
		checkSize(context);
		
		String type;
		if(ty==0)
			type="Task";
		else
			type="Event";
		
		String track = 
				"<event>" +
				"<eventtype>"+trackNotificationClicked+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA["+title+"]]></title>"+
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked notificationClicked: " + track);
		else
			Log.e(TAG,"notificationClicked tracking failed.");
	};
	
	/**
	 * Tracks which hint is chosen by the user.
	 * 
	 * @param time		The moment in which hint is clicked.
	 * @param location Location of the request
	 * @param title		Title of the content whose hint is clicked.
	 * @param chosen	Title of the chosen hint.
	 * @param context	Application context.
	 */
	public static void hintChosen(Date time,Location location ,String title,String chosen,ArrayList<String> unchosen,Context context)
	{

		Log.i(TAG,"Tracking hintChosen.");
		
		checkSize(context);
		
		String track = 
				"<event>" +
				"<eventtype>"+trackHintChosen+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<title><![CDATA["+title+"]]></title>";
		if(location!=null)
			track+=
			"<location " +
			"latitude=\""+Double.toString(location.getLatitude())+"\" "+
			"longitude=\""+Double.toString(location.getLongitude())+"\" " +
			"accuracy=\""+Float.toString(location.getAccuracy())+"\" "+"/>";
		track+="<chosenhint><![CDATA["+chosen+"]]></chosenhint>";
		if(unchosen.size()>0)
		{
			track += "<unchosenhints>";
			for(String s:unchosen)
			{
				track+= "<title><![CDATA["+s+"]]></title>"; 
			}
			track += "</unchosenhints></infos></event>";
		}
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked hintChosen: " + track);
		else
			Log.e(TAG,"hintChosen tracking failed.");
		
	};
	
	/**
	 * Tracks a request to the user to activate Wifi for location fixing.
	 * @param time		Moment of the request
	 * @param location	Location of the request
	 * @param context	Application context
	 */
	public static void wifiRequest(Date time,Location location,Context context)
	{

		Log.i(TAG,"Tracking wifiRequest.");
		
		checkSize(context);
		
		
		String track = 
				"<event>" +
				"<eventtype>"+trackWifiRequest+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>";
		if(location!=null)
			track+=
			"<location " +
			"latitude=\""+Double.toString(location.getLatitude())+"\" "+
			"longitude=\""+Double.toString(location.getLongitude())+"\" " +
			"accuracy=\""+Float.toString(location.getAccuracy())+"\" "+"/>";
		track+="</infos></event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked wifiRequest: " + track);
		else
			Log.e(TAG,"hintChosen tracking failed.");
		
	};
	
	/**
	 * Tracks a request to the user to activate Gps for location fixing.
	 * @param time		Moment of the request
	 * @param location	Location of the request
	 * @param context	Application context
	 */
	public static void gpsRequest(Date time,Location location,Context context)
	{

		Log.i(TAG,"Tracking gpsRequest.");
		
		checkSize(context);
		
		String track = 
				"<event>" +
				"<eventtype>"+trackGpsRequest+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>";
		if(location!=null)
			track+=
			"<location " +
			"latitude=\""+Double.toString(location.getLatitude())+"\" "+
			"longitude=\""+Double.toString(location.getLongitude())+"\" " +
			"accuracy=\""+Float.toString(location.getAccuracy())+"\" "+"/>";
		track+="</infos></event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked gpsRequest: " + track);
		else
			Log.e(TAG,"hintChosen tracking failed.");
		
	};
	
	/**
	 * Tracks user answer to a Wifi Request for activating Wifi.
	 * @param time		Moment of the answer.
	 * @param location	Location of the answer.
	 * @param response	POSITIVE,NEGATIVE or TIMEOUT
	 * @param context	Application context.
	 */
	public static void wifiResponse(Date time,Location location,String response,Context context)
	{

		Log.i(TAG,"Tracking wifiResponse.");
		
		checkSize(context);
		
		String track = 
				"<event>" +
				"<eventtype>"+trackWifiResponse+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>";
		if(location!=null)
			track+=
			"<location " +
			"latitude=\""+Double.toString(location.getLatitude())+"\" "+
			"longitude=\""+Double.toString(location.getLongitude())+"\" " +
			"accuracy=\""+Float.toString(location.getAccuracy())+"\" "+"/>";
		
		track+=
				"<requestresponse>"+response+"</requestresponse>" +
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked wifiResponse: " + track);
		else
			Log.e(TAG,"hintChosen tracking failed.");
	};
	
	/**
	 * Tracks user answer to a Gps Request for activating Gps.
	 * @param time		Moment of the answer.
	 * @param location	Location of the answer.
	 * @param response	POSITIVE,NEGATIVE or TIMEOUT
	 * @param context	Application context.
	 */
	public static void gpsResponse(Date time,Location location,String response,Context context)
	{

		Log.i(TAG,"Tracking gpsResponse.");
		
		checkSize(context);
		
		
		String track = 
				"<event>" +
				"<eventtype>"+trackGpsResponse+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>";
		if(location!=null)
			track+=
			"<location " +
			"latitude=\""+Double.toString(location.getLatitude())+"\" "+
			"longitude=\""+Double.toString(location.getLongitude())+"\" " +
			"accuracy=\""+Float.toString(location.getAccuracy())+"\" "+"/>";
		
		track+=
				"<requestresponse>"+response+"</requestresponse>" +
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked gpsResponse: " + track);
		else
			Log.e(TAG,"hintChosen tracking failed.");
		
	};
		
	/**
	 * Track when a notification is snoozed.
	 * @param time		The moment in wich notification is snoozed.
	 * @param title		The title of the activity whose notification is snoozed.
	 * @param context	Application context
	 * @param type		Type of activity whose notification is snoozed.
	 * @param delay		Snooze time delay.
	 */
	public static void notificationSnooze(Date time, String title,Context context,String type, int delay)
	{
		
		Log.i(TAG,"Tracking notificationSnooze for "+type+": " + title+".");
		
		checkSize(context);
		
		String track = 
				"<event>" +
				"<eventtype>"+trackNotificationSnooze+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA["+title+"]]></title>"+
				"<snoozevalue>"+delay+"</snoozevalue>"+
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked notificationSnooze: " + track);
		else
			Log.e(TAG,"notificationSnooze tracking failed.");
	};
	
	/**
	 * Track when a notification snooze setting is deleted.
	 * @param time		The time when snooze setting is deleted.
	 * @param type		Type of the activity whose snooze setting is deleted.
	 * @param title		Title of the activity whose snooze setting is deleted.
	 * @param context	Application context.
	 */
	public static void notificationSnoozeDeleted(Date time,String type, String title,Context context)
	{
		Log.i(TAG,"Tracking notificationSnoozeDeleted.");
		
		checkSize(context);
				
		String track = 
				"<event>" +
				"<eventtype>"+trackNotificationSnoozeDeleted+"</eventtype>" +
				"<date>"+TrackUtilities.dateFormat.format(time)+"</date>" +
				"<time>"+TrackUtilities.timeFormat.format(time)+"</time>" +
				"<infos>"+
				"<type>"+type+"</type>"+
				"<title><![CDATA["+title+"]]></title>"+
				"</infos>" +
				"</event>";
		
		if(TrackUtilities.writeToFile(FILENAME, track, context))
			Log.i(TAG,"Tracked notificationSnoozeDeleted: " + track);
		else
			Log.e(TAG,"notificationSnoozeDeleted tracking failed");
	};
	
	/**
	 * Sends to server tracking file. A temporary old tracking file is created
	 * and a new one is initiated for future trackings.
	 * 
	 * @param context Application context.
	 */
	public static void sendToServer(Context context)
	{
		Log.i(TAG,"sendToServer.");
		
		String finish = "</events>";
		
		if(TrackUtilities.writeToFile(FILENAME, finish, context))
			Log.i(TAG,FILENAME+" closed.");
		else
			Log.i(TAG,FILENAME+" closing failed.");
		 
		//Rename closed tracking file to avoid writing while waiting to send
		File file = context.getFileStreamPath(FILENAME);
		file.renameTo(new File(context.getFilesDir(),OLDFILENAME));
		//Re-Init a new tracking file
		ActionTracker.Init(Calendar.getInstance().getTime(), context);
		TrackingResource.uploadTrack(0, OLDFILENAME, handler, context);
		
	}
	
	/**
	 * If upload is complete delete old tracking file.
	 * 
	 * @param result Success of upload.
	 * @param context Application context.
	 */
	public static void finishSave(boolean result,Context context)
	{
		Log.i(TAG,"finishSave");
		if(result)
		{
			Log.d(TAG, "Action tracking sent to server.");
			File file = context.getFileStreamPath(OLDFILENAME);
			file.delete();
		}
		else
		{
			Log.e(TAG, "Failed to send action tracking to server.");
		}
	}
}