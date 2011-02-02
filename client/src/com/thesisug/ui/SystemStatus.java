package com.thesisug.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.Constants;
import com.thesisug.R;
import com.thesisug.communication.LoginResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.valueobject.TestConnectionReply;

public class SystemStatus extends Activity {
	private static final String TAG = "thesisug - SystemStatus";
	public final static int UPDATE_STATUS = 0;
	public final static int BACK = 1;
	public final static int FIX_LOCATION_PROBLEM=2;
	public final static int CHANGE_SERVER=3;
	
	private String locationProvider;
    private LocationManager locManager;
    private Criteria criteria;
    private Location userLocation;
	private Thread tryConnectionThread, checkVerThread;
	private final Handler handler = new MyHandler();
	
    //Location
    private TextView txt_location_provider;
    private TextView txt_last_location_latitude;
    private TextView txt_last_location_longitude;

    //Server
    private TextView txt_server_version;
    private TextView txt_server_name_value;
    private TextView txt_server_connection_status_value;
    
    //Client
    private TextView txt_client_version;
    
    private class MyHandler extends Handler {
    	@Override
        public void handleMessage(Message msg) {
    		Bundle bundle = msg.getData();
    		Log.i(TAG, "Valori: status="+bundle.getInt("status")+" serverVersion="+bundle.getString("serverVersion")+" versionOK="+bundle.getBoolean("versionOk")+" serverURI="+bundle.getString("serverURI"));
    		if(bundle.containsKey("serverVersion")) {
    			txt_server_version.setText(bundle.getString("serverVersion"));
    			
    		}
        }

    }
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_status);
        criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
    	txt_client_version=(TextView)findViewById(R.id.client_ver_value);
    	
    	txt_server_version=(TextView)findViewById(R.id.server_ver_value);
    	txt_server_name_value=(TextView)findViewById(R.id.server_name_value);
    	txt_server_connection_status_value=(TextView)findViewById(R.id.server_connection_status_value);
    	
    	txt_location_provider=(TextView)findViewById(R.id.location_provider_status_value);
    	txt_last_location_latitude=(TextView)findViewById(R.id.current_position_status_latitude);
        txt_last_location_longitude=(TextView)findViewById(R.id.current_position_status_longitude);
        
        
        
        
        
        txt_server_connection_status_value.setText(getText(R.string.checking_server_connection));
        updateSystemStatus();
        
    	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,UPDATE_STATUS,0,"Update").setIcon(R.drawable.sync);
		menu.add(0,FIX_LOCATION_PROBLEM,0,"Select location provider").setIcon(R.drawable.sync);
		menu.add(0,CHANGE_SERVER,0,"Change server").setIcon(R.drawable.sync);
		menu.add(0,BACK,0,"BACK").setIcon(R.drawable.exit);
		return true;
	}
	
	private boolean updateSystemStatus(){
		//Client status
		txt_client_version.setText(Constants.VERSION);
		
		//Server status
		Log.i(TAG, "ServerURI="+NetworkUtilities.SERVER_URI);
		//String url = 
		checkVerThread=NetworkUtilities.checkVersion(NetworkUtilities.SERVER_URI, Constants.VERSION, handler, SystemStatus.this);
		txt_server_name_value.setText(NetworkUtilities.SERVER_URI);
		//Server connection
		//showDialog(0); //will call onCreateDialog method 
		tryConnectionThread=NetworkUtilities.tryConnection(NetworkUtilities.SERVER_URI,true, handler, SystemStatus.this);
		
		
		//Location service
		
		//Location provider		
		if(locManager==null ){
			txt_location_provider.setText(R.string.location_provider_status_ko);
	    	locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		}
		locationProvider=locManager.getBestProvider(criteria,true);
		if(locationProvider==null ){
			txt_location_provider.setText(R.string.location_provider_status_ko);
		}else{
			txt_location_provider.setText(locationProvider.toUpperCase());
		}
		//Current location
		if(locationProvider!=null ){
			userLocation = locManager.getLastKnownLocation(locationProvider);
			if(userLocation==null ){
				txt_last_location_latitude.setText(R.string.user_position_unknown);
				
			}else{
				txt_last_location_latitude.setText("Lat: "+userLocation.getLatitude());
				txt_last_location_longitude.setText("Lon: "+userLocation.getLongitude());
			}
		}

		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case UPDATE_STATUS:
			updateSystemStatus();
			break;				
		case FIX_LOCATION_PROBLEM:
			startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
			break;
		case CHANGE_SERVER:
			startActivityForResult(new Intent(SystemStatus.this,Preferences.class), 0);
			break;
		default:
			finish();
			break;
		}
		return true;
	}


    protected Dialog onCreateDialog(int id) {
        /*final ProgressDialog dialog = new ProgressDialog(this);
        
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
        */
    	return null;
    }	
	
	public void tryConnection(TestConnectionReply result) {
		//dismissDialog(0); //disable the progress dialog
		Log.d(TAG, "Status:"+result.status);
		if(result.status==1){
			txt_server_connection_status_value.setText(getText(R.string.server_connection_ok));
		}else{
			txt_server_connection_status_value.setText(getText(R.string.server_connection_ko));
		}
		
	}
}
