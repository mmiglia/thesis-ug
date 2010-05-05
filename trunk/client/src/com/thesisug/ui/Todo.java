package com.thesisug.ui;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thesisug.R;

public class Todo extends Activity {
	public final static String TAG = "TodoActivity";
	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";
	private Thread downloadThread;
	
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
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		List<LinkedHashMap<String,?>> tasks = new LinkedList<LinkedHashMap<String,?>>();
		tasks.add(createItem("jogging"));
		tasks.add(createItem("buy ticket"));
		tasks.add(createItem("study"));

		List<LinkedHashMap<String,?>> event = new LinkedList<LinkedHashMap<String,?>>();
		event.add(createItem("Meeting with supervisor", "8:30AM @ lab"));
		event.add(createItem("Ambient Intelligence", "14:15PM @ E5"));
		event.add(createItem("Party", "23:00 @ Banana Tsunami"));
		
		// create our list and custom adapter
		SeparatedListAdapter adapter = new SeparatedListAdapter(this);
		
		adapter.addSection("Gotta do these dude !!", new SimpleAdapter(this, tasks, R.layout.list_item,
				new String[] { ITEM_TITLE }, new int[] { R.id.list_simple_title }));
		adapter.addSection("Today, your appointment are", new SimpleAdapter(this, event, R.layout.list_complex,
				new String[] { ITEM_TITLE, ITEM_CAPTION }, new int[] { R.id.list_complex_title, R.id.list_complex_caption }));
			
		
		ListView list = new ListView(this);
		list.setAdapter(adapter);
		this.setContentView(list);

	}
//
//	@Override
//    protected Dialog onCreateDialog(int id) {
//        final ProgressDialog dialog = new ProgressDialog(this);
//        dialog.setMessage(getText(R.string.retrieving_event));
//        dialog.setIndeterminate(true);
//        dialog.setCancelable(true);
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            public void onCancel(DialogInterface dialog) {
//                Log.i(TAG, "Cancel has been invoked");
//                if (downloadThread != null) {
//                    downloadThread.interrupt();
//                    finish();
//                }
//            }
//        });
//        return dialog;
//    }
}
