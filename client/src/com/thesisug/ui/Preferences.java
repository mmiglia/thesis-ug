package com.thesisug.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.Constants;
import com.thesisug.R;
import com.thesisug.communication.LoginResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.valueobject.LoginReply;


import com.thesisug.R;
import com.thesisug.communication.LoginResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.valueobject.TestConnectionReply;
import com.thesisug.notification.TaskNotification;



public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	private static final String TAG = "thesisug - PreferenceActivity";
	private Thread tryConnectionThread;
	private final Handler handler = new Handler();
	private String insertedURI="";
	
	private String regExCorrectURL="^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$";
	
	private Button updateServerListBtn=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {

		/*
		 * Manually inserted  server URI
		 */
		Log.d(TAG, "KEY:"+key);
		if(key.equals("serverURI_from_text")){
			insertedURI=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("serverURI_from_text",NetworkUtilities.SERVER_URI);
			if(insertedURI.matches(regExCorrectURL)){
				showDialog(0); //will call onCreateDialog method 
				//Test to verify that the inserted URI is a valid URI. The response of the validation will be sent to changeServerURI method of this class
				tryConnectionThread=LoginResource.tryConnection(insertedURI, handler, Preferences.this);
			}else{
				Toast.makeText(getApplicationContext(), R.string.invalid_server_url, Toast.LENGTH_LONG).show();
			}
		}		
		
		/**
		 * Server URI selected from list
		 */
		if(key.equals("serverURI")){
			Log.d(TAG,"ServerURI change request!");
			String oldURI=NetworkUtilities.SERVER_URI;
			Log.d(TAG,"oldURI:"+oldURI);
			insertedURI=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("serverURI",NetworkUtilities.SERVER_URI);
			Log.d(TAG,"insertedURI:"+insertedURI);
			
			showDialog(0); //will call onCreateDialog method 
			//Test to verify that the inserted URI is a valid URI. The response of the validation will be sent to changeServerURI method of this class
			tryConnectionThread=LoginResource.tryConnection(insertedURI, handler, Preferences.this);
		}


		
	}

    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        
        Log.e(TAG,"insertedURI2:"+insertedURI);
        
	        dialog.setMessage("Try to connect to the server...");
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(true);
	        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                Log.i(TAG, "dialog cancel has been invoked");
	                if (tryConnectionThread != null) {
	                	tryConnectionThread.interrupt();
	                    finish();
	                }
	            }
	        });

        return dialog;
    }	
	
	
	public void changeServerURI(TestConnectionReply result){

		dismissDialog(0); //disable the progress dialog

		
		Log.i(TAG,"Received serverURI:"+result.serverURI);
		if(result.status==1){			
			NetworkUtilities.changeServerURI(result.serverURI);
			
			Toast.makeText(getApplicationContext(), R.string.tryConnectionSuccess,
	                Toast.LENGTH_LONG).show();
				
			//TODO login to the new server and ask tasks and events
			
		}else{
			Toast.makeText(getApplicationContext(), R.string.tryConnectionFail,
	                Toast.LENGTH_LONG).show();
			//TODO comunicate to the user that the server hasn't returned a response as expected
			//the server uri can be found in: result.serverURI
		}
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }


}