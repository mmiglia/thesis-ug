package com.thesisug.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

import com.thesisug.R;

public class ParentTab extends TabActivity {
	AccountManager accountManager;
	String username, session;
	Account[] accounts;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parenttab);
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

        Intent input = new Intent(ParentTab.this, Input.class);
        spec = tabHost.newTabSpec("Input").setIndicator("Input",
        		res.getDrawable(R.drawable.ic_tab_artists))
        		.setContent(input);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
}
