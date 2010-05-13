package com.thesisug.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.thesisug.R;

public class ShowActivities extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        Bundle extras = getIntent().getExtras();
        Log.i("ShowActivities",getIntent().getStringExtra("event_title"));
        setContentView(R.layout.show_activity);
        TextView toShow = (TextView) findViewById(R.id.text);
        toShow.setText(extras.getString("event_title"));
	
	}
}
