package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
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
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	// get the drawables and setup of overlays
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);  
        
        mapView = (MapView) findViewById(R.id.mapView1);    
        mc = mapView.getController();
        center = new MyLocationOverlay(this, mapView);
        mapOverlays = mapView.getOverlays();
        marker = new MarkerOverlay(this.getResources().getDrawable(R.drawable.androidmarker));
        place = new MarkerOverlay(this.getResources().getDrawable(R.drawable.place));
        
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
        setUserLocatiOnOnMap(userLocation);
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
	
	private void setUserLocatiOnOnMap(Location location){
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
	        marker.addOverlay(new OverlayItem(currentLocation, "", ""));
	        mapOverlays.add(marker);
        }
        mapOverlays.add(center);
        // set up zoom and center of the map
        mapView.setBuiltInZoomControls(true);
        if (currentLocation != null) mc.animateTo(currentLocation);
        mc.setZoom(20);
	}
	
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    @Override
	public void onLocationChanged(Location location) {
    	userLocation=location;
    	setUserLocatiOnOnMap(userLocation);
    }

    private void showNoProviderMessage(){
    	Toast.makeText(getBaseContext(), R.string.no_location_provider_found, Toast.LENGTH_SHORT).show();
    }
    
    private void updateProviderAndPosition(){
		//Change provider if there's one active
		locationProvider=lm.getBestProvider(criteria,true);
		if(locationProvider==null){
			showNoProviderMessage();
			return;
		}
		userLocation = lm.getLastKnownLocation(locationProvider);
		setUserLocatiOnOnMap(userLocation);
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
	
	public void afterHintsAcquired (List<Hint> result){
    	if (result.isEmpty()) return; // immediately return if there is no result
    	mapOverlays.remove(place);
    	for (Hint o : result){
    		place.addOverlay(new OverlayItem(new GeoPoint((int)(Float.parseFloat(o.lat)*1e6),(int)(Float.parseFloat(o.lng)*1e6)), o.titleNoFormatting,o.listingType));
    	}
    	mapOverlays.add(place);
	}
}

class MarkerOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	public MarkerOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
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

}
