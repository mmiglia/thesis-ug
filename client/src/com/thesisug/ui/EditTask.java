package com.thesisug.ui;

import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;

public class EditTask extends Activity {
	private static final String TAG = "thesisug - EditTask";
	// constants for dialog box choosing
	private static final int TIMEFROM_DIALOG_ID = 0, DEADLINE_DATE_ID = 1, TIMETO_DIALOG_ID = 2, DEADLINE_TIME_ID = 3, SAVE_DATA_ID = 4, CREATE_DATA_ID = 5, DATE_ERROR_ID=6;
    // constants for origin activity chooser
	private static final int CREATE_TASK = 1, EDIT_TASK = 2; 
    
	// date and time
    private Calendar deadline=Calendar.getInstance(), notifyStart = Calendar.getInstance(), notifyEnd = Calendar.getInstance();
    
    private final Handler handler = new Handler();
    // button
    private Button deadlineDate, deadlineTime, timeFrom, timeTo, save, back;
    private EditText title, description;

	//TODO Eliminare e rimpiazzare con una lista dei gruppi disponibili
    private EditText groupId;
    
    private RatingBar priority;
    private float latitude, longitude;
	private int currentDialog;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle packet = getIntent().getExtras();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_task);
		title = (EditText) findViewById(R.id.task_title);
		deadlineDate = (Button) findViewById(R.id.date_deadline);
		deadlineTime = (Button) findViewById(R.id.time_deadline);
		//TODO Eliminare e rimpiazzare con una lista dei gruppi disponibili
		groupId=(EditText) findViewById(R.id.task_groupId);
		
		//Set deadline to tomorrow
		deadline.add(Calendar.DAY_OF_MONTH, 1);
		
		// set default notification time for a task
		notifyStart.set(Calendar.HOUR_OF_DAY, 6);
		notifyStart.set(Calendar.MINUTE, 0);
		notifyEnd.setTimeInMillis(notifyStart.getTimeInMillis());
		notifyEnd.set(Calendar.HOUR_OF_DAY, 21);
		notifyEnd.set(Calendar.MINUTE, 0);
		
		timeFrom = (Button) findViewById(R.id.time_from);
		timeTo = (Button) findViewById(R.id.time_to);
        save = (Button) findViewById(R.id.save_button);
        back = (Button) findViewById(R.id.back_button);
        description = (EditText) findViewById(R.id.task_description);
		priority = (RatingBar) findViewById(R.id.task_priority);
		if (packet !=null) updateText(packet);
		
		deadlineDate.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(DEADLINE_DATE_ID);
    		}
    	});
		deadlineTime.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(DEADLINE_TIME_ID);
    		}
    	});
		timeFrom.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(TIMEFROM_DIALOG_ID);
    		}
    	});
    	timeTo.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(TIMETO_DIALOG_ID);
    		}
    	});
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!timeIsValid(notifyStart, notifyEnd)) {showDialog(DATE_ERROR_ID); return;}
				SingleTask task;
				currentDialog = packet.getInt("originator");
				switch (currentDialog) {
				case EDIT_TASK:
					showDialog(SAVE_DATA_ID);
					task = new SingleTask();
					task.taskID = packet.getString("taskID");
					task.reminderID=packet.getString("reminderID");
					task.title = title.getText().toString();
					task.dueDate = new XsDateTimeFormat().format(deadline);
					task.notifyTimeStart = new XsDateTimeFormat(false,true).format(notifyStart);
					task.notifyTimeEnd = new XsDateTimeFormat(false,true).format(notifyEnd);
					task.priority = Math.round(priority.getRating());
					task.description = description.getText().toString();
					task.gpscoordinate.longitude = longitude;
					task.gpscoordinate.latitude = latitude;
					
					//TODO Eliminare e rimpiazzare con una lista dei gruppi disponibili
					task.groupId=groupId.getText().toString();
					
					Thread savingThread = TaskResource.updateTask(task,
							handler, EditTask.this);
					break;
				case CREATE_TASK:
					showDialog(CREATE_DATA_ID);
					
					//TODO Verificare la gestione dell'id del reminder e del gruppo (per ora metto -1 ad entrambi visto che è poi il sistema ad assegnare questi valori)
					task = new SingleTask("-1",title.getText().toString(), 
							new XsDateTimeFormat(false,true).format(notifyStart),
							new XsDateTimeFormat(false,true).format(notifyEnd), 
							new XsDateTimeFormat().format(deadline), 
							description.getText().toString(),
							Math.round(priority.getRating()),
							"-1", 
							groupId.getText().toString());
					//TODO Eliminare elemento sopra e rimpiazzare con una lista dei gruppi disponibili
					
					
					task.gpscoordinate.longitude = longitude;
					task.gpscoordinate.latitude = latitude;
					Thread creationThread = TaskResource.createTask(task,
							handler, EditTask.this);
					break;
				default:
					break;
				}
			}
		});
    	back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}
    
    public void finishSave (boolean result) {
    	switch (currentDialog){
    		case EDIT_TASK : dismissDialog(SAVE_DATA_ID); break;
    		case CREATE_TASK : dismissDialog(CREATE_DATA_ID); break;
    	}
    	if (result) {
    		Intent intent = new Intent();
			intent.putExtra("title", title.getText().toString());
			intent.putExtra("deadline", new XsDateTimeFormat().format(deadline));
			intent.putExtra("notifystart", new XsDateTimeFormat(false,true).format(notifyStart));
			intent.putExtra("notifyend", new XsDateTimeFormat(false,true).format(notifyEnd));
			intent.putExtra("priority", Math.round(priority.getRating()));
			intent.putExtra("description", description.getText().toString());
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);
			setResult(RESULT_OK, intent);
			finish();
    	} else {
    		Toast.makeText(EditTask.this, R.string.saving_error,
                    Toast.LENGTH_LONG).show();
    	}    	
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        latitude = intent.getFloatExtra("latitude", 0);
        longitude = intent.getFloatExtra("longitude", 0);
        Log.i(TAG, "latitude = "+latitude+" longitude = "+longitude);
    }
    
	private void updateText(Bundle packet) {
		title.setText((packet.getString("title")==null)?"":packet.getString("title"));
		groupId.setText((packet.getString("groupId")==null)?"":packet.getString("groupId"));
		description.setText((packet.getString("description")==null)?"":packet.getString("description"));
		priority.setRating((packet.getInt("priority")==0)?3:packet.getInt("priority"));
		if (packet.getString("deadline")!=null) extractDate(packet.getString("deadline"), DEADLINE_DATE_ID);
		if (packet.getString("notifystart")!=null) extractDate(packet.getString("notifystart"), TIMEFROM_DIALOG_ID);
		if (packet.getString("notifyend")!=null) extractDate(packet.getString("notifyend"), TIMETO_DIALOG_ID);
		deadlineDate.setText(getDateString(deadline));
		deadlineTime.setText(getTimeString(deadline));
		timeFrom.setText(getTimeString(notifyStart));
		timeTo.setText(getTimeString(notifyEnd));
		latitude = packet.getFloat("latitude");
		longitude = packet.getFloat("longitude");
	}

	private void extractDate(String xsDateTime, int code) {
		try {
			Calendar cal ;
			switch (code){
			case DEADLINE_DATE_ID:
				cal = (Calendar)new XsDateTimeFormat().parseObject(xsDateTime);
				deadline = Calendar.getInstance();
				deadline.setTimeInMillis(cal.getTimeInMillis());
				break;
			case TIMEFROM_DIALOG_ID:
				cal = (Calendar)new XsDateTimeFormat(false,true).parseObject(xsDateTime);
				notifyStart = Calendar.getInstance();
				notifyStart.setTimeInMillis(cal.getTimeInMillis());
				Log.i(TAG, "notifystart is = "+notifyStart.getTime().toLocaleString());
				break;
			case TIMETO_DIALOG_ID:
				cal = (Calendar)new XsDateTimeFormat(false,true).parseObject(xsDateTime);
				notifyEnd = Calendar.getInstance();
				notifyEnd.setTimeInMillis(cal.getTimeInMillis());
				Log.i(TAG, "notifyend is = "+notifyEnd.getTime().toLocaleString());
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DEADLINE_TIME_ID:
			return new TimePickerDialog(this, DeadlineTimeSetListener, deadline.get(Calendar.HOUR_OF_DAY),
					deadline.get(Calendar.MINUTE), true);
		case DEADLINE_DATE_ID:
			
			return new DatePickerDialog(this, DeadlineDateSetListener, deadline.get(Calendar.YEAR),
					deadline.get(Calendar.MONTH), deadline.get(Calendar.DAY_OF_MONTH));
		case TIMETO_DIALOG_ID:
			return new TimePickerDialog(this, TimeToSetListener, notifyEnd.get(Calendar.HOUR_OF_DAY),
					notifyEnd.get(Calendar.MINUTE), true);
		case TIMEFROM_DIALOG_ID:
			return new TimePickerDialog(this, TimeFromSetListener, notifyStart.get(Calendar.HOUR_OF_DAY),
					notifyStart.get(Calendar.MINUTE), true);
		case SAVE_DATA_ID:
			final ProgressDialog savedialog = new ProgressDialog(this);
			savedialog.setCancelable(true);
			savedialog.setMessage(getText(R.string.saving));
			return savedialog;
		case CREATE_DATA_ID:
			final ProgressDialog createdialog = new ProgressDialog(this);
			createdialog.setCancelable(true);
			createdialog.setMessage(getText(R.string.creating));
			return createdialog;
		case DATE_ERROR_ID:
			return new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.notification_time_wrong)
            .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            })
            .create();
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener DeadlineDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			deadline.set(year, monthOfYear, dayOfMonth);
			deadlineDate.setText(getDateString(deadline));
		}
	};

	private TimePickerDialog.OnTimeSetListener DeadlineTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			deadline.set(Calendar.HOUR_OF_DAY, hourOfDay);
			deadline.set(Calendar.MINUTE, minute);
			deadlineTime.setText(getTimeString(deadline));
		}
	};
	
	private TimePickerDialog.OnTimeSetListener TimeFromSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			notifyStart.set(Calendar.HOUR_OF_DAY, hourOfDay);
			notifyStart.set(Calendar.MINUTE, minute);
			timeFrom.setText(getTimeString(notifyStart));
		}
	};
	
	private TimePickerDialog.OnTimeSetListener TimeToSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			notifyEnd.set(Calendar.HOUR_OF_DAY, hourOfDay);
			notifyEnd.set(Calendar.MINUTE, minute);
			timeTo.setText(getTimeString(notifyEnd));
		}
	};	
	
	private boolean timeIsValid (Calendar starting, Calendar ending){
		return (starting.before(ending));
	}

	private CharSequence getDateString(Calendar cal) {
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		CharSequence monthChar="";
		switch (month){
			case 0: monthChar = getText(R.string.January); break;
			case 1: monthChar = getText(R.string.February); break;
			case 2: monthChar = getText(R.string.March); break;
			case 3: monthChar = getText(R.string.April); break;
			case 4: monthChar = getText(R.string.May); break;
			case 5: monthChar = getText(R.string.June); break;
			case 6: monthChar = getText(R.string.July); break;
			case 7: monthChar = getText(R.string.August); break;
			case 8: monthChar = getText(R.string.September); break;
			case 9: monthChar = getText(R.string.October); break;
			case 10: monthChar = getText(R.string.November); break;
			case 11: monthChar = getText(R.string.December); break;
			default: break;
		}
		return String.valueOf(day)+" "+monthChar+" "+String.valueOf(year);
	}

	private CharSequence getTimeString(Calendar cal) {
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		return String.valueOf((hour==0)?12:cal.get(Calendar.HOUR))+":"+((minute<10)?"0":"")+String.valueOf(minute)+ ((hour>=12)?" PM":" AM");
	}
    
}
