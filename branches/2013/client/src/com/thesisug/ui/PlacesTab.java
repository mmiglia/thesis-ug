package com.thesisug.ui;

import java.util.Calendar;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;

import com.thesisug.R;
import com.thesisug.tracking.ActionTracker;

public class PlacesTab extends TabActivity{
	
	Intent intent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placestab);
		
		Resources res = getResources(); // Resource object to get Drawables
	 	TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	      // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, PrivatePlaces.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Private Places").setIndicator("Private Places",
	                      res.getDrawable(R.drawable.elvis))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tabs
	    intent = new Intent().setClass(this, PublicPlaces.class);
	    spec = tabHost.newTabSpec("Public Places").setIndicator("Public Places",
	                      res.getDrawable(R.drawable.persongroup))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	     	tabHost.setCurrentTab(0);
		
	}
	
	//this method create a different menu depending on wich tab is selected
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		Activity thisActivity = this.getCurrentActivity();
		
		boolean ret = thisActivity.onCreateOptionsMenu(menu);
		return ret;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Activity thisActivity = this.getCurrentActivity();
		
		boolean ret = thisActivity.onOptionsItemSelected(item);
		return ret;
	}

}
