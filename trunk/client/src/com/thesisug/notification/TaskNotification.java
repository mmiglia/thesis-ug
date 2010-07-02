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
	private static final String TAG = "TaskNotificationService";
	
	 // variable which controls the notification thread 
    private ConditionVariable condvar, downloadlock;
    private Thread downloadTaskThread;
    private List<SingleTask> tasks;
    private Handler handler=new Handler();
    private LocationManager lm;
    private NotificationManager manager;
    private static SharedPreferences usersettings;
    Location gpslocation;
    private RemoteViews contentView;
    
    @Override
    public void onCreate() {
    	contentView = new RemoteViews(getPackageName(), R.layout.notification);
    	manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    	usersettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, new GPSListener());
    	Thread notifyingThread = new Thread(null, mainthread, "NotifyingService");
        condvar = new ConditionVariable(false);
        downloadlock = new ConditionVariable(false);
        notifyingThread.start();
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
        // Tell the user we stopped.
        Toast.makeText(this, R.string.task_notification_stopped, Toast.LENGTH_SHORT).show();
    }
    
    private Runnable mainthread = new Runnable() {
        public void run() {
        	while (true){
        		// get preference on query period, return default 5 min if not set
        		int delay = Integer.parseInt(usersettings.getString("queryperiod", "300")) * 1000;
        		if (delay == 0) {condvar.block(60000); continue;}
        		Log.i(TAG, "delay is "+delay);
        		if (condvar.block(10000)) break;
        		// get preference on distance, return default 0 (dont filter distance) if not set
        		int distance = Integer.parseInt(usersettings.getString("maxdistance", "0"));
        		List<Thread> threads = new LinkedList<Thread>();
				// get current location
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				String provider = lm.getBestProvider(criteria, true);
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
		public void onStatusChanged(String provider, int status,
				Bundle extras) {}
	}
}
