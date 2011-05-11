package com.thesisug.ui;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.valueobject.GroupMember;
import com.thesisug.communication.valueobject.SingleItemLocation;




public class ViewAssertions extends ListActivity{
	private static final String TAG ="thesisug - ViewAssertions";
	private static final int CREATE_ASSERTION=0;	
	private static final int UPDATE_ASSERTIONS=1;
	private static final int BACK=2;
	
	private static final int CREATE_GROUP_DIALOG=0;
	
	private static int currentDialog;	
	private static Thread downloadAssertionsThread;
	
	private final static Handler handler = new Handler();
	private List<SingleItemLocation> listitemlocatin;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 	
		//downloadAssertionsThread = AssertionsResource.getAllAssertions(handler, this);
		//Get join to group request list 
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
			/*currentDialog=GET_JOIN_USER_GROUP_REQUEST;
			showDialog(currentDialog);
			downloadJoinGroupRequestThread = GroupResource.getUserJoinGroupRequest(handler, ManageGroupMenu.this);*/
			break;
			

		case UPDATE_ASSERTIONS:
			/*intent=new Intent(ManageGroupMenu.this,InviteToJoinGroup.class);
			startActivityForResult(intent, 0);*/
			break;			
			
		case BACK:
			finish();
			break;
		}
		return true;
	}

	
	public void afterAssertionsListLoaded(final List<SingleItemLocation> itemLocationList){
		//Dismiss dialog
    	//dismissDialog(GET_JOIN_USER_GROUP_REQUEST);   
		
	
    	//Log.i(TAG,"groupInviteList.size():"+groupInviteList.size());
    	
		if(itemLocationList==null || itemLocationList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.noAssertions, Toast.LENGTH_LONG).show();
		}
		/*
		String[] item = new String[20];
		

		Toast.makeText(getApplicationContext(), R.string.noGroupInviteForUser, Toast.LENGTH_LONG).show();
		String[] item = new String[20];
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(item[0].toString())
		    .setCancelable(false)
		  
		    .setPositiveButton("Done", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int id) {
		        dialog.dismiss();
		        }
		    });
		
		final AlertDialog alert = builder.create();
		 alert.show();
		 
		
		int n_list=0;
		                                
		 for (SingleItemLocation o : ItemLocationList){
         	Log.d(TAG,"ItemLocationList in afterAssertionsListLoaded ");
				// add to the listview
	
				item[n_list]= o.item + " -> " + o.location;
				n_list++;
			}
		
		
		for (SingleItemLocation o : itemLocationList){
		
		Toast.makeText(getApplicationContext(),o.item + "->"+o.location , Toast.LENGTH_SHORT).show();
		}
		*/
		
		ListView l1 = (ListView) findViewById(R.id.assertionslist);
		 l1.setAdapter(new assertionsListAdapter(this,itemLocationList));
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Toast.makeText(getBaseContext(), "Details for "+groupMemberList.get(arg2).username, Toast.LENGTH_LONG).show();
				
			}
			 
		 });

		/*
		setListAdapter(new ArrayAdapter<SingleItemLocation>(this, R.layout.view_assertions, itemLocationList));

		  ListView lv = getListView();
		  lv.setTextFilterEnabled(true);

		  lv.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		        int position, long id) {
		      // When clicked, show a toast with the TextView text
		      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
		          Toast.LENGTH_SHORT).show();
		    }
		  });
		
		  
		  
		 ListView l1 = (ListView) findViewById(R.id.assertionslist);
		 l1.setAdapter(new itemLocationRequestListItemLocationAdapter(this,assertionslist));
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getBaseContext(), "Details for "+itemLocationList.get(arg2).groupName, Toast.LENGTH_LONG).show();
				
			}
			 
		 });
		
		}
	
	/private static class itemLocationRequestListItemLocationAdapter extends BaseAdapter {
		 private LayoutInflater mInflater;

		 private List<SingleItemLocation> memberList;
		 
		 public itemLocationRequestListItemLocationAdapter(Context context,List<SingleItemLocation> list) {
			 mInflater = LayoutInflater.from(context);
			 memberList=list;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) {
			 final ViewHolder holder;
			 if (convertView == null) {
				 convertView = mInflater.inflate(R.layout.group_members_list_item, null);
				 holder = new ViewHolder();
				 
				 holder.member=memberList.get(position);
				 
				 holder.txtUsername = (TextView) convertView
				 .findViewById(R.id.txt_member_username);

				 holder.txtUsername.setText(memberList.get(position).username);
				 
				 holder.txtJoinDate = (TextView) convertView
				 .findViewById(R.id.txt_member_joindate);				 
				 
				 holder.txtJoinDate.setText(memberList.get(position).joinDate);
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView txtUsername;
				 TextView txtJoinDate;
	
				 GroupMember member;
			 }

			@Override
			public int getCount() {
				
				return memberList.size();
			}

			@Override
			public Object getItem(int position) {
				return memberList.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(Integer.toString(memberList.get(position).hashCode()));
			}*/


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
				 
				 holder.item = (TextView) convertView.findViewById(R.id.item);

				 holder.item.setText(itemLocationList.get(position).item +"->"+ itemLocationList.get(position).location );
				 
				 
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView item;
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
