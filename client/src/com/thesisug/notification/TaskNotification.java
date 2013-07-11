package com.thesisug.notification;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thesisug.R;
import com.thesisug.caching.Area;
import com.thesisug.caching.CachingDbManager;
import com.thesisug.communication.ContextResource;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.location.CustomLocationManager;
import com.thesisug.tracking.ActionTracker;
import com.thesisug.ui.HintList;
import com.thesisug.ui.NotificationSnooze;
import com.thesisug.ui.Todo;
import com.thesisug.ui.accessibility.Morse;
import com.thesisug.widget.ExampleAppWidgetProvider;
//import android.content.Context;
//import android.net.Uri;
//import android.provider.MediaStore.Audio;
//import java.util.Random;
//import java.lang.Math.*;

/**
 * This class use also OnSharedPreferenceChangeListener to catch if the queryDistance preference is changed
 * @author jaxer
 *
 */ 
public class TaskNotification extends Service implements LocationListener,OnSharedPreferenceChangeListener{
	private static final String TAG = "thesisug - TaskNotification";
	
	 // variable which controls the notification thread 
    private static ConditionVariable condvar, downloadlock;
    private static Object stopThreadObject = new Object();
    private static Object hintsFoundSync = new Object();
    private Thread downloadTaskThread;
    private List<SingleTask> tasks;
    private static Handler handler=new Handler();
    //private static LocationManager lm;
    private static NotificationManager notificationManager;
    private static SharedPreferences userSettings;
    public static Location userLocation, lastPosition;
    //private RemoteViews contentView;
    //private double lastdelayquery, delayzerovelocity;
    private float minUpdateDistance=0;
    private static Criteria criteria;
   
    
    //private static boolean canChangeLocation=true;
    //private static boolean checkMinDistance=true;
    
    //Used to prevent that userLocation became null before it has used form the notificationThread
    private static Object userLocationChangeLock=new Object();
    
    private static int PAUSE=10000;
    
    private static Calendar calendar;
   
    private static boolean runningThread;
    private Thread notifyingThread;
    private static int hintsFound = 0;
    private static CustomLocationManager customLocationManager;
	private static Context context;
	private static class InstanceHolder 
	{ 
		private static TaskNotification INSTANCE ;
	}
	
	public static TaskNotification getInstance() 
	{
		if(InstanceHolder.INSTANCE == null)
		{
			Log.d(TAG,"Instance null");
			InstanceHolder.INSTANCE = new TaskNotification();
		}
		else
			Log.d(TAG,"Instance not null");
		return InstanceHolder.INSTANCE;
		
	}
    /**
     * This method call the requestLocationUpdates of the LocationManager for the GPS_PROVIDER and the NETWORK_PROVIDER
     * every time it get the minDistance value from the user settings so it has to be called whenever these settings change
     */
	/*
    public void registerToGetLocationUpdates()
    {
    	Log.d(TAG, "QueryPeriod update registering for location udpates..");
    	/*minUpdateDistance = Float.parseFloat(userSettings.getString("queryperiod", "100")); 
    	
    	if(lm==null && !getLocationService()){
    		Log.e(TAG,"Cannot execute registerToGetLocationUpdates because LocationManager is null");
    		return;
    	}
    	
    	customLocationManager.requestLocationUpdates();
    	//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, minUpdateDistance, TaskNotification.getInstance());    	
    	//if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    	//	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) 0, minUpdateDistance, TaskNotification.getInstance());
    	Log.d(TAG, "QueryPeriod update registered!");
    }    */
	
