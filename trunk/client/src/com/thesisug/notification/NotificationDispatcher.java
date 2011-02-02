package com.thesisug.notification;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.thesisug.R;
import com.thesisug.notification.TaskNotification.LocalBinder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
/**
 * This class handles notification forwarding to the notification manager
 * such that morse notifications, when active, are given their time to
 * finish their vibrating message without successive notifications canceling
 * it.
 * @author lorenzo
 *
 */
public class NotificationDispatcher {
	// inessential forwarded notification counter
	private static int notificationsSoFar = 0;
	// TAG attribute for logging
	private static String TAG = "thesisug - NotificationDispatcher";
	// queue where notifications are put to be forwarded to the
	// notification manager in an appropriate moment
	private static BlockingQueue<NotificationContainer> queue;
	// notification manager handle
	private static NotificationManager notificationManager;
	// shared preferences handle
	
	/**
	 * initializes NotificationDispatcher's private fields in
	 * case it had not been done yet.
	 * @param nm Handle to the notification manager
	 */
	private static void init(NotificationManager nm) {
		// initialize the queue
		queue = new LinkedBlockingQueue<NotificationContainer>();
		// save the notification manager handle
		notificationManager = nm;
		// register for morse preference change
		
		// define the code for the dispatcher thread
		Runnable dispatcherTask = new Runnable(){
			public void run() {
				// forever
				while (true) {
					NotificationContainer n = null;
					// get a notification from the queue, if possible
					try {
						n = queue.take();
					} catch (InterruptedException e) {
						Log.v(TAG, "Dispatcher thread was interrupted while blocked on empty queue");
					}
					// forward it to the notification manager
					if (n != null) {
						Log.v(TAG, "Presa la notifica " + n.sentence + ", la inoltro");
						notificationManager.notify(n.sentence.hashCode(), n.notification);
						Log.v(TAG, n.sentence + " no. "+ ++notificationsSoFar +" inoltrata, dormo per " + n.duration);
						// sleep until the morse vibration pattern finished
						try {
							Thread.sleep(n.duration);
						} catch (InterruptedException e) {
							Log.v(TAG, "Dispatcher thread was interrupted while waiting for notification to end");
						}
					}
				}
			}
		};
		// start the dispatcher thread
		Thread dispatcher = new Thread(dispatcherTask);
		dispatcher.start();
	}
	
	/**
	 * Gives NotificationDispatcher a notification object to be dispatched.
	 * @param sentence title the Task to remember was given when creating it
	 * @param newNotification notification object to be dispatched
	 * @param nm handle of the notification manager
	 * @param vibration value of the shared preference notification_hint_vibrate
	 */
	public static void dispatch(String sentence, Notification newNotification, NotificationManager nm, String vibration) {
		// if DispatcherManager had not been initialized yet, do it
		if (notificationManager == null || queue == null) {
			Log.v(TAG, "Dispatcher is uninitialized");
			init(nm);
			Log.v(TAG, "Dispatcher was successfully initialized");
		}
		// if morse notifications are disabled simply forward notification to notification manager
		boolean morseYes = vibration.equals("morse");
		if (!morseYes) {
			Log.v(TAG, "Notification received, morse OFF, handing it over to the notification manager");
			notificationManager.notify(sentence.hashCode(), newNotification);
		} else {
			// otherwise compute the duration of morse vibrating pattern
			Log.v(TAG, "Notification received, morse ON, enqueueing it");
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
	}

}
