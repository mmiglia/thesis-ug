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
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

import com.thesisug.R;
import com.thesisug.communication.EventResource;
import com.thesisug.communication.valueobject.Reminder;
import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.valueobject.SingleTask;

public class Todo extends ListActivity {
	public final static String TAG = "TodoActivity";
	
	public final static String ITEM_DATA = "data";
	private Thread downloadThread;
	private String username, session;
	private AccountManager accountManager;
	private Account[] accounts;
	private final Handler handler = new Handler();
	private static List<LinkedHashMap<String,?>> event = new LinkedList<LinkedHashMap<String,?>>();
	private static List<LinkedHashMap<String,?>> tasks = new LinkedList<LinkedHashMap<String,?>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(0);
        accountManager = AccountManager.get(getApplicationContext());
        accounts = accountManager.getAccountsByType(com.thesisug.Constants.ACCOUNT_TYPE);
        if (accounts.length == 0) {
        	dismissDialog(0);
        	Intent login = new Intent(getApplicationContext(),Login.class);
        	startActivityForResult(login, LOGIN_SCREEN);
        } else {
        	session = accountManager.blockingGetAuthToken(accounts[0],com.thesisug.Constants.ACCOUNT_TYPE, true);
        	username = accounts[0].name;
    		downloadThread = EventResource.getAllEvent(username, session, handler, this);
        }
	}
	
	private LinkedHashMap<String,?> createItem(Reminder reminder) {
		LinkedHashMap<String,Reminder> item = new LinkedHashMap<String,Reminder>();
		item.put(ITEM_DATA, reminder);
		return item;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "hallo");
		Log.i(TAG, "position " + Integer.toString(position));
		Log.i(TAG, "id " + Long.toString(id));
		LinkedHashMap<String,Reminder> item = (LinkedHashMap<String, Reminder>) (l.getItemAtPosition(position));
		Intent intent;
		if (item.get(ITEM_DATA) instanceof SingleEvent){
			SingleEvent ev = (SingleEvent) item.get(ITEM_DATA);
			intent = new Intent("com.thesisug.SHOW_EVENT");
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
			intent = new Intent("com.thesisug.SHOW_ACTIVITY");
			//title = ((SingleTask) item.get(ITEM_DATA)).title;
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
			return dialog;
		}
	
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		return dialog;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		showDialog(0);
		event = new LinkedList<LinkedHashMap<String,?>>();
		tasks = new LinkedList<LinkedHashMap<String,?>>();
		// refresh content from server
		downloadThread = EventResource.getAllEvent(username, session, handler, this);
	}
	
	public void afterDataLoaded(List<SingleEvent> data){
		dismissDialog(0); //disable the progress dialog
		for (SingleEvent o : data){
			event.add(createItem(o));
		}
		tasks.add(createItem(new SingleTask("buy milk", "2309", "around Genoa")));
		tasks.add(createItem(new SingleTask("buy train ticket", "2309", "Principe FS")));
		
		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		
		SimpleAdapter taskAdapter = new SimpleAdapter(this, tasks, R.layout.todo_task,
		new String[] { ITEM_DATA }, new int[] { R.id.list_simple_title });
		taskAdapter.setViewBinder(new TaskBinder());
		adapter.addSection("Gotta do these dude !!", taskAdapter);
		
		SimpleAdapter eventAdapter = new SimpleAdapter(this, event, R.layout.todo_event,
				new String[] { ITEM_DATA, ITEM_DATA }, new int[] { R.id.list_complex_title, R.id.list_complex_caption });
		adapter.addSection("Today, your appointments are", eventAdapter);
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
