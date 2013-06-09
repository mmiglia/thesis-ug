package com.thesisug.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.valueobject.Hint.PhoneNumber;
import com.thesisug.notification.TaskNotification;
import com.thesisug.tracking.ActionTracker;

public class HintList extends ListActivity 
{
	public final static String TAG = "thesisug - HintListActivity";
	public final static String HINT_TITLE = "data";
	public final static String HINT_ADDRESS = "address";
	public final static String HINT_PHONE = "phone";
	private static ArrayList<Hint> hintlist;
	private static String tasktitle="";
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		List<LinkedHashMap<String,?>> hints = new LinkedList<LinkedHashMap<String,?>>();
		hintlist = getIntent().getExtras().getParcelableArrayList("hints");
		tasktitle= getIntent().getExtras().getString("tasktitle");
		
       	// create our list and custom adapter
       	SeparatedListAdapter adapter = new SeparatedListAdapter(this);		
       	if (hintlist==null||hintlist.isEmpty()) {
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
       	ActionTracker.notificationViewed(Calendar.getInstance().getTime(), tasktitle, getApplicationContext(), 1);
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
		//Position is 1-based, hintList is 0-based
		if(hintlist==null){
			Toast.makeText(getApplicationContext(), R.string.problem_loading_hint_map,Toast.LENGTH_SHORT).show();
		}
		Hint selectedHint=hintlist.get(position-1);
		Log.i(TAG, "item "+position+" is clicked");
		Log.i(TAG, "item title:"+selectedHint.title);
		Log.i(TAG, "item ddUrl:"+selectedHint.ddUrl);
		Log.i(TAG, "item ddUrlToHere:"+selectedHint.ddUrlToHere);
		Log.i(TAG, "item ddUrlFromHere:"+selectedHint.ddUrlFromHere);
		Log.i(TAG, "item staticMapUrl:"+selectedHint.staticMapUrl);
		
		//Show only this in mapView
		Intent intentShowInMap = new Intent(HintList.this, ParentTab.class);
		intentShowInMap.putExtra("maptab",1);
		intentShowInMap.putExtra("hintlist",hintlist);
		intentShowInMap.putExtra("selectedPos",position);
		intentShowInMap.putExtra("tasktitle",tasktitle);
		intentShowInMap.putExtra("time", Calendar.getInstance().getTime().toString());
		intentShowInMap.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//startActivityForResult(intentShowInMap, 0);
		startActivity(intentShowInMap);
		ArrayList<String> unchosen = new ArrayList<String>(); 
		for(Hint h: hintlist)
		{
			if(!h.titleNoFormatting.equals(selectedHint.titleNoFormatting))
				unchosen.add(h.titleNoFormatting);
		}
		ActionTracker.hintChosen(Calendar.getInstance().getTime(),TaskNotification.getInstance().getLastKnownLocation(), tasktitle, selectedHint.titleNoFormatting,unchosen, getApplicationContext());
		
		super.finish();
		finish();
		//View path in browser
		//Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(selectedHint.ddUrl));
		//startActivity(browserIntent);

		
	}
	
}
