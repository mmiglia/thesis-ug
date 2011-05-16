package com.thesisug.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.valueobject.SingleActionLocation;


public class Create_Assertion_action extends Activity{
	
	private static final String TAG = "thesisug - Create_Assertion_action";
	private final Handler handler = new Handler();	
	
	private SingleActionLocation actionLocation;
	private Button save;
	private Button back;
	private EditText editAction;
	private EditText editLocation;
	private EditText editDescription;
	private String action;
	private String location;
	private String description;
	Intent intent;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.create_assertion_action);
		
		back = (Button) findViewById(R.id.back_button1);
		
		editAction = (EditText) findViewById(R.id.action_edit);
		editLocation = (EditText) findViewById(R.id.location_edit1);
		editDescription = (EditText) findViewById(R.id.description1);

		save = (Button) findViewById(R.id.save_button1);
		
		
	
		
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			//Inizio corpo tasto SAVE----------------------------------------------------------------------------	
				
				action = editAction.getText().toString();
				location = editLocation.getText().toString();
				description = editDescription.getText().toString();
				Log.i(TAG,"Parole dai Edittext:" + action + location);
				Toast.makeText(Create_Assertion_action.this, "Parole dai Edittext:" + action + location,Toast.LENGTH_LONG).show();
				
				actionLocation = new SingleActionLocation(action,location);
				actionLocation.action = action;
				actionLocation.location= location;
				
				Thread creationAssertionAction = AssertionsResource.createActionLocation(actionLocation,
						handler, Create_Assertion_action.this);

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
			 Toast.makeText(Create_Assertion_action.this, R.string.edit_success,Toast.LENGTH_LONG).show();
		 	
		 
		 else
			 Toast.makeText(Create_Assertion_action.this, R.string.saving_error, Toast.LENGTH_LONG).show();
		 
		 
	    	
	    }

}
