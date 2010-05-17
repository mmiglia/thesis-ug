package com.thesisug.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.RatingBar;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;

public class ShowEvent extends Activity{
	private static final String TAG ="ShowEvent";
	private static final int EDIT = 1;
	private static final int DELETE = 2;
	private static final int BACK = 3;
	private Bundle packet;
	private TextView title, location, fromtext, totext, priority_value, description;
	private RatingBar priority;
	private Calendar from, to;
	private static final XsDateTimeFormat xs_DateTime = new XsDateTimeFormat();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.show_event);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar);
		TextView titlebar = (TextView) findViewById(R.id.customtitlebar);
		titlebar.setText("Event");
		
		//get the fields
		packet = getIntent().getExtras();
		title = (TextView) findViewById(R.id.event_title); 
		location = (TextView) findViewById(R.id.event_location);
		fromtext = (TextView) findViewById(R.id.event_from); 
		totext = (TextView) findViewById(R.id.event_to); 
		priority_value = (TextView) findViewById(R.id.priority_value);
		description = (TextView) findViewById(R.id.event_description);
		priority = (RatingBar) findViewById(R.id.priority_bar);
		
		//set fields value
		title.setText(packet.getString("title"));
		location.setText(packet.getString("location"));
		priority_value.setText(Integer.toString(packet.getInt("priority")));
		priority.setRating(packet.getInt("priority"));
		description.setText(packet.getString("description"));
		try {
			from = (Calendar) xs_DateTime.parseObject(packet.getString("startTime"));
			to = (Calendar) xs_DateTime.parseObject(packet.getString("endTime"));
			fromtext.setText(getText(R.string.from)+" : "+ printCalendar(from));
			totext.setText(getText(R.string.until)+"  : "+ printCalendar(to));
		} catch (ParseException e) {
			e.printStackTrace();
		}
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
			Intent intent = new Intent(("com.thesisug.EDIT_EVENT"));
			intent.putExtra("title", title.getText().toString());
			intent.putExtra("location", location.getText().toString());
			intent.putExtra("startTime", new XsDateTimeFormat().format(from));
			intent.putExtra("endTime", new XsDateTimeFormat().format(to));
			intent.putExtra("priority", Math.round(priority.getRating()));
			intent.putExtra("description", description.getText().toString());
			startActivityForResult(intent, 0);
			break;			
		case DELETE:
			showDialog(0);
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
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getText(R.string.retrieving_event));
			dialog.setIndeterminate(true);
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					Log.i(TAG, "Retrieving data is canceled");
				}
			});
			return dialog;
		}
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		return dialog;
	}
	
	private String printCalendar(Calendar cal){
		return cal.getTime().toLocaleString(); 
	}
}
