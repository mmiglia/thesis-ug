package com.thesisug.ui;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.GroupResource;
import com.thesisug.communication.valueobject.GroupData;
import com.thesisug.communication.valueobject.GroupMember;

public class ViewGroupMembers extends Activity {
	private static final String TAG = "thesisug - ViewGroup";
	
	private static final int VIEW_GROUP = 0;
	private static final int GET_USER_GROUP_LIST = 1;
	private static final int DELETE_ME_FROM_GROUP = 2;
	
	private final Handler handler = new Handler();
	private ProgressDialog createDialog;
	private Button btnUpdateGroupList,btnViewGroup,btn_ExitFromGroup;
	private Spinner spinnerGroupList;
	private Thread downloadGroupListThread,viewGroupThread,deleteFromGroupThread;
	private int currentDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.view_group_members);
		
		btnUpdateGroupList=(Button)this.findViewById(R.id.btn_updateGroupList);
		btnUpdateGroupList.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				updateUserGroupList();
			}
			
		});
		
		spinnerGroupList=(Spinner)findViewById(R.id.SpinnerGroupList);
		
		btnViewGroup=(Button)this.findViewById(R.id.btn_ViewGroup);
		btnViewGroup.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				String groupId=spinnerGroupList.getSelectedItem().toString().split("-")[0];
				currentDialog=VIEW_GROUP;
				showDialog(currentDialog);
				viewGroupThread = GroupResource.viewGroup(groupId, handler, ViewGroupMembers.this);
			}
		});
		
		
		btn_ExitFromGroup=(Button)this.findViewById(R.id.btn_ExitFromGroup);
		btn_ExitFromGroup.setOnClickListener(new OnClickListener(){
	
			@Override
			public void onClick(View v) {
				String groupId=spinnerGroupList.getSelectedItem().toString().split("-")[0];
				currentDialog=DELETE_ME_FROM_GROUP;
				showDialog(currentDialog);
				deleteFromGroupThread = GroupResource.deleteFromGroup(groupId, handler, ViewGroupMembers.this);
			}
		});
		
		
		updateUserGroupList();
	}
	
	private void updateUserGroupList(){
		//Get group list 
		currentDialog=GET_USER_GROUP_LIST;
		showDialog(currentDialog);
		downloadGroupListThread = GroupResource.getUserGroup(handler, ViewGroupMembers.this);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case GET_USER_GROUP_LIST:
				createDialog = new ProgressDialog(this);
				createDialog.setCancelable(true);
				createDialog.setMessage(getText(R.string.getting_user_group_list));
				return createDialog;
				
			case VIEW_GROUP:
				createDialog = new ProgressDialog(this);
				createDialog.setCancelable(true);
				createDialog.setMessage(getText(R.string.getting_group_data));
				return createDialog;
			case DELETE_ME_FROM_GROUP:
				createDialog = new ProgressDialog(this);
				createDialog.setCancelable(true);
				createDialog.setMessage(getText(R.string.delting_from_group));
				return createDialog;
		}
		
		return null;
	}	
	
	public void afterGroupListLoaded(List<GroupData> groupList){
    	//Dismiss dialog
    	dismissDialog(GET_USER_GROUP_LIST);   
		
		if(groupList==null){
			Toast.makeText(getApplicationContext(), R.string.fail_to_update_group_list, Toast.LENGTH_LONG).show();;
			finish();
		}
    	
		//Update groupSpinnerList
	
    	ArrayAdapter<String> arrGroupsAdapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item);
    	
    	for(GroupData g:groupList){
    		arrGroupsAdapter.add(g.groupID+"-"+g.groupName);
    	}
    	
    	spinnerGroupList.setAdapter(arrGroupsAdapter);

		if(groupList==null || groupList.isEmpty()){
			Toast.makeText(getApplicationContext(), R.string.noGroupForUser, Toast.LENGTH_LONG).show();
		}
		
	}

	public void afterGroupMemberListLoaded(final List<GroupMember> groupMemberList) {
    	//Dismiss dialog
    	dismissDialog(VIEW_GROUP);   
    	
    	
		if(groupMemberList==null){
			Toast.makeText(getApplicationContext(), R.string.fail_to_get_group_member_list, Toast.LENGTH_LONG).show();
			return;
		}
		if(groupMemberList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.no_group_members, Toast.LENGTH_LONG).show();
			return;
		}
		
		
		 ListView l1 = (ListView) findViewById(R.id.groupMembersList);
		 l1.setAdapter(new GroupMembersListItemAdapter(this,groupMemberList));
		 
		 l1.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getBaseContext(), "Details for "+groupMemberList.get(arg2).username, Toast.LENGTH_LONG).show();
				
			}
			 
		 });
		
	}
	
	public void afterDeltingFromGroup(final boolean result){
    	//Dismiss dialog
    	dismissDialog(DELETE_ME_FROM_GROUP);
    	
    	if(result){
    		Toast.makeText(getApplicationContext(), R.string.deleting_from_group_ok, Toast.LENGTH_LONG).show();
    		updateUserGroupList();
    	}else{
    		Toast.makeText(getApplicationContext(), R.string.deleting_from_group_failed, Toast.LENGTH_LONG).show();
    	}
		
	}
	
	private static class GroupMembersListItemAdapter extends BaseAdapter {
		 private LayoutInflater mInflater;

		 private List<GroupMember> memberList;
		 
		 public GroupMembersListItemAdapter(Context context,List<GroupMember> list) {
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
			}


		 }
}
