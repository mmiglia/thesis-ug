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
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;

public class ShowTask extends Activity{
	private static final String TAG ="ShowTask";
	private static final int ASK_CONFIRMATION = 1;
	private static final int WAIT_DELETION = 2;
	private static final int EDIT = 1;
	private static final int DELETE = 2;
	private static final int BACK = 3;
	private Bundle packet;
	private TextView title, priority_value, description, deadline, notifystart, notifyend;
	private RatingBar priority;
	private Calendar deadlinecal, nstart, nend;
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,EDIT,0,getText(R.string.edit_task)).setIcon(R.drawable.edit);
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
			intent.putExtra("id", packet.getString("id"));
			intent.putExtra("title", title.getText().toString());
			intent.putExtra("deadline", new XsDateTimeFormat().format(deadlinecal));
			intent.putExtra("notifystart", new XsDateTimeFormat(false,true).format(nstart));
			intent.putExtra("notifyend", new XsDateTimeFormat(false, true).format(nend));
			intent.putExtra("priority", Math.round(priority.getRating()));
			intent.putExtra("description", description.getText().toString());
			intent.putExtra("longitude", longitude);
			intent.putExtra("latitude", latitude);
			intent.putExtra("originator", 2); //EDIT_TASK code in EditTask
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
            .setTitle(R.string.ask_for_deletion)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	Thread deleteThread = TaskResource.removeTask(packet.getString("id"), handler, ShowTask.this);
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
		return formatter.format(cal.getTime());
	}
	
	public void finishDeletion(boolean success){
		dismissDialog(WAIT_DELETION);
		if (!success) Toast.makeText(ShowTask.this, R.string.deletion_error,
                Toast.LENGTH_LONG).show();
		else finish();
	}
}

