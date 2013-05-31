package com.thesisug.notification;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.thesisug.R;
import com.thesisug.ui.Preferences;
import com.thesisug.ui.Todo;
import com.thesisug.ui.accessibility.ShakeListener;
//import android.os.Binder;
//import android.content.SharedPreferences.Editor;
//import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
//import com.thesisug.notification.TaskNotification.LocalBinder;
//import android.widget.Toast;
/**
 * This class handles notification forwarding to the notification manager
 * such that morse notifications, when active, are given their time to
 * finish their vibrating message without successive notifications canceling
 * it.
 * @author lorenzo
 *
 */ 
public class NotificationDispatcher extends Service {
	// inessential forwarded notification counter
	private static int notificationsSoFar = 0;
	// TAG attribute for logging
	private static String TAG = "thesisug - NotificationDispatcher";
	// queue where notifications are put to be forwarded to the
	// notification manager in an appropriate moment
	private static BlockingQueue<NotificationContainer> queue;
	// notification manager handle
	private static NotificationManager notificationManager;
	private static ShakeListener shakeListener;
	private static int latestForwardedNotification;
	private static Context context;
	private static SharedPreferences userSettings;
	private static boolean initialized = false;
	private static PowerManager.WakeLock wakeLock;
	private static final long PROGRAMMER_DEFINED_SLEEP_TIME = 3000L;
	private static boolean runningThread;
	/**
	 * initializes NotificationDispatcher's private fields in
	 * case it had not been done yet.
	 * @param nm Handle to the notification manager
	 * @param cx Handle to the context
	 */
	public static void init(NotificationManager nm, Context cx) {
		// initialize the queue
		queue = new LinkedBlockingQueue<NotificationContainer>();
		// save the notification manager handle
		notificationManager = nm;
		// save context for convenience
		context = cx;
		// get handle to default settings
		userSettings = PreferenceManager.getDefaultSharedPreferences(context);
		// initialize listener to calm the phone down if shook
		shakeListener = new ShakeListener((SensorManager) cx.getSystemService(Context.SENSOR_SERVICE));
		shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
			
			@Override
			public void onShake() {
				// if a notification was ever sent stop it and remove it
				if (latestForwardedNotification != -1) {
					notificationManager.cancel(latestForwardedNotification);
				}
				// shuts up the text-to-speech engine
				Todo.shutUp();
				// sets notifications to be produced to be quiet
				Preferences.setQuiet();
				shakeListener.reset();
				if (wakeLock.isHeld()) {
					wakeLock.release();
				}
			}
		});
		shakeListener.shutDown();
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		/**
		 * FULL_WAKE_LOCK was deprecated in API level 17. 
		 * Most applications should use FLAG_KEEP_SCREEN_ON 
		 * instead of this type of wake lock, 
		 * as it will be correctly managed by the platform 
		 * as the user moves between applications 
		 * and doesn't require a special permission.
		 * @author Alberto Servetti 11/04/2013
		 */
		wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
		runningThread=true;
		// define the code for the dispatcher thread
		Runnable dispatcherTask = new Runnable(){
			public void run() {
				// forever
				while (runningThread) {
					shakeListener.shutDown();
					if (wakeLock.isHeld()) {
						wakeLock.release();
					}
					NotificationContainer n = null;
					// get a notification from the queue, if possible
					try {
						n = queue.take();
					} catch (InterruptedException e) {
						Log.i(TAG, "Dispatcher thread was interrupted while blocked on empty queue");
					}
					// forward it to the notification manager
					if (n != null) {
						Log.i(TAG, "Presa la notifica " + n.sentence + ", la inoltro");
						
						int notificationId = n.sentence.hashCode();
						latestForwardedNotification = notificationId;
						if (wasSilenced()) {
							fix(n);
						} else {
							if (!wakeLock.isHeld()) {
								wakeLock.acquire();
							}
							shakeListener.activate();
						}
						notificationManager.notify(notificationId, n.notification);
						Log.i(TAG, n.sentence + " no. "+ ++notificationsSoFar +" inoltrata, dormo per " + n.duration);
						// sleep until the morse vibration pattern finished
						try {
							Thread.sleep(n.duration);
						} catch (InterruptedException e) {
							Log.i(TAG, "Dispatcher thread was interrupted while waiting for notification to end");
						}
					}
				}
				Log.i(TAG,"Dispatcher thread stopped");
			}
		};
		// start the dispatcher thread
		Thread dispatcher = new Thread(dispatcherTask);
		dispatcher.start();
		initialized = true;
	}
	
	/**
	 * Gives NotificationDispatcher a notification object to be dispatched.
	 * @param sentence title the Task to remember was given when creating it
	 * @param newNotification notification object to be dispatched
	 * @param nm handle of the notification manager
	 * @param vibration value of the shared preference notification_hint_vibrate
	 */
	public static void dispatch(String sentence, Notification newNotification, NotificationManager nm, String vibration, Context context) {
		// if DispatcherManager had not been initialized yet, do it
		if (notificationManager == null || queue == null) {
			Log.i(TAG, "Dispatcher is uninitialized");
			init(nm, context);
			Log.i(TAG, "Dispatcher was successfully initialized");
		}
		if (vibration.equals("morse")) {
			if (userSettings.getBoolean("notification_hint_speak", false)) {
				Todo.speakIt((String) context.getString(R.string.new_hints_found_for) + sentence);
			}
			enqueueVibratingNotification(newNotification, sentence);
		} else if (userSettings.getBoolean("notification_hint_speak", false)) {
			forwardSpokenNotification(newNotification, sentence);
		} else if (vibration.equals("priority")) {
			enqueueVibratingNotification(newNotification, sentence);
		} else if (userSettings.getBoolean("notification_hint_sound", false)) {
			forwardSoundOnlyNotification(newNotification, sentence);
		} else {
			if(notificationManager == null)
			{
				Log.e(TAG,"notificationManager null");
			}
			if(nm==null)
			{
				Log.e(TAG,"Passed notificationManager null");
			}
			notificationManager.notify(sentence.hashCode(), newNotification);
		} 
		/*
		// if morse notifications are disabled simply forward notification to notification manager
		boolean vibratingYes = vibration.equals("morse") || vibration.equals("priority");
		if (!vibratingYes) {
			Log.v(TAG, "Notification received, vibration " + vibration.toUpperCase() + ", handing it over to the notification manager");
			notificationManager.notify(sentence.hashCode(), newNotification);
		} else {
			// otherwise compute the duration of morse vibrating pattern
			Log.v(TAG, "Notification received, vibration " + vibration.toUpperCase() + ", enqueueing it");
			long duration = 0L;
			if (newNotification == null) {
				Log.v(TAG, "Nessuna nuova notifica");
				System.exit(0);
			}
			for (int i = 0 ; i < newNotification.vibrate.length; i++) {
				duration += newNotification.vibrate[i];
			}
			// and enqueue notification
			Log.v(TAG, "Trying to enqueue notification");
			try {
				queue.put(new NotificationContainer(newNotification, sentence, duration));
			} catch (InterruptedException e) {
				Log.v(TAG, "Dispatcher interrupted while waiting for free space to enqueue a new notification");
			}
			Log.v(TAG, "Notification enqueued");
		}
		*/
	}
	private static void forwardSpokenNotification(final Notification newNotification,
			final String sentence) {
		Thread spokenNotificationDisPatcher = new Thread(new Runnable() {
			public void run() {
				Log.d(TAG, "Speak:"+userSettings.getBoolean("notification_hint_speak",false));
				if (!wakeLock.isHeld()) {
					wakeLock.acquire();
				}
				Todo.speakIt((String) context.getText(R.string.new_hints_found_for)+sentence);
				notificationManager.notify(sentence.hashCode(), newNotification);
			}
		});
		spokenNotificationDisPatcher.start();
		
	}

	private static void forwardSoundOnlyNotification(
			final Notification newNotification, final String sentence) {
		Thread timedDispatcher = new Thread(new Runnable() {
			public void run() {
				if (!wakeLock.isHeld()) {
					wakeLock.acquire();
				}
				shakeListener.activate();
				notificationManager.notify(sentence.hashCode(), newNotification);
				try {
					Thread.sleep(PROGRAMMER_DEFINED_SLEEP_TIME);
				} catch (InterruptedException e) {
					Log.e(TAG, "Timed dispatcher interrupted while sleeping");
				}
				shakeListener.shutDown();
				if (wakeLock.isHeld()) {
					wakeLock.release();
				}
			}
		});
		timedDispatcher.start();
		
	}

	private static void enqueueVibratingNotification(
			Notification newNotification, String sentence) {
		// compute the duration of vibrating pattern
		long duration = computeVibratePatternDuration(newNotification.vibrate);
		// and enqueue notification
		Log.i(TAG, "Trying to enqueue notification");
		try {
			queue.put(new NotificationContainer(newNotification, sentence, duration));
		} catch (InterruptedException e) {
			Log.i(TAG, "Dispatcher interrupted while waiting for free space to enqueue a new notification");
		}
		Log.i(TAG, "Notification enqueued");
		
	}

	private static long computeVibratePatternDuration(long[] vibrate) {
		long duration = 0L;
		for (int i = 0 ; i < vibrate.length; i++) {
			duration += vibrate[i];
		}
		return duration;
	}

	/**
	 * Removes any sound and vibration from the notification contained in n
	 * @param n container of the notification which should be quieted
	 */
	private static void fix(NotificationContainer n) {
		n.notification.vibrate = null;
		n.notification.defaults = 0;
	}
	/**
	 * Tests whether the user set the phone to be silent
	 * @return true if the phone was set to be silent (i.e. neither sound or vibration), false otherwise
	 */
	private static boolean wasSilenced() {
		return userSettings.getString("notification_hint_vibrate", "off").equals("off")
		&& !userSettings.getBoolean("notification_hint_sound", false)
		&& !userSettings.getBoolean("notification_hint_speak", false);
	}

	public static void onSpokenMessageEnd() {
		shakeListener.shutDown();
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}

	public static void onSpokenMessageStart() {
		shakeListener.activate();
		
	}

	public static void onSpokenMessageShutUp(int listNum) {
		while (listNum > 0) {
			shakeListener.shutDown();
			listNum--;
		}
		
	}
	public static void deleteNotification(int id)
	{
		notificationManager.cancel(id);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// we do not need a IBinder interface
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
    	Log.i(TAG, "NotificationDispatcher service is started");
        return START_STICKY;
    }
	
	@Override
    public void onDestroy() 
    {
		runningThread=false;
    }
}
