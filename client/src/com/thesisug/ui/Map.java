package com.thesisug.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
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

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.thesisug.R;
import com.thesisug.communication.ContextResource;
import com.thesisug.communication.valueobject.Hint;

public class Map extends MapActivity implements LocationListener{
	private static final String TAG = "thesisug - Map Activity";
	private LocationManager lm;
	private Thread downloadThread;
	private Handler handler = new Handler();
	private MarkerOverlay place, marker;
	private List<Overlay> mapOverlays;
    private MapController mc;
    private Criteria criteria;
    
    private String locationProvider;
    private Location userLocation;
    private MyLocationOverlay center;
    private MapView mapView;
    private float minUpdateDistance=0;
    private static SharedPreferences usersettings;
    
    private static TextView txtHint;
    private static Button btnPrevHint;
    private static Button btnNextHint;
    
	public final static int FORCE_HINT_SEARCH=0;
	public final static int TODO_LIST = 1;
	public final static int GET_MAP=2;
	public final static int INFO=3;

	
	private int baseZoom=16;
	
	private static ArrayList<Hint> hintlistToShow;
	private static int hintToShow;
	private static Hint selectedHint;
	
	private static final ArrayList<String> tasks_with_hints = new ArrayList<String>();
	private static final Hashtable map_todoElem_hints=new Hashtable();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	// get the drawables and setup of overlays
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);  
        
        mapView = (MapView) findViewById(R.id.mapView1);   
        
        mc = mapView.getController();
        center = new MyLocationOverlay(this, mapView);
        mapOverlays = mapView.getOverlays();
        marker = new MarkerOverlay(this.getResources().getDrawable(R.drawable.man),this.getResources().getDrawable(R.drawable.man),this);
        place = new MarkerOverlay(this.getResources().getDrawable(R.drawable.marker_normal_2),this.getResources().getDrawable(R.drawable.marker_highlight),this);
        
    	usersettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        // get the GPS coordinate
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        requestLocationUpdate();
        
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        
        locationProvider = lm.getBestProvider(criteria, true);
        
        if(locationProvider==null){
        	showNoProviderMessage();
        	return;
        }
        userLocation = lm.getLastKnownLocation(locationProvider);
        setUserLocatiOnOnMapAndCheckHints(userLocation);
        
        Bundle packet = getIntent().getExtras();
        Log.i(TAG,"Packet null:"+String.valueOf(packet==null));
        
        //Elements on top of the map
        txtHint=(TextView)findViewById(R.id.map_txt_Hint);
        btnPrevHint=(Button)findViewById(R.id.map_btn_prev_Hint);
        
        btnPrevHint.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(hintlistToShow!=null && selectedHint!=null){
        			hintToShow--;
        			if(hintToShow<1){
        				hintToShow=hintlistToShow.size();
        			}
        			selectedHint=hintlistToShow.get(hintToShow-1);
        			highlightThisHint(selectedHint);
        			
        		}else{
        			Toast.makeText(getApplicationContext(), "No hint to show",Toast.LENGTH_SHORT).show();
        		}
        	}
        });

        
        btnNextHint=(Button)findViewById(R.id.map_btn_next_Hint);
        
        btnNextHint.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(hintlistToShow!=null && selectedHint!=null){
        			hintToShow++;
        			if(hintToShow>hintlistToShow.size()){
        				hintToShow=1;
        			}
        			selectedHint=hintlistToShow.get(hintToShow-1);
        			highlightThisHint(selectedHint);
        		}else{
        			Toast.makeText(getApplicationContext(), "No hint to show",Toast.LENGTH_SHORT).show();
        		}
        	}
        });
        
        
		
       
        //The packet arrive from the intent sent by the HintList activity
        if(packet!=null){
        	//Remove all overlay previous drawn
        	mapOverlays.clear();
        	
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
        }


        
    }


	private void highlightThisHint(Hint selHint){
		selectedHint=selHint;
		GeoPoint placePoint =new GeoPoint((int)(Float.parseFloat(selHint.lat)*1e6),(int)(Float.parseFloat(selHint.lng)*1e6));
		OverlayItem overlayHint=new OverlayItem(placePoint, selHint.titleNoFormatting,selHint.streetAddress);
    	place.addOverlay(overlayHint,true,selHint);
    	
    	place.setHighlited(overlayHint);
		txtHint.setText(selHint.titleNoFormatting);
    	mapOverlays.add(place);
    	mc.animateTo(placePoint);
    	Toast.makeText(getApplicationContext(),selectedHint.titleNoFormatting,Toast.LENGTH_SHORT).show();
    	
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(lm.getBestProvider(criteria,true)==null){
			showNoProviderMessage();
		}

	}
	
    /**
     * This method call the requestLocationUpdates of the LocationManager for the GPS_PROVIDER and the NETWORK_PROVIDER
     * every time it get the minDistance value from the user settings so it has to be called whenever these settings change
     */
    public void requestLocationUpdate(){
    	minUpdateDistance = Float.parseFloat(usersettings.getString("queryperiod", "100"));    	
    	lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, minUpdateDistance, this);    	
    	lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, (long) 0, minUpdateDistance, this);
    }
	
	private void setUserLocatiOnOnMapAndCheckHints(Location location){
		userLocation=location;
		if (marker.size() > 0) marker.removeOverlay(0);
        GeoPoint currentLocation=null;

        if (userLocation != null){
	        Log.d(TAG, "ContextResource.checkLocationAll: "+userLocation.getLatitude()+"##"+userLocation.getLongitude());
	        downloadThread = ContextResource.checkLocationAll(
	        		new Float(userLocation.getLatitude()),
					new Float(userLocation.getLongitude()),
					0, handler, Map.this);
	        // add the overlays
	        currentLocation = new GeoPoint((int)Math.floor (userLocation.getLatitude()*1e6), (int) Math.floor(userLocation.getLongitude()*1e6));
	        marker.addOverlay(new OverlayItem(currentLocation, "This is you!", "Don't you like this marker?"),false,null);
	        mapOverlays.add(marker);
        }
        mapOverlays.add(center);
        // set up zoom and center of the map
        mapView.setBuiltInZoomControls(true);
        if (currentLocation != null) mc.animateTo(currentLocation);
        mc.setZoom(baseZoom);
	}
	
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
	public void onLocationChanged(Location location) {
    	userLocation=location;
    	setUserLocatiOnOnMapAndCheckHints(userLocation);
    }

    public void searchForHints(){
    	updateProviderAndPosition();
		Toast.makeText(getApplicationContext(), R.string.hint_search_started, Toast.LENGTH_SHORT).show();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,FORCE_HINT_SEARCH,0,R.string.start_hint_search).setIcon(R.drawable.radar);
		menu.add(0,TODO_LIST,0,R.string.to_do_list).setIcon(R.drawable.todo);
		menu.add(0,GET_MAP,0,R.string.get_hint_jouney).setIcon(R.drawable.compass);
		menu.add(0,INFO,0,R.string.hint_information).setIcon(R.drawable.info);
		
		
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case FORCE_HINT_SEARCH:
			updateProviderAndPosition();
			Toast.makeText(getApplicationContext(), R.string.hint_search_started, Toast.LENGTH_SHORT).show();
			break;
		case TODO_LIST:
			show_todo_list_dialog();
			break;
		case GET_MAP:
			getSelectedHintJourney();
			break;
		case INFO:
			if(selectedHint!=null){
			       AlertDialog LDialog = new AlertDialog.Builder(this)
	                .setTitle(selectedHint.titleNoFormatting)
	                .setMessage(selectedHint.streetAddress)
	                .setPositiveButton(android.R.string.ok, null).create();
	                LDialog.show();
			}else{
				Toast.makeText(getApplicationContext(), R.string.no_selected_hint, Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			finish();
			break;
		}
		return true;
	}
    
	
	
	private void getSelectedHintJourney(){
		if(selectedHint!=null){
			if (usersettings.getString("selected_navigator", "ListGoogle").equals("ListGoogle")){
				Toast.makeText(getApplicationContext(), "Utilizzo ListGoosle", Toast.LENGTH_SHORT).show();
				if(isValidURI(selectedHint.ddUrl)){
					Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(selectedHint.ddUrl));
					startActivity(browserIntent);
				}
				
			}else if(usersettings.getString("selected_navigator", "ListGoogle").equals("Navigator")){
				Toast.makeText(getApplicationContext(), "Utilizzo Navigator", Toast.LENGTH_SHORT).show();
				 Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri
			               .parse("google.navigation:q=" + selectedHint.lat + "," + selectedHint.lng));
			       // Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345");
			       startActivity(intent);
			       //Speech.speak("viaggere con prudenza", false);
			
			}
			else{
				Toast.makeText(getApplicationContext(), R.string.cannot_get_journey, Toast.LENGTH_SHORT).show();
			}
		}else{
			Toast.makeText(getApplicationContext(), R.string.no_selected_hint, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private static boolean isValidURI(String uriToTest){
		if(uriToTest.toString()==""){
			return false;
		}
		return true;
	}
    
    private void showNoProviderMessage(){
    	Toast.makeText(getApplicationContext(), R.string.no_location_provider_found, Toast.LENGTH_SHORT).show();
    }
    
    private void updateProviderAndPosition(){
		//Change provider if there's one active
		locationProvider=lm.getBestProvider(criteria,true);
		if(locationProvider==null){
			showNoProviderMessage();
			return;
		}
		userLocation = lm.getLastKnownLocation(locationProvider);
		setUserLocatiOnOnMapAndCheckHints(userLocation);
    }
    
	@Override
	public void onProviderDisabled(String provider) {
		updateProviderAndPosition();		
	}

	@Override
	public void onProviderEnabled(String provider) {
		updateProviderAndPosition();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	

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

	
	private static void printNumElemHintList(Hashtable map){
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
    	if (result.isEmpty()) return; // immediately return if there is no result
    	
    	if(selectedHint==null){
    		selectedHint=result.get(0);
    	}
    	
    	place.reset();
    	mapOverlays.remove(place);
    	for (Hint o : result){
    		if(!o.equals(selectedHint)){
    			place.addOverlay(new OverlayItem(new GeoPoint((int)(Float.parseFloat(o.lat)*1e6),(int)(Float.parseFloat(o.lng)*1e6)), o.titleNoFormatting,o.streetAddress),false,o);
    			
    		
    		
    		}
    	}
    	mapOverlays.add(place);

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


		  

		  
		  
		  

		  /*TextView txt_title = (TextView) dialog.findViewById(R.id.hint_title);
		  txt_title.setText(hint.titleNoFormatting);

		  TextView txt_street = (TextView) dialog.findViewById(R.id.hint_street);
		  txt_street.setText(hint.streetAddress);
		  */
		  
/*
		  Button btnOk=(Button)dialog.findViewById(R.id.btn_ok);
		  
		  btnOk.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				dialog.hide();
			}
		  });
*/		 
		  dialog.show();
	}
}

class MarkerOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private ArrayList<Boolean> highlitedArray = new ArrayList<Boolean>();
	private ArrayList<Hint> hintArray = new ArrayList<Hint>();
	private Drawable defaultMarker;
	private Drawable highlightedMarker;
	Context markerContext;
	private OverlayItem oldHighlited;
	
	
	public void reset(){
		
		
		for(int i=0;i<mOverlays.size();i++){
			this.removeOverlay(i);
		}
		
		mOverlays.clear();
		
		highlitedArray.clear();
		hintArray.clear();
		
	}
	
	public MarkerOverlay(Drawable _defaultMarker,Drawable _highlightetMarker, Context context) {
		super(boundCenterBottom(_defaultMarker));
		defaultMarker=_defaultMarker;
		highlightedMarker=_highlightetMarker;
		markerContext = context;
	}

	
	
	@Override
	protected OverlayItem createItem(int i) {
		OverlayItem item=mOverlays.get(i);
		if(highlitedArray.get(i)){
			item.setMarker(boundCenterBottom(defaultMarker));
		}else{
			item.setMarker(boundCenterBottom(defaultMarker));
		}
		return item;
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay,boolean highlighted, Hint hint) {
		
	    mOverlays.add(overlay);
	    highlitedArray.add(new Boolean(highlighted));
	    hintArray.add(hint);
	    populate();
	}
	
	public void removeOverlay(OverlayItem overlay) {
	    mOverlays.remove(overlay);
	    populate();
	}
	
	public void removeOverlay(int index) {
	    mOverlays.remove(index);
	    populate();
	}

	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  setHighlited(item);
	  final Hint hint=hintArray.get(index);
	  
	  if(hint==null){
		  return true;
	  }
	  
	  
	  final Context mContext = markerContext;
	  final Dialog dialog = new Dialog(mContext);

	  dialog.setContentView(R.layout.hint_map_dialog);
	  dialog.setTitle(hint.titleNoFormatting);

	  TextView txt_title = (TextView) dialog.findViewById(R.id.hint_title);
	  txt_title.setText(hint.titleNoFormatting);

	  TextView txt_street = (TextView) dialog.findViewById(R.id.hint_street);
	  txt_street.setText(hint.streetAddress);
	  
	  

	  Button btnOk=(Button)dialog.findViewById(R.id.btn_ok);
	  
	  btnOk.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View arg0) {
			
			dialog.hide();
		}
	  });
		 
	  Button btnTakeMeThere=(Button)dialog.findViewById(R.id.btn_take_me_there);
	  btnTakeMeThere.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				SharedPreferences usersettings = PreferenceManager.getDefaultSharedPreferences(mContext);
				
				if(hint!=null)
				{
					
					if (usersettings.getString("selected_navigator", "ListGoogle").equals("ListGoogle"))
					{
						//Toast.makeText(getApplicationContext(), "Utilizzo ListGoosle", Toast.LENGTH_SHORT).show();
						if(hint.ddUrl!=""){
							Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(hint.ddUrl));
							mContext.startActivity(browserIntent);
						}
						
						
					}else if(usersettings.getString("selected_navigator", "ListGoogle").equals("Navigator"))
					{
						//Toast.makeText(getApplicationContext(), "Utilizzo Navigator", Toast.LENGTH_SHORT).show();
						 Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri
					               .parse("google.navigation:q=" + hint.lat + "," + hint.lng));
					       
						mContext.startActivity(intent);
					       

					}else{
						Toast.makeText(mContext, R.string.cannot_get_journey, Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(mContext, R.string.no_selected_hint, Toast.LENGTH_SHORT).show();
				}
			}
	  });
	  dialog.show();
	  return true;
	}
	
	public void setHighlited(OverlayItem item){		
		item.setMarker(boundCenterBottom(highlightedMarker));
		if(oldHighlited!=null){
			oldHighlited.setMarker(boundCenterBottom(defaultMarker));
		}
		oldHighlited=item;
	}
	
}
