package com.thesisug.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;

public class DetailsPlace extends Activity{
	
	private static final String TAG = "thesisug - Details_place";
	private final Handler handler = new Handler();	
	private static final int DELETE_PLACE=0;
	private static final int BACK=1;
	private PlaceClient place;
	private String title;
	private String lat;
	private String lng;
	private String streetAddress;
    private String streetNumber;
    private String cap;
    private String city;
    private String type;
    private String category;
    
    Thread deletePlace;
   
   
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle packet = getIntent().getExtras();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.details_place);
		
		  Intent intent=getIntent(); // l'intent di questa activity
        
         
          title=intent.getStringExtra("title");
          streetAddress=intent.getStringExtra("streetAddress");
          streetNumber=intent.getStringExtra("streetNumber");
          cap=intent.getStringExtra("cap");
          city=intent.getStringExtra("city");
          lat=intent.getStringExtra("lat");
          lng=intent.getStringExtra("lng");
          type=intent.getStringExtra("type");
          category=intent.getStringExtra("category");
         
          
          place = new PlaceClient(title,lat,lng,streetAddress,streetNumber,cap,city,category);
          
          TextView textObject=(TextView)findViewById(R.id.object_text);   
          textObject.append(title);
          
          TextView textlocation=(TextView)findViewById(R.id.street_address_text);   
          textlocation.append(streetAddress);
          
          TextView textusername=(TextView)findViewById(R.id.street_number_text);   
          textusername.append(streetNumber);
          
          TextView textn_views=(TextView)findViewById(R.id.cap_text);   
          textn_views.append(cap);
          
          TextView textn_votes=(TextView)findViewById(R.id.city_text);   
          textn_votes.append(city);
          
          TextView text_latlng=(TextView)findViewById(R.id.lat_lng_text);   
          text_latlng.append(lat + " - " + lng);
          
          TextView text_vote=(TextView)findViewById(R.id.category_text);   
          text_vote.append(category);
          
          Button btn_look_place = (Button)findViewById(R.id.btn_streetview);   
          
          	btn_look_place.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("google.streetview:cbll="+lat+","+lng+"&cbp=1"));
				startActivity(searchAddress);
				
			}
			});

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,DELETE_PLACE,0,R.string.delete_place).setIcon(R.drawable.trash);	
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		case DELETE_PLACE:
			if (type.equals("Private")){
				Toast.makeText(DetailsPlace.this,place.title + place.lat+" , "+place.lng,Toast.LENGTH_LONG).show();
				deletePlace = PlacesResource.deletePrivatePlace(place, handler, DetailsPlace.this);
		}else if (type.equals("Public"))
				deletePlace = PlacesResource.deletePublicPlace(place, handler, DetailsPlace.this);
			break;			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	
public void finishSave (boolean result) {
	
	Log.i(TAG,"finishSave  Done!");
	 if (result)
	 { 
		Toast.makeText(DetailsPlace.this, R.string.edit_success,Toast.LENGTH_LONG).show();
		
	 	finish();
	 }
	 else
		 Toast.makeText(DetailsPlace.this, R.string.saving_error, Toast.LENGTH_LONG).show();
	    
	 
   	
   }
   /*
public void afterAssertionsListLoaded(final boolean result){
	
    	
	 if (result)
	 { 
		Toast.makeText(Details_assertion_item.this, R.string.edit_success,Toast.LENGTH_LONG).show();
	 	
	 }
	 else
		 Toast.makeText(Details_assertion_item.this, R.string.saving_error, Toast.LENGTH_LONG).show();
	 

		 }*/
	
	
}



