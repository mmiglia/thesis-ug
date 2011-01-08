package com.thesisug.ui;

import java.text.ParseException;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.EventResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.Reminder;
import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;
import com.thesisug.notification.EventNotification;

public class Todo extends ListActivity implements OnInitListener{
	public final static String TAG = "thesisug - TodoActivity";
	public final static String ITEM_DATA = "data";
	public final static String REMIND_ME = "remindme";
	public final static int CREATE_EVENT = 1;
	public final static int CREATE_TASK = 2;
	public final static int VOICE_INPUT = 3;
	public final static int BACK = 4;
	public final static int UPDATE_TASK_EVENT = 5;
	public final static int MANAGE_GROUPS=6;
	
	private Thread downloadEventThread, downloadTaskThread;
	private static int counter = 0; // counter for task and event thread completion
	private static String username, session;
	private AccountManager accountManager;
	private AlarmManager am;
	private Account[] accounts;
	private final Handler handler = new Handler();
	private static SharedPreferences usersettings;
	private Intent eventNotificationIntent;
	private static PendingIntent alarmIntent;
	private static List<LinkedHashMap<String,?>> event = new LinkedList<LinkedHashMap<String,?>>();
	private static List<LinkedHashMap<String,?>> tasks = new LinkedList<LinkedHashMap<String,?>>();
	private static String serverURI="";
	
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        accountManager = AccountManager.get(getApplicationContext());
        Log.d(TAG,"Todo_3");
        accounts = accountManager.getAccountsByType(com.thesisug.Constants.ACCOUNT_TYPE);

        am = (AlarmManager)getSystemService(ALARM_SERVICE);
        eventNotificationIntent = new Intent(Todo.this, EventNotification.class);
        alarmIntent = PendingIntent.getBroadcast(Todo.this,
                0, eventNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        usersettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (accounts.length == 0) {
        	Log.d(TAG, "accounts.length");
        	Intent login = new Intent(getApplicationContext(),Login.class);
        	startActivityForResult(login, 0);
        } else {
        	Log.d(TAG, "jumlah ="+accounts.length);
        	SeparatedListAdapter adapter = new SeparatedListAdapter(this);
        	setListAdapter(adapter);
        	showDialog(0);
        	username = accounts[0].name;
        	serverURI=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("serverURI",NetworkUtilities.SERVER_URI);
        	if(!serverURI.equals(NetworkUtilities.SERVER_URI)){
        		NetworkUtilities.changeServerURI(serverURI);
        	}
    		downloadEventThread = EventResource.getAllEvent(handler, this);
    		downloadTaskThread = TaskResource.getFirstTask(handler, this);
        }

	}
	
	
	@Override
	public void onPause() {
		super.onPause();
		Log.i (TAG, "Todo Activity is onPause, saving preferences");
		// save checkbox states on pause
		ListView currentlist = getListView();
		SharedPreferences.Editor editor = usersettings.edit();
		for( int i=0;i<currentlist.getChildCount();i++ ) { 
			View currentview = (View)currentlist.getChildAt(i);
		    TextView title = (TextView) currentview.findViewById(R.id.list_complex_title);
			CheckBox cbox = (CheckBox) currentview.findViewById(R.id.complex_checkbox);
			if (title == null || cbox == null) continue;
		    editor.putBoolean(title.getText().toString(), cbox.isChecked());
		}
		editor.commit();
	}
	
	private LinkedHashMap<String,?> createItem(Reminder reminder, boolean remindme) {
		LinkedHashMap item = new LinkedHashMap();
		item.put(ITEM_DATA, reminder);
		item.put(REMIND_ME, remindme);
		return item;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,CREATE_EVENT,0,"Create Event").setIcon(R.drawable.event);
		menu.add(0,CREATE_TASK,0,"Create Task").setIcon(R.drawable.task);
		menu.add(0,VOICE_INPUT,0,"Voice Input").setIcon(R.drawable.voice);
		menu.add(0,UPDATE_TASK_EVENT,0,"Synchronize").setIcon(R.drawable.sync);
		menu.add(0,MANAGE_GROUPS,0,"Groups").setIcon(R.drawable.group);
		menu.add(0,BACK,0,"EXIT").setIcon(R.drawable.exit);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case CREATE_EVENT:
			// need to recreate intent to solve the case when user 
			// goes back and forth between edit-show
			intent = new Intent(Todo.this, EditEvent.class);
			intent.putExtra("originator", 1);// CREATE_EVENT code in EditEvent
			startActivityForResult(intent, 0);
			break;			
		case CREATE_TASK:
			// need to recreate intent to solve the case when user 
			// goes back and forth between edit-show
			intent = new Intent(Todo.this, EditTask.class);
			intent.putExtra("originator", 1);// CREATE_EVENT code in EditEvent
			startActivityForResult(intent, 0);
			break;
			
