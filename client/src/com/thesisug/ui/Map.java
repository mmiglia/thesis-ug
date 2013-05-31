package com.thesisug.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thesisug.R;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.notification.TaskNotification;
import com.thesisug.tracking.ActionTracker;
/**/


public class Map extends Activity 
{
	private static final String TAG = "thesisug - Map Activity";
	
	
	@SuppressWarnings("unused")
	private Thread downloadThread;
	private Handler handler = new Handler();
    private Location userLocation;
    private static SharedPreferences usersettings;
    
    private static TextView txtHint;
    private static Button btnPrevHint;
    private static Button btnNextHint;
    
	public final static int FORCE_HINT_SEARCH=0;
	public final static int TODO_LIST = 1;
	public final static int GET_MAP=2;
	public final static int INFO=3;
	
	private Bundle packet; 
	private int baseZoom=16;
	
	private static ArrayList<Hint> hintlistToShow;
	private static int hintToShow;
	private static Hint selectedHint;
	
	private static final ArrayList<String> tasks_with_hints = new ArrayList<String>();
	private static final Hashtable<String, List<Hint>> map_todoElem_hints=new Hashtable<String, List<Hint>>();
	
	/** Added 8/04/2013 by Alberto Servetti*/
	 
	private GoogleMap mMap;
	
	private boolean showingHint = false;
	
	private CircleOptions accuracyCircleOptions;
	private Circle accuracyCircle;
	private Marker mrk;
	
	
	private static final int LOCATIONCHANGED = 0;
	private static final int PROVIDERENABLED = 1;
	private static final int PROVIDERDISABLED = 2;
	private static final int STATUSCHANGED = 3;


	private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	//receive messages from TaskNotification to update position on map;
	private BroadcastReceiver receiver = new BroadcastReceiver() 
	{
        @Override
        public void onReceive(Context context, Intent intent) 
        {
           	int type = intent.getExtras().getInt("messagetype");
           	switch(type)
           	{
           	case LOCATIONCHANGED:
           		locationChanged((Location) intent.getExtras().get("location"));
           		break;
           	case PROVIDERENABLED:
           		providerEnabled(intent.getExtras().getString("provider"));
           		break;
           	case PROVIDERDISABLED:
           		providerDisabled(intent.getExtras().getString("provider"));
           		break;
           	case STATUSCHANGED:
           		statusChanged(intent.getExtras().getString("provider"), 
           				intent.getExtras().getInt("status"), 
           				(Bundle)intent.getExtras().get("extras"));
           		break;
           		
           	}
        }
    };
    /**
     * Shows an error dialog.
     * 
     * @author Alberto Servetti
     */
    

