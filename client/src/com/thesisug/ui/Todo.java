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

import com.thesis.communication.valueobject.SingleEvent;
import com.thesisug.R;
import com.thesisug.communication.EventResource;

public class Todo extends ListActivity {
	public final static String TAG = "TodoActivity";
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";
	private Thread downloadThread;
	private final Handler handler = new Handler();
	private final List<LinkedHashMap<String,?>> event = new LinkedList<LinkedHashMap<String,?>>();
	private final List<LinkedHashMap<String,?>> tasks = new LinkedList<LinkedHashMap<String,?>>();
	
	private LinkedHashMap<String,?> createItem(String title, String caption) {
		LinkedHashMap<String,String> item = new LinkedHashMap<String,String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}
	
	private LinkedHashMap<String,?> createItem(String title) {
		LinkedHashMap<String,String> item = new LinkedHashMap<String,String>();
		item.put(ITEM_TITLE, title);
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
		Intent intent = new Intent("com.thesisug.SHOW_ACTIVITY");
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
			tasks.add(createItem(o.title));
		}
		
		event.add(createItem("Meeting with supervisor", "8:30AM @ lab"));
		event.add(createItem("Ambient Intelligence", "14:15PM @ E5"));
		event.add(createItem("Party", "23:00PM @ Banana Tsunami"));
		
		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		
		adapter.addSection("Gotta do these dude !!", new SimpleAdapter(this, tasks, R.layout.list_item,
				new String[] { ITEM_TITLE }, new int[] { R.id.list_simple_title }));
		adapter.addSection("Today, your appointments are", new SimpleAdapter(this, event, R.layout.list_complex,
				new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));
			
		
		setListAdapter(adapter);

	}
}
