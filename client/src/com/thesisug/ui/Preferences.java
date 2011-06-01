package com.thesisug.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
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
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.Constants;
import com.thesisug.R;
import com.thesisug.communication.LoginResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.valueobject.LoginReply;

import com.thesisug.communication.valueobject.TestConnectionReply;
import com.thesisug.notification.TaskNotification;



public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "thesisug - PreferenceActivity";
	private Thread tryConnectionThread, checkVersionThread;
	private final Handler handler = new Handler();
	private final Handler handlerVer = new MyHandler();
	private boolean VERSION_OK = false;
	private String insertedURI="";
	public String navigator;
	private static int currentDialog=0;
	private static SharedPreferences userSettings;
	
	private String regExCorrectURL="^([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,6}$";
	
	private Button updateServerListBtn=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preference);
		userSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		updateVibrationModeSummary("notification_hint_vibrate");
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
		

		/*
		 * Manually inserted  server URI
		 */
		Log.d(TAG, "KEY:"+key);
		
		if(key.equals("parserlang")){
			String pl = userSettings.getString("parserlang", "en.lang");
			//Toast.makeText(getApplicationContext(), "cambiato parser language in "+pl, Toast.LENGTH_SHORT).show();
		}
		
		if (key.equals("selected_navigator")){
			
			navigator = userSettings.getString("selected_navigator", "ListGoogle");
			Toast.makeText(getApplicationContext(), navigator, Toast.LENGTH_SHORT).show();
		}
			
		if(key.equals("serverURI_from_text")){
			insertedURI=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("serverURI_from_text",NetworkUtilities.SERVER_URI);
			Log.d(TAG,"insertedURI:"+insertedURI);
			//Check retreive serverURI
			if(insertedURI.equals(NetworkUtilities.SERVER_URI)){
				//Got default value 
				Toast.makeText(getApplicationContext(), R.string.error_during_retreiving_server_address, Toast.LENGTH_LONG).show();
				return;
			}

			if(insertedURI.matches(regExCorrectURL)){
				currentDialog=0;
				showDialog(currentDialog); //will call onCreateDialog method 
				//Test to verify that the inserted URI is a valid URI. The response of the validation will be sent to changeServerURI method of this class
				//tryConnectionThread=LoginResource.tryConnection(insertedURI, handler, Preferences.this);
				tryConnectionThread=NetworkUtilities.tryConnection(insertedURI,false, handler, Preferences.this);
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
			Log.d(TAG,"SelectedURI:"+insertedURI);
			
			showDialog(0); //will call onCreateDialog method 
			//Test to verify that the inserted URI is a valid URI. The response of the validation will be sent to changeServerURI method of this class
			//tryConnectionThread=LoginResource.tryConnection(insertedURI, handler, Preferences.this);
			tryConnectionThread=NetworkUtilities.tryConnection(insertedURI,false, handler, Preferences.this);
		}
//		if (key.equals("notification_hint_vibrate")) {
//			Log.d(TAG, "vibration change request");
//			// if vibration mode change was requested
//			// change is communicated through a toast
//			// message and summary (little notice below
//			// preference name) is updated and can be
//			// checked with a single glance
//			updateVibrationModeSummary(key);
//			
//			/* Toast.makeText(getApplicationContext(), res.getString(R.string.vibration_change_message)
//			
//					+ " " + userSettings.getString(key, "off"),
//					Toast.LENGTH_LONG).show();*/	
//		}
		// update preferences displayed in the user interface
		// to reflect changes performed from another Activity
		// (e.g. using setQuiet())
		if (key.equals("notification_hint_sound")) {
			Log.v(TAG, "Updating widget for " + key);
			Log.v(TAG, "sound");
			CheckBoxPreference soundOnOff =
				(CheckBoxPreference) getPreferenceScreen().findPreference(key);
			soundOnOff.setChecked(userSettings.getBoolean(key, false));
		} else if (key.equals("notification_hint_speak")) {
			Log.v(TAG, "Updating widget for " + key);
			Log.v(TAG, "speak");
			CheckBoxPreference speakOnOff =
				(CheckBoxPreference) getPreferenceScreen().findPreference(key);
			speakOnOff.setChecked(userSettings.getBoolean(key, false));
		} else if (key.equals("notification_hint_vibrate")) {
			Log.v(TAG, "Updating widget for " + key);
			Log.v(TAG, "vibration");
			ListPreference morseMode =
				(ListPreference) getPreferenceScreen().findPreference(key);
			morseMode.setValue(userSettings.getString(key, "off"));
			updateVibrationModeSummary(key);
		}
	}
	
	private void updateVibrationModeSummary(String key) {
		String vibrationMode = userSettings.getString(key, "off");
		ListPreference vibratePreference = (ListPreference) getPreferenceScreen().findPreference(key);
		String newSummary = getResources().getString(R.string.set_vibration_summary_customized)
		+ " currently " + vibrationMode.toUpperCase();
		vibratePreference.setSummary(newSummary);
	}

    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        
        
	        dialog.setMessage("Try to connect to the server...");
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(true);
	        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
	            public void onCancel(DialogInterface dialog) {
	                Log.i(TAG, "dialog cancel has been invoked");
	                if (tryConnectionThread != null) {
	                	currentDialog=-1;
	                	tryConnectionThread.interrupt();
	                    finish();
	                    
	                }
	            }
	        });

        return dialog;
    }	
	
    private class MyHandler extends Handler {
    	@Override
        public void handleMessage(Message msg) {
    		//dismissDialog(1);
    		Bundle bundle = msg.getData();
    		if(bundle.containsKey("status")) {
    			Log.i(TAG, "Recieved values: status="+bundle.getInt("status")+
    					" serverVersion="+bundle.getString("serverVersion")+
    					" versionOk="+bundle.getBoolean("versionOk")+
    					" serverURI="+bundle.getString("serverURI"));
    			int value = bundle.getInt("status");
    			boolean versionOk = bundle.getBoolean("versionOk");
				if (value==1) {
					Toast.makeText(getApplicationContext(), R.string.client_not_compatible, Toast.LENGTH_SHORT).show();
				}
				if (value==2 && !versionOk) {
					// value=2 -> client version compatible with server version
					// VERSION_OK=false -> server version not compatible with client version
					Toast.makeText(getApplicationContext(), R.string.server_not_compatible, Toast.LENGTH_SHORT).show();
				}
				if (value==2 && versionOk) {
					// value=2 -> client version compatible with server version
					// VERSION_OK=true -> server version not compatible with client version
//					usersettings.edit().putString("ServerURI", bundle.getString("serverURI"));
//    				NetworkUtilities.changeServerURI(bundle.getString("serverURI"));
    				VERSION_OK = true;
					//Toast.makeText(getApplicationContext(), R.string.client_server_version_ok, Toast.LENGTH_SHORT).show();
				}
				if (value==404) {
					Toast.makeText(getApplicationContext(), R.string.tryConnectionFail, Toast.LENGTH_SHORT).show();
				}
    		}
        }

    }
	
	public void changeServerURI(TestConnectionReply result){

		dismissDialog(0); //disable the progress dialog
		//Toast.makeText(getApplicationContext(), "sono qua", Toast.LENGTH_SHORT);

		//The request has been interrupted
		if(currentDialog==-1){
			return;
		}
		
		dismissDialog(currentDialog); //disable the progress dialog
				
		Log.i(TAG,"Tested serverURI:"+result.serverURI);
		if(result.status==1){			
			Log.i(TAG,"Tested serverURI:"+result.serverURI+" - server reachable");
			//Log.i(TAG, "result.status=1 -> controllo versione");
			//Log.i(TAG, "result.serverURI="+result.serverURI+" version="+Constants.VERSION);
			checkVersionThread = NetworkUtilities.checkVersion(result.serverURI, Constants.VERSION, handlerVer, Preferences.this);
			
			if (VERSION_OK) {
				//Log.i(TAG, "VERSION_OK=true");
				NetworkUtilities.changeServerURI(result.serverURI);
				//Log.i(TAG, "VERSION_OK=true - changed serverUri");
				Toast.makeText(getApplicationContext(), R.string.tryConnectionSuccess, Toast.LENGTH_LONG).show();
				//Log.i(TAG, "VERSION_OK=true - visualizzato toast");
			}
			else {
				//Log.i(TAG, "VERSION_OK=false");
				Toast.makeText(getApplicationContext(), R.string.change_server_ver_not_compatible, Toast.LENGTH_LONG);
			}
			//TODO login to the new server and ask tasks and events
			
		}else{
			Log.i(TAG,"Tested serverURI:"+result.serverURI+" - server NOT reachable");
			Toast.makeText(getApplicationContext(), R.string.tryConnectionFail, Toast.LENGTH_LONG).show();
			//TODO comunicate to the user that the server hasn't returned a response as expected
			//the server uri can be found in: result.serverURI
		}
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes            
        // commented out to allow preference screen widgets to be updated
        // when phone is shaked with the aim of shutting it up
        // listener is registered once when the activity is created (onCreate())
//        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes           
        // commented out to allow preference screen widgets to be updated
        // when phone is shaked with the aim of shutting it up
        // listener is registered once when the activity is created (onCreate())
//        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);    
    }
    public static void setQuiet() {
    	Log.v(TAG, "entering setQuiet");
    	// set notifications to be quiet
		Editor settingsEditor = userSettings.edit();
		settingsEditor.putString("notification_hint_vibrate", "off");
		settingsEditor.putBoolean("notification_hint_sound", false);
		settingsEditor.putBoolean("notification_hint_speak", false);
		settingsEditor.commit();
		Log.v(TAG, "leaving setQuiet");
    }
}