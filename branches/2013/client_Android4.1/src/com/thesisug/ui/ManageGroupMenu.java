package com.thesisug.ui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.GroupResource;
import com.thesisug.communication.valueobject.GroupData;
import com.thesisug.communication.valueobject.GroupInviteData;

/*
 * This activity is used to manage group and join_to_group request.
 * It also give the possibility, with the menu that appears after the user press the menu-button, to
 * create groups and invite user to join them (see the InviteToJoinGroup activity)
 */
public class ManageGroupMenu extends Activity {
	private static final String TAG = "thesisug - ManageGroupMenu";
	private static final int CREATE_GROUP_MENU=0;
	private static final int GET_USER_GROUP = 1;
	private static final int INVITE_JOIN_GROUP = 2;
	private static final int GET_JOIN_USER_GROUP_REQUEST=3;
	private static final int ACCEPT_JOIN_USER_GROUP_REQUEST=4;
	private static final int UPDATE_JOIN_TO_GROUP_REQUEST=5;
	private static final int BACK=6;
	private static final int VIEW_GROUP_MEMBER=7;
	
	private static final int CREATE_GROUP_DIALOG=0;
	
	private static int currentDialog;	
	private static Thread downloadJoinGroupRequestThread;
	
	private final static Handler handler = new Handler();
	
	private ProgressDialog createDialog;
	
