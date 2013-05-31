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
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;

public class Create_new_place extends Activity{
	
	private static final String TAG = "thesisug - Create_new_place";
	protected final Handler handler = new Handler();
	Intent intent = new Intent();
	
	EditText edit_name_place;
	EditText edit_streetAddress;
	EditText edit_streetNumber;
	EditText edit_cap;
	EditText edit_city;
	Spinner spinner;
	Button btn_add_place;
	Button btn_back;
	Button btn_select_cat;
	
	String name;
	String streetAddress;
	String streetNumber;
	String cap;
	String city;
	String type;
	String category="";
	Thread creationNewPlace;
	
	private List<String> listCateg = new ArrayList<String>();
	

	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.create_new_place);
		
		
		intent=getIntent(); // l'intent di questa activity
		type=intent.getStringExtra("type");
		
		edit_name_place = (EditText) findViewById(R.id.edit_name_place);
		edit_streetAddress = (EditText) findViewById(R.id.edit_streetAddress);
		edit_streetNumber = (EditText) findViewById(R.id.edit_streetNumber);
		edit_cap = (EditText) findViewById(R.id.edit_cap);
		edit_city = (EditText) findViewById(R.id.edit_city);
		
		btn_add_place = (Button) findViewById(R.id.add_button);
		btn_back = (Button) findViewById(R.id.back_button);
		btn_select_cat = (Button) findViewById(R.id.select_cat);
		
		
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
					streetAddress = edit_streetAddress.getText().toString();
					streetNumber = edit_streetNumber.getText().toString();
					cap = edit_cap.getText().toString();
					city = edit_city.getText().toString();
					
					//Toast.makeText(Create_new_place.this, "OK1",Toast.LENGTH_LONG).show(); 
					
					if (name.equals("") || streetAddress.equals("") || streetNumber.equals("") || cap.equals("") || city.equals("")){
						 Toast.makeText(Create_new_place.this, "Empty ALL fields!Compile!",Toast.LENGTH_LONG).show();
					}else if (category.equals(""))
					{	
						Toast.makeText(Create_new_place.this, "Select a category for the place to add!",Toast.LENGTH_LONG).show();
					}
					else
					{	
						Log.i(TAG, "Parole dai Edittext:" + name + ", " + streetAddress + ", " + streetNumber + ", " + cap + ", " + city + ", " + type);
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
						PlaceClient newPlace = new PlaceClient(name,"","",streetAddress,streetNumber,cap,city,category);
						
						if (spinner.getSelectedItem().toString().equals("Private"))
							creationNewPlace = PlacesResource.createPrivatePlace(newPlace, handler, Create_new_place.this);
						else if (spinner.getSelectedItem().toString().equals("Public"))
							creationNewPlace = PlacesResource.createPublicPlace(newPlace, handler, Create_new_place.this);
						
						
					
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
                 
            	 //category= "abitazione";
            	 	category = data.getStringExtra("category");
            	 	btn_add_place.setSelected(true);
					
             }
         }
	 }
	
	public void finishSaveToAdd (boolean result, PlaceClient placeAdded) {
		 
		 if (result)
		 { 
			 
			 
			 category="";
			 //Toast.makeText(Create_new_place.this,placeAdded.title + placeAdded.streetAddress+placeAdded.category , Toast.LENGTH_LONG).show();
			 intent.putExtra("title", placeAdded.title);
			 intent.putExtra("streetAddress", placeAdded.streetAddress);
			 intent.putExtra("streetNumber", placeAdded.streetNumber);
			 intent.putExtra("cap", placeAdded.cap);
			 intent.putExtra("city", placeAdded.city);
			 intent.putExtra("category", placeAdded.category);
			 setResult(RESULT_OK, intent);
			 
			 finish();
				
			 
		 }else
			 Toast.makeText(Create_new_place.this, R.string.saving_error, Toast.LENGTH_LONG).show();
			 
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
