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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.LoginResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.valueobject.TestConnectionReply;

public class SystemStatus extends Activity {
	private static final String TAG = "thesisug - SystemStatus";
	public final static int UPDATE_STATUS = 0;
	public final static int BACK = 1;
	private String locationProvider;
    private LocationManager locManager;
    private Criteria criteria;
    private Location userLocation;
	private Thread tryConnectionThread;
	private final Handler handler = new Handler();
	
    //Location
    private TextView txt_location_provider;
    private TextView txt_last_location_latitude;
    private TextView txt_last_location_longitude;

    //Server
    private TextView txt_server_name_value;
    private TextView txt_server_connection_status_value;
    
    private TextView txt_server_status;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.system_status);
        criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);
    	locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    	
    	
    	txt_location_provider=(TextView)findViewById(R.id.location_provider_status_value);
    	txt_last_location_latitude=(TextView)findViewById(R.id.current_position_status_latitude);
        txt_last_location_longitude=(TextView)findViewById(R.id.current_position_status_longitude);
        
        txt_server_name_value=(TextView)findViewById(R.id.server_name_value);
        txt_server_connection_status_value=(TextView)findViewById(R.id.server_connection_status_value);
        
        updateSystemStatus();
        
    	
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,UPDATE_STATUS,0,"Update").setIcon(R.drawable.sync);
		menu.add(0,BACK,0,"BACK").setIcon(R.drawable.exit);
		return true;
	}
	
	private boolean updateSystemStatus(){
		//Server status
		txt_server_name_value.setText(NetworkUtilities.SERVER_URI);
		//Server connection
		showDialog(0); //will call onCreateDialog method 
		tryConnectionThread=LoginResource.tryConnection(NetworkUtilities.SERVER_URI, handler, SystemStatus.this);
		
		
		//Location service
		
		//Location provider		
		locationProvider=locManager.getBestProvider(criteria,true);
		if(locationProvider==null ){
			txt_location_provider.setText(R.string.location_provider_status_ko);
		}else{
			txt_location_provider.setText(locationProvider);
		}
		//Current location
		if(locationProvider!=null ){
			userLocation = locManager.getLastKnownLocation(locationProvider);
			if(userLocation==null ){
				
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
		default:
			finish();
			break;
		}
		return true;
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
	                	tryConnectionThread.interrupt();
	                    finish();
	                }
	            }
	        });

        return dialog;
    }	
	
	public void tryConnection(TestConnectionReply result) {
		dismissDialog(0); //disable the progress dialog
		Log.d(TAG, "Status:"+result.status);
		if(result.status==1){
			txt_server_connection_status_value.setText(R.string.server_connection_ok);
		}else{
			txt_server_connection_status_value.setText(R.string.server_connection_ko);
		}
		
	}
}
