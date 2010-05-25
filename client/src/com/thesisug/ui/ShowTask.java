package com.thesisug.ui;

import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.EventResource;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;

public class ShowTask extends Activity{
	private static final String TAG ="ShowEvent";
	private static final int ASK_CONFIRMATION = 1;
	private static final int WAIT_DELETION = 2;
	private static final int EDIT = 1;
	private static final int DELETE = 2;
	private static final int BACK = 3;
	private Bundle packet;
	private TextView title, location, fromtext, totext, priority_value, description;
	private RatingBar priority;
	private Calendar from, to;
	private float latitude, longitude;
	private static final XsDateTimeFormat xs_DateTime = new XsDateTimeFormat();
	private final Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.show_task);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar);
		TextView titlebar = (TextView) findViewById(R.id.customtitlebar);
		titlebar.setText("Task");
		
		//get the fields
		packet = getIntent().getExtras();
		title = (TextView) findViewById(R.id.task_title); 
		location = (TextView) findViewById(R.id.task_location);
		fromtext = (TextView) findViewById(R.id.task_from); 
		totext = (TextView) findViewById(R.id.task_to); 
		priority_value = (TextView) findViewById(R.id.priority_value);
		description = (TextView) findViewById(R.id.task_description);
		priority = (RatingBar) findViewById(R.id.priority_bar);
		
		//set fields value
		title.setText(packet.getString("title"));
		priority_value.setText(Integer.toString(packet.getInt("priority")));
		priority.setRating(packet.getInt("priority"));
		description.setText(packet.getString("description"));
		latitude = packet.getFloat("latitude");
		longitude = packet.getFloat("longitude");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,EDIT,0,"Edit Event").setIcon(R.drawable.edit);
		menu.add(0,DELETE,0,"Delete Event").setIcon(R.drawable.trash);
		menu.add(0,BACK,0,"Back").setIcon(R.drawable.exit);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT:
			// need to recreate intent to solve the case when user 
			// goes back and forth between edit-show
			Intent intent = new Intent(("com.thesisug.EDIT_EVENT"));
			intent.putExtra("username", packet.getString("username"));
			intent.putExtra("session", packet.getString("session"));
			intent.putExtra("id", packet.getString("id"));
			intent.putExtra("title", title.getText().toString());
			intent.putExtra("location", location.getText().toString());
			intent.putExtra("startTime", new XsDateTimeFormat().format(from));
			intent.putExtra("endTime", new XsDateTimeFormat().format(to));
			intent.putExtra("priority", Math.round(priority.getRating()));
			intent.putExtra("description", description.getText().toString());
			intent.putExtra("longitude", longitude);
			intent.putExtra("latitude", latitude);
			intent.putExtra("originator", 2); //EDIT_EVENT code in EditEvent
			startActivityForResult(intent, 0);
			break;			
		case DELETE:
			showDialog(ASK_CONFIRMATION);
			break;
		default:
			finish();
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) {Log.i(TAG, "no bundle "+resultCode+" "+requestCode);return ;}
        Bundle packet = intent.getExtras();
        try {
			from = (Calendar) xs_DateTime.parseObject(packet.getString("startTime"));
			to = (Calendar) xs_DateTime.parseObject(packet.getString("endTime"));
			//update the fields value
			title.setText(packet.getString("title"));
			location.setText(packet.getString("location"));
			fromtext.setText(getText(R.string.from)+" : "+ printCalendar(from));
			totext.setText(getText(R.string.until)+"  : "+ printCalendar(to));
			priority_value.setText(Integer.toString(packet.getInt("priority")));
			priority.setRating(packet.getInt("priority"));
			description.setText(packet.getString("description"));
			latitude = packet.getFloat("latitude");
			longitude = packet.getFloat("longitude");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id){
		case ASK_CONFIRMATION :
			return new AlertDialog.Builder(ShowTask.this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.ask_for_deletion)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	Thread deleteThread = EventResource.removeEvent(packet.getString("id"), handler, ShowTask.this);
                	showDialog(WAIT_DELETION);
                }
            })
            .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {                	
                }
            })
            .create(); 
		case WAIT_DELETION:
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setCancelable(true);
			dialog.setMessage(getText(R.string.deleting));
			return dialog;
		default : return new ProgressDialog(this);
		}
	}
	
	private String printCalendar(Calendar cal){
		return cal.getTime().toLocaleString(); 
	}
	
	public void finishDeletion(boolean success){
		dismissDialog(WAIT_DELETION);
		if (!success) Toast.makeText(ShowTask.this, R.string.deletion_error,
                Toast.LENGTH_LONG).show();
	}
}

