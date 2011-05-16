package com.thesisug.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.thesisug.R;

public class AssertionsInfo extends Activity{
	
	private Button goon;
	private Button back;
	Intent intent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.assertions_info);
	
		goon = (Button) findViewById(R.id.goon_button);
		back = (Button) findViewById(R.id.back_button);
		
		goon.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent = new Intent(getApplicationContext(), Assertions.class);
				startActivityForResult(intent,0);
				}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				}
		});
		
			
	}
}
