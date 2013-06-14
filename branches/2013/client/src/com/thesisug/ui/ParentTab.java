package com.thesisug.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.location.ActivityRecognitionRemover;
import com.thesisug.location.ActivityRecognitionRequester;
import com.thesisug.notification.NotificationDispatcher;
import com.thesisug.notification.SnoozeHandler;
import com.thesisug.notification.TaskNotification;
import com.thesisug.tracking.ActionTracker;

public class ParentTab extends TabActivity 
{
	public final static String TAG = "thesisug - ParentTab";
	AccountManager accountManager;
	String username, session; 
	Account[] accounts;
	TabHost tabHost;// The activity TabHost
	TabHost.TabSpec spec;// Reusable TabSpec for each tab
	Bundle packet;
	private int currTab=0;
	
	public final static int BACKGROUND=10;
	public final static int EXIT=11;
	protected Intent taskNotificationIntent;
	private Intent notificationDispatcherIntent;
	private ActivityRecognitionRequester activityRecognitionRequester;
	private ActivityRecognitionRemover activityRecognitionRemover;

	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate.");
        setContentView(R.layout.parenttab);
        // start Task Notification service
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
        	Log.d(TAG,"Location service off.");
        	final AlertDialog.Builder builder = new AlertDialog.Builder(ParentTab.this);
        	builder.setTitle(R.string.app_name);
        	builder.setMessage("The location service is off. This app can't work without location servicec, do you want to turn it on?");
            builder.setPositiveButton("Yes.",
            new DialogInterface.OnClickListener() 
            {
            	@Override
            	public void onClick(final DialogInterface dialogInterface,final int i) 
            	{
                	startActivityForResult(new Intent(
                	android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),1);
                }
            });
            builder.setNegativeButton("No (Exit).", null);
            builder.create().show();
        }
        else
        {
        	Log.d(TAG,"Location service on.");
        	finishOnCreate();
        }
    }
	
	@Override
	public void onDestroy()
	{
	super.onDestroy();	
	}
	/**
	 * Finish onCreate duties if location service is enabled!
	 */
	private void finishOnCreate()
	{
		taskNotificationIntent = new Intent(ParentTab.this, TaskNotification.class);
    	ComponentName componentName = startService(taskNotificationIntent);
    	notificationDispatcherIntent = new Intent(ParentTab.this, NotificationDispatcher.class);
    	startService(notificationDispatcherIntent);
    	NotificationDispatcher.init((NotificationManager) getSystemService(NOTIFICATION_SERVICE), getApplicationContext());
	     
        //startService(new Intent(ParentTab.this, ErrorNotification.class));
        Resources res = getResources(); // Resource object to get Drawables
        tabHost = getTabHost();
        packet = getIntent().getExtras();
        //Change color to tabHost tabs
        tabHost.getTabWidget().setBackgroundColor(Color.DKGRAY);
        
       
        // Initialize a TabSpec for each tab and add it to the TabHost
        addTab("Todo","Activities",R.drawable.tab_activity);
        addTab("Map","Maps",R.drawable.tab_map);
        addTab("Preferences","Preferences",R.drawable.preferences);
        /*
        Intent map = new Intent(ParentTab.this, Map.class);
        spec = tabHost.newTabSpec("Map").setIndicator("Maps",
        		res.getDrawable(R.drawable.tab_map))
        		.setContent(map);
        tabHost.addTab(spec);

        Intent input = new Intent(ParentTab.this, Preferences.class);
        spec = tabHost.newTabSpec("Preferences").setIndicator("Preferences",
        		res.getDrawable(R.drawable.preferences))
        		.setContent(input);
        tabHost.addTab(spec);
       
        */
        
        
        
        /*Bundle packet;        
        if(getIntent()!=null){
        	packet = getIntent().getExtras();
	        if(packet!=null){
	        	currTab=packet.getInt("tabNumber");
	        }else{
	        	currTab=0;
	        }
        }
        */
        
        //Obtain a reference to ActionBar
        ActionBar actionBar = getActionBar();
              
        //Disable showing title
        //actionBar.setDisplayShowTitleEnabled(false);
        //Disable showing icon
        //actionBar.setDisplayShowHomeEnabled(false);
        
        //Ivalidates menu options every time tab is changed
        
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() 
        {
			
			@Override
			public void onTabChanged(String tabId) 
			{
				invalidateOptionsMenu();				
			}
		});
        
        tabHost.setCurrentTab(currTab);
       
      
        if(packet!=null)
        	if(packet.getInt("maptab")==1)
        		tabHost.setCurrentTab(1);
        
        Calendar calendar = Calendar.getInstance();
        ActionTracker.Init(calendar.getTime(), getApplicationContext());
        
        ActionTracker.appOpened(calendar.getTime(), getApplicationContext());
        
        //activityRecognitionRequester = new ActivityRecognitionRequester(getBaseContext());
        //activityRecognitionRemover = new ActivityRecognitionRemover(getBaseContext());
       // activityRecognitionRequester.requestUpdates();
	}
	/**
	 * @author Alberto Servetti
	 * @when 13/04/2013 
	 * @param tag	tag used for TabSpec
	 * @param text	name displayed on the tab
	 * @param drawableId	icon displayed on the tab
	 */
	private void addTab(String tag, String text, int drawableId)
	{
		Log.i(TAG,"addTab");
		Intent intent;
		if(tag.equals("Todo"))
			 intent = new Intent(ParentTab.this, Todo.class);
		else
			if(tag.equals("Map"))
				intent = new Intent(ParentTab.this, Map.class);
			else
				if(tag.equals("Preferences"))
					intent = new Intent(ParentTab.this, Preferences.class);
				else
					return;

		 if(packet!=null)
		 {
	        	if(packet.getInt("maptab")==1)
	        		{
	        			intent.putExtra("hintlist",(ArrayList<Hint>) packet.get("hintlist"));
	        			intent.putExtra("selectedPos",packet.getInt("selectedPos"));
	        			intent.putExtra("tasktitle",packet.getString("tasktitle"));
	        			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        		}
	        	else
	        		Log.i(TAG,"No Map Tab.");
		 }
		 else
			 Log.i(TAG,"Packet null.");
		spec = tabHost.newTabSpec(tag);
        View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tab_layout, tabHost.getTabWidget(), false);
        TextView title = (TextView) tabIndicator.findViewById(R.id.tab_title);
        title.setText(text);
        ImageView icon = (ImageView) tabIndicator.findViewById(R.id.tab_icon);
        icon.setImageResource(drawableId);
        spec.setIndicator(tabIndicator);
        spec.setContent(intent);
        tabHost.addTab(spec);
	}
	
	//this method create a different menu depending on wich tab is selected
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		//Get a reference to current tab Activity
		Activity thisActivity = this.getCurrentActivity();
		
		boolean ret = thisActivity.onCreateOptionsMenu(menu);
		menu.add(0,BACKGROUND,0,"Background").setIcon(R.drawable.back).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0,EXIT,0,"Exit").setIcon(R.drawable.exit).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return ret;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		boolean ret = true;
			switch (item.getItemId())
			{
			case BACKGROUND:
				finish();
				break;
			case EXIT:
				stopService(notificationDispatcherIntent);
				stopService(taskNotificationIntent);
				//activityRecognitionRemover.removeUpdates(activityRecognitionRequester.getRequestPendingIntent());
				ActionTracker.appClosed(Calendar.getInstance().getTime(), getApplicationContext());
				//android.os.Process.killProcess(android.os.Process.myPid());
				//System.exit(0);
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.cancelAll();
				super.finish();
				finish();
				break;
				default:
			//Get a reference to current tab Activity
			Activity thisActivity = this.getCurrentActivity();
			//The specific tab Activity handles button press
			ret = thisActivity.onOptionsItemSelected(item);
			break;
			}
			return ret;
	}
	/**
	 * This method is called when user clicks an Hint in HintList class. Instead
	 * of creating a new Activity, now the Intent is submitted to existing Activity
	 * and the overload is fewer.
	 * 
	 * @author Alberto Servetti
	 * @date 22/04/13
	 */
	@Override
	public void onNewIntent(Intent intent)
	{
		Log.i(TAG,"onNewIntent");
        Resources res = getResources(); // Resource object to get Drawables
        tabHost = getTabHost();
        tabHost.clearAllTabs();
        packet = intent.getExtras();
        //Change color to tabHost tabs
        tabHost.getTabWidget().setBackgroundColor(Color.DKGRAY);
        startService(notificationDispatcherIntent);
        startService(taskNotificationIntent);
        // Initialize a TabSpec for each tab and add it to the TabHost
        addTab("Todo","Activities",R.drawable.tab_activity);
        addTab("Map","Maps",R.drawable.tab_map);
        addTab("Preferences","Preferences",R.drawable.preferences);
        
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) 
			{
				invalidateOptionsMenu();				
			}
		});
        tabHost.setCurrentTab(currTab);
        NotificationDispatcher.init((NotificationManager) getSystemService(NOTIFICATION_SERVICE), getApplicationContext());
        
      
        if(packet!=null)
        {
        	if(packet.getInt("maptab")==1)
        		tabHost.setCurrentTab(1);
        }
        else
        	Log.i(TAG,"Packet null");
		
	}
	/**
	 * Called after AlertDialog for enablig location services.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if(requestCode == 1)
		{
			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			if(!lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			{
				this.finish();
			}
			else
			{
				finishOnCreate();
			}
		}
	}
}
