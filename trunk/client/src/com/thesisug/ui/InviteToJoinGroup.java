package com.thesisug.ui;

import java.util.List;

import com.thesisug.R;
import com.thesisug.communication.GroupResource;
import com.thesisug.communication.valueobject.GroupData;
import com.thesisug.communication.valueobject.GroupInviteData;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class InviteToJoinGroup extends Activity {
	private static final String TAG = "thesisug - InviteToJoinGroup";
	
	private static final int GET_USER_GROUP = 0;
	private static final int INVITE_JOIN_GROUP = 1;
	
	private final Handler handler = new Handler();
	
	private Button btnUpdateGroupList,btnInviteToGroup;
	private EditText txtUserToInvite,txtMessageForUserToInvite;
	
	private ProgressDialog createDialog;
	
	private Spinner spinnerGroupList;
	
	private int currentDialog;
	private Thread downloadGroupListThread,groupInviteThread;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.invite_to_join_group);
		
		btnUpdateGroupList=(Button)this.findViewById(R.id.btn_updateGroupList);
		btnUpdateGroupList.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				updateUserGroupList();
			}
			
		});
		
		spinnerGroupList=(Spinner)findViewById(R.id.SpinnerGroupToInvite);
		
		txtUserToInvite=(EditText)findViewById(R.id.txt_userToInvite);
		txtMessageForUserToInvite=(EditText)findViewById(R.id.txt_message_for_userToInvite);
		
		btnInviteToGroup=(Button)this.findViewById(R.id.btn_inviteToGroup);
		btnInviteToGroup.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				//Get the username
				String userToInvite=txtUserToInvite.getText().toString();
				
				String groupId=spinnerGroupList.getSelectedItem().toString().split("-")[0];
				String message=txtMessageForUserToInvite.getText().toString();
				//Send invite to the server
				currentDialog=INVITE_JOIN_GROUP;
				showDialog(currentDialog);
				//Request id will be set by the server, the groupName and sender username are not required
				GroupInviteData invite=new GroupInviteData("-1",groupId,"",userToInvite,message,"");
				groupInviteThread = GroupResource.inviteUserToJoinTheGroup(invite, handler, InviteToJoinGroup.this);
			}
			
		});
		updateUserGroupList();
	}
	
	private void updateUserGroupList(){
		//Get group list 
		currentDialog=GET_USER_GROUP;
		showDialog(currentDialog);
		downloadGroupListThread = GroupResource.getUserGroup(handler, InviteToJoinGroup.this);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case GET_USER_GROUP:
				createDialog = new ProgressDialog(this);
				createDialog.setCancelable(true);
				createDialog.setMessage(getText(R.string.getting_user_group_list));
				return createDialog;
				
			case INVITE_JOIN_GROUP:
				createDialog = new ProgressDialog(this);
				createDialog.setCancelable(true);
				createDialog.setMessage(getText(R.string.creating_invite_to_join_the_group));
				return createDialog;
		}
		
		return null;
	}	
	public void finishJoinGroupInvite(boolean result) {
    	//Dismiss dialog
    	dismissDialog(INVITE_JOIN_GROUP);    	
    	
    	//Show feedback
    	if(result){
    		Toast.makeText(getApplicationContext(), R.string.group_invite_ok, Toast.LENGTH_LONG).show();
    	}else{
    		Toast.makeText(getApplicationContext(),  R.string.group_invite_ko, Toast.LENGTH_LONG).show();
    	}
		
	}
	
	public void afterGroupListLoaded(List<GroupData> groupList){
    	//Dismiss dialog
    	dismissDialog(GET_USER_GROUP);   
		
		//Update groupSpinnerList
	
    	ArrayAdapter<String> arrGroupsAdapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item);
    	
    	for(GroupData g:groupList){
    		arrGroupsAdapter.add(g.groupID+"-"+g.groupName);
    	}
    	
    	spinnerGroupList.setAdapter(arrGroupsAdapter);

		if(groupList!=null){
			Toast.makeText(getApplicationContext(), R.string.thereAreGroupForUser, Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(getApplicationContext(), R.string.noGroupForUser, Toast.LENGTH_LONG).show();
		}
		
	}
	
	

}
