package com.thesisug.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.thesisug.R;

public class ShowActivities extends Activity{
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        // Inflate our UI from its XML layout description.
        setContentView(R.layout.show_activity);
	}
}
