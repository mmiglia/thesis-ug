package com.thesisug.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;
import com.thesisug.notification.NotificationDispatcher;
import com.thesisug.notification.SnoozeHandler;
import com.thesisug.tracking.ActionTracker;

public class ShowTask extends Activity{
	private static final String TAG ="thesisug - ShowTask";
	
	//Dialogs
	private static final int ASK_CONFIRMATION = 1;
	private static final int WAIT_DELETION = 2;
	private static final int SET_TASK_DONE=3;
	
	//Menu buttons	
	private static final int EDIT = 1;
	private static final int DELETE = 2;
	private static final int BACK = 3;
	private static final int DONE=4;
	
	
	private Bundle packet;
	private TextView title, priority_value, description, deadline, notifystart, notifyend;
	private RatingBar priority;
	private Calendar deadlinecal, nstart, nend;
	private double latitude, longitude;
	private static final XsDateTimeFormat xs_DateTime = new XsDateTimeFormat();
	private final Handler handler = new Handler();
	
	//Used to mark task as done (we also report the current user location)
	private String locationProvider;
	private LocationManager locManager;
	private Location userLocation;
    private Criteria criteria;
    private double defaultNullLatitude=0.0;
    private double defaultNullLongitude=0.0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.show_task);
		//getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_titlebar);
		TextView titlebar = (TextView) findViewById(R.id.customtitlebar);
		titlebar.setText(R.string.task);
		
		//Hide keyboard
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		//get the fields
		packet = getIntent().getExtras();
		title = (TextView) findViewById(R.id.task_title); 
		deadline = (TextView) findViewById(R.id.task_deadline);
		notifystart = (TextView) findViewById(R.id.notify_start); 
		notifyend = (TextView) findViewById(R.id.notify_end); 
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
		try {
			deadlinecal = (Calendar) new XsDateTimeFormat().parseObject(packet.getString("deadline"));
			nstart = (Calendar) new XsDateTimeFormat(false,true).parseObject(packet.getString("notifystart"));
			nend = (Calendar) new XsDateTimeFormat(false,true).parseObject(packet.getString("notifyend"));
			deadline.setText(printCalendar(deadlinecal));
			notifystart.setText(getText(R.string.from)+" : "+ printTime(nstart));
			notifyend.setText(getText(R.string.until)+"  : "+ printTime(nend));
		} catch (ParseException e) {
			Log.i(TAG, "Date Parse exception catched");
			e.printStackTrace();
		}
		
		//This is used to mark tasks as done
        criteria = new Criteria();
    	criteria.setAccuracy(Criteria.ACCURACY_FINE);		
		locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,EDIT,0,getText(R.string.edit_task)).setIcon(R.drawable.edit);
		menu.add(0,DONE,0,getText(R.string.mark_task_as_done)).setIcon(R.drawable.done);
		menu.add(0,DELETE,0,getText(R.string.delete_task)).setIcon(R.drawable.trash);
		menu.add(0,BACK,0,getText(R.string.back)).setIcon(R.drawable.back);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case EDIT:
			// need to recreate intent to solve the case when user 
			// goes back and forth between edit-show
			Intent intent = new Intent(ShowTask.this, EditTask.class);
			intent.putExtra("username", packet.getString("username"));
			intent.putExtra("session", packet.getString("session"));
			intent.putExtra("taskID", packet.getString("taskID"));
			intent.putExtra("reminderID", packet.getString("reminderID"));
			intent.putExtra("title", title.getText().toString());
			intent.putExtra("deadline", new XsDateTimeFormat().format(deadlinecal));
			intent.putExtra("notifystart", new XsDateTimeFormat(false,true).format(nstart));
			intent.putExtra("notifyend", new XsDateTimeFormat(false, true).format(nend));
			intent.putExtra("priority", Math.round(priority.getRating()));
			intent.putExtra("description", description.getText().toString());
			intent.putExtra("longitude", longitude);
			intent.putExtra("latitude", latitude);
			intent.putExtra("originator", 2); //EDIT_TASK code in EditTask
			Log.i(TAG,"GroupID to EditTask:"+packet.getString("groupID"));
			intent.putExtra("groupID",packet.getString("groupID"));
			startActivityForResult(intent, 0);
			break;			
		case DELETE:
			showDialog(ASK_CONFIRMATION);
			break;
		case DONE:
			showDialog(SET_TASK_DONE);
			
			//Getting last known location
			Location userLocation=getCurrentLocation();
			if(userLocation==null){
				Log.e(TAG, "userLocation is null!");
				latitude=defaultNullLatitude;
				longitude=defaultNullLongitude;
			}else{
				Log.i(TAG, "userLocation NOT is null!");
				latitude=userLocation.getLatitude();
				longitude=userLocation.getLongitude();
			}
			
			Thread taskDoneThread = TaskResource.markTaskAsDone(handler, ShowTask.this,
					packet.getString("taskID"),latitude,longitude);
			SnoozeHandler.removeSnooze(title.getText().toString());
			ActionTracker.contentCompleted(Calendar.getInstance().getTime(), title.getText().toString(), getApplicationContext(), 0);
			
			break;
		default:
			finish();
			break;
		}
		return true;
	}

	
	private Location getCurrentLocation(){
		//Location provider		
		if(locManager==null){
			Log.e(TAG, "locManager==null");
			return null;
		}
		locationProvider=locManager.getBestProvider(criteria,true);
		if(locationProvider==null ){
			Log.e(TAG, "locationProvider==null");
			return null;
		}
		//Current location
		if(locationProvider!=null ){
			userLocation = locManager.getLastKnownLocation(locationProvider);
		}
		return userLocation;
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) {Log.i(TAG, "no bundle "+resultCode+" "+requestCode);return ;}
        Bundle packet = intent.getExtras();
        try {
			nstart = (Calendar) new XsDateTimeFormat(false,true).parseObject(packet.getString("notifystart"));
			nend = (Calendar) new XsDateTimeFormat(false,true).parseObject(packet.getString("notifyend"));
			deadlinecal = (Calendar) new XsDateTimeFormat().parseObject(packet.getString("deadline"));
			//update the fields value
			title.setText(packet.getString("title"));
			deadline.setText(printCalendar(deadlinecal));
			notifystart.setText(getText(R.string.from)+" : "+ printTime(nstart));
			notifyend.setText(getText(R.string.until)+"  : "+ printTime(nend));
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
            .setTitle(R.string.ask_for_deletion_task)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
        			
                	Thread deleteThread = TaskResource.removeTask(packet.getString("taskID"), handler, ShowTask.this);
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
	
	private String printTime(Calendar cal){
		SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");
		Calendar temp = Calendar.getInstance();
		cal.set(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH));
		return formatter.format(cal.getTime());
	}
	
	public void finishDeletion(boolean success){
		dismissDialog(WAIT_DELETION);
		if (!success) Toast.makeText(ShowTask.this, R.string.deletion_error,
                Toast.LENGTH_LONG).show();
		else {
			// comunica che la cancellazione è avvenuta con successo
			Toast.makeText(ShowTask.this, R.string.deletion_success,
	                Toast.LENGTH_LONG).show();
			//Delete eventual snooze and notification corresponding to this task
			SnoozeHandler.removeSnooze(title.getText().toString());
			NotificationDispatcher.deleteNotification(title.getText().hashCode());
			
			finish();
		}
	}

	public void afterTaskDoneSet(boolean success) {
		dismissDialog(SET_TASK_DONE);
		if (!success) Toast.makeText(ShowTask.this, R.string.task_done_error,
                Toast.LENGTH_LONG).show();
		else {
			// comunica che la cancellazione è avvenuta con successo
			Toast.makeText(ShowTask.this, R.string.task_done_success,
	                Toast.LENGTH_LONG).show();
			finish();
		}
		
	}
}

