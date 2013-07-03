package com.thesisug.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.thesisug.R;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.notification.NotificationDispatcher;
import com.thesisug.notification.SnoozeHandler;
import com.thesisug.notification.TaskNotification;
import com.thesisug.tracking.ActionTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

/**
 * Handles dialogs for managing user's choices about notifications.
 * 
 * @author Alberto Servetti
 *
 */
public class NotificationSnooze extends Activity
{
	private static final String TAG = "thesisug - NotificationSnooze";
	private static Bundle packet;
	private static Context context;
@Override
public void onCreate(Bundle savedInstanceState) 
{
	super.onCreate(savedInstanceState);
	Log.i(TAG,"onCreate");
	showNotificationDialogs();
}
private void showNotificationDialogs()
{
	context = this;
	packet = getIntent().getExtras();
	AlertDialog.Builder temp = new AlertDialog.Builder(this);
	temp.setOnCancelListener(new OnCancelListener() 
	{
           public void onCancel(DialogInterface dialog) 
           {
               finish();
           }
	});
	temp.setOnDismissListener(new OnDismissListener()
	{
           public void onDismiss(DialogInterface dialog) 
           {
               finish();
           }
	});
	final AlertDialog.Builder snoozeListBuilder=temp;
	//Check if user clicked on dismiss button
	if(packet!=null)
	{
		final String type = packet.getString("type");
		final String sentence = packet.getString("tasktitle");
		final int ty;
 	   	if(type.equals("Task"))
 	   		ty = 0;
 	   	else 
 	   		ty = 1;
		boolean dismiss = packet.getBoolean("dismiss");
		if(dismiss)
		{
			Log.d(TAG,"Going to dismiss " + sentence);
     	   	NotificationDispatcher.deleteNotification(sentence.hashCode());
     	   	
     	   	ActionTracker.notificationDismissed(Calendar.getInstance().getTime(), sentence, context, ty);
     	   	packet.clear();
            finish();
            return;
		}
		boolean snooze = packet.getBoolean("snooze");
		//Check if user clicked on snooze button
		if(snooze)
		{
			
			Log.d(TAG,"Asking user for snoozing time for "+ type + ":" + sentence);
			snoozeListBuilder.setTitle(R.string.snooze_choice)
		           .setItems(R.array.snooze, new DialogInterface.OnClickListener() 
		           {
		               public void onClick(DialogInterface dialog, int which) 
		               {
		            	   String [] snoozeArray = getResources().getStringArray(R.array.snoozeValue);
		            	   Log.d(TAG,"Setting " + snoozeArray[which] +" min snooze for " + sentence +".");
		            	   SnoozeHandler.snoozeTask(sentence,type, Integer.parseInt(snoozeArray[which]));
		            	   Log.d(TAG,"Going to dismiss " + sentence);
		            	   NotificationDispatcher.deleteNotification(sentence.hashCode());
		            	   packet.clear();
		                   finish();
		               }      

		           });

            AlertDialog snoozeListDialog = snoozeListBuilder.create();
            snoozeListDialog.show();    
			return;
		}
		//User clicked on Notification
		else
		{
			Log.d(TAG,"Creating dialog box");
			ActionTracker.notificationClicked(Calendar.getInstance().getTime(), sentence, context, ty);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setOnCancelListener(new OnCancelListener() 
			{
		           public void onCancel(DialogInterface dialog) 
		           {
		               finish();
		           }
			});
			builder.setMessage(getText(R.string.notification_dialog_message)).setTitle(packet.getString("tasktitle"));
			builder.setPositiveButton(R.string.show_hints, new DialogInterface.OnClickListener() 
			{
		           public void onClick(DialogInterface dialog, int id) 
		           {
		        	   	String type= packet.getString("type");
		        	   	
		        	   	if(type.equals("Task"))
		        	   	{
			        	   	Log.d(TAG,"Going to show hints for " + sentence +".");
			        	   	List<Hint> result = packet.getParcelableArrayList("hints");
				        	Intent showHintsIntent = new Intent(getApplicationContext(), HintList.class);
				           	showHintsIntent.putParcelableArrayListExtra("hints", new ArrayList<Hint>(result));
				           	showHintsIntent.putExtra("tasktitle", sentence);
				           	showHintsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			           	
				           	PendingIntent showHints = PendingIntent.getActivity(getApplicationContext(), sentence.hashCode(), showHintsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				           	try 
				           	{
								showHints.send();
							} catch (CanceledException e) 
							{
								
								e.printStackTrace();
							}
				           	packet.clear();
				           
				           	finish();
		        	   	}
		        	   	if(type.equals("Event"))
		        	   	{
		        	   		Log.d(TAG,"Going to show event " + sentence +".");
				        	Intent notificationIntent = new Intent(getApplicationContext(), ShowEvent.class);
				        	notificationIntent.putExtras(packet);
				        	notificationIntent.putExtra("notification", "true");
				        	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), notificationIntent.getStringExtra("eventID").hashCode(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				        	
				           	try 
				           	{
				           		contentIntent.send();
							} 
				           	catch (CanceledException e) 
							{
								
								e.printStackTrace();
							}
				           	packet.clear();
				           
				           	finish();
		        	   	}
		           }
		       });
			builder.setNeutralButton(R.string.snooze_hints, new DialogInterface.OnClickListener() 
			{
		           public void onClick(DialogInterface dialog, int id) 
		           {
		        	   Log.d(TAG,"Display snooze choice");
		        	   snoozeListBuilder.setTitle(R.string.snooze_choice)
			           .setItems(R.array.snooze, new DialogInterface.OnClickListener() 
			           {
			               public void onClick(DialogInterface dialog, int which) 
			               {
			            	   String [] snoozeArray = getResources().getStringArray(R.array.snoozeValue);
			            	   Log.d(TAG,"Setting " + snoozeArray[which] +" min snooze for " + sentence +".");
			            	   SnoozeHandler.snoozeTask(sentence,type, Integer.parseInt(snoozeArray[which]));
			            	   Log.d(TAG,"Going to dismiss " + sentence);
			            	   NotificationDispatcher.deleteNotification(sentence.hashCode());
			            	   packet.clear();
			                   finish();
			               }      

			           });

		        	   AlertDialog snoozeListDialog = snoozeListBuilder.create();
		        	   snoozeListDialog.show();       
		              
		           }
			});
			builder.setNegativeButton(R.string.dismiss_hints, new DialogInterface.OnClickListener() 
			{
		           public void onClick(DialogInterface dialog, int id) 
		           {
		        	   Log.d(TAG,"Going to dismiss " + sentence);
		        	   NotificationDispatcher.deleteNotification(sentence.hashCode());
		        	   ActionTracker.notificationDismissed(Calendar.getInstance().getTime(), sentence, context, ty);
		        	   packet.clear();
		               finish();
		           }
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
}
@Override
public void onDestroy()
{
	super.onDestroy();
	Log.i(TAG,"onDestroy");

	
}
@Override
public void onNewIntent(Intent intent)
{
	Log.i(TAG,"onNewIntent");
	showNotificationDialogs();
}
}