	private Button btnUpdateJoinToGroupRequest;
	

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_group_menu);
		//Get join to group request list 
		currentDialog=GET_JOIN_USER_GROUP_REQUEST;
		showDialog(currentDialog);
		downloadJoinGroupRequestThread = GroupResource.getUserJoinGroupRequest(handler, ManageGroupMenu.this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0,UPDATE_JOIN_TO_GROUP_REQUEST,0,R.string.updateJoinRequest).setIcon(R.drawable.sync).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0,CREATE_GROUP_MENU,0,R.string.createGroup).setIcon(R.drawable.user_group).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0,INVITE_JOIN_GROUP,0,R.string.inviteToGroup).setIcon(R.drawable.user_group_add).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		menu.add(0,VIEW_GROUP_MEMBER,0,R.string.view_group_member).setIcon(R.drawable.user_group).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);
			
		menu.add(0,BACK,0,R.string.back).setIcon(R.drawable.go_previous_black);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case UPDATE_JOIN_TO_GROUP_REQUEST:
			currentDialog=GET_JOIN_USER_GROUP_REQUEST;
			showDialog(currentDialog);
			downloadJoinGroupRequestThread = GroupResource.getUserJoinGroupRequest(handler, ManageGroupMenu.this);
			break;
			
		case CREATE_GROUP_MENU:
			AlertDialog.Builder alertGroupName=new AlertDialog.Builder(this);
			alertGroupName.setTitle(R.string.group_name);
			alertGroupName.setMessage(R.string.insert_group_name);

			// Set an EditText view to get user input   
		    final EditText input = new EditText(this);  
		    alertGroupName.setView(input);  
			      
		    alertGroupName.setPositiveButton("Ok", new DialogInterface.OnClickListener() {  
			   public void onClick(DialogInterface dialog, int whichButton) {  
				   String groupName = input.getText().toString(); 
					
					if (!groupNameIsValid(groupName)) {
						// comunica che i titoli vuoti non sono gestiti
							Toast.makeText(getApplicationContext(), R.string.bad_group_name, Toast.LENGTH_SHORT).show();
						// esce (senza inviare nulla)
						return;
					}
					Log.d(TAG, "Creating the group with name"+groupName);
					currentDialog=CREATE_GROUP_DIALOG;
					showDialog(currentDialog);
					GroupData newGroup=new GroupData("-1",groupName,"");
					Thread creatingThread=GroupResource.createGroup(newGroup, handler, ManageGroupMenu.this);

			     }  
			   });  
			     
			   alertGroupName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {  
			     public void onClick(DialogInterface dialog, int whichButton) {  
			    	 
			     }  
			   });  
			     
			   alertGroupName.show(); 
			break;

		case INVITE_JOIN_GROUP:
			intent=new Intent(ManageGroupMenu.this,InviteToJoinGroup.class);
			startActivityForResult(intent, 0);
			break;			
			
		case VIEW_GROUP_MEMBER:
			intent=new Intent(ManageGroupMenu.this,ViewGroupMembers.class);
			startActivityForResult(intent, 0);
			break;
			
		case BACK:
			finish();
			break;
		}
		return true;
	}
	
	private boolean groupNameIsValid(String groupName) {
		// c'� un'occorrenza di .*\\S.* in una stringa se c'� almeno
		// un carattere non white-space. Per Java i caratteri white-space sono
		//  \t,\n,\x0B,\f e \r.
		return groupName.matches(".*\\S.*");
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		
		switch (id) {
		case GET_JOIN_USER_GROUP_REQUEST:
			createDialog = new ProgressDialog(this);
			createDialog.setCancelable(true);
			createDialog.setMessage(getText(R.string.getting_join_to_user_group_request));
			return createDialog;
		
		case CREATE_GROUP_DIALOG:
			createDialog = new ProgressDialog(this);
			createDialog.setCancelable(true);
			createDialog.setMessage(getText(R.string.creating_group));
			return createDialog;
			
		}
		return null;
	}
	
	public void afterGroupInviteListLoaded(final List<GroupInviteData> groupInviteList) {
    	//Dismiss dialog
    	dismissDialog(GET_JOIN_USER_GROUP_REQUEST);   
		
	
    	Log.i(TAG,"groupInviteList.size():"+groupInviteList.size());
    	
		if(groupInviteList==null || groupInviteList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.noGroupInviteForUser, Toast.LENGTH_LONG).show();
		}
		
		 ListView l1 = (ListView) findViewById(R.id.joinToGroupRequestList);
		 l1.setAdapter(new GroupRequestListItemAdapter(this,groupInviteList));
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getBaseContext(), "Details for "+groupInviteList.get(arg2).groupName, Toast.LENGTH_LONG).show();
				
			}
			 
		 });
		
	}
	private static class GroupRequestListItemAdapter extends BaseAdapter {
		 private LayoutInflater mInflater;

		 private List<GroupInviteData> inviteList;
		 
		 public GroupRequestListItemAdapter(Context context,List<GroupInviteData> list) {
			 mInflater = LayoutInflater.from(context);
			 inviteList=list;

		 }

		 public View getView(int position, View convertView, ViewGroup parent) {
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
				
				return Long.parseLong(inviteList.get(position).groupID);
			}
		 }
	public void finishAcceptGroupInvite(boolean result) {
    	//Show feedback
    	if(result){
    		Toast.makeText(getApplicationContext(), R.string.join_to_group_accept_ok, Toast.LENGTH_SHORT).show();
    	}else{
    		Toast.makeText(getApplicationContext(),  R.string.join_to_group_accept_ko, Toast.LENGTH_SHORT).show();
    	}
		
	}
	
	public void finishRefuseGroupInvite(boolean result) {
    	//Show feedback
    	if(result){
    		Toast.makeText(getApplicationContext(), R.string.join_to_group_refuse_ok, Toast.LENGTH_SHORT).show();
    	}else{
    		Toast.makeText(getApplicationContext(),  R.string.join_to_group_refuse_ko, Toast.LENGTH_SHORT).show();
    	}
		
	}
	
    public void finishCreateGroup (boolean result) {
    	//Dismiss dialog
    	dismissDialog(CREATE_GROUP_DIALOG);   	
    	
    	//Show feedback
    	if(result){
    		Toast.makeText(getApplicationContext(), R.string.group_creation_ok, Toast.LENGTH_LONG).show();
    	}else{
    		Toast.makeText(getApplicationContext(),  R.string.group_creation_ko, Toast.LENGTH_LONG).show();
    	}
    }


	
}
