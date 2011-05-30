package com.thesisug.ui;

import com.thesisug.R;
import com.thesisug.communication.AccountUtil;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.SingleItemLocation;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;
import android.util.Log;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.communication.AssertionsResource;

public class Create_Assertion_item extends Activity{
	
	private static final String TAG = "thesisug - Create_Assertion_item";
	private final Handler handler = new Handler();	
	
	private SingleItemLocation itemLocation;
	private Button save;
	private Button back;
	private EditText editObject;
	private EditText editLocation;
	private EditText editDescription;
	private String object;
	private String location;
	private String description;
	
	
	Intent intent;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.create_assertion_item);
		
		
		editObject = (EditText) findViewById(R.id.object_edit);
		editLocation = (EditText) findViewById(R.id.location_edit);
		editDescription = (EditText) findViewById(R.id.description_edit);

		save = (Button) findViewById(R.id.save_button);
		back = (Button) findViewById(R.id.back_button);
		
	
		
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			//Inizio corpo tasto SAVE----------------------------------------------------------------------------	
				
				object = editObject.getText().toString();
				location = editLocation.getText().toString();
				description = editDescription.getText().toString();
				Log.i(TAG,"Parole dai Edittext:" + object + location);
				//Toast.makeText(Create_Assertion_item.this, "Parole dai Edittext:" + object + location,Toast.LENGTH_LONG).show();
				
				itemLocation = new SingleItemLocation(object,location);
				itemLocation.item = object;
				itemLocation.location= location;
				
				Thread creationAssertionItem = AssertionsResource.createItemLocation(itemLocation,
						handler, Create_Assertion_item.this);

		    //Fine corpo tasto SAVE----------------------------------------------------------------------------		
				}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				}
		});
		
		
	}
	
	 public void finishSave (boolean result) {
		 
		 if (result)
		 { 
			Toast.makeText(Create_Assertion_item.this, R.string.edit_success,Toast.LENGTH_LONG).show();
			intent = new Intent(getApplicationContext(), Assertions.class);
			intent.putExtra("item", editObject.getText().toString());
			intent.putExtra("location", editLocation.getText().toString());
			intent.putExtra("description", editDescription.getText().toString());
			
			startActivityForResult(intent, 0);
			finish();
			
			/*
			intent = new Intent();
			intent.putExtra("item", editObject.getText().toString());
			intent.putExtra("location", editLocation.getText().toString());
			intent.putExtra("description", editDescription.getText().toString());
			setResult(RESULT_OK, intent);
			finish();*/
		 }
		 else
			 Toast.makeText(Create_Assertion_item.this, R.string.saving_error, Toast.LENGTH_LONG).show();
		 
		 
	    	
	    }

}
