package com.thesisug.ui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.valueobject.SingleItemLocation;


public class Details_assertion_item extends Activity{
	
	private static final String TAG = "thesisug - Details_assertion_item";
	private final Handler handler = new Handler();	
	private static final int DELETE_ASSERTIONS=0;
	private static final int BACK=1;
	private SingleItemLocation itemLocation;
	private String item;
    private String location;
    private String username;
    private String n_views;
    private String n_votes;
    private String vote;
    private String description;
   
	
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle packet = getIntent().getExtras();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.details_assertion_item);
		
		  Intent intent=getIntent(); // l'intent di questa activity
        
         
          item=intent.getStringExtra("item");
          location=intent.getStringExtra("location");
          username=intent.getStringExtra("username");
          n_views=intent.getStringExtra("n_views");
          n_votes=intent.getStringExtra("n_votes");
          vote=intent.getStringExtra("vote");
          description=intent.getStringExtra("description");
          
          itemLocation = new SingleItemLocation(item,location,username,n_views,n_votes,vote);
          
          TextView textObject=(TextView)findViewById(R.id.object_text);   
          textObject.append(item);
          
          TextView textlocation=(TextView)findViewById(R.id.location_text);   
          textlocation.append(location);
          
          TextView textusername=(TextView)findViewById(R.id.username_text);   
          textusername.append(username);
          
          TextView textn_views=(TextView)findViewById(R.id.n_views_text);   
          textn_views.append(n_views);
          
          TextView textn_votes=(TextView)findViewById(R.id.n_votes_text);   
          textn_votes.append(n_votes);
          
          TextView text_vote=(TextView)findViewById(R.id.vote_text);   
          text_vote.append(vote);
          
          TextView text_description=(TextView)findViewById(R.id.description_text);   
          text_description.append(description);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,DELETE_ASSERTIONS,0,"Delete vote!").setIcon(R.drawable.trash);	
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {

		case DELETE_ASSERTIONS:
			
			Thread deleteAssertionItem = AssertionsResource.deleteAssertions_item(itemLocation, handler, Details_assertion_item.this);
			break;			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	
public void finishSave (boolean result) {
	Intent intent;
	Log.i(TAG,"finishSave  FATTO!");
	 if (result)
	 { 
		Toast.makeText(Details_assertion_item.this, R.string.edit_success,Toast.LENGTH_LONG).show();
		
	 	finish();
	 }
	 else
		 Toast.makeText(Details_assertion_item.this, R.string.saving_error, Toast.LENGTH_LONG).show();
	    
	 
   	
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
