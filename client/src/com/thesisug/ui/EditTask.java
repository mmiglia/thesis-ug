package com.thesisug.ui;

import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.thesisug.R;
import com.thesisug.communication.GroupResource;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.GroupData;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;
import com.thesisug.notification.TaskNotification;

public class EditTask extends Activity {
	private static final String TAG = "thesisug - EditTask";
	// constants for dialog box choosing
	private static final int TIMEFROM_DIALOG_ID = 0, DEADLINE_DATE_ID = 1, TIMETO_DIALOG_ID = 2, DEADLINE_TIME_ID = 3, SAVE_DATA_ID = 4, CREATE_DATA_ID = 5, DATE_ERROR_ID=6, ASSERTIONS=8;
    // constants for origin activity chooser
	private static final int CREATE_TASK = 1, EDIT_TASK = 2; 
	private static final int GET_USER_GROUP_DIALOG = 7;
	
	private ProgressDialog createDialog;
	private Thread downloadGroupListThread;
	private ArrayAdapter<String> arrGroupsAdapter;
	
	// date and time
    private Calendar deadline=Calendar.getInstance(), notifyStart = Calendar.getInstance(), notifyEnd = Calendar.getInstance();
    
    private final Handler handler = new Handler();
    // button
    private Button deadlineDate, deadlineTime, timeFrom, timeTo, save, back,btn_updateGroupList;
    private EditText title, description;

	//TODO Eliminare e rimpiazzare con una lista dei gruppi disponibili
    //private EditText groupId;
    private Spinner spinnerGroupList;
    
    private RatingBar priority;
    private float latitude, longitude;
	private int currentDialog;
    
