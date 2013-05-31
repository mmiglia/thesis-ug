package com.thesisug.ui;

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
import com.thesisug.communication.valueobject.SingleActionLocation;

public class ViewAssertions_Action extends Activity{
	
	private static final String TAG ="thesisug - ViewAssertions_Action";
	private static final int CREATE_ASSERTION=0;	
	private static final int UPDATE_ASSERTIONS=1;
	private static final int BACK=2;
	public final static String ITEM_DATA = "data";
	
	public Intent intent;
	private static final int CREATE_GROUP_DIALOG=0;
	
	private static int currentDialog;	
	private static Thread downloadAssertionsThread;
	
	private final static Handler handler = new Handler();
	private List<SingleActionLocation> listActionlocatin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_assertions_action);
		 	
		//downloadAssertionsThread = AssertionsResource.getAllAssertions(handler, this);
		//Get join to group request list 
		downloadAssertionsThread = AssertionsResource.getAssertions_action(handler, ViewAssertions_Action.this);
	}
	 protected void onResume() {
	        super.onResume();
	        // The activity has become visible (it is now "resumed").
	        downloadAssertionsThread = AssertionsResource.getAssertions_action(handler, ViewAssertions_Action.this);
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
			
			intent = new Intent(getApplicationContext(), Create_Assertion_action.class);
			startActivityForResult(intent,0);
			break;
			

		case UPDATE_ASSERTIONS:
			downloadAssertionsThread = AssertionsResource.getAssertions_action(handler, ViewAssertions_Action.this);
			break;			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	


	
	public void afterAssertionsListLoaded(final List<SingleActionLocation> actionLocationList){
	
    	
		if(actionLocationList==null || actionLocationList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.noAssertions, Toast.LENGTH_SHORT).show();
		}

		
		ListView l1 = (ListView) findViewById(R.id.assertionslist_1);
		 l1.setAdapter(new assertionsListAdapter(this,actionLocationList));
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				
					//LinkedHashMap item = (LinkedHashMap) (arg0.getItemAtPosition(arg2));
					//SingleItemLocation itemLocation = (SingleItemLocation) item.get(ITEM_DATA);
					//Toast.makeText(getBaseContext(), "Details for "+itemLocationList.get(arg2).item, Toast.LENGTH_LONG).show();
					
					intent = new Intent(getApplicationContext(), Details_assertion_action.class);
					intent.putExtra("action", actionLocationList.get(arg2).action);
					intent.putExtra("location", actionLocationList.get(arg2).location);
					intent.putExtra("username", actionLocationList.get(arg2).username);
					intent.putExtra("n_views", actionLocationList.get(arg2).n_views);
					intent.putExtra("n_votes", actionLocationList.get(arg2).n_votes);
					intent.putExtra("vote", actionLocationList.get(arg2).vote);
					intent.putExtra("description", actionLocationList.get(arg2).action + " si può trovare in " + actionLocationList.get(arg2).location); 
					Log.i(TAG,"details_assertion_action:"+ actionLocationList.get(arg2).action + "->" + actionLocationList.get(arg2).location);	
					startActivityForResult(intent, 0);
			
			}
			 
		 });
		 }
	
	private static class assertionsListAdapter extends BaseAdapter {
		 
		private LayoutInflater mInflater;

		 private List<SingleActionLocation> actionLocationList;
		 
		 public assertionsListAdapter(Context context,List<SingleActionLocation> list) {
			 mInflater = LayoutInflater.from(context);
			 actionLocationList=list;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) 
		 {
		
			 final ViewHolder holder;
			 if (convertView == null) {
				 
				 convertView = mInflater.inflate(R.layout.assertionslist_action, null);
				 holder = new ViewHolder();
				 
				 
				 holder.actionlocation = actionLocationList.get(position);
				 
				 holder.txt_action = (TextView) convertView.findViewById(R.id.txt_action);

				 holder.txt_action.setText("'" +  actionLocationList.get(position).action +"'");
				 
				 holder.txt_location1 = (TextView) convertView.findViewById(R.id.txt_location1);
				 
				 holder.txt_location1.setText(actionLocationList.get(position).location);
				 
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			 
			 holder.txt_action.setText("'" +  actionLocationList.get(position).action +"'");

			 holder.txt_location1.setText(actionLocationList.get(position).location);
			
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView txt_action;
				 TextView txt_location1;
				 SingleActionLocation actionlocation;
			 }

			@Override
			public int getCount() {
				
				return actionLocationList.size();
			}

			@Override
			public Object getItem(int position) {
				return actionLocationList.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(Integer.toString(actionLocationList.get(position).hashCode()));
			}


		 }
	
	
}


