package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.thesisug.notification.TaskNotification;
 
public class EditGPS extends MapActivity implements LocationListener{
	private static final String TAG = "thesisug - EditGPS Activity";
	private static final String LATITUDE = "latitude";
	private static final String LONGITUDE = "longitude";
	private MyLocationOverlay center;
	private LocationManager lm;
	private LocationOverlay lo;
	private List<Overlay> mapOverlays;
	private int latitude;
	private int longitude;
	private Intent intent = new Intent();

	@Override
    public void onCreate(Bundle savedInstanceState) {
		// get the drawables and setup of overlays
		super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_gps); 
        MapView mapView = (MapView) findViewById(R.id.mapView);     
        mapOverlays = mapView.getOverlays();
        center = new MyLocationOverlay(EditGPS.this, mapView);
        lo = new LocationOverlay(this.getResources().getDrawable(R.drawable.place));
        mapView.setBuiltInZoomControls(true);
        MapController mc = mapView.getController();
        
        // get the GPS positions
        lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, (long) 0, 0.0f, this);
        Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String provider = lm.getBestProvider(criteria, true);
        Location gpslocation = lm.getLastKnownLocation(provider);
        
        // get latitude and longitude from intent
        latitude = (int) (getIntent().getFloatExtra(LATITUDE, 0)*1e6);
        longitude = (int) (getIntent().getFloatExtra(LONGITUDE, 0)*1e6);
        Log.i(TAG, "from Intent latitude = "+latitude+", longitude ="+longitude);
        GeoPoint point;
        // if GPS coordinate is specified, we use it. Otherwise use GPS
        point = ( latitude == 0 && longitude == 0 && gpslocation != null ) ?
        	new GeoPoint((int)Math.floor (gpslocation.getLatitude()*1e6), (int) Math.floor(gpslocation.getLongitude()*1e6))
        	:new GeoPoint (latitude , longitude);
        lo.addOverlay(new OverlayItem(point, "", ""));
        
        // add overlays, setting the zoom and center
        mapOverlays.add(lo);
        mapOverlays.add(center);
        mc.animateTo(point);
        mc.setZoom(17);
        intent.putExtra(LATITUDE, new Float(new Integer(latitude).floatValue() / 1e6));
		intent.putExtra(LONGITUDE, new Float (new Integer(longitude).floatValue() / 1e6));
		setResult(RESULT_OK, intent);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		center.enableMyLocation();
		center.enableCompass();
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
		center.disableMyLocation();
		center.disableCompass();
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
	
	class LocationOverlay extends ItemizedOverlay {
		private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
		
		public LocationOverlay(Drawable defaultMarker) {
			super(boundCenterBottom(defaultMarker));
		}
		
		@Override
		public boolean onTap(GeoPoint p, MapView mapView) {
			mOverlays.remove(0);
			latitude = p.getLatitudeE6();
			longitude = p.getLongitudeE6();
			mOverlays.add(new OverlayItem(p, "", ""));
			populate();
			intent.putExtra(LATITUDE, new Float(new Integer(latitude).floatValue() / 1e6));
			intent.putExtra(LONGITUDE, new Float (new Integer(longitude).floatValue() / 1e6));
			setResult(RESULT_OK, intent);
			return false;
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
	}
}


