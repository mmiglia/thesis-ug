package com.thesisug.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
import com.thesisug.communication.GroupResource;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.Reminder.GPSLocation;
import com.thesisug.communication.valueobject.GroupInviteData;
import com.thesisug.communication.valueobject.Reminder;
import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.valueobject.SingleItemLocation;
import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.communication.xmlparser.XsDateTimeFormat;


public class ViewAssertions extends Activity{
	private static final String TAG ="thesisug - ViewAssertions";
	private static final int CREATE_ASSERTION=0;	
	private static final int UPDATE_ASSERTIONS=1;
	private static final int BACK=2;
	
	private static final int CREATE_GROUP_DIALOG=0;
	
	private static int currentDialog;	
	private static Thread downloadAssertionsThread;
	
	private final static Handler handler = new Handler();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_assertions);
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

	
	public void afterAssertionsListLoaded(List<SingleItemLocation> ItemLocationList){
		//Dismiss dialog
    	//dismissDialog(GET_JOIN_USER_GROUP_REQUEST);   
		
	
    	//Log.i(TAG,"groupInviteList.size():"+groupInviteList.size());
    	
		if(ItemLocationList==null || ItemLocationList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.noGroupInviteForUser, Toast.LENGTH_LONG).show();
		}
		
		 ListView l1 = (ListView) findViewById(R.id.joinToGroupRequestList);
		 l1.setAdapter(new GroupRequestListItemAdapter(this,ItemLocationList));
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				//Toast.makeText(getBaseContext(), "Details for "+groupInviteList.get(arg2).groupName, Toast.LENGTH_LONG).show();
				
			}
			 
		 });
}
	
	
	private static class GroupRequestListItemAdapter extends BaseAdapter {
		 private LayoutInflater mInflater;

		 private List<SingleItemLocation> inviteList;
		 
		 public GroupRequestListItemAdapter(Context context,List<SingleItemLocation> list) {
			 mInflater = LayoutInflater.from(context);
			 inviteList=list;

		 }

		public View getView(int position, View convertView, ViewGroup parent) {/* 
			 final ViewHolder holder;
			 if (convertView == null) {
				 convertView = mInflater.inflate(R.layout.join_to_group_list_item, null);
				 holder = new ViewHolder();
				 
				 holder.invite=inviteList.get(position);
				 
				 holder.txtSender = (TextView) convertView
				 .findViewById(R.id.group_invite_sender);

				 holder.txtSender.setText(inviteList.get(position).sender);
				 
				 holder.txtMessage = (TextView) convertView
				 .findViewById(R.id.group_invite_message);				 
				 
				 holder.txtMessage.setText(inviteList.get(position).message);
				 
				 holder.txtGroupName = (TextView) convertView
				 .findViewById(R.id.group_invite_group_name);
				 
				 holder.txtGroupName.setText(inviteList.get(position).groupName);
				 
				 
				 holder.btnAccept=(Button)convertView.findViewById(R.id.btn_groupInviteAccept);
				 
				 holder.btnAccept.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						Toast.makeText(arg0.getContext(), "Send accept for "+holder.txtSender.getText().toString()+"invite", Toast.LENGTH_LONG).show();
						GroupResource.acceptJoinToGroupRequest(holder.invite, handler, arg0.getContext());
					}
					 
				 });

				 
				 holder.btnRefuse=(Button)convertView.findViewById(R.id.btn_groupInviteRefuse);
				 
				 holder.btnRefuse.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View arg0) {
							Toast.makeText(arg0.getContext(), "Send refuse for "+holder.txtGroupName.getText().toString()+" invite", Toast.LENGTH_LONG).show();
							GroupResource.refuseJoinToGroupRequest(holder.invite, handler, arg0.getContext());
						}
						 
					 });
				 convertView.setTag(holder);
			 } else {
				 holder = (ViewHolder) convertView.getTag();
			 }
			*/
			 return convertView;
		 }

			 static class ViewHolder {
				 TextView txtSender;
				 TextView txtMessage;
				 TextView txtGroupName;
				 
				 Button btnAccept;
				 Button btnRefuse;
				 
				 GroupInviteData invite;
			 }

			@Override
			public int getCount() {
				
				return inviteList.size();
			}

			@Override
			public Object getItem(int position) {
				return inviteList.get(position);
			}

			@Override
			public long getItemId(int position) {
				
				return Long.parseLong(inviteList.get(position).item);
			}
		 }
}