	/***************************************/
	 
	
	
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
    	// get the drawables and setup of overlays
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.map);  
        
        Log.i(TAG,"onCreate");
     
        if(googlePlayServicesConnected())
        {
        	onCreateGPServicesConnected();
        }
    }
	
	private void onCreateGPServicesConnected()
	{
		 mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	        
	        Log.d(TAG,"Registering BroadcastReceiver");
	        IntentFilter filter = new IntentFilter();
	        filter.addAction("com.thesisug.location");
	        registerReceiver(receiver, filter);
	        
	        if(mMap==null)
	        {
	        	AlertDialog ad = new AlertDialog.Builder(this).create();  
	        	ad.setMessage("mMap null");  
	        	ad.show();
	        }
	        
	        //Sets the map type to be "normal"
	        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	 
	    	usersettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	        
	    	userLocation = TaskNotification.getInstance().getLastKnownLocation();
	        setUserLocatiOnOnMapAndCheckHints(userLocation,true);
	        
	        packet = getIntent().getExtras();
	        Log.v(TAG,"Packet null:"+String.valueOf(packet==null));
	        
	        //Elements on top of the map
	        txtHint=(TextView)findViewById(R.id.map_txt_Hint);
	        btnPrevHint=(Button)findViewById(R.id.map_btn_prev_Hint);
	        
	        btnPrevHint.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v)
	        	{
	        		if(hintlistToShow!=null && selectedHint!=null)
	        		{
	        			hintToShow--;
	        			if(hintToShow<1)
	        			{
	        				hintToShow=hintlistToShow.size();
	        			}
	        			selectedHint=hintlistToShow.get(hintToShow-1);
	        			highlightThisHint(selectedHint);
	        			
	        		}
	        		else
	        		{
	        			Toast.makeText(getApplicationContext(), "No hint to show",Toast.LENGTH_SHORT).show();
	        		}
	        	}
	        });
	
	        
	        btnNextHint=(Button)findViewById(R.id.map_btn_next_Hint);
	        
	        btnNextHint.setOnClickListener(new OnClickListener()
	        {
	        	public void onClick(View v)
	        	{
	        		if(hintlistToShow!=null && selectedHint!=null)
	        		{
	        			hintToShow++;
	        			if(hintToShow>hintlistToShow.size())
	        			{
	        				hintToShow=1;
	        			}
	        			selectedHint=hintlistToShow.get(hintToShow-1);
	        			highlightThisHint(selectedHint);
	        		}else
	        		{
	        			Toast.makeText(getApplicationContext(), "No hint to show",Toast.LENGTH_SHORT).show();
	        		}
	        	}
	        });
	       
	        //The packet arrive from the intent sent by the HintList activity
	        if(packet!=null)
	        {
	        	
	        	//clear previous hints
	        	mMap.clear();
	        	
	        	//redraw user marker
	        	mrk = mMap.addMarker(new MarkerOptions().position(new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).title("This is you!").snippet("Don't you like this marker?").icon(BitmapDescriptorFactory.fromResource(R.drawable.man)).anchor((float)0.5,(float)0.5));
	        	//Add circle representing the error
		        // Instantiates a new CircleOptions object and defines the center and radius
		        accuracyCircleOptions = new CircleOptions()
		        .center(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()))
		        .radius(userLocation.getAccuracy())
		        //Fill color of the circle
	            // 0x represents, this is an hexadecimal code
	            // 55 represents percentage of transparency. For 100% transparency, specify 00.
	            // For 0% transparency ( ie, opaque ) , specify ff
	            // The remaining 6 characters(FF0000) specify the fill color (red)
		        .fillColor(0x50FF0000)
		        .strokeColor(Color.RED)
		        .strokeWidth(1);
		        accuracyCircle = mMap.addCircle(accuracyCircleOptions);
		        
	        	hintlistToShow=(ArrayList<Hint>) packet.get("hintlist");
	        	String tasktitle=packet.getString("tasktitle");
	        	
	        	Log.i(TAG,"Got "+hintlistToShow.size()+" hints");
	        	hintToShow=packet.getInt("selectedPos");
	        	Log.i(TAG,"Got to show the number "+hintToShow);
	        	selectedHint=hintlistToShow.get(hintToShow-1);
	        	Log.i(TAG,"Got to show "+selectedHint.title);
	        	//Draw all hints except the selected one
	        	afterHintsAcquired(tasktitle,hintlistToShow,selectedHint);
	        	
	        	showHints(hintlistToShow);
	    		
	        	showingHint=true;
	        }
	        else
	        {
	        	showingHint=false;
	        }
	        
	        accuracyCircleOptions = new CircleOptions();
	}
	
	/**
	 * Check if Google Play Services are avaiable.
	 * @return	True if they are, false otherwise.
	 */
	 private boolean googlePlayServicesConnected() 
	 {
        // Check that Google Play services is available
        int resultCode =GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (resultCode == ConnectionResult.SUCCESS) 
        {
            Log.d(TAG,"Google Play services is available.");
            return true;
        } 
        else 
        {	
            // Get the error code
            int errorCode = resultCode;
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode,this,CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) 
            {
            	errorDialog.show();

            }
            return false;
        }
	 }
	
	@Override
	public void onDestroy()
    {
		super.onDestroy();
		Log.i(TAG,"onDestroy");
		Log.d(TAG,"Unregistering BroadcastReceiver");
		unregisterReceiver(receiver);
    }
	
	//Highlight a selected hint
	private void highlightThisHint(Hint selHint)
	{
		selectedHint=selHint;
		LatLng placePoint = new LatLng(Double.parseDouble(selHint.lat),Double.parseDouble(selHint.lng));
		txtHint.setText(selHint.titleNoFormatting);
		//Add a marker to selected Hint point and shows marker infos
    	Marker thisHint = mMap.addMarker(new MarkerOptions().position(placePoint).title(selHint.titleNoFormatting).snippet(selHint.streetAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal_2)));
    	thisHint.showInfoWindow();
    	//Moves camera to hint geopoint
    	mMap.moveCamera(CameraUpdateFactory.newLatLng(placePoint));
    	Toast.makeText(getApplicationContext(),selectedHint.titleNoFormatting,Toast.LENGTH_SHORT).show();
    	
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.i(TAG,"onResume");
		googlePlayServicesConnected();
		//customLocationManager.updateProvider();

	}
	
    /*
     * This method call the requestLocationUpdates of the LocationManager for the GPS_PROVIDER and the NETWORK_PROVIDER
     * every time it get the minDistance value from the user settings so it has to be called whenever these settings change
     
    public void requestLocationUpdate()
    {
    	
    	minUpdateDistance = Float.parseFloat(usersettings.getString("queryperiod", "100"));    	
    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, minUpdateDistance, this);    	
    	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) 0, minUpdateDistance, this);
    }
	*/
	private void setUserLocatiOnOnMapAndCheckHints(Location location,boolean first)
	{
		userLocation=location;
		
		LatLng currentLocation = null;
        if (userLocation != null)
        {
        	/*
	        Log.d(TAG, "ContextResource.checkLocationAll: "+userLocation.getLatitude()+"##"+userLocation.getLongitude());
	        downloadThread = ContextResource.checkLocationAll(
	        		new Float(userLocation.getLatitude()),
					new Float(userLocation.getLongitude()),
					0, handler, Map.this);
			*/
        	if(mrk!=null)
        	{
        		mrk.remove(); 
        	}
        	if(accuracyCircle != null)
        	{
        		accuracyCircle.remove();
        	}
	        Log.d(TAG,"Drawing user marker in: " + Double.toString(userLocation.getLatitude()) + " " +Double.toString(userLocation.getLongitude()));
	        currentLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
	        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.man);
	        mrk = mMap.addMarker(new MarkerOptions().position(currentLocation).title("This is you!").snippet("Red circle represents the position error.").icon(icon).anchor((float)0.5, (float)0.5));
	        
	        //Add circle representing the error
	        // Instantiates a new CircleOptions object and defines the center and radius
	        accuracyCircleOptions = new CircleOptions()
	        .center(currentLocation)
	        .radius(userLocation.getAccuracy())
	        //Fill color of the circle
            // 0x represents, this is an hexadecimal code
            // 55 represents percentage of transparency. For 100% transparency, specify 00.
            // For 0% transparency ( ie, opaque ) , specify ff
            // The remaining 6 characters(FF0000) specify the fill color (red)
	        .fillColor(0x50FF0000)
	        .strokeColor(Color.RED)
	        .strokeWidth(1);
	        accuracyCircle = mMap.addCircle(accuracyCircleOptions);

        }
        else
        {
        	Log.e(TAG, "userLocation null!");
        }
       
        if (currentLocation != null && !showingHint) 
        {
        	if(!first)
        	{
        		baseZoom = (int) mMap.getCameraPosition().zoom;
        		
        	}
        	else
        		mrk.showInfoWindow();
        	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, baseZoom));
        	
        }
        //if(showingHint)
        //	highlightThisHint(selectedHint);
        	 
	}
	
    
    protected boolean isRouteDisplayed() 
    {
        return false;
    }
   

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(0,FORCE_HINT_SEARCH,0,R.string.start_hint_search).setIcon(R.drawable.searchloc).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0,TODO_LIST,0,R.string.to_do_list).setIcon(R.drawable.taskdo).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0,GET_MAP,0,R.string.get_hint_jouney).setIcon(R.drawable.goin).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0,INFO,0,R.string.hint_information).setIcon(R.drawable.info).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		return true;
	}
    
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) 
    {
		switch (item.getItemId()) 
		{
		case FORCE_HINT_SEARCH:
			
			Log.d(TAG,"Going to check hints.");
			TaskNotification.getInstance().forceLocationFix();
			ActionTracker.forceHintSearch(Calendar.getInstance().getTime(), userLocation, getApplicationContext());
			Toast.makeText(getApplicationContext(), R.string.hint_search_started, Toast.LENGTH_SHORT).show();
			break;
		case TODO_LIST:
			show_todo_list_dialog();
			break;
		case GET_MAP:
			getSelectedHintJourney();
			break;
		case INFO:
			if(selectedHint!=null)
			{
			       AlertDialog LDialog = new AlertDialog.Builder(this)
	                .setTitle(selectedHint.titleNoFormatting)
	                .setMessage(selectedHint.streetAddress)
	                .setPositiveButton(android.R.string.ok, null).create();
	                LDialog.show();
			}
			else
			{
				Toast.makeText(getApplicationContext(), R.string.no_selected_hint, Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			finish();
			break;
		}
		return true;
	}
	
	private void getSelectedHintJourney()
	{
		if(selectedHint!=null){
			if (usersettings.getString("selected_navigator", "ListGoogle").equals("ListGoogle")){
				Toast.makeText(getApplicationContext(), "Utilizzo ListGoosle", Toast.LENGTH_SHORT).show();
				if(isValidURI(selectedHint.ddUrl)){
					Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(selectedHint.ddUrl));
					startActivity(browserIntent);
				}
				
			}
			else if(usersettings.getString("selected_navigator", "ListGoogle").equals("Navigator")){
				Toast.makeText(getApplicationContext(), "Utilizzo Navigator", Toast.LENGTH_SHORT).show();
				 Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri
			               .parse("google.navigation:q=" + selectedHint.lat + "," + selectedHint.lng));
			       startActivity(intent);
			       //Speech.speak("viaggere con prudenza", false);
			
			}
			else
			{
				Toast.makeText(getApplicationContext(), R.string.cannot_get_journey, Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			Toast.makeText(getApplicationContext(), R.string.no_selected_hint, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private static boolean isValidURI(String uriToTest)
	{
		if(uriToTest.toString()=="")
		{
			return false;
		}
		return true;
	}
    
	/*
    private void showNoProviderMessage()
    {
    	Toast.makeText(getApplicationContext(), R.string.no_location_provider_found, Toast.LENGTH_SHORT).show();
    }
    */
	
	//With new approach of CustomLocationManager this method is never called.
    private void updateProviderAndPosition()
    {
    	Log.i(TAG,"UpdateProviderAndPosition");
    	userLocation = TaskNotification.getInstance().getLastKnownLocation();
		setUserLocatiOnOnMapAndCheckHints(userLocation,false);
    }
    
      
    //@Override
	public void locationChanged(Location location) 
    {
    	Log.i(TAG, "Location changed.");
    	setUserLocatiOnOnMapAndCheckHints(location,false);
    }
     
	//@Override
	//With new approach of CustomLocationManager this method is never called.
	public void providerDisabled(String provider) 
	{
		Log.i(TAG, "onProviderDisabled");
		updateProviderAndPosition();		
	}

	//@Override
	//With new approach of CustomLocationManager this method is never called.
	public void providerEnabled(String provider) 
	{
		Log.i(TAG, "onProviderEnabled");
		updateProviderAndPosition();
	}

	//@Override
	public void statusChanged(String provider, int status, Bundle extras) {}
	

	public void afterHintsAcquired (String tasktitle,List<Hint> result,Hint selectedHint){
		List<Hint> newList=new ArrayList<Hint>();
		Hint currHint;
		for(int j=0;j<result.size();j++){
			currHint=result.get(j);
			newList.add(currHint);			
		}
		
		
		
		map_todoElem_hints.put(tasktitle, newList);
		Log.d(TAG, "Got "+map_todoElem_hints.size()+" element into the map");
		printNumElemHintList(map_todoElem_hints);
		
	
		
	}

	

	private static void printNumElemHintList(Hashtable<String, List<Hint>> map){
		List<Hint> list;
		for(int i=0;i<map.size();i++){
			list=(List<Hint>)map.get(map.keySet().toArray()[i]);
			if(list!=null){
				Log.d(TAG, "Element "+i+":"+list.size());
			}else{
				Log.d(TAG, "Element "+i+":null!");
			}
		}
	}
	
	private void showHints(List<Hint> result){
		//Toast.makeText(getApplicationContext(), "ShowHints", Toast.LENGTH_LONG);
    	if (result.isEmpty()) return; // immediately return if there is no result
    	
    	if(selectedHint==null)
    	{
    		selectedHint=result.get(0);
    	}
    	for (Hint o : result)
    	{
    		if(!o.equals(selectedHint))
    		{
    			//place.addOverlay(new OverlayItem(new GeoPoint((int)(Float.parseFloat(o.lat)*1e6),(int)(Float.parseFloat(o.lng)*1e6)), o.titleNoFormatting,o.streetAddress),false,o);
    			mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(o.lat),Double.parseDouble(o.lng))).title(o.titleNoFormatting).snippet(o.streetAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_normal_2)));
    		
    		
    		}
    		
    	}
    	highlightThisHint(selectedHint);
    	
	}
	
	private void show_todo_list_dialog(){
		  final Dialog dialog = new Dialog(this);

		  dialog.setContentView(R.layout.todo_map_list);
		  dialog.setTitle("WOW");


		  ListView thelist =(ListView) dialog.findViewById(R.id.todo_map_list);
		  
		  String[] arrTasks=(String[])map_todoElem_hints.keySet().toArray(new String[0]);
		  if(map_todoElem_hints.keySet().size()==0){
			  tasks_with_hints.add("No tasks selected from the notification bar");
		  }else{
			  Log.d(TAG, "Got "+arrTasks.length+ " tasks");
			  	tasks_with_hints.clear();
			  for(int i=0;i<arrTasks.length;i++){
				  Log.d(TAG, "Adding "+arrTasks[i]+" to the list");
				  tasks_with_hints.add(arrTasks[i].toString());
			  }
			  Log.d(TAG, "List: "+tasks_with_hints.size()+ " tasks");
		  }
		  thelist.setAdapter(new ArrayAdapter<String>(this, R.layout.todo_list_item, tasks_with_hints));
		  
		  thelist.setTextFilterEnabled(true);

		  thelist.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		          Toast.LENGTH_SHORT).show();
		      Log.d(TAG, "Got "+map_todoElem_hints.size()+" element into the map");
		      printNumElemHintList(map_todoElem_hints);
		      
		      List<Hint> hintListToShow=(List<Hint>) map_todoElem_hints.get(map_todoElem_hints.keySet().toArray()[position]);
		      if(hintListToShow!=null){
		    	  showHints(hintListToShow);
		    	  dialog.hide();
		      }else{
		    	  Toast.makeText(getApplicationContext(),"No hints",
				          Toast.LENGTH_SHORT).show();  
		      }
		      
		    }
		  });

		  dialog.show();
	}
	
	//Called when a new Intent is loaded in this activity
	@SuppressWarnings("unchecked")
	public void onNewIntent(Intent intent)
	{
		Log.i(TAG,"onNewIntent");
		if(googlePlayServicesConnected())
		{
			packet = intent.getExtras();
			if(packet!=null)
	        {
	        	//clear previous hints
	        	mMap.clear();
	        	
	        	//redraw user marker
	        	mrk = mMap.addMarker(new MarkerOptions().position(new LatLng(userLocation.getLatitude(),userLocation.getLongitude())).title("This is you!").snippet("Don't you like this marker?").icon(BitmapDescriptorFactory.fromResource(R.drawable.man)).anchor((float)0.5,(float)0.5));
	        	//Add circle representing the error
		        // Instantiates a new CircleOptions object and defines the center and radius
		        accuracyCircleOptions = new CircleOptions()
		        .center(new LatLng(userLocation.getLatitude(),userLocation.getLongitude()))
		        .radius(userLocation.getAccuracy())
		        //Fill color of the circle
	            // 0x represents, this is an hexadecimal code
	            // 55 represents percentage of transparency. For 100% transparency, specify 00.
	            // For 0% transparency ( ie, opaque ) , specify ff
	            // The remaining 6 characters(FF0000) specify the fill color (red)
		        .fillColor(0x50FF0000)
		        .strokeColor(Color.RED)
		        .strokeWidth(1);
		        accuracyCircle = mMap.addCircle(accuracyCircleOptions);
	        	hintlistToShow=(ArrayList<Hint>) packet.get("hintlist");
	        	String tasktitle=packet.getString("tasktitle");
	        	
	        	Log.i(TAG,"Got "+hintlistToShow.size()+" hints");
	        	hintToShow=packet.getInt("selectedPos");
	        	Log.i(TAG,"Got to show the number "+hintToShow);
	        	selectedHint=hintlistToShow.get(hintToShow-1);
	        	Log.i(TAG,"Got to show "+selectedHint.title);
	        	//Draw all hints except the selected one
	        	afterHintsAcquired(tasktitle,hintlistToShow,selectedHint);
	        	
	        	showHints(hintlistToShow);
	    		
	        	//highlight the selected hint
	        	highlightThisHint(selectedHint);
	        	showingHint=true;
	        }
	        else
	        {
	        	showingHint=false;
	        }
		}
		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        // Choose what to do based on the request code
        switch (requestCode) 
        {   
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch (resultCode) 
                {
                    case Activity.RESULT_OK:
                    	onCreateGPServicesConnected();
                    	break;
                    default:
                        Log.d(TAG, "GooglePlayServices unavaiable.");
                        break;
                }
            default:
            Log.d(TAG,"Unkown requestCode.");
               break;
        }
    }
}