	 @Override
	    public void onCreate() 
	    {
	    	Log.d(TAG, "Creation of TaskNotification");
	    	//contentView = new RemoteViews(getPackageName(), R.layout.notification);
	    	context = getApplicationContext();
	    	InstanceHolder.INSTANCE = this;

			SnoozeHandler.Init(this);
	    	notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
	    	
	    	//lm = (LocationManager) getSystemService(LOCATION_SERVICE);
	    	
	    	//Log.d(TAG,"lm="+lm);
	   	
	    	userSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

	    	userSettings.registerOnSharedPreferenceChangeListener(this);
	    	customLocationManager = new CustomLocationManager("-TaskNotification",TaskNotification.getInstance(),userSettings,getApplicationContext());
	    	//registerToGetLocationUpdates();
	    	
	    	//lm1.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, new GPSListener1());
	    	notifyingThread = new Thread(null, mainthread, "NotifyingService");
	    	//Thread gpsNotificatorThread = new Thread(null, gpsthread, "NotifyingGPSChange");
	        condvar = new ConditionVariable(false);
	        downloadlock = new ConditionVariable(false);
	        Log.d(TAG, "All condvar created");
	        //Setting criteria to choose the locationProvider
	        criteria = new Criteria();
	    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
	    	runningThread = true;
	        notifyingThread.start();
	        Log.i(TAG, "notifyingThread started with id:"+notifyingThread.getId());
	        //gpsNotificatorThread.start();
	        //Log.i(TAG, "gpsNotificatorThread has id:"+gpsNotificatorThread.getId());
	        
	        calendar = Calendar.getInstance();
	        CachingDbManager.Init(TaskNotification.this);

	       
	    } 
	 	
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) 
	    {
	        // We want this service to continue running until it is explicitly
	        // stopped, so return sticky.
	    	Log.i(TAG, "Task Notification service is started");
	        return START_STICKY;
	    }
	    
	    @Override
	    public void onDestroy() 
	    {
	    	Log.i(TAG, "Task Notification service is stopped");
	        // Stop the thread from generating further notifications
	        condvar.open();
	        runningThread = false;
	        SnoozeHandler.unregister();
	        SnoozeHandler.saveToFile();
	        synchronized(stopThreadObject)
	        {
	        	stopThreadObject.notify();
	        }
	        
	        customLocationManager.onDestroy();
	       /* activityRecognitionRemover.removeUpdates(activityRecognitionRequester.getRequestPendingIntent());
	        activityRecognitionRequester.getRequestPendingIntent().cancel();*/
	        // Tell the user we stopped.
	        //Toast.makeText(this, R.string.task_notification_stopped, Toast.LENGTH_SHORT).show();
	    }
	
    /**
     * This function is used for each change of the location provider status to get the best one between
     * all active providers 
     */
	
	private boolean updatePosition(Location newLocation)
	{
		Log.i(TAG,"updatePosition " + Thread.currentThread().getId());
    	
    	synchronized(userLocationChangeLock)
    	{
    		Log.d(TAG,"Unlocked.");
	    	//New location already provided?
	    	if(newLocation!=null)
	    	{
	    		
	    		lastPosition=userLocation;
	    		userLocation=newLocation;
	    		return true;
	    	}
    	
		
			//Getting last known location
			userLocation = customLocationManager.getLastKnownPosition();
			if(userLocation==null)
			{
				return false;
			}
		}
		
		return true;
    	
	}
	//With new approach of CustomLocationManager this method is never called.
    private boolean updateProviderAndPosition(String reason,Location newLocation)
    {
    	Log.i(TAG,"updateProviderAndPosition");
    	
    	synchronized(userLocationChangeLock)
    	{
    		Log.d(TAG,"Unlocked.");
	    	//New location already provided?
	    	if(newLocation!=null)
	    	{
	    		
	    		lastPosition=userLocation;
	    		userLocation=newLocation;
	    		return true;
	    	}
    	}
    	
		//Change provider if there's one active
    	Log.d(TAG,"UpdatingProviderAndPosition because:"+reason);
    	/*
    	if(lm==null && !getLocationService()){
    		Log.e(TAG,"Cannot execute registerToGetLocationUpdates because LocationManager and LocationService are null");
    		return false;
    	}
    	*/
    	/*
    	//if(customLocationManager.checkLocationProviderNotNull())
    	if(!customLocationManager.updateProvider())
    		return false;
		locationProvider=lm.getBestProvider(criteria,true);
		if(locationProvider==null ){
			notifyNoLocationProvider();
			return false;
		}
		*/
		synchronized(userLocationChangeLock)
		{
			//Getting last known location
			//userLocation = lm.getLastKnownLocation(locationProvider);	
			customLocationManager.getLastKnownPosition();
			if(userLocation==null)
			{
				return false;
			}
		}
		
		return true;
		//Log.i(TAG, "Current user location: "+userLocation.getLatitude()+" - "+ userLocation.getLongitude());
    }
    
    /*
     * Try to get a new Location service
    
    private boolean getLocationService()
    {
    	lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    	return lm!=null?true:false;
    }
     */
   
    
    /*	//TODO Maybe deprecated
    private Runnable gpsthread = new Runnable() {
    
    	public void run() {
    		Log.i(TAG, "gpsThread started");
    		Looper.prepare();
    		lastdelayquery=999999999;
    		delayzerovelocity=2.5;
    		while (true) {
    			//Looper.prepare();
    			int mindist = Integer.parseInt(usersettings.getString("queryperiod", "100"));
    			//Log.i(TAG, "gpsthread - Min Distance Query is "+mindist);
    			Criteria crit = new Criteria();
				crit.setAccuracy(Criteria.ACCURACY_FINE);
				String pr = lm1.getBestProvider(crit, false);
				//Log.i(TAG, "gpsthread - provider: "+pr);
				position = lm1.getLastKnownLocation(pr);
				if (position==null) {
					//Log.i(TAG, "gpsthread - Waiting for the location signal");
					condvargps1.block(10000);
					continue;
				}
				double dist = calculateDistance(position, lastposition);
				//if (lastposition!=null)
				//	Log.i(TAG, "gpsthread - lastposition: LAT "+lastposition.getLatitude()+" LONG "+lastposition.getLongitude());
				//Log.i(TAG, "gpsthread - Position: LAT "+position.getLatitude()+" LONG "+position.getLongitude()+" distance since lastposition: "+dist);
				// terminal moved of dist in time lastdelayquery
				double velocity;
				if (lastdelayquery==999999999)
					velocity=0;
				else
					velocity=dist/lastdelayquery;
				double delayquery = 60;
				if (velocity==0) {
					delayquery = delayzerovelocity*2;
					if (delayquery>80)
						delayquery=90;
					delayzerovelocity = delayquery;
					lastdelayquery = delayquery;
					// dealayzerovelocity grows 2x faster (5-10-20-40-80sec) up to a max value of 1'30"
					//Log.i(TAG, "gpsthread - Terminal has stopped. Delay: "+delayquery);
				}
				else {
					// the next query will be beyond the mindist distance
					delayquery = mindist/velocity;
					delayquery = Math.ceil(delayquery);
					lastdelayquery = delayquery;
					//this means that the terminal is moving, so set the delayzerovelocity to the start value
					delayzerovelocity = 2.5;
					//Log.i(TAG, "gpsthread - Terminal is moving. Velocity: "+velocity+" Delay: "+delayquery);
				}
				
				lastposition = position;
				if (dist>mindist) {
					//Log.i(TAG, "gpsthread - mainthread waking up");
					//condvargps.open();
				}
				condvargps1.block((long)delayquery*1000);
    		}
    	}
    };
     */
    
    private double calculateDistance(Location location1, Location location2) {
    	if (location1==null || location2==null)
    		return 0;
    	else {
	    	double radlat1 = Math.PI*location1.getLatitude()/180;
	    	double radlon1 = Math.PI*location1.getLongitude()/180;
	    	double radlat2 = Math.PI*location2.getLatitude()/180;
	    	double radlon2 = Math.PI*location2.getLongitude()/180;
	    	double radius = 6372795.477598; // mt
	    	double phi = Math.abs(radlon1-radlon2);
	    	// p = acos((sen(radlat1)*sen(radlat2))+(cos(radlat1)*cos(radlat2)*cos(phi)))
	    	double p = Math.acos((Math.sin(radlat1)*Math.sin(radlat2))+(Math.cos(radlat1)*Math.cos(radlat2)*Math.cos(phi)));
	    	double distance = p * radius;
	    	return distance; // mt
    	}
    }
    
    private Runnable mainthread = new Runnable() 
    {
        public void run() 
        {
        	while (runningThread)
        	{
        		// get preference on query period, return default 5 min if not set
        		//int delay = Integer.parseInt(usersettings.getString("queryperiod", "300")) * 1000;
        		minUpdateDistance = Integer.parseInt(userSettings.getString("queryperiod", "100"));
        		if(minUpdateDistance==0)
        		{
        			try 
        			{
        				Log.i(TAG, "No notification required (minUpdateDistance="+minUpdateDistance+") sleeping for "+PAUSE+" milliseconds");
						Thread.sleep(PAUSE);
					} 
        			catch (InterruptedException e) 
        			{
						e.printStackTrace();
					}
        		}
				//Log.i(TAG, "mainthread - distance query is "+delay+" mt");
	    		Log.d(TAG, "mainthread is going to block on "+stopThreadObject.hashCode());
	    		//condvargps.block();
	    		try 
	    		{
	    			synchronized(stopThreadObject)
	    			{
						stopThreadObject.wait();
	    			}
				} 
	    		catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
	    		
	    		if(!runningThread)
	    		{
	    			Log.d(TAG, "Mainthread stopping.");
	    			return;
	    		}
	    		Log.d(TAG, "Starting hint search after unlock of the thread");
	    		
	    		//if (condvargps.block(delay)) break;
	    		// get preference on distance, return default 0 (dont filter distance) if not set
	    		int maxDistance = (int)customLocationManager.GetMaxHintDistance();
	    		Log.d(TAG, "minUpdateDistance="+customLocationManager.GetMinUpdateDistance());
	    		
	    		List<Thread> threads = new LinkedList<Thread>();
	    		Log.i(TAG,"TaskNotification thread blocking.");
	    		synchronized(userLocationChangeLock)
	    		{
	    			Log.i(TAG,"TaskNotification thread unlocked.");
	        		if (userLocation == null)
	        		{
	        			Log.d(TAG, "userLocation == null -> going to sleep again");	
	        			continue;
	        		}
	        		
	        		Log.d(TAG, "We got a location!"+userLocation.getLatitude()+"-"+userLocation.getLongitude());
	        		
	        		Log.e(TAG,"No check if actual real distance is more than minDistance");
	        
	        		//asynchronous operation to download thread
	        		Log.d(TAG,"Retreiving first tasks.." + Thread.currentThread().getId());
	        		downloadTaskThread = TaskResource.getFirstTask(handler, TaskNotification.this);
	        			        		
	        		//block execution to make it synchronous
	        		Log.d(TAG,"Blocking execution to make it synchronous.");
	        		downloadlock.block();
	        		Log.d(TAG,"Going to do checkLocationSingle for each task START");
	        		downloadlock.close();
	        		if(tasks==null) 
	        		{
	        			Log.d(TAG,"Error retrieving tasks, going to sleep again");
	        			continue;
	        		}
	        		if(tasks.size()==0)
	        		{ 
	        			//customLocationManager.setHints(0);
	        			Log.d(TAG,"No task retreived, going to sleep again");
	        			customLocationManager.NoTasks();
	        			continue;
	        		} 
	        		else
	        		{ 
	        			Log.d(TAG,"Retreived "+tasks.size()+" task, checking location..");
	        		}
	        		for (SingleTask o : tasks)
	        		{
	        			//if user has chose not to be reminded for this task
	        			if (!userSettings.getBoolean(o.title, true)) 
	        				continue; 
	        			//check if task has been snoozed
	        			if(SnoozeHandler.checkIfTaskIsSnoozed(o.title))
	        				continue;
	        			// dispatch thread to get hints
	        			
	        			int hintsDistance = maxDistance;
	        			Log.d(TAG,Integer.toString(hintsDistance));
	        			if(!customLocationManager.isAccuracyOk())
	        			{
	        				
	        				hintsDistance+=(int)userLocation.getAccuracy();
	        			}
	        			Log.d(TAG,"Check location for "+o.title +" at distance "+hintsDistance);
    					threads.add(ContextResource.checkLocationSingle(o.title, o.priority,
						Float.valueOf(Double.toString(userLocation.getLatitude())),
						Float.valueOf(Double.toString(userLocation.getLongitude())),
						hintsDistance, handler, TaskNotification.this));
	        		
						customLocationManager.NewTaskHintsSearch();
					}
	        		Log.d(TAG,"Going to do checkLocationSingle for each task DONE");
	    		}
        	
	    		
			}
        }
    }; 
    
    public void afterTaskLoaded (List<SingleTask> result)
    {
    	Log.d(TAG,"afterTaskLoaded");
    	tasks = result;
    	if(tasks != null)
    	{
    		Log.d(TAG,"tasks: "+tasks.size());
    	}
    	else
    		Log.d(TAG,"tasks null");
    	downloadlock.open();
    }
    
    public void notifyNoLocationProvider()
    {
    	Log.d(TAG,"notifyNoLocationProvider start"); 	
    	String message="no location provider";
    	Log.d(TAG,message);
    	/*Notification notificationNoProvider = new Notification(R.drawable.icon, message, System.currentTimeMillis());
    	Intent notificationIntent = new Intent();
    	PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
    	notificationNoProvider.setLatestEventInfo(this,message,message, pendingIntent);
    	notificationManager.notify(message.hashCode(), notificationNoProvider);
    	 */
    }
    public void afterHintsAcquiredCacheUpdate(Area area,String sentence,List<Hint> result, int priority) 
    {
		Log.i(TAG,"afterHintsAcquiredCacheUpdate");
		if(result==null)
		{
			Log.e(TAG,"Update failed for area: lat "+area.lat+" lng "+area.lng +" radius "+area.rad+" .");
		}
		else
		{
			Log.d(TAG,"Going to update area: lat "+area.lat+" lng "+area.lng +" radius "+area.rad+" for "+ sentence +" .");
			CachingDbManager.updateArea(area,sentence,result);
		}
		
		
	}
    public void afterHintsAcquiredFromCache(String sentence, List<Hint> result, int priority)
    {
    	Log.i(TAG,"afterHintsAcquiredFromCache");
    	customLocationManager.TaskHintsSearchFinished(sentence,result,priority);
    	
    	if(result==null)
    	{
    		Log.e(TAG,"No results.");
    	}
    	else
	    	synchronized(hintsFoundSync)
	    	{
	    		hintsFound += result.size();
	    		
	    	}
    	
    	//If last search for hints returned 
    	if(!customLocationManager.MoreHintsSearch())
    	{
    		Log.d(TAG,"Last task checked.");
    		customLocationManager.SetHints(hintsFound);
    		hintsFound = 0;
	    	customLocationManager.EvaluateAccuracy();
    	}
    }
    
    public void afterHintsAcquired (String sentence, List<Hint> result,Area area, int priority)
    {
    	Log.i(TAG,"afterHintsAcquired");
    	if(result!=null)
    	{
    		//If hints come from server and not from cache, because of possibility of areas union  
        	//it is necessary to filter them again for the notification
        	List<Hint> filteredResult = filterHints(result,area,Float.parseFloat(userSettings.getString("maxdistance", "100")));
        	customLocationManager.TaskHintsSearchFinished(sentence,filteredResult,priority);
    		Log.d(TAG,"Result not null");
	    	synchronized(hintsFoundSync)
	    	{
	    		hintsFound += filteredResult.size();
	    		
	    		CachingDbManager.insertHints(area,sentence, result);
	    		
	    	}
    	}
    	//If last search for hints returned 
    	if(!customLocationManager.MoreHintsSearch())
    	{
    		Log.d(TAG,"Last task checked.");
    		customLocationManager.SetHints(hintsFound);
    		hintsFound = 0;
	    	customLocationManager.EvaluateAccuracy();
    	}
    
    }
    private List<Hint> filterHints(List<Hint> result,Area area, float distance) 
    {
    	List<Hint> ret = new ArrayList<Hint>();
    	for(Hint h:result)
    	{
    		if(CachingDbManager.calculateDistance(Double.parseDouble(h.lat), Double.parseDouble(h.lng), area.lat,area.lng)<distance)
    			ret.add(h);
    	}
		return ret;
	}
	/**
     * Notify hints for a task.
     * @param sentence	Title of the task
     * @param result	List of hints for the task.
     * @param priority	Priority of the task.
     * @param context	Application context.
     */
    public void notifyHints(String sentence,List<Hint> result, int priority)
    {
    	if(result!=null)
    	{
	    	if (result.isEmpty()) 
	    	{
	    		Log.i(TAG, "No hints for task : "+sentence);
	        	//Voice notification
	    		if(userSettings.getBoolean("notification_hint_speak",false))
	    		{
	    			Todo.speakIt((String) context.getText(R.string.no_hints_found_for)+sentence);
	    			sendDataToWidget(result,sentence);
	    		}
	    		return; // immediately return if there is no result
	    	} 
	    	
	    	Log.i(TAG, "Received "+result.size()+ " hints for task : "+sentence);
	    		    	
	    	String message = context.getText(R.string.capable)+" "+" "+context.getText(R.string.around_location);
	    	/**
	    	 * The Notification.Builder has been added 
	    	 * to make it easier to construct Notifications.
	    	 * @author Alberto Servetti 11/04/2013
	    	 */
	    	//Notification newnotification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
	    	Random rand = new Random();
	    	rand.setSeed(System.currentTimeMillis());
	    	int requestID = rand.nextInt(Integer.MAX_VALUE) + sentence.hashCode();
	    	Intent intent = new Intent(context,NotificationSnooze.class);
	    	intent.putParcelableArrayListExtra("hints", new ArrayList<Hint>(result));
	    	intent.putExtra("tasktitle", sentence);
	    	intent.putExtra("type","Task");
	    	intent.putExtra("dismiss", false);
	    	intent.putExtra("snooze", false);
	    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    	PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID + 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
	    	
	    	Intent dismissNotificationIntent = new Intent(context,NotificationSnooze.class);
	    	dismissNotificationIntent.putExtra("tasktitle", sentence);
	    	dismissNotificationIntent.putExtra("type","Task");
	    	dismissNotificationIntent.putExtra("dismiss", true);
	    	dismissNotificationIntent.putExtra("snooze", false);
	    	dismissNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    	PendingIntent dismissNotificationPendingIntent = PendingIntent.getActivity(context, requestID +1, dismissNotificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
	    	
	    	Intent showHintsIntent = new Intent(context, HintList.class);
	    	
	    	showHintsIntent.putParcelableArrayListExtra("hints", new ArrayList<Hint>(result));
	    	showHintsIntent.putExtra("tasktitle", sentence);
	    	showHintsIntent.putExtra("type","Task");
	    	showHintsIntent.putExtra("dismiss", false);
	    	showHintsIntent.putExtra("snooze", false);
	    	showHintsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
	    	PendingIntent showHints = PendingIntent.getActivity(context, requestID +2, showHintsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    	
	    	Intent snoozeHintsIntent = new Intent(context,NotificationSnooze.class);
	    	snoozeHintsIntent.putExtra("dismiss", false);
	    	snoozeHintsIntent.putExtra("type","Task");
	    	snoozeHintsIntent.putExtra("snooze", true);
	    	snoozeHintsIntent.putExtra("tasktitle", sentence);
	    	showHintsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	PendingIntent snoozeHints = PendingIntent.getActivity(context, requestID +3, snoozeHintsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	    	
	    	Notification newnotification =
	    			new Notification.Builder(context)
	    			.setSmallIcon(R.drawable.icon)
	    			.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.task2))
	    			.setContentText(message)
	    			.setContentTitle(sentence)
	    			.setContentIntent(pendingIntent)
	    			.addAction(R.drawable.ok, context.getText(R.string.show_hints), showHints)
	    			.addAction(R.drawable.no,context.getText(R.string.dismiss_hints),dismissNotificationPendingIntent)
	    			.addAction(R.drawable.snooze,context.getText(R.string.snooze_set),snoozeHints)
	    			.setWhen(System.currentTimeMillis()).build()
	    			;
	    	/*
	    	Intent notificationIntent = new Intent(context, HintList.class);
	    	notificationIntent.putParcelableArrayListExtra("hints", new ArrayList<Hint>(result));
	    	notificationIntent.putExtra("tasktitle", sentence);
	    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	
	    	PendingIntent contentIntent = PendingIntent.getActivity(context, sentence.hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	
	    	newnotification.contentIntent = contentIntent;
	    	 */
	    	newnotification=addNotificationAlertMethod(context,newnotification,sentence,priority);
	    	//For some reason sometimes notificationManager gets null
	    	if (notificationManager == null)
	    	    	notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
	    	NotificationDispatcher.dispatch(sentence, newnotification, notificationManager,userSettings.getString("notification_hint_vibrate", "off"), context);
	    	
	    	ActionTracker.notificationSent(Calendar.getInstance().getTime(), sentence, context, 0);
    	}
    }
    /**
     * Inform customLocationManager that user answered to wifi activation request.
     */
    public void wifiRequestAnswered()
    {
    	customLocationManager.UnlockEvaluateThreadWifi();
    }
    /**
     * Inform customLocationManager that user answered to gps activation request.
     */
    public void gpsRequestAnswered()
    {
    	customLocationManager.UnlockEvaluateThreadGps();
    }
   
    /**
     * Add vibration and sound to the notification, manage also speaking, everything according to user settings
     * @param newNotification
     * @param sentence
     * @param priority
     * @return THE SAME object passed in newNotification parameter
     */
    public Notification addNotificationAlertMethod(Context context,Notification newNotification,String sentence,int priority){
    	//Adding sound
    	Log.d(TAG, "Sound:"+userSettings.getBoolean("notification_hint_sound",false));
    	if(userSettings.getBoolean("notification_hint_sound",false)){
    		newNotification.defaults |= Notification.DEFAULT_SOUND;
    	}
    	
    	//Adding vibration
    	//Log.d(TAG, "Vibration:"+userSettings.getBoolean("notification_hint_vibrate",false));
    	Log.d(TAG, "Vibration" + userSettings.getString("notification_hint_vibrate", "off"));
    	//if(userSettings.getBoolean("notification_hint_vibrate",false)){
    	//Resources res = context.getResources();
    	if(userSettings.getString("notification_hint_vibrate", "off").equals("priority")){
    		//newnotification.defaults |= Notification.DEFAULT_VIBRATE;

        	
        	long totTime=5000;
        	long usedTime=0;
        	Log.d(TAG, "Priority:"+priority);        	
        	long minVibrate=150;
        	long vibrate=minVibrate+minVibrate*(1+(1*priority));
        	Log.d(TAG, "Vibrate:"+vibrate);
        	long minPause=100;
        	long pause=minPause+(long) Math.floor((double)minPause*((double)1+((double)1/(double)priority)));
        	Log.d(TAG, "Pause:"+pause);
        	
        	int totElem=(int) (Math.floor((double)totTime/(double)(vibrate+pause))+1);
        	Log.d(TAG, "Tot position:"+totElem);
        	//The +5 is only to prevent the IndexOutOfBoundException 
        	long[] vibratePattern = new long[(int) (totTime/Math.min(minVibrate,minPause))+5];
        	int pos=0;
        	while(usedTime<totTime){
            	//Pattern: The first value is how long to wait (off) before beginning
        		vibratePattern[pos]=pause;
        		usedTime+=pause;
        		pos++;
        		//the second value is the length of the first vibration
        		vibratePattern[pos]=vibrate;
        		usedTime+=vibrate;
        		pos++;
        	}
        	
        	newNotification.vibrate = vibratePattern;
    	} else if (userSettings.getString("notification_hint_vibrate", "off").equals("morse")) {
    		newNotification.vibrate = Morse.getMorseVibrationPattern(sentence);
    	}

    	/*
    	//Voice notification
    	Log.d(TAG, "Speak:"+userSettings.getBoolean("notification_hint_speak",false));
    	if(userSettings.getBoolean("notification_hint_speak",false)){
    		Todo.speakIt((String) getText(R.string.new_hints_found_for)+sentence);
    	}
    	*/
    	return newNotification;
    }
    
    /**
     * Creates a new PendingIntent and send it to the widget
     */
    private void sendDataToWidget(List<Hint> result,String sentence){
    	Intent foundNewHintIntent = new Intent(TaskNotification.this, ExampleAppWidgetProvider.class);
	    if(!result.isEmpty()){
	        foundNewHintIntent.setAction(ExampleAppWidgetProvider.ACTION_NEW_HINT_FOUND);
	        Log.d(TAG, "Put "+sentence+" as sentence for the widget");
	        foundNewHintIntent.putExtra("task", sentence+" "+getText(R.string.around_location));
	        Log.d(TAG,"There is "+foundNewHintIntent.getExtras().getString("task"));
	        foundNewHintIntent.putParcelableArrayListExtra("hints", new ArrayList<Hint>(result));
	        Log.d(TAG, "Put "+result.size()+" hint into the list for the widget");
	    }else{
	        foundNewHintIntent.setAction(ExampleAppWidgetProvider.ACTION_NO_HINT_FOUND);
	        foundNewHintIntent.putExtra("task", sentence+" "+getText(R.string.around_location));
	        Log.d(TAG, "NO hint into the list for the widget");
	    }
	        PendingIntent widgetIntent = PendingIntent.getBroadcast(TaskNotification.this, 0, foundNewHintIntent,   PendingIntent.FLAG_ONE_SHOT);
    	Log.d(TAG, "WidgetIntent created!");
    	try 
    	{
			widgetIntent.send();
		} 
    	catch (CanceledException e) 
    	{
			e.printStackTrace();
		}
    	Log.d(TAG, "WidgetIntent sent!");
    }
   
    
    @Override
	public IBinder onBind(Intent intent) {
		 return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder 
	{
        public TaskNotification getService() 
        {
            return TaskNotification.this;
        }
    }
	
	/**
	 * Forces customLocationManager to obtain a new fix immediatly
	 */
	
	public void forceLocationFix()
	{
		customLocationManager.forceLocationFix();
	}
	/**
	 * This method get all the change of the location according to the setup made by the requestLocationUpdate method
	 * that set the minDistance to be informed of the location change 
	 */
	public void startHintSearch(Location newLocation,Boolean checkMinDistance)
	{
		//customLocationManager.setHints(0);
		
		
		//updateProviderAndPosition("startHintSearch",newLocation);
		
		updatePosition(newLocation);
		 
		Log.d(TAG,"Going to wake mainthread.");
		synchronized(stopThreadObject)
		{
			stopThreadObject.notify();
		}
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		
			Log.d(TAG,"onLocationChanged");
			if(customLocationManager.checkForHints(location))
			{
				Log.d(TAG, "Location changed to "+location.getLatitude()+" -- "+location.getLongitude());
				//condvargps.open();
				startHintSearch(location,true);	
			}
			else
				Log.d(TAG,"No hints check needed!");
		
	}
    
	@Override
	//With new approach of CustomLocationManager this method is never called.
	public void onProviderDisabled(String provider) 
	{
		Log.i(TAG,"onProviderDisabled");
		
		customLocationManager.providerDisabled(provider);
		
		updateProviderAndPosition("onProviderDisabled:"+provider,null);	
	}

	@Override
	//With new approach of CustomLocationManager this method is never called.
	public void onProviderEnabled(String provider) 
	{
		Log.i(TAG,"onProviderEnabled");
		
		customLocationManager.providerEnabled(provider);
		
		updateProviderAndPosition("onProviderEnabled:"+provider,null);
	}

	@Override
	//With new approach of CustomLocationManager this method is never called.
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		Log.i(TAG,"onStatusChanged: " + provider);	
		
		customLocationManager.statusChanged(provider, status, extras);
	}



	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
		/**
		 * Query period updated
		 
		if(key.equals("queryperiod")){
			Log.d(TAG,"Got query period updated, registering..");
			
			registerToGetLocationUpdates();
			Log.d(TAG," done!");
		}
		*/
		customLocationManager.onSharedPreferenceChanged(arg0, key);
	}
	/**
	 * Obtain last known position.
	 * 
	 * @return 		Last known position from customLocationManager.
	 */
	public Location getLastKnownLocation()
	{
		return customLocationManager.getLastKnownPosition();
	}
	
}