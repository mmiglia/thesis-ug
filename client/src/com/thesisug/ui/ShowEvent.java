package com.thesisug.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

import com.thesisug.R;

public class ShowEvent extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.show_event);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar);
		TextView tv = (TextView) findViewById(R.id.customtitlebar);
		tv.setText("Event");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,1,0,"Edit Contact");
		menu.add(0,2,0,"Delete Contact").setIcon(R.drawable.trash);
		menu.add(0,3,0,"Exit");
		return true;
		}
}
