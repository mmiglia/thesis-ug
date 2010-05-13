package com.thesisug.ui;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
	private final Handler handler = new Handler();
	private final List<LinkedHashMap<String,?>> event = new LinkedList<LinkedHashMap<String,?>>();
	private final List<LinkedHashMap<String,?>> tasks = new LinkedList<LinkedHashMap<String,?>>();
	
	private LinkedHashMap<String,?> createItem(Reminder reminder) {
		LinkedHashMap<String,Reminder> item = new LinkedHashMap<String,Reminder>();
		item.put(ITEM_DATA, reminder);
		return item;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showDialog(0);
		downloadThread = EventResource.getAllEvent("user", "", handler, this);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "hallo");
		Log.i(TAG, "position " + Integer.toString(position));
		Log.i(TAG, "id " + Long.toString(id));
		LinkedHashMap<String,Reminder> item = (LinkedHashMap<String, Reminder>) (l.getItemAtPosition(position));
		SingleEvent ev = (SingleEvent) item.get(ITEM_DATA);
		Log.i(TAG,"ev "+ ev.title);
		Intent intent = new Intent("com.thesisug.SHOW_ACTIVITY");
		intent.putExtra("event_title", ev.title);
        startActivity(intent);
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
		if (id == 1) {
			final ProgressDialog dialog = new ProgressDialog(this);
			dialog.setCancelable(true);
			return dialog;
		}
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setCancelable(true);
		return dialog;
	}
	
	public void afterDataLoaded(List<SingleEvent> data){
		dismissDialog(0); //disable the progress dialog
		for (SingleEvent o : data){
			event.add(createItem(o));
		}

		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		
//		SimpleAdapter taskAdapter = new SimpleAdapter(this, tasks, R.layout.list_item,
//				new String[] { ITEM_DATA }, new int[] { R.id.list_simple_title });
//		adapter.addSection("Gotta do these dude !!", taskAdapter);
		
		SimpleAdapter eventAdapter = new SimpleAdapter(this, event, R.layout.list_complex,
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
