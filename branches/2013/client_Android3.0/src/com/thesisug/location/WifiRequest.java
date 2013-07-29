package com.thesisug.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.AccountUtil;
import com.thesisug.notification.NotificationDispatcher;
import com.thesisug.notification.TaskNotification;

public class WifiRequest extends Activity 
{
	private static final String TAG = "thesisug - WifiRequest";
	private Bundle packet; 
	private WifiManager wifiManager;
	private static final int OK = 0;
	private static final int NO = 1;
	private static SharedPreferences userSettings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi_request);
		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		packet = getIntent().getExtras();
		if(packet!=null)
		{
			String answer = packet.getString("answer");
			NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			nm.cancel("wifi".hashCode());
			if(answer.equals("ok"))
			{
				startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),0);
			}
			if(answer.equals("no"))
			{
				TaskNotification.getInstance().wifiRequestAnswered();
				finish();
			}
		}
		else
		{
			AccountUtil util = new AccountUtil();
			String text_message = "Dear "+ util.getUsername(getApplicationContext()) +", enabling Wifi you let " + getString(R.string.app_name)+ " to obtain localization information through wireless networks. This could be very useful for the app to offer you a better service. Do you want to enable Wifi?";
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(text_message).setTitle(getString(R.string.app_name));
			builder.setOnCancelListener(new OnCancelListener() 
			{
		           public void onCancel(DialogInterface dialog) 
		           {
		               finish();
		           }
			});
			
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
			{
		           public void onClick(DialogInterface dialog, int id) 
		           {
		        	   startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS),0);
		           }
			});
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() 
			{
		           public void onClick(DialogInterface dialog, int id) 
		           {
		        	   TaskNotification.getInstance().wifiRequestAnswered();
		   				finish();
		           }
			});
			
			AlertDialog dialog = builder.create();
			dialog.setOnDismissListener(new OnDismissListener()
			{
		           public void onDismiss(DialogInterface dialog) 
		           {
		               finish();
		           }
			});
			dialog.show();
		}
		
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if(requestCode==0)
		{
			if(!wifiManager.isWifiEnabled())
				TaskNotification.getInstance().wifiRequestAnswered();
		}
		finish();
	}
	
	/**
     * Sends a notification asking user to activate Wifi.
     * @param context	Application context
     */
    public static void sendWifiNotification(Context context) 
    {
    	Intent answer = new Intent(context,WifiRequest.class);
    	answer.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
    	PendingIntent notificationClick = PendingIntent.getActivity(context, 2, answer, PendingIntent.FLAG_CANCEL_CURRENT);
    	
    	PendingIntent positiveAnswer = PendingIntent.getActivity(context, 0, answer.putExtra("answer", "ok"),  PendingIntent.FLAG_CANCEL_CURRENT);
    	PendingIntent negativeAnswer = PendingIntent.getActivity(context, 1, answer.putExtra("answer", "no"), PendingIntent. FLAG_CANCEL_CURRENT);
    	
    	Notification newnotification =
    			new Notification.Builder(context)
    			.setSmallIcon(R.drawable.icon)
    			.setContentTitle(context.getText(R.string.app_name))
    			.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.wifi))
    			.setContentText("Wifi needed. Enable?")
    			.setWhen(System.currentTimeMillis())
    			.setContentIntent(notificationClick)
    			//.addAction(R.drawable.ok, "Ok", positiveAnswer)
    			//.addAction(R.drawable.no,"No",negativeAnswer)
    			.setAutoCancel(true)
    			//.build()
    			.getNotification()
    			;
    	
    	newnotification=TaskNotification.getInstance().addNotificationAlertMethod(context,newnotification,"prova",5);
    	//For some reason sometimes notificationManager gets null
    
    	NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    	userSettings = PreferenceManager.getDefaultSharedPreferences(context);
    	NotificationDispatcher.dispatch("wifi", newnotification, notificationManager,userSettings.getString("notification_hint_vibrate", "off"), context);
    }
}