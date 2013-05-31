package com.thesisug.ui;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;

public class Create_new_place_gps extends Activity{
	
	private static final String TAG = "thesisug - Create_new_place_gps";
	protected final Handler handler = new Handler();
	
	Intent intent;
	
	EditText edit_name_place;
	TextView text_coordinate;
	Spinner spinner;
	Button btn_add_place;
	Button btn_back;
	Button btn_select_cat;
	
	String name;
	
	String lat;
	String lng;
	String type;
	String category="";
	Thread creationNewPlace;
	
	private List<String> listCateg = new ArrayList<String>();
	
	
	

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.create_new_place_gps);
		
		intent=getIntent(); // l'intent di questa activity
		
		lat=intent.getStringExtra("lat");
		lng=intent.getStringExtra("lng");
		type=intent.getStringExtra("type");
		
		//Toast.makeText(getApplicationContext(),lat + ", " + lng + ", " + type, Toast.LENGTH_LONG).show();
		text_coordinate = (TextView) findViewById(R.id.txt_coordinate);
		edit_name_place = (EditText) findViewById(R.id.edit_name_place);
		
		btn_add_place = (Button) findViewById(R.id.add_button);
		btn_back = (Button) findViewById(R.id.back_button);
		btn_select_cat = (Button) findViewById(R.id.select_cat);
		
		text_coordinate.setText(lat + ", " +lng);
		text_coordinate.postInvalidate(); 
		//text_coordinate.refreshDrawableState();

		
		
		spinner = (Spinner) findViewById(R.id.spinner_type);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.type_array, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinner.setAdapter(adapter);
	    
	    spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
	    
	    if (type.equals("Private"))
		{
			spinner.setSelection(0);
		}else if (type.equals("Public")){
			spinner.setSelection(1);
	 	}
	    
	    
	    btn_add_place.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			//Inizio corpo tasto SAVE----------------------------------------------------------------------------	
				
				name = edit_name_place.getText().toString();
				
				//Toast.makeText(Create_new_place.this, "OK1",Toast.LENGTH_LONG).show(); 
				
				if (name.equals("") ){
					 Toast.makeText(Create_new_place_gps.this, "ALL fields are empty!Compile!",Toast.LENGTH_LONG).show();
				}else if (category.equals(""))
				{	
					Toast.makeText(Create_new_place_gps.this, "Select a category for the place to add!",Toast.LENGTH_LONG).show();
				}
				else
				{	
					Log.i(TAG, "Parole dai Edittext:" + name + ", " + lat + ", " + lng + ", " + type);
				//Toast.makeText(Create_Assertion_item.this, "Parole dai Edittext:" + object + location,Toast.LENGTH_LONG).show();
					//intent = new Intent(getApplicationContext(), SelectCategory.class);
					/*intent.putExtra("name", name);
					intent.putExtra("streetAddress",streetAddress);
					intent.putExtra("streetNumber", streetNumber);
					intent.putExtra("cap", cap);
					intent.putExtra("city", city);
					intent.putExtra("type", type);*/
					//startActivityForResult(intent,0);
					//Toast.makeText(Create_Assertion_item.this, "Parole dai Edittext:" + object + location,Toast.LENGTH_LONG).show();
					//Toast.makeText(Create_new_place.this, "Chiedo risultati!!!",Toast.LENGTH_LONG).show();
					PlaceClient newPlace = new PlaceClient(name,lat,lng,"","","","",category);
					
					if (spinner.getSelectedItem().toString().equals("Private"))
						creationNewPlace = PlacesResource.createPrivatePlaceGPS(newPlace, handler, Create_new_place_gps.this);
					else if (spinner.getSelectedItem().toString().equals("Public"))
						creationNewPlace = PlacesResource.createPublicPlaceGPS(newPlace, handler, Create_new_place_gps.this);
					
					
				
				}
				
				
				
				//select the list category
				
			}
		});
	    
	    
		    
		    btn_select_cat.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					intent = new Intent(getApplicationContext(), SelectCategory.class);
					startActivityForResult(intent,0);
					}
			});
			
		    btn_back.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					finish();
					}
			});
		
	
	}
	
	 protected void onActivityResult(int requestCode, int resultCode,Intent data) {
         if (requestCode == 0) {
             if (resultCode == RESULT_OK) {
                 
            	
            	 	category = data.getStringExtra("category");
            	 	btn_add_place.setSelected(true);
					
             }
         }
	 }
	
	
	public void finishSaveToAdd (boolean result, PlaceClient placeAdded) {
		 
		 if (result)
		 { 
			 Toast.makeText(Create_new_place_gps.this, "Place added!", Toast.LENGTH_SHORT).show();
			 finish();
		 }else 
			 Toast.makeText(Create_new_place_gps.this, R.string.saving_error, Toast.LENGTH_SHORT).show();
	}
	
	public class MyOnItemSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent,
	        View view, int pos, long id) {
	    	type = parent.getItemAtPosition(pos).toString();
	    	//Toast.makeText(parent.getContext(), type, Toast.LENGTH_LONG).show();
		    
	    }

	    public void onNothingSelected(AdapterView parent) {
	    	Toast.makeText(parent.getContext(), "Select type!", Toast.LENGTH_LONG).show();
	    }
	}

}





