package com.thesisug.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.thesisug.R;
import com.thesisug.ui.ShowEvent;
import com.thesisug.ui.accessibility.Morse;

public class EventNotification extends BroadcastReceiver
{
	private static final String TAG = "thesisug - EventNotification";
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	Log.i(TAG, "Event notification is initiated");
    	String sentence = intent.getStringExtra("title");
    	NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	String message = context.getText(R.string.have_appointment)+" "+sentence;
    	Notification eventnotification = new Notification(R.drawable.icon, message, System.currentTimeMillis());
    	Intent notificationIntent = new Intent(context, ShowEvent.class);
    	notificationIntent.putExtras(intent.getExtras());
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    	PendingIntent contentIntent = PendingIntent.getActivity(context, notificationIntent.getStringExtra("eventID").hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
    	contentView.setTextViewText(R.id.notification_title, message);
    	contentView.setTextViewText(R.id.notification_content, context.getText(R.string.click_details));
    	eventnotification.contentView = contentView;
    	eventnotification.contentIntent = contentIntent;
    	
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
    	NotificationDispatcher.dispatch(sentence, eventnotification, manager, notification_hint_vibrate);
    }
}
