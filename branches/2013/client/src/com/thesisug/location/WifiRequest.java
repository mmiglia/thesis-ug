package com.thesisug.location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.AccountUtil;
import com.thesisug.notification.TaskNotification;

public class WifiRequest extends Activity 
{
	private Bundle packet; 
	private WifiManager wifiManager;
	private static final int OK = 0;
	private static final int NO = 1;

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
			String text_message = "Dear "+ util.getUsername(getApplicationContext()) +", enabling Wifi you let " + getString(R.string.app_name)+ " to obtain localization information through wireless networks. This could be very useful for the app to offer you a better service. Do you want to enable Gps?";
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(text_message).setTitle(getString(R.string.app_name));
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
}