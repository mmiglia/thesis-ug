package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	// get the drawables and setup of overlays
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);  
        
        MapView mapView = (MapView) findViewById(R.id.mapView1);    
        mc = mapView.getController();
        MyLocationOverlay center = new MyLocationOverlay(this, mapView);
        mapOverlays = mapView.getOverlays();
        marker = new MarkerOverlay(this.getResources().getDrawable(R.drawable.androidmarker));
        place = new MarkerOverlay(this.getResources().getDrawable(R.drawable.place));
        
        // get the GPS coordinate
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, this);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String provider = lm.getBestProvider(criteria, true);
        Location gpslocation = lm.getLastKnownLocation(provider);
        GeoPoint currentLocation=null;
        if (gpslocation != null){
        Log.d(TAG, "ContextResource.checkLocationAll: "+gpslocation.getLatitude()+"##"+gpslocation.getLongitude());
        downloadThread = ContextResource.checkLocationAll(
        		new Float(gpslocation.getLatitude()),
				new Float(gpslocation.getLongitude()),
				0, handler, Map.this);
		
        // add the overlays
        currentLocation = new GeoPoint((int)Math.floor (gpslocation.getLatitude()*1e6), (int) Math.floor(gpslocation.getLongitude()*1e6));
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
    	if (marker.size() > 0) marker.removeOverlay(0);
    	GeoPoint current = new GeoPoint((int)Math.floor (location.getLatitude()*1e6), (int) Math.floor(location.getLongitude()*1e6));
    	Log.d(TAG,"Location changed to: " + location.getLatitude()+"#"+location.getLongitude());

    	marker.addOverlay(new OverlayItem(current, "", ""));
    	mapOverlays.add(marker);
    	ContextResource.checkLocationAll(new Float(location.getLatitude()),
				new Float(location.getLongitude()),
				0, handler, Map.this);
    }

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

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
