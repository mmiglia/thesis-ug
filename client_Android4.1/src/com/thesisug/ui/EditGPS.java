package com.thesisug.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.maps.MapActivity;
import com.thesisug.R;

 
public class EditGPS extends MapActivity implements LocationListener{
	private static final String TAG = "thesisug - EditGPS Activity";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	//private MyLocationOverlay center;
	private LocationManager lm;
	//private LocationOverlay lo;
	//private List<Overlay> mapOverlays;
	private double latitude;
	private double longitude;
	private Intent intent = new Intent();
	/** Added 24/04/2013 by Alberto Servetti*/
	private GoogleMap mMap;
	private int baseZoom=16;
	private Marker location;
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		// get the drawables and setup of overlays
		super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_gps); 
            
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.edit_gps)).getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //MapView mapView = (MapView) findViewById(R.id.mapView);
        //mapOverlays = mapView.getOverlays();
        //center = new MyLocationOverlay(EditGPS.this, mapView);
        //lo = new LocationOverlay(this.getResources().getDrawable(R.drawable.place));
        //mapView.setBuiltInZoomControls(true);
        //MapController mc = mapView.getController();
        
        // get the GPS positions
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, this);
        Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
        Location gpslocation = lm.getLastKnownLocation(provider);
        
        // get latitude and longitude from intent
        latitude = (double) (getIntent().getFloatExtra(LATITUDE, 0));
        longitude = (double) (getIntent().getFloatExtra(LONGITUDE, 0));
        Log.i(TAG, "from Intent latitude = "+latitude+", longitude ="+longitude);
        //GeoPoint point;
        // if GPS coordinate is specified, we use it. Otherwise use GPS
       // point = ( latitude == 0 && longitude == 0 && gpslocation != null ) ?
        //	new GeoPoint((int)Math.floor (gpslocation.getLatitude()*1e6), (int) Math.floor(gpslocation.getLongitude()*1e6))
        //	:new GeoPoint (latitude , longitude);
        LatLng point;
        point = ( latitude == 0 && longitude == 0 && gpslocation != null ) ?
        		new LatLng(gpslocation.getLatitude(),gpslocation.getLongitude())
        		:
        			new LatLng(latitude,longitude);
        location = mMap.addMarker(new MarkerOptions().draggable(true).position(point).title("Is your event here?").snippet("Long press to drag.").icon(BitmapDescriptorFactory.fromResource(R.drawable.place)));
        
        location.showInfoWindow();
        
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, baseZoom));
        //lo.addOverlay(new OverlayItem(point, "", ""));
        
        // add overlays, setting the zoom and center
       // mapOverlays.add(lo);
       // mapOverlays.add(center);
       // mc.animateTo(point);
       // mc.setZoom(17);
        intent.removeExtra(LATITUDE);
        intent.removeExtra(LONGITUDE);
        intent.putExtra(LATITUDE, (float)point.latitude);
		intent.putExtra(LONGITUDE, (float)point.longitude);
		setResult(RESULT_OK, intent);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//center.enableMyLocation();
		//center.enableCompass();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.equals(MotionEvent.ACTION_UP))
		{
			intent.putExtra(LATITUDE, (float)location.getPosition().latitude);
			intent.putExtra(LONGITUDE, (float)location.getPosition().longitude);
			setResult(RESULT_OK, intent);
		}
			
		return false;
	}
	@Override
	public void onStop(){
		super.onStop();
		Log.i(TAG, "I'm stopped");
		lm.removeUpdates(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//center.disableMyLocation();
		//center.disableCompass();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {}

	@Override
	public void onProviderDisabled(String provider) {}

	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	
}


