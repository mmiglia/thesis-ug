package com.thesisug.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.thesisug.R;
import com.thesisug.notification.ErrorNotification;
import com.thesisug.notification.NotificationDispatcher;
import com.thesisug.notification.TaskNotification;

public class ParentTab extends TabActivity {
	public final static String TAG = "thesisug - ParentTab";
	AccountManager accountManager;
	String username, session;
	Account[] accounts;
	
	private int currTab=0;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parenttab);
        // start Task Notification service
        startService(new Intent(ParentTab.this, TaskNotification.class));
        //startService(new Intent(ParentTab.this, ErrorNotification.class));
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab

        Intent todo = new Intent(ParentTab.this, Todo.class);
        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("Todo").setIndicator("Activities",
        		res.getDrawable(R.drawable.tab_activity))
        		.setContent(todo);
        tabHost.addTab(spec);
        
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
        tabHost.setCurrentTab(currTab);
        startService(new Intent(ParentTab.this, NotificationDispatcher.class));
        NotificationDispatcher.init((NotificationManager) getSystemService(NOTIFICATION_SERVICE), getApplicationContext());
    }

}
