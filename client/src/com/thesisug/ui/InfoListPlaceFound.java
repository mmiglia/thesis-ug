package com.thesisug.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thesisug.R;

public class InfoListPlaceFound extends Activity{

	private Button back;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_places_found);
	
		
		back = (Button) findViewById(R.id.back_button);
		
		
		
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				}
		});
		
			
	}
}
