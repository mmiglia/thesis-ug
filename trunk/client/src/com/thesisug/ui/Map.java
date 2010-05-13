package com.thesisug.ui;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.thesisug.R;

public class Map extends MapActivity{
	private static final String TAG = "Map Activity";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);  
        MapView mapView = (MapView) findViewById(R.id.mapView);        
        List<Overlay> mapOverlays = mapView.getOverlays();
        MarkerOverlay marker = new MarkerOverlay(this.getResources().getDrawable(R.drawable.androidmarker));
        MarkerOverlay place = new MarkerOverlay(this.getResources().getDrawable(R.drawable.place));
        GeoPoint point = new GeoPoint(44415692,8927861);
        OverlayItem overlayitem = new OverlayItem(point, "", "");
        place.addOverlay(new OverlayItem(new GeoPoint(44414194, 8927099), "", ""));
        place.addOverlay(new OverlayItem(new GeoPoint(44414976, 8925747), "haha", ""));
        marker.addOverlay(overlayitem);
        mapOverlays.add(marker);
        mapOverlays.add(place);
        mapView.setBuiltInZoomControls(true);
        MapController mc = mapView.getController();
        mc.animateTo(point);
        mc.setZoom(17);
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
