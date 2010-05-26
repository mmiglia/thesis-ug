package com.thesisug.notification;

import java.util.LinkedList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.ContextResource;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.ui.HintList;

public class TaskNotification extends Service{
	private static final String TAG = "TaskNotificationService";
	private static final int TASK_NOTIFICATION = R.drawable.icon;
	
	 // variable which controls the notification thread 
    private ConditionVariable condvar, downloadlock;
    private NotificationManager manager;
    private Thread downloadTaskThread;
    private List<SingleTask> tasks;
    private Handler handler=new Handler();
    private LocationManager lm;
    Location gpslocation;
    
    @Override
    public void onCreate() {
    	manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	lm = (LocationManager) getSystemService(LOCATION_SERVICE);
    	Thread notifyingThread = new Thread(null, mainthread, "NotifyingService");
        condvar = new ConditionVariable(false);
        downloadlock = new ConditionVariable(false);
        notifyingThread.start();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    
    @Override
    public void onDestroy() {
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
        		condvar.block(8000);
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
    	Intent notificationIntent = new Intent(this, HintList.class);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
    	newnotification.setLatestEventInfo(getApplicationContext(), message, "click here to see the list of hints ...", contentIntent);
    }
    
    @Override
	public IBinder onBind(Intent intent) {
		 return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
        TaskNotification getService() {
            return TaskNotification.this;
        }
    }
}
