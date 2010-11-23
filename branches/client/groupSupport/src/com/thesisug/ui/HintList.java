package com.thesisug.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thesisug.R;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.valueobject.Hint.PhoneNumber;

public class HintList extends ListActivity {
	public final static String TAG = "HintListActivity";
	public final static String HINT_TITLE = "data";
	public final static String HINT_ADDRESS = "address";
	public final static String HINT_PHONE = "phone";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		List<LinkedHashMap<String,?>> hints = new LinkedList<LinkedHashMap<String,?>>();
		ArrayList<Hint> hintlist = getIntent().getExtras().getParcelableArrayList("hints");
       	// create our list and custom adapter
       	SeparatedListAdapter adapter = new SeparatedListAdapter(this);		
       	if (hintlist.isEmpty()) {
       		Log.i(TAG, "Cannot find any Hint object in the intent");
       		setListAdapter(adapter);
       		return;
       	} else {
       		for (Hint o : hintlist) hints.add(createItem(o));
       	SimpleAdapter hintAdapter = new SimpleAdapter(this, hints, R.layout.hint_list,
       			new String[] { HINT_TITLE, HINT_ADDRESS, HINT_PHONE }, new int[] { R.id.hint_complex_title, R.id.hint_complex_caption, R.id.hint_phone_caption });
       	adapter.addSection(getText(R.string.capable)+" "+getIntent().getExtras().getString("tasktitle")+" in", hintAdapter);
       	setListAdapter(adapter);
       	}
	}
	
	private LinkedHashMap<String,?> createItem(Hint currenthint) {
		LinkedHashMap<String,String> item = new LinkedHashMap<String,String>();
		item.put(HINT_TITLE, currenthint.titleNoFormatting);
		item.put(HINT_ADDRESS, currenthint.streetAddress);
		if (currenthint.phoneNumbers.size()>0){
			String phones = "";
			for (PhoneNumber o : currenthint.phoneNumbers){
				phones += o.number.replaceAll(" ", "")+" ,";
			}
			item.put(HINT_PHONE, phones.substring(0, phones.length()-2));
		}
		else item.put(HINT_PHONE, "");
		return item;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "item "+position+" is clicked");
	}
}
