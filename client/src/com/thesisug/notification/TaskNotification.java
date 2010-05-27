package com.thesisug.notification;

import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
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
	private static final int TASK_NOTIFICATION = R.layout.notification;
	
	 // variable which controls the notification thread 
    private ConditionVariable condvar, downloadlock;
    private Thread downloadTaskThread;
    private List<SingleTask> tasks;
    private Handler handler=new Handler();
    private LocationManager lm;
    private NotificationManager manager;
    Location gpslocation;
    private RemoteViews contentView;
    
    @Override
    public void onCreate() {
    	contentView = new RemoteViews(getPackageName(), R.layout.notification);
    	manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	lm = (LocationManager) getSystemService(LOCATION_SERVICE);
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
        // Cancel the persistent notification.
        manager.cancel(TASK_NOTIFICATION);
        // Stop the thread from generating further notifications
        condvar.open();
        // Tell the user we stopped.
        Toast.makeText(this, R.string.task_notification_stopped, Toast.LENGTH_SHORT).show();
    }
    
    private Runnable mainthread = new Runnable() {
        public void run() {
        	while (true){
        		if (condvar.block(20000)) break;
        		List<Thread> threads = new LinkedList<Thread>();
        		// get current location
        		gpslocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        		//asynchronous operation to download thread
        		downloadTaskThread = TaskResource.getFirstTask(handler, TaskNotification.this);
        		//block execution to make it synchronous
        		downloadlock.block();
        		for (SingleTask o : tasks){
        			// dispatch thread to get hints
					threads.add(ContextResource.checkLocationSingle(o.title,
							(int) Math.floor(gpslocation.getLatitude() * 1e6),
							(int) Math.floor(gpslocation.getLongitude() * 1e6),
							50, handler, TaskNotification.this));
				}
        	}
        }
    };
    
    public void afterTaskLoaded (List<SingleTask> result){
    	tasks = result;
    	downloadlock.open();
    }
    
    public void afterHintsAcquired (String sentence, List<Hint> result){
    	String message = "You can "+sentence+" around here";
    	Notification newnotification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
    	Intent notificationIntent = new Intent(getApplicationContext(), HintList.class);
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    	PendingIntent contentIntent = PendingIntent.getActivity(TaskNotification.this, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
    	contentView.setTextViewText(R.id.text, sentence);
    	newnotification.contentView = contentView;
    	newnotification.contentIntent = contentIntent;
    	Log.i(TAG, "I'm here, before notifying");
    	manager.notify(TASK_NOTIFICATION, newnotification);
    	Log.i(TAG, "after notifying");
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
