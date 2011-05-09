package com.thesisug.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.AssertionsResource;
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

	
	public void afterAssertionsListLoaded(List<SingleItemLocation> ItemLocationList){
		//Dismiss dialog
    	//dismissDialog(GET_JOIN_USER_GROUP_REQUEST);   
		
	
    	//Log.i(TAG,"groupInviteList.size():"+groupInviteList.size());
    	
		if(ItemLocationList==null || ItemLocationList.size()==0){
			Toast.makeText(getApplicationContext(), R.string.noAssertions, Toast.LENGTH_LONG).show();
		}
		
		String[] item = new String[20];
		/*

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
		 
		 */
		int n_list=0;
		                                
		 for (SingleItemLocation o : ItemLocationList){
         	Log.d(TAG,"ItemLocationList in afterAssertionsListLoaded ");
				// add to the listview
	
				item[n_list]= o.item + " -> " + o.location;
				n_list++;
			}
		
		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.view_assertions, item));

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
		
}
	
	
}
