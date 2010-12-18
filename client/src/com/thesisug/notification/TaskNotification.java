package com.thesisug.notification;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.ContextResource;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.ui.HintList;

public class TaskNotification extends Service{
	private static final String TAG = "thesisug - TaskNotificationService";
	
	 // variable which controls the notification thread 
    private ConditionVariable condvar, condvargps, condvargps1, downloadlock;
    private Thread downloadTaskThread;
    private List<SingleTask> tasks;
    private Handler handler=new Handler();
    private LocationManager lm, lm1;
    private NotificationManager manager;
    private static SharedPreferences usersettings;
    Location gpslocation, position, lastposition;
    private RemoteViews contentView;
    private double lastdelayquery, delayzerovelocity;
    
    @Override
    public void onCreate() {
    	contentView = new RemoteViews(getPackageName(), R.layout.notification);
    	manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    	lm1 = (LocationManager) getSystemService(LOCATION_SERVICE);
    	usersettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, new GPSListener());
    	//lm1.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, new GPSListener1());
    	Thread notifyingThread = new Thread(null, mainthread, "NotifyingService");
    	Thread gpsNotificatorThread = new Thread(null, gpsthread, "NotifyingGPSChange");
        condvar = new ConditionVariable(false);
        condvargps = new ConditionVariable(false);
        condvargps1 = new ConditionVariable(false);
        downloadlock = new ConditionVariable(false);
        notifyingThread.start();
        Log.i(TAG, "notifyingThread has id:"+notifyingThread.getId());
        gpsNotificatorThread.start();
        Log.i(TAG, "gpsNotificatorThread has id:"+gpsNotificatorThread.getId());
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
    	Log.i(TAG, "Task Notification service is started");
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
    	Log.i(TAG, "Task Notification service is stopped");
        // Stop the thread from generating further notifications
        condvar.open();
        condvargps.open();
        // Tell the user we stopped.
        Toast.makeText(this, R.string.task_notification_stopped, Toast.LENGTH_SHORT).show();
    }
    
    private Runnable gpsthread = new Runnable() {
    	public void run() {
    		Log.i(TAG, "gpsThread started");
    		Looper.prepare();
    		lastdelayquery=999999999;
    		delayzerovelocity=2.5;
    		while (true) {
    			//Looper.prepare();
    			int mindist = Integer.parseInt(usersettings.getString("queryperiod", "100"));
    			Log.i(TAG, "gpsthread - Min Distance Query is "+mindist);
    			Criteria crit = new Criteria();
				crit.setAccuracy(Criteria.ACCURACY_FINE);
				String pr = lm1.getBestProvider(crit, true);
				Log.i(TAG, "gpsthread - provider: "+pr);
				position = lm1.getLastKnownLocation(pr);
				if (position==null) {
					Log.i(TAG, "gpsthread - Waiting for the location signal");
					condvargps1.block(10000);
					continue;
				}
				double dist = calculateDistance(position, lastposition);
				if (lastposition!=null)
					Log.i(TAG, "gpsthread - lastposition: LAT "+lastposition.getLatitude()+" LONG "+lastposition.getLongitude());
				Log.i(TAG, "gpsthread - Position: LAT "+position.getLatitude()+" LONG "+position.getLongitude()+" distance since lastposition: "+dist);
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
					Log.i(TAG, "gpsthread - Terminal has stopped. Delay: "+delayquery);
				}
				else {
					// the next query will be beyond the mindist distance
					delayquery = mindist/velocity;
					delayquery = Math.ceil(delayquery);
					lastdelayquery = delayquery;
					//this means that the terminal is moving, so set the delayzerovelocity to the start value
					delayzerovelocity = 2.5;
					Log.i(TAG, "gpsthread - Terminal is moving. Velocity: "+velocity+" Delay: "+delayquery);
				}
				
				lastposition = position;
				if (dist>mindist) {
					Log.i(TAG, "gpsthread - mainthread waking up");
					condvargps.open();
				}
				condvargps1.block((long)delayquery*1000);
    		}
    	}
    };
    
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
    
    private Runnable mainthread = new Runnable() {
        public void run() {
        	while (true){
        		// get preference on query period, return default 5 min if not set
        		//int delay = Integer.parseInt(usersettings.getString("queryperiod", "300")) * 1000;
        		int delay = Integer.parseInt(usersettings.getString("queryperiod", "100"));
        		if (delay == 0) {
        			Log.i(TAG, "mainthread - Thread blocked for 60000 millis");
        			condvar.block(60000); 
        			continue;
        		}
        		Log.i(TAG, "mainthread - distance query is "+delay+" mt");
        		condvargps.block();
        		//if (condvargps.block(delay)) break;
        		// get preference on distance, return default 0 (dont filter distance) if not set
        		int distance = Integer.parseInt(usersettings.getString("maxdistance", "0"));
        		List<Thread> threads = new LinkedList<Thread>();
				// get current location
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				String provider = lm.getBestProvider(criteria, true);
				Log.i(TAG, "mainthread - Provider: "+provider);
				gpslocation = lm.getLastKnownLocation(provider);				
        		if (gpslocation == null) continue;
        		//asynchronous operation to download thread
        		downloadTaskThread = TaskResource.getFirstTask(handler, TaskNotification.this);
        		//block execution to make it synchronous
        		downloadlock.block();
        		for (SingleTask o : tasks){
        			// if user has chose not to be reminded for this task
        			if (!usersettings.getBoolean(o.title, true)) continue; 
        			// dispatch thread to get hints
					threads.add(ContextResource.checkLocationSingle(o.title,
							new Float(gpslocation.getLatitude()),
							new Float(gpslocation.getLongitude()),
							distance, handler, TaskNotification.this));
				}
        		condvargps.close();
        	}
        }
    };
    
    public void afterTaskLoaded (List<SingleTask> result){
    	tasks = result;
    	downloadlock.open();
    }
    
    public void afterHintsAcquired (String sentence, List<Hint> result){
    	if (result.isEmpty()) {
    		Log.i(TAG, "No hints for task : "+sentence);
    		return; // immediately return if there is no result
    	} else Log.i(TAG, "Received "+result.size()+ " hints for task : "+sentence);
    	String message = getText(R.string.capable)+" "+sentence+" "+getText(R.string.around_location);
    	Notification newnotification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
    	Intent notificationIntent = new Intent(getApplicationContext(), HintList.class);
    	notificationIntent.putParcelableArrayListExtra("hints", new ArrayList<Hint>(result));
    	notificationIntent.putExtra("tasktitle", sentence);
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    	PendingIntent contentIntent = PendingIntent.getActivity(TaskNotification.this, sentence.hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	contentView.setTextViewText(R.id.notification_title, getText(R.string.want_to)+" "+sentence+" "+getText(R.string.now)+" ?");
    	contentView.setTextViewText(R.id.notification_content, getText(R.string.click_hint));
    	newnotification.contentView = contentView;
    	newnotification.contentIntent = contentIntent;
    	manager.notify(sentence.hashCode(), newnotification);
    }
    
    @Override
	public IBinder onBind(Intent intent) {
		 return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
        public TaskNotification getService() {
            return TaskNotification.this;
        }
    }
	
	private class GPSListener implements LocationListener{
		@Override
		public void onLocationChanged(Location location) {}

		@Override
		public void onProviderDisabled(String provider) {}

		@Override
		public void onProviderEnabled(String provider) {}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	}

}