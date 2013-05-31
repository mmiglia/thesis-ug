package com.thesisug.notification;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.thesisug.R;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.tracking.ActionTracker;
import com.thesisug.ui.HintList;
import com.thesisug.ui.NotificationSnooze;
import com.thesisug.ui.ShowEvent;
import com.thesisug.ui.accessibility.Morse;

public class EventNotification extends BroadcastReceiver
{
	private static final String TAG = "thesisug - EventNotification";
    @Override
    public void onReceive(Context context, Intent incomingIntent)
    {
    	Log.i(TAG, "Event notification is initiated");
    	String sentence = incomingIntent.getStringExtra("title");
    	Log.d(TAG,sentence);
    	
    	NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	String message = context.getString(R.string.have_appointment);
    	/**
    	 * The Notification.Builder has been added 
    	 * to make it easier to construct Notifications.
    	 * @author Alberto Servetti 11/04/2013
    	 */
    	//Notification eventnotification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
    	int requestID = (int) System.currentTimeMillis();
    	
    	Intent intent = new Intent(context,NotificationSnooze.class);
    	intent.putExtras(incomingIntent.getExtras());
    	intent.putExtra("tasktitle", sentence);
    	intent.putExtra("type","Event");
    	intent.putExtra("dismiss", false);
    	intent.putExtra("snooze", false);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    	PendingIntent pendingIntent = PendingIntent.getActivity(context, requestID + sentence.hashCode() + 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	Intent dismissNotificationIntent = new Intent(context,NotificationSnooze.class);
    	dismissNotificationIntent.putExtra("tasktitle", sentence);
    	intent.putExtra("type","Event");
    	dismissNotificationIntent.putExtra("dismiss", true);
    	dismissNotificationIntent.putExtra("snooze", false);
    	dismissNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    	PendingIntent dismissNotificationPendingIntent = PendingIntent.getActivity(context, requestID + sentence.hashCode() + 1, dismissNotificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	Intent showEventIntent = new Intent(context, ShowEvent.class);
    	showEventIntent.putExtras(intent.getExtras());
    	showEventIntent.putExtra("notification", "true");
    	showEventIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent showEvent = PendingIntent.getActivity(context, requestID + sentence.hashCode() + 2, showEventIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	Intent snoozeHintsIntent = new Intent(context,NotificationSnooze.class);
    	snoozeHintsIntent.putExtra("dismiss", false);
    	snoozeHintsIntent.putExtra("snooze", true);
    	intent.putExtra("type","Event");
    	snoozeHintsIntent.putExtra("tasktitle", sentence);
    	snoozeHintsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	PendingIntent snoozeHints = PendingIntent.getActivity(context, requestID + sentence.hashCode() + 3, snoozeHintsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	
    	Notification eventnotification = new Notification.Builder(context)
        .setContentText(message)
        .setContentTitle(sentence)
        .setSmallIcon(R.drawable.icon)
        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.event2))
        .addAction(R.drawable.ok, context.getText(R.string.show_hints), showEvent)
    	.addAction(R.drawable.no,context.getText(R.string.dismiss_hints),dismissNotificationPendingIntent)
    	.addAction(R.drawable.snooze,context.getText(R.string.snooze_set),snoozeHints)
    	.setContentIntent(pendingIntent)
        .setWhen(System.currentTimeMillis())
        .build();
		/*
    	Intent notificationIntent = new Intent(context, ShowEvent.class);
    	notificationIntent.putExtras(intent.getExtras());
    	notificationIntent.putExtra("notification", "true");
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    	PendingIntent contentIntent = PendingIntent.getActivity(context, notificationIntent.getStringExtra("eventID").hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
    	contentView.setTextViewText(R.id.notification_title, message);
    	contentView.setTextViewText(R.id.notification_content, context.getText(R.string.click_details));
    	eventnotification.contentView = contentView;
    	eventnotification.contentIntent = contentIntent;
    	*/
    	//Adding sound
    	eventnotification.defaults |= Notification.DEFAULT_SOUND;
    	boolean morseYes;
    	String notification_hint_vibrate = PreferenceManager.getDefaultSharedPreferences(context).getString("notification_hint_vibrate", "off");
    	morseYes = notification_hint_vibrate.equals("morse");
    	//Adding vibration
    	if (!morseYes) {
    		eventnotification.defaults |= Notification.DEFAULT_VIBRATE;
    	} else {
    	//Pattern: The first value is how long to wait (off) before beginning, the second value is the length of the first vibratio
    	//long[] vibratePattern = {100,150,200,300};
    	//newnotification.vibrate = vibratePattern;
    		eventnotification.vibrate = Morse.getMorseVibrationPattern(sentence);
    	}
    	
    	//manager.notify(sentence.hashCode(), eventnotification);
    	NotificationDispatcher.dispatch(sentence, eventnotification, manager, notification_hint_vibrate, context);
    	
    	ActionTracker.notificationSent(Calendar.getInstance().getTime(), sentence, context, 1);
	
    }
   
}
