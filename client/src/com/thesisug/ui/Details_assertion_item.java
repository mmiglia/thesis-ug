package com.thesisug.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.valueobject.SingleItemLocation;

public class Details_assertion_item extends Activity{
	
	private static final String TAG = "thesisug - Details_assertion_item";
	private final Handler handler = new Handler();	
	private static final int DELETE_ASSERTIONS=0;
	private static final int BACK=1;
	private SingleItemLocation itemLocation;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle packet = getIntent().getExtras();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.details_assertion_item);
		
		  Intent intent=getIntent(); // l'intent di questa activity
        
         
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

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,DELETE_ASSERTIONS,0,R.string.updateAssertions).setIcon(R.drawable.trash);	
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {

		case DELETE_ASSERTIONS:
			/*intent=new Intent(ManageGroupMenu.this,InviteToJoinGroup.class);
			startActivityForResult(intent, 0);*/
			break;			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	
	
	
}
