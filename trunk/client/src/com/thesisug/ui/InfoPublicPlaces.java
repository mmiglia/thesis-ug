package com.thesisug.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thesisug.R;

public class InfoPublicPlaces extends Activity{
	
	private Button back;

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_public_places);
	
		back = (Button) findViewById(R.id.back_button);

		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				}
		});
		
			
	}
}