		case VOICE_INPUT:
			intent = new Intent(Todo.this, Input.class);
			startActivityForResult(intent, 0);
			break;
		
		case MANAGE_GROUPS:
			intent=new Intent(Todo.this,ManageGroupMenu.class);
			startActivityForResult(intent, 0);
			break;
			
		case UPDATE_TASK_EVENT:
        	showDialog(0);
    		downloadEventThread = EventResource.getAllEvent(handler, this);
    		downloadTaskThread = TaskResource.getFirstTask(handler, this);
			break;			
			
		default:
			finish();
			break;
		}
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		LinkedHashMap item = (LinkedHashMap) (l.getItemAtPosition(position));
		Intent intent;
		if (item.get(ITEM_DATA) instanceof SingleEvent){
			SingleEvent ev = (SingleEvent) item.get(ITEM_DATA);
			// return if there are no event today
			if (ev.title == getText(R.string.no_event_today).toString()) return;
			intent = new Intent(Todo.this, ShowEvent.class);
			intent.putExtra("eventID", ev.eventID);
			intent.putExtra("reminderID", ev.reminderID);
			intent.putExtra("username", username);
			intent.putExtra("session", session);
			intent.putExtra("title", ev.title);
			intent.putExtra("location", ev.location);
			intent.putExtra("startTime", ev.startTime);
			intent.putExtra("endTime", ev.endTime);
			intent.putExtra("priority", ev.priority);
			intent.putExtra("description", ev.description);
			intent.putExtra("eventID", ev.eventID);
			intent.putExtra("longitude", ev.gpscoordinate.longitude);
			intent.putExtra("latitude", ev.gpscoordinate.latitude);
		}
		else {
			SingleTask task = (SingleTask) item.get(ITEM_DATA);
			if (task.title == getText(R.string.no_task_today).toString()) return;
			intent = new Intent(Todo.this, ShowTask.class);
			intent.putExtra("taskID", task.taskID);
			intent.putExtra("reminderID", task.reminderID);
			intent.putExtra("username", username);
			intent.putExtra("session", session);
			intent.putExtra("title", task.title);
			intent.putExtra("priority", task.priority);
			intent.putExtra("deadline", task.dueDate);
			intent.putExtra("notifystart", task.notifyTimeStart);
			intent.putExtra("notifyend", task.notifyTimeEnd);
			intent.putExtra("description", task.description);
			intent.putExtra("eventID", task.taskID);
			intent.putExtra("longitude", task.gpscoordinate.longitude);
			intent.putExtra("latitude", task.gpscoordinate.latitude);
			Log.i(TAG,"GroupID to showTask:"+task.groupId);
			intent.putExtra("groupID",task.groupId);
		}
        startActivityForResult(intent, 0);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 0) {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setMessage(getText(R.string.retrieving_event));
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					Log.i(TAG, "Retrieving data is canceled");
				}
			});
			Log.i(TAG, "created dialog"+id);
			return dialog;
		}
	
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		return dialog;
	}
	
	/**
	 * Called when user returns after login screen
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		showDialog(0);
		accountManager = AccountManager.get(getApplicationContext());
        accounts = accountManager.getAccountsByType(com.thesisug.Constants.ACCOUNT_TYPE);
        username = accounts[0].name;
		// refresh content from server
		downloadEventThread = EventResource.getAllEvent(handler, this);
		downloadTaskThread = TaskResource.getFirstTask(handler, this);
		Log.i(TAG, "onActivityResult create new thread to download");
	}
	
	
	
	public void afterTaskLoaded(List<SingleTask> data){
		tasks = new LinkedList<LinkedHashMap<String,?>>();
		Log.d(TAG,"afterTaskLoaded");
		if (data == null){
			tasks.add(createItem (new SingleTask(getText(R.string.error_connect_task).toString(), "", "", "", "", ""), false));
		}else {	
			if (data.isEmpty()){
				tasks.add(createItem (new SingleTask(getText(R.string.no_task_today).toString(), "", "", "", "", ""), false));	
			}else {
				for (SingleTask o : data){
					tasks.add(createItem(o, usersettings.getBoolean(o.title, true)));
				}
			}
		}
		
		if (dataComplete()){
			combineResult(); //if events is already loaded too
		}
	}
	
	public void afterEventLoaded(List<SingleEvent> data){
		Log.d(TAG,"afterEventLoaded");
		event = new LinkedList<LinkedHashMap<String,?>>();
		if (data == null)  event.add(createItem (new SingleEvent("-1",getText(R.string.error_connect_event).toString(), "", "", "", "", ""), false));
		else if (data.isEmpty()) event.add(createItem (new SingleEvent("-1",getText(R.string.no_event_today).toString(), "", "", "", "", ""), false));
		else {
			Log.d(TAG,"We got "+data.size()+" event!");
			Calendar cal;
			int counter = 0;
            for (SingleEvent o : data){
            	Log.d(TAG,"Event id in afterEventLoaded: "+o.eventID);
				// add to the listview
				event.add(createItem(o, usersettings.getBoolean(o.title, true)));
				try {
					cal = (Calendar) new XsDateTimeFormat().parseObject(o.startTime);
					eventNotificationIntent.putExtra("username", username);
					eventNotificationIntent.putExtra("session", session);
					eventNotificationIntent.putExtra("title", o.title);
					eventNotificationIntent.putExtra("location", o.location);
					eventNotificationIntent.putExtra("startTime", o.startTime);
					eventNotificationIntent.putExtra("endTime", o.endTime);
					eventNotificationIntent.putExtra("priority", o.priority);
					eventNotificationIntent.putExtra("description", o.description);
					Log.d(TAG,"eventNotificationIntent.putExtra(eventID="+o.eventID);
					eventNotificationIntent.putExtra("eventID", o.eventID);
					eventNotificationIntent.putExtra("reminderID", o.reminderID);
					eventNotificationIntent.putExtra("longitude", o.gpscoordinate.longitude);
					eventNotificationIntent.putExtra("latitude", o.gpscoordinate.latitude);
					alarmIntent = PendingIntent.getBroadcast(Todo.this,
			                counter++, eventNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
					if (usersettings.getBoolean(o.title, true))	am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
				} catch (ParseException e) {
					Log.i(TAG, "Error when adding parsing event time to be added to alarm manager");
					e.printStackTrace();
				}
			}
		}
		if (dataComplete()) combineResult(); // if tasks is already downloaded too
	}
	
	private synchronized boolean dataComplete(){
		counter++;
		if (counter >=2 ){
			counter = 0;
			return true;
		} else return false;
	}
	
	public void combineResult(){
		Log.i(TAG, "after data is loaded, dismissed dialog 0");
		dismissDialog(0); //disable the progress dialog
		counter = 0; // reset the counter

		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);		
		

		//Tasks
		SimpleAdapter taskAdapter = new SimpleAdapter(this, tasks, R.layout.todo_task,
		new String[] { ITEM_DATA, REMIND_ME }, new int[] { R.id.list_complex_title, R.id.complex_checkbox });
		
		taskAdapter.setViewBinder(new TaskBinder());			
		adapter.addSection(getText(R.string.task_list_header).toString(), taskAdapter);
		
		//Event
		SimpleAdapter eventAdapter = new SimpleAdapter(this, event, R.layout.todo_event,
				new String[] { ITEM_DATA, ITEM_DATA, REMIND_ME }, new int[] { R.id.list_complex_title, R.id.list_complex_caption, R.id.complex_checkbox });
		adapter.addSection(getText(R.string.event_list_header).toString(), eventAdapter);
		eventAdapter.setViewBinder(new EventBinder());
		
		setListAdapter(adapter);
	}
	


	class EventBinder implements ViewBinder{
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if (view instanceof CheckBox){
				CheckBox temp = (CheckBox) view;
				temp.setChecked((Boolean)data);
				return true;
			} else if (view instanceof TextView) {
				SingleEvent event = (SingleEvent) data;
				TextView temp = (TextView) view;
				if (temp.getId()==R.id.list_complex_title) temp.setText(event.title);
				else if (temp.getId()==R.id.list_complex_caption) temp.setText(event.description);
				return true;
			}
			return false;
		}
	}


	class TaskBinder implements ViewBinder{
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if (view instanceof CheckBox){
				CheckBox temp = (CheckBox) view;
				temp.setChecked((Boolean)data);
				return true;
			} else if (view instanceof TextView) {
				SingleTask task = (SingleTask) data;
				TextView temp = (TextView) view;
				Log.i(TAG, "TaskBinder "+temp.getId());
				temp.setText(task.title);
				Log.i(TAG,"Task groupId:"+task.groupId);
				if(!task.groupId.equals("0")){
					//Group task-> different background color
					temp.setBackgroundColor(Color.rgb( 136, 242, 137));
					temp.setTextColor(Color.BLACK);
				}
				
				return true;
			}
			return false;
		}
	}


	@Override
	public void onInit(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
