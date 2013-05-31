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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.EventResource;
import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;
import com.thesisug.tracking.ActionTracker;

public class EditEvent extends Activity {
	private static final String TAG = "thesisug - EditEvent";
	// constants for dialog box choosing
	private static final int TIMEFROM_DIALOG_ID = 0, DATEFROM_DIALOG_ID = 1, TIMETO_DIALOG_ID = 2, DATETO_DIALOG_ID = 3, SAVE_DATA_ID = 4, CREATE_DATA_ID = 5, DATE_ERROR_ID=6;
    // constants for origin activity chooser
	private static final int CREATE_EVENT = 1, EDIT_EVENT = 2; 
    
	// date and time
    private Calendar from=Calendar.getInstance(), to = Calendar.getInstance();
    
    private final Handler handler = new Handler();
    // button
    private Button dateFrom, timeFrom, dateTo, gps, timeTo, save, back;
    private EditText title, location, description;
    private RatingBar priority;
    private float latitude, longitude;
	private int currentDialog;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Bundle packet = getIntent().getExtras();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_event);
		
		//Hide keyboard
				this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
						
		//Set deadline to tomorrow
		to.add(Calendar.DAY_OF_MONTH, 1);
		
		dateFrom = (Button) findViewById(R.id.date_from);
		timeFrom = (Button) findViewById(R.id.time_from);
		dateTo = (Button) findViewById(R.id.date_to);
		timeTo = (Button) findViewById(R.id.time_to);
		gps = (Button) findViewById(R.id.event_gps);
        save = (Button) findViewById(R.id.save_button);
        back = (Button) findViewById(R.id.back_button);
		title = (EditText) findViewById(R.id.event_title);
        location = (EditText) findViewById(R.id.event_location);
        description = (EditText) findViewById(R.id.event_description);
		priority = (RatingBar) findViewById(R.id.event_priority);
		if (packet !=null) updateText(packet);
		
        dateFrom.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(DATEFROM_DIALOG_ID);
    		}
    	});
    	timeFrom.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(TIMEFROM_DIALOG_ID);
    		}
    	});
    	dateTo.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(DATETO_DIALOG_ID);
    		}
    	});
    	timeTo.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			showDialog(TIMETO_DIALOG_ID);
    		}
    	});
    	gps.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			Intent intent = new Intent(EditEvent.this, EditGPS.class);
    			intent.putExtra("latitude", latitude);
    			intent.putExtra("longitude", longitude);
    			startActivityForResult(intent, 1);
    		}
    	});
		save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!dateIsValid(from, to)) {showDialog(DATE_ERROR_ID); return;}
				// controlla che il titolo non sia vuoto o contenga solo caratteri white-space
				if (!titleIsValid(title.getText().toString())) {
					// comunica che i titoli vuoti non sono gestiti
						Toast.makeText(getApplicationContext(), R.string.bad_event_name, Toast.LENGTH_SHORT).show();
					// sembra che in Android non sia possibile imporre il focus su un
					// elemento dell'interfaccia utente, allora si "richiede" il focus
					title.requestFocus();
					// esce (senza inviare nulla)
					return;
				}
				SingleEvent ev;
				currentDialog = packet.getInt("originator");
				switch (currentDialog) {
				case EDIT_EVENT:
					showDialog(SAVE_DATA_ID);
					ev = new SingleEvent();
					ev.eventID = packet.getString("eventID");
					Log.d(TAG,"packet.getString(reminderID)"+packet.getString("reminderID"));
					ev.reminderID=packet.getString("reminderID");
					ev.title = title.getText().toString();
					ev.location = location.getText().toString();
					ev.startTime = new XsDateTimeFormat().format(from);
					ev.endTime = new XsDateTimeFormat().format(to);
					ev.priority = Math.round(priority.getRating());
					ev.description = description.getText().toString();
					ev.gpscoordinate.longitude = longitude;
					ev.gpscoordinate.latitude = latitude;
					Thread savingThread = EventResource.updateEvent(ev,
							handler, EditEvent.this);
					break;
				case CREATE_EVENT:
					showDialog(CREATE_DATA_ID);
					ev = new SingleEvent("-1",
							title.getText().toString(),
							new XsDateTimeFormat().format(from),
							new XsDateTimeFormat().format(to), 
							location.getText().toString(),
							description.getText().toString(),
							"0", 
							Math.round(priority.getRating())
							);
					ev.gpscoordinate.longitude = longitude;
					ev.gpscoordinate.latitude = latitude;
					Thread creationThread = EventResource.createEvent(ev,
							handler, EditEvent.this);
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
    		case EDIT_EVENT : dismissDialog(SAVE_DATA_ID); break;
    		case CREATE_EVENT : dismissDialog(CREATE_DATA_ID); break;
    	}
    	if (result) {
    		Intent intent = new Intent();
			intent.putExtra("title", title.getText().toString());
			intent.putExtra("location", location.getText().toString());
			intent.putExtra("startTime", new XsDateTimeFormat().format(from));
			intent.putExtra("endTime", new XsDateTimeFormat().format(to));
			intent.putExtra("priority", Math.round(priority.getRating()));
			intent.putExtra("description", description.getText().toString());
			intent.putExtra("latitude", latitude);
			intent.putExtra("longitude", longitude);
			setResult(RESULT_OK, intent);
			
			// segnala che il contenuto è stato correttamente salvato sul server
			// (distingue fra creazione e modifica)
			switch (currentDialog) {
			case EDIT_EVENT: 
				Toast.makeText(EditEvent.this, R.string.edit_success,Toast.LENGTH_LONG).show(); 
				break;
			case CREATE_EVENT: 
				Toast.makeText(EditEvent.this, R.string.create_success,Toast.LENGTH_LONG).show(); 
				ActionTracker.contentAdded(Calendar.getInstance().getTime(), title.getText().toString(), getApplicationContext(),1);
				break;
			}
			finish();
    	} else {
    		Toast.makeText(EditEvent.this, R.string.saving_error,
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
		location.setText((packet.getString("location")==null)?"":packet.getString("location"));
		description.setText((packet.getString("description")==null)?"":packet.getString("description"));
		priority.setRating((packet.getInt("priority")==0)?3:packet.getInt("priority"));
		Log.i(TAG, "xsDateTime start"+packet.getString("startTime"));
		if (packet.getString("startTime")!=null) extractDate(packet.getString("startTime"), DATEFROM_DIALOG_ID);
		if (packet.getString("endTime")!=null) extractDate(packet.getString("endTime"), DATETO_DIALOG_ID);
		dateFrom.setText(getDateString(from));
		timeFrom.setText(getTimeString(from));
		dateTo.setText(getDateString(to));
		timeTo.setText(getTimeString(to));
		latitude = packet.getFloat("latitude");
		longitude = packet.getFloat("longitude");
	}

	private void extractDate(String xsDateTime, int code) {
		XsDateTimeFormat formatter = new XsDateTimeFormat();
		try {
			Calendar cal = (Calendar)formatter.parseObject(xsDateTime);
			switch (code){
			case DATEFROM_DIALOG_ID:
				from = Calendar.getInstance();
				from.setTimeInMillis(cal.getTimeInMillis());
				break;
			case DATETO_DIALOG_ID:
				to = Calendar.getInstance();
				to.setTimeInMillis(cal.getTimeInMillis());
				break;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIMEFROM_DIALOG_ID:
			return new TimePickerDialog(this, TimeFromSetListener, from.get(Calendar.HOUR_OF_DAY),
					from.get(Calendar.MINUTE), true);
		case DATEFROM_DIALOG_ID:
			return new DatePickerDialog(this, DateFromSetListener, from.get(Calendar.YEAR),
					from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH));
		case TIMETO_DIALOG_ID:
			return new TimePickerDialog(this, TimeToSetListener, to.get(Calendar.HOUR_OF_DAY),
					to.get(Calendar.MINUTE), true);
		case DATETO_DIALOG_ID:
			return new DatePickerDialog(this, DateToSetListener, to.get(Calendar.YEAR),
					to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH));
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
            .setTitle(R.string.date_wrong)
            .setPositiveButton(R.string.change, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            })
            .create();
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener DateFromSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			from.set(year, monthOfYear, dayOfMonth);	
			dateFrom.setText(getDateString(from));
		}
	};

	private TimePickerDialog.OnTimeSetListener TimeFromSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			from.set(Calendar.HOUR_OF_DAY, hourOfDay);
			from.set(Calendar.MINUTE, minute);
			timeFrom.setText(getTimeString(from));
		}
	};
	
	private DatePickerDialog.OnDateSetListener DateToSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			to.set(year, monthOfYear, dayOfMonth);
			dateTo.setText(getDateString(to));
		}
	};

	private TimePickerDialog.OnTimeSetListener TimeToSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			to.set(Calendar.HOUR_OF_DAY, hourOfDay);
			to.set(Calendar.MINUTE, minute);
			timeTo.setText(getTimeString(to));
		}
	};	

	private boolean dateIsValid (Calendar starting, Calendar ending){
		return (starting.before(ending));
	}
	
	private boolean titleIsValid(String taskTitle) {
		// c'� un'occorrenza di .*\\S.* in una stringa se c'� almeno
		// un carattere non white-space. Per Java i caratteri white-space sono
		//  \t,\n,\x0B,\f e \r.
		return taskTitle.matches(".*\\S.*");
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
