package com.thesisug.ui;

import java.util.LinkedHashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.valueobject.SingleItemLocation;
import com.thesisug.ui.Details_assertion_item;




public class ViewAssertions extends Activity{
	private static final String TAG ="thesisug - ViewAssertions";
	private static final int CREATE_ASSERTION=0;	
	private static final int UPDATE_ASSERTIONS=1;
	private static final int BACK=2;
	public final static String ITEM_DATA = "data";
	
	public Intent intent;
	private static final int CREATE_GROUP_DIALOG=0;
	
	private static int currentDialog;	
	private static Thread downloadAssertionsThread;
	private assertionsListAdapter adapter;
	private final static Handler handler = new Handler();
	private List<SingleItemLocation> listitemlocatin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_assertions); 	
		//downloadAssertionsThread = AssertionsResource.getAllAssertions(handler, this);
		//Get join to group request list 
		downloadAssertionsThread = AssertionsResource.getAssertions(handler, ViewAssertions.this);
	}
	
	  protected void onResume() {
	        super.onResume();
	        // The activity has become visible (it is now "resumed").
	        
	        downloadAssertionsThread = AssertionsResource.getAssertions(handler, ViewAssertions.this);
	        
	    }

	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,CREATE_ASSERTION,0,R.string.createAssertion).setIcon(R.drawable.user_group_add);
		menu.add(0,UPDATE_ASSERTIONS,0,R.string.updateAssertions).setIcon(R.drawable.sync);	
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case CREATE_ASSERTION:
			
			intent = new Intent(getApplicationContext(), Create_Assertion_item.class);
			startActivityForResult(intent,0);
			finish();
			break;
			

		case UPDATE_ASSERTIONS:
			downloadAssertionsThread = AssertionsResource.getAssertions(handler, ViewAssertions.this);
			break;			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	


	
	public void afterAssertionsListLoaded(final List<SingleItemLocation> itemLocationList){
	
    	
		if(itemLocationList==null || itemLocationList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.noAssertions, Toast.LENGTH_LONG).show();
		}

		
		ListView l1 = (ListView) findViewById(R.id.assertionslist);
		 
		 adapter = new assertionsListAdapter(this,itemLocationList);
		
		l1.setAdapter(adapter);
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				
					//LinkedHashMap item = (LinkedHashMap) (arg0.getItemAtPosition(arg2));
					//SingleItemLocation itemLocation = (SingleItemLocation) item.get(ITEM_DATA);
					//Toast.makeText(getBaseContext(), "Details for "+itemLocationList.get(arg2).item, Toast.LENGTH_LONG).show();
					
					intent = new Intent(getApplicationContext(), Details_assertion_item.class);
					intent.putExtra("item", itemLocationList.get(arg2).item);
					intent.putExtra("location", itemLocationList.get(arg2).location);
					intent.putExtra("username", itemLocationList.get(arg2).username);
					intent.putExtra("n_views", itemLocationList.get(arg2).n_views);
					intent.putExtra("n_votes", itemLocationList.get(arg2).n_votes);
					intent.putExtra("vote", itemLocationList.get(arg2).vote);
					intent.putExtra("description", itemLocationList.get(arg2).item + " si può trovare in " + itemLocationList.get(arg2).location); 
					Log.i(TAG,"details_assertion_item:"+ itemLocationList.get(arg2).item + "->" + itemLocationList.get(arg2).location);	
					startActivityForResult(intent, 0);
			
			}
			 
		 });
		 
		 
		
		 }
	
	private static class assertionsListAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;

		 private List<SingleItemLocation> itemLocationList;
		 
		 public assertionsListAdapter(Context context,List<SingleItemLocation> list) {
			 mInflater = LayoutInflater.from(context);
			 itemLocationList=list;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
		
			 final ViewHolder holder;
			 if (convertView == null) {
				 
				 convertView = mInflater.inflate(R.layout.assertionslist_item, null);
				 holder = new ViewHolder();
				 
				 
				 holder.itemlocation = itemLocationList.get(position);
				 
				 holder.txt_item = (TextView) convertView.findViewById(R.id.txt_item);

				 holder.txt_item.setText("'" +  itemLocationList.get(position).item +"'");
				 
				 holder.txt_location = (TextView) convertView.findViewById(R.id.txt_location);
				 
				 holder.txt_location.setText(itemLocationList.get(position).location);
				 
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView txt_item;
				 TextView txt_location;
				 SingleItemLocation itemlocation;
			 }

			@Override
			public int getCount() {
				
				return itemLocationList.size();
			}

			@Override
			public Object getItem(int position) {
				return itemLocationList.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(Integer.toString(itemLocationList.get(position).hashCode()));
			}


		 }
	
	
}
