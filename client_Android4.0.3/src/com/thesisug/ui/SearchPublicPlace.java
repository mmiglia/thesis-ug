package com.thesisug.ui;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.thesisug.R;
import com.thesisug.communication.valueobject.PlaceClient;

public class SearchPublicPlace extends Activity{
	
	private static final String TAG = "thesisug - Search_place";
	protected final Handler handler = new Handler();
	Intent intent;
	Intent i;
	
	EditText edit_name_place;
	EditText edit_streetAddress;
	EditText edit_streetNumber;
	EditText edit_cap;
	EditText edit_city;
	
	Button btn_search;
	Button btn_back;
	Button btn_select_cat;
	
	String name="";
	String streetAddress="";
	String streetNumber="";
	String cap="";
	String city="";
	String category="";
	PlaceClient place;
	Thread searchPlace;
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.search_public_place);
		
		
		edit_name_place = (EditText) findViewById(R.id.edit_name_place);
		edit_streetAddress = (EditText) findViewById(R.id.edit_streetAddress);
		edit_streetNumber = (EditText) findViewById(R.id.edit_streetNumber);
		edit_cap = (EditText) findViewById(R.id.edit_cap);
		edit_city = (EditText) findViewById(R.id.edit_city);
		
		btn_search = (Button) findViewById(R.id.search);
		btn_select_cat = (Button) findViewById(R.id.select_cat);
		btn_back = (Button) findViewById(R.id.back_button);
		
		btn_search.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//PlaceClient newPlace = new PlaceClient(name,streetAddress,streetNumber,cap,city,category);
				//searchPlace = PlacesResource.searchPublicPlace(newPlace, handler, Create_new_place.this);
				intent = new Intent(getApplicationContext(),ListPlacesFound.class);
				intent.putExtra("title", name);
				intent.putExtra("streetAddress", streetAddress);
				intent.putExtra("streetNumber", streetNumber);
				intent.putExtra("cap", cap);
				intent.putExtra("city", city);
				intent.putExtra("category", category);
				//Log.i(TAG,"details_public_place:" + placeList.get(arg2).title);	
				startActivity(intent);
				finish();
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
           	 	btn_search.setSelected(true);
					
            }
        }
	 }
	
	public void finishSave(List<PlaceClient> placeList) {
		 
		/*
		  Bundle b = new Bundle();
		  b.p
		  intent.putExtras(b);
		  setResult(RESULT_OK, intent);
		
		
		intent.putExtra("placeList", placeList);
		
		//Toast.makeText(SelectCategory.this, "Invio risultati!",Toast.LENGTH_LONG).show();
		setResult(RESULT_OK, intent);*/
		finish();
		
			 
			
			 finish();
			 
	}
}