	private String packetGroupID="0";
	private boolean setSpinnerSelectedElementAsBundle=true;
	String titleForCheckVoted;
	
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
		//groupId=(EditText) findViewById(R.id.task_groupId);
		spinnerGroupList=(Spinner)findViewById(R.id.spinnerGroupTask);
		
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
				// controlla che il titolo non sia vuoto o contenga solo caratteri white-space
				if (!titleIsValid(title.getText().toString())) {
					// comunica che i titoli vuoti non sono gestiti
						Toast.makeText(getApplicationContext(), R.string.bad_task_name, Toast.LENGTH_SHORT).show();
					// sembra che in Android non sia possibile imporre il focus su un
					// elemento dell'interfaccia utente, allora si "richiede" il focus
					title.requestFocus();
					// esce (senza inviare nulla)
					return;
				}
				SingleTask task;
				currentDialog = packet.getInt("originator");
				String groupID="0";
				switch (currentDialog) {
				case EDIT_TASK:
					showDialog(SAVE_DATA_ID);
					groupID=spinnerGroupList.getSelectedItem().toString().split("-")[0];
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
					task.groupId=groupID;
					Log.i(TAG, "Sending groupID for update:"+task.groupId);
					
					Thread savingThread = TaskResource.updateTask(task,
							handler, EditTask.this);
					break;
				case CREATE_TASK:
					showDialog(CREATE_DATA_ID);
					groupID=spinnerGroupList.getSelectedItem().toString().split("-")[0];
					
					//TODO Verificare la gestione dell'id del reminder e del gruppo (per ora metto -1 ad entrambi visto che è poi il sistema ad assegnare questi valori)
					titleForCheckVoted = title.getText().toString();
					task = new SingleTask("-1",title.getText().toString(), 
							new XsDateTimeFormat(false,true).format(notifyStart),
							new XsDateTimeFormat(false,true).format(notifyEnd), 
							new XsDateTimeFormat().format(deadline), 
							description.getText().toString(),
							Math.round(priority.getRating()),
							"-1", 
							groupID);
					//TODO Eliminare elemento sopra e rimpiazzare con una lista dei gruppi disponibili
					
					
					task.gpscoordinate.longitude = longitude;
					task.gpscoordinate.latitude = latitude;
					Thread creationThread = TaskResource.createTask(task,
							handler, EditTask.this);
					break;
				default:
					break;
				}
				//Check Hints for Tasks (maybe after insert or change we can have some hints here around)
				boolean checkMinDistanceInHintSearch=false;
				TaskNotification.getInstance().startHintSearch(null,checkMinDistanceInHintSearch);
			}
		});
    	back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
    	btn_updateGroupList=(Button)findViewById(R.id.btn_updateGroupList);
    	btn_updateGroupList.setOnClickListener(new OnClickListener(){
    		public void onClick(View v){
    			updateUserGroupList(false);
    		}
    	});
	}
    
    /**
     * 
     * @param setAsDefault: if true set the spinner selected element to the one arrived with the bundle of the intent
     */
	private void updateUserGroupList(boolean setAsDefault){
		//Get group list 
		setSpinnerSelectedElementAsBundle=setAsDefault;
		currentDialog=GET_USER_GROUP_DIALOG;
		showDialog(currentDialog);
		downloadGroupListThread = GroupResource.getUserGroup(handler, EditTask.this);
	}
    
    public void finishSave (boolean result) {
    	switch (currentDialog){
    		case EDIT_TASK : dismissDialog(SAVE_DATA_ID); 
    		
    		break;
    		case CREATE_TASK : dismissDialog(CREATE_DATA_ID);
    		
    		break;
    	}
    	
    	
    	Intent intent1 = new Intent(this, Vote_ont_db.class);
    	intent1.putExtra("title", title.getText().toString());
    	startActivity(intent1);
    	
		
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
			
			// segnala che il contenuto è stato correttamente salvato sul server
			// (distingue fra creazione e modifica)
			switch (currentDialog) {
			case EDIT_TASK: Toast.makeText(EditTask.this, R.string.edit_success,
                    Toast.LENGTH_LONG).show(); 
					
			break;
			case CREATE_TASK: Toast.makeText(EditTask.this, R.string.create_success,
                    Toast.LENGTH_LONG).show(); 
			
			break;
			}
			
			
			/*CustomizeDialog customizeDialog = new CustomizeDialog(this, EditTask.this);
			customizeDialog.show();*/
		
		
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
		
		//Retrive the correct group, getting group list		
		this.packetGroupID=packet.getString("groupID");
		
		updateUserGroupList(true);

		
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
		case GET_USER_GROUP_DIALOG:
			createDialog = new ProgressDialog(this);
			createDialog.setCancelable(true);
			createDialog.setMessage(getText(R.string.getting_user_group_list));
			return createDialog;
		//Prova dialogo ontologica-----------------------------------------------------
			
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

	public void afterGroupListLoaded(List<GroupData> groupList){
    	//Dismiss dialog
    	dismissDialog(GET_USER_GROUP_DIALOG);   

		//Update groupSpinnerList
	
    	arrGroupsAdapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item);
    	arrGroupsAdapter.add("0-"+this.getString(R.string.personal_task));

		if(groupList==null){
			Toast.makeText(this.getApplicationContext(), R.string.fail_to_update_group_list, Toast.LENGTH_LONG).show();;
			spinnerGroupList.setAdapter(arrGroupsAdapter);
			spinnerGroupList.setSelection(0);
			return;
		}

    	
    	for(GroupData g:groupList){
    		arrGroupsAdapter.add(g.groupID+"-"+g.groupName);
    	}
    	
    	spinnerGroupList.setAdapter(arrGroupsAdapter);


		
		//Select default element
		if(setSpinnerSelectedElementAsBundle){
			if(arrGroupsAdapter!=null){
				for(int i=0;i<arrGroupsAdapter.getCount();i++){
					Log.i(TAG,arrGroupsAdapter.getItem(i).split("-")[0]+" vs "+this.packetGroupID+" =..");
					if(arrGroupsAdapter.getItem(i).split("-")[0].equals(this.packetGroupID)){
						spinnerGroupList.setSelection(i);
						break;
					}
				}			
			}else{
				spinnerGroupList.setSelection(0);
			}
		}
		
	}
    
}
