package com.thesisug.ui;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import com.thesisug.R;

public class Assertions extends TabActivity{
	
	Intent intent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assertions);
		
		 	Resources res = getResources(); // Resource object to get Drawables
		 	TabHost tabHost = getTabHost();  // The activity TabHost
		    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
		      // Reusable Intent for each tab

		    // Create an Intent to launch an Activity for the tab (to be reused)
		    intent = new Intent().setClass(this, ViewAssertions.class);

		    // Initialize a TabSpec for each tab and add it to the TabHost
		    spec = tabHost.newTabSpec("Item-Location").setIndicator("Item-Location",
		                      res.getDrawable(R.drawable.ic_tab_assertions_itemlocation))
		                  .setContent(intent);
		    tabHost.addTab(spec);

		    // Do the same for the other tabs
		    intent = new Intent().setClass(this, ViewAssertions_Action.class);
		    spec = tabHost.newTabSpec("Action-Location").setIndicator("Action-Location",
		                      res.getDrawable(R.drawable.ic_tab_assertions_actionlocation))
		                  .setContent(intent);
		    tabHost.addTab(spec);

		     	tabHost.setCurrentTab(0);
			
			
	}	
	


}
