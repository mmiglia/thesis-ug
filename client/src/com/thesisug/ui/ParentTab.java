package com.thesisug.ui;

import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

import com.thesisug.R;

public class ParentTab extends TabActivity {
	AccountManager accountManager;
	String username, session;
	Account[] accounts;
	private static final int LOGIN_SCREEN = 1;
	private static Intent intent = new Intent();
	private static final String TAG = "ParentTab";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parenttab);
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
		intent.setClass(ParentTab.this, Todo.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("Todo").setIndicator("Activities",
                          res.getDrawable(R.drawable.tab_activity))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        intent.setClass(ParentTab.this, Map.class);
        spec = tabHost.newTabSpec("Map").setIndicator("Maps",
                          res.getDrawable(R.drawable.tab_map))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent.setClass(ParentTab.this, Input.class);
        spec = tabHost.newTabSpec("Input").setIndicator("Input",
                          res.getDrawable(R.drawable.ic_tab_artists))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);

    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		Log.i(TAG, "Im inside onActivityResult");
		accounts = accountManager.getAccountsByType(com.thesisug.Constants.ACCOUNT_TYPE);
		username = accounts[0].name;
	/*	try {
			//session = accountManager.blockingGetAuthToken(accounts[0], com.thesisug.Constants.ACCOUNT_TYPE, true);
			Log.i(TAG, "Im inside onActivityResult2");
			this.intent.putExtra("session", session);
			
			Log.i(TAG, "Im inside onActivityResult3");
			TabHost tabHost = getTabHost(); 
			tabHost.setCurrentTab(0);
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}
