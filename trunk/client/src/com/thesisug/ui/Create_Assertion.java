package com.thesisug.ui;

import com.thesisug.R;
import com.thesisug.communication.valueobject.SingleItemLocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class Create_Assertion extends Activity{
	
	private static final String TAG = "thesisug - Create_Assertion";
	private final Handler handler = new Handler();	
	
	private SingleItemLocation itemLocation;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.create_assertion);
		
		  /*Intent intent=getIntent(); // l'intent di questa activity
        
         
          String item=intent.getStringExtra("item");
          String location=intent.getStringExtra("location");
          String username=intent.getStringExtra("username");
          String n_views=intent.getStringExtra("n_views");
          String n_votes=intent.getStringExtra("n_votes");
          String vote=intent.getStringExtra("vote");
          String description=intent.getStringExtra("description");
          
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
*/
	}

}
