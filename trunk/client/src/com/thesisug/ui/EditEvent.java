package com.thesisug.ui;

import java.text.ParseException;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TimePicker;

import com.thesisug.R;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;

public class EditEvent extends Activity {
	private static final String TAG = "EditEvent";
	private static final int TIMEFROM_DIALOG_ID = 0;
	private static final int DATEFROM_DIALOG_ID = 1;
	private static final int TIMETO_DIALOG_ID = 2;
	private static final int DATETO_DIALOG_ID = 3;
    
	// date and time
    private Calendar from, to;
    
    // button
    private Button dateFrom, timeFrom, dateTo, timeTo, save, back;
    private EditText title, location, description;
    private RatingBar priority;
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle packet = getIntent().getExtras();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.edit_event);
		dateFrom = (Button) findViewById(R.id.date_from);
		timeFrom = (Button) findViewById(R.id.time_from);
		dateTo = (Button) findViewById(R.id.date_to);
		timeTo = (Button) findViewById(R.id.time_to);
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
    	save.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//Thread savingThread = EventResource.s
				Intent intent = new Intent();
				intent.putExtra("title", title.getText().toString());
				intent.putExtra("location", location.getText().toString());
				intent.putExtra("startTime", new XsDateTimeFormat().format(from));
				intent.putExtra("endTime", new XsDateTimeFormat().format(to));
				intent.putExtra("priority", Math.round(priority.getRating()));
				intent.putExtra("description", description.getText().toString());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
    	back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
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
			return new TimePickerDialog(this, TimeFromSetListener, from.get(Calendar.HOUR),
					from.get(Calendar.MINUTE), true);
		case DATEFROM_DIALOG_ID:
			return new DatePickerDialog(this, DateFromSetListener, from.get(Calendar.YEAR),
					from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH));
		case TIMETO_DIALOG_ID:
			return new TimePickerDialog(this, TimeToSetListener, to.get(Calendar.HOUR),
					to.get(Calendar.MINUTE), true);
		case DATETO_DIALOG_ID:
			return new DatePickerDialog(this, DateToSetListener, to.get(Calendar.YEAR),
					to.get(Calendar.MONTH), to.get(Calendar.DAY_OF_MONTH));
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
			from.set(Calendar.HOUR, hourOfDay);
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
			to.set(Calendar.HOUR, hourOfDay);
			to.set(Calendar.MINUTE, minute);
			timeTo.setText(getTimeString(to));
		}
	};	

	protected CharSequence getDateString(Calendar cal) {
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

	protected CharSequence getTimeString(Calendar cal) {
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		return String.valueOf((hour==0)?12:hour)+":"+((minute<10)?"0":"")+String.valueOf(minute)+ ((hour>=12)?" PM":" AM");
	}
    
}
