package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.PlacesResource;
import com.thesisug.communication.valueobject.PlaceClient;

public class DetailsPlaceToVote extends Activity{
	private static final String TAG = "thesisug - Details_place_to_vote";
	private final Handler handler = new Handler();	
	private static final int VOTE_PLACE=0;
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
    private String category="";
    private String categoryVoted="";
    private List<String> listCateg = new ArrayList<String>();
    
    
    Thread deletePlace;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle packet = getIntent().getExtras();
		
		setContentView(R.layout.details_place_to_vote);
		
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
          //category="bar,alimentari";
          
          place = new PlaceClient(title,lat,lng,streetAddress,streetNumber,cap,city,category);
          
          TextView textObject=(TextView)findViewById(R.id.object_text);   
          textObject.append(title);
          
          TextView textlocation=(TextView)findViewById(R.id.street_address_text);   
          textlocation.append(streetAddress + ", " +  streetNumber + " " + city + " " + cap);
          
          TextView text_latlng=(TextView)findViewById(R.id.lat_lng_text);   
          text_latlng.append(lat + " - " + lng);
          
          String[] categoryCheck = category.split(",");
          for(int i = 0; i < categoryCheck.length; i++) {
        	  listCateg.add(categoryCheck[i].toString());
        	  
          }
          //Toast.makeText(DetailsPlaceToVote.this, listCateg.toString(),Toast.LENGTH_LONG).show();
		
          TableLayout tl = (TableLayout)findViewById(R.id.list_checkbox);
          
          for(int i = 0; i < categoryCheck.length; i++) {
        	  	    final CheckBox cb = new CheckBox(this);
        	  	    cb.setText(categoryCheck[i]);
        	  	    cb.setChecked(true);
        	  	    
        	        // create a new TableRow
        	        TableRow row = new TableRow(this);
        	        row.addView(cb);
        	 
        	        cb.setOnClickListener(new OnClickListener() {

        	            @Override
        	            public void onClick(View arg0) {

        	            if (cb.isChecked()) {

        	            	listCateg.add(cb.getText().toString());
        	               
        	            } else {

        	            	listCateg.remove(cb.getText().toString());
        	            	
        	            }

        	            //Toast.makeText(DetailsPlaceToVote.this, listCateg.toString(),Toast.LENGTH_LONG).show();	

        	            }

        	        });

        	 
        	  	    tl.addView(row);
 
        	  	}
          
      

          TextView text_vote=(TextView)findViewById(R.id.category_text);   
          text_vote.append(category);
          
          
          
          Button btn_look_place = (Button)findViewById(R.id.btn_streetview);   
          
          	btn_look_place.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent searchAddress = new Intent(Intent.ACTION_VIEW, Uri.parse("google.streetview:cbll="+lat+","+lng+"&cbp=1"));
				startActivity(searchAddress);
				
			}
			});
          	
          	/*Button btn_vote = (Button)findViewById(R.id.vote_button);   
            
          	btn_vote.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ListIterator<String> iterator= listCateg.listIterator();
				
				//crea l'iteratore
				while (iterator.hasNext()) {
					
					categoryVoted = categoryVoted + iterator.next().toString() + ",";

				}
				
				if (!categoryVoted.equals(""))
	               {
	                   int u=categoryVoted.lastIndexOf(",");
	                   categoryVoted=categoryVoted.substring(0, u);
	               }
				
				PlaceClient place = new PlaceClient(title,lat,lng,streetAddress,streetNumber,cap,city,categoryVoted);
				Toast.makeText(DetailsPlaceToVote.this, place.title +place.lat + " , " + place.lng + place.category ,Toast.LENGTH_LONG).show();
				Thread votePlace = PlacesResource.votePlace(place, handler, DetailsPlaceToVote.this);
			}
			});
          	
          	Button btn_back = (Button)findViewById(R.id.back_button);   
            
          	btn_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				
			}
			});*/
          	

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,VOTE_PLACE,0,"Vote").setIcon(R.drawable.votee);	
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		case VOTE_PLACE:
			
ListIterator<String> iterator= listCateg.listIterator();
			
			//crea l'iteratore
			while (iterator.hasNext()) {
				
				categoryVoted = categoryVoted + iterator.next().toString() + ",";

			}
			
			if (!categoryVoted.equals(""))
               {
                   int u=categoryVoted.lastIndexOf(",");
                   categoryVoted=categoryVoted.substring(0, u);
               }
			
			if (categoryVoted.equals(""))
				Toast.makeText(DetailsPlaceToVote.this, "You must select almost a category!",Toast.LENGTH_LONG).show();
			else
			{
				
			
			PlaceClient place = new PlaceClient(title,lat,lng,streetAddress,streetNumber,cap,city,categoryVoted);
			//Toast.makeText(DetailsPlaceToVote.this, place.title +place.lat + " , " + place.lng + place.category ,Toast.LENGTH_LONG).show();
			Thread votePlace = PlacesResource.votePlace(place, handler, DetailsPlaceToVote.this);
			
			}
			break;			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	
public void finish(boolean result) {
	
	Log.i(TAG,"finishSave  Done!");
	 if (result)
	 { 
		Toast.makeText(DetailsPlaceToVote.this, R.string.edit_success,Toast.LENGTH_LONG).show();
		
	 	finish();
	 }
	 else
		 Toast.makeText(DetailsPlaceToVote.this, R.string.saving_error, Toast.LENGTH_LONG).show();
	    
	 
   	
   }
}
