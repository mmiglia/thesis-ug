package com.thesisug.ui;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ZoomControls;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.thesisug.R;

public class Map extends MapActivity{
	LinearLayout linearLayout;
	MapView mapView;
	ZoomControls mZoom;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setBuiltInZoomControls(true);
        linearLayout.addView(mZoom);
        setContentView(R.layout.map);        
    }
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
}
