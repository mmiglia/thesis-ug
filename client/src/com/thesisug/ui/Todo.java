package com.thesisug.ui;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

import com.thesisug.R;
import com.thesisug.communication.EventResource;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.Reminder;
import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.valueobject.SingleTask;

public class Todo extends ListActivity {
	public final static String TAG = "TodoActivity";
	public final static String ITEM_DATA = "data";
	public final static int CREATE_EVENT = 1;
	public final static int CREATE_TASK = 2;
	public final static int VOICE_INPUT = 3;
	public final static int BACK = 4;
	
	private Thread downloadEventThread, downloadTaskThread;
	private static int counter = 0; // counter for task and event thread completion
	private static String username, session;
	private AccountManager accountManager;
	private Account[] accounts;
	private final Handler handler = new Handler();
	private static List<LinkedHashMap<String,?>> event = new LinkedList<LinkedHashMap<String,?>>();
	private static List<LinkedHashMap<String,?>> tasks = new LinkedList<LinkedHashMap<String,?>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(getApplicationContext());
        accounts = accountManager.getAccountsByType(com.thesisug.Constants.ACCOUNT_TYPE);
        if (accounts.length == 0) {
        	Intent login = new Intent(getApplicationContext(),Login.class);
        	startActivityForResult(login, 0);
        } else {
        	SeparatedListAdapter adapter = new SeparatedListAdapter(this);
        	setListAdapter(adapter);
        	showDialog(0);
        	username = accounts[0].name;
    		downloadEventThread = EventResource.getAllEvent(handler, this);
    		downloadTaskThread = TaskResource.getAllTask(handler, this);
        }
	}
	
	private LinkedHashMap<String,?> createItem(Reminder reminder) {
		LinkedHashMap<String,Reminder> item = new LinkedHashMap<String,Reminder>();
		item.put(ITEM_DATA, reminder);
		return item;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,CREATE_EVENT,0,"Create Event").setIcon(R.drawable.edit);
		menu.add(0,CREATE_TASK,0,"Create Task").setIcon(R.drawable.edit);
		menu.add(0,VOICE_INPUT,0,"Voice Input").setIcon(R.drawable.edit);
		menu.add(0,BACK,0,"Back").setIcon(R.drawable.exit);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CREATE_EVENT:
			// need to recreate intent to solve the case when user 
			// goes back and forth between edit-show
			Intent intent = new Intent(("com.thesisug.EDIT_EVENT"));
			intent.putExtra("originator", 1);// CREATE_EVENT code in EditEvent
			startActivityForResult(intent, 0);
			break;			
		case CREATE_TASK:
			break;
		case VOICE_INPUT:
			break;
		default:
			finish();
			break;
		}
		return true;
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		LinkedHashMap<String,Reminder> item = (LinkedHashMap<String, Reminder>) (l.getItemAtPosition(position));
		Intent intent;
		if (item.get(ITEM_DATA) instanceof SingleEvent){
			SingleEvent ev = (SingleEvent) item.get(ITEM_DATA);
			// return if there are no event today
			if (ev.title == getText(R.string.no_event_today).toString()) return;
			intent = new Intent(Todo.this, ShowEvent.class);
			intent.putExtra("username", username);
			intent.putExtra("session", session);
			intent.putExtra("title", ev.title);
			intent.putExtra("location", ev.location);
			intent.putExtra("startTime", ev.startTime);
			intent.putExtra("endTime", ev.endTime);
			intent.putExtra("priority", ev.priority);
			intent.putExtra("description", ev.description);
			intent.putExtra("id", ev.ID);
			intent.putExtra("longitude", ev.gpscoordinate.longitude);
			intent.putExtra("latitude", ev.gpscoordinate.latitude);
		}
		else {
			SingleTask task = (SingleTask) item.get(ITEM_DATA);
			if (task.title == getText(R.string.no_task_today).toString()) return;
			intent = new Intent(Todo.this, ShowTask.class);
			intent.putExtra("username", username);
			intent.putExtra("session", session);
			intent.putExtra("title", task.title);
			intent.putExtra("priority", task.priority);
			intent.putExtra("description", task.description);
			intent.putExtra("id", task.ID);
			intent.putExtra("longitude", task.gpscoordinate.longitude);
			intent.putExtra("latitude", task.gpscoordinate.latitude);
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
		downloadTaskThread = TaskResource.getAllTask(handler, this);
		Log.i(TAG, "onActivityResult create new thread to download");
	}
	
	public void afterTaskLoaded(List<SingleTask> data){
		tasks = new LinkedList<LinkedHashMap<String,?>>();
		if (data.isEmpty()) tasks.add(createItem (new SingleTask(getText(R.string.no_task_today).toString(), "", "", "", "")));
		else {
			for (SingleTask o : data){
				tasks.add(createItem(o));
			}
		}
		if (dataComplete()) combineResult();
	}
	
	public void afterEventLoaded(List<SingleEvent> data){
		event = new LinkedList<LinkedHashMap<String,?>>();
		if (data.isEmpty()) event.add(createItem (new SingleEvent(getText(R.string.no_event_today).toString(), "", "", "", "")));
		else {
			for (SingleEvent o : data){
				event.add(createItem(o));
			}
		}
		if (dataComplete()) combineResult();
	}
	
	private synchronized boolean dataComplete(){
		counter++;
		return (counter >= 2) ? true:false;
	}
	
	public void combineResult(){
		Log.i(TAG, "after data is loaded, dismissed dialog 0");
		dismissDialog(0); //disable the progress dialog
		counter = 0; // reset the counter

		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);		
		SimpleAdapter taskAdapter = new SimpleAdapter(this, tasks, R.layout.todo_task,
		new String[] { ITEM_DATA }, new int[] { R.id.list_simple_title });
		taskAdapter.setViewBinder(new TaskBinder());
		adapter.addSection(getText(R.string.task_list_header).toString(), taskAdapter);
		SimpleAdapter eventAdapter = new SimpleAdapter(this, event, R.layout.todo_event,
				new String[] { ITEM_DATA, ITEM_DATA }, new int[] { R.id.list_complex_title, R.id.list_complex_caption });
		adapter.addSection(getText(R.string.event_list_header).toString(), eventAdapter);
		eventAdapter.setViewBinder(new EventBinder());
		
		setListAdapter(adapter);
	}
	

	class EventBinder implements ViewBinder{
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if (view instanceof TextView) {
				TextView temp = (TextView) view;
				SingleEvent event = (SingleEvent) data;
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
			if (view instanceof TextView) {
				TextView temp = (TextView) view;
				SingleTask task = (SingleTask) data;
				Log.i(TAG, "TaskBinder "+temp.getId());
				temp.setText(task.title);
				return true;
            } 
			return false;
		}
	}
}
