package com.thesisug.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.valueobject.SingleItemLocation;

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
				
				if (object.equals("") || location.equals("")){
					 Toast.makeText(Create_Assertion_item.this, "Empty fields!Compile!",Toast.LENGTH_LONG).show();
				}else
				{	
				Log.i(TAG,"Parole dai Edittext:" + object + location);
				
				itemLocation = new SingleItemLocation(object,location);
				itemLocation.item = object;
				itemLocation.location= location;
				
				
				Thread creationAssertionItem = AssertionsResource.createItemLocation(itemLocation,
						handler, Create_Assertion_item.this);
				}
		    //Fine corpo tasto SAVE----------------------------------------------------------------------------		
				}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				intent = new Intent(getApplicationContext(), Assertions.class);
				startActivityForResult(intent,0);
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
			
			
		 }
		 else
			 Toast.makeText(Create_Assertion_item.this, R.string.saving_error, Toast.LENGTH_LONG).show();
		 
		 
	    	
	    }

}
