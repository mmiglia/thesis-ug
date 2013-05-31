package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.LoginResource;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.valueobject.LoginReply;


/**
 * This activity is shown when the user add new account service in
 * an android device
 */
public class CopyOfLogin extends AccountAuthenticatorActivity {
	private static final String TAG = "thesisug - ui.LoginActivity";
	public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
    private static final int DEFAULT_SERVER_POS_IN_LIST=0;
	private AccountManager accountManager;
	private String username;
	private String password;
	private String authtokenType;
	private boolean usernameIsEmpty;
	private boolean confirmCredentials; // just checking if user knows their credentials
	private TextView message;
	private EditText usernameBox;
	private EditText passwordBox;
	private Thread authenticationThread;
	private final Handler handler = new Handler();
    private static SharedPreferences usersettings;
	private String serverURI="";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.login);
        
        accountManager = AccountManager.get(this);
        Log.i(TAG, "loading data from intent");
        final Intent intent = getIntent();
        
        username = intent.getStringExtra(PARAM_USERNAME);
        authtokenType = intent.getStringExtra(PARAM_AUTHTOKEN_TYPE);
        usernameIsEmpty = username == null; // request new account if user name = null
        confirmCredentials = intent.getBooleanExtra(PARAM_CONFIRMCREDENTIALS, false);
        
        message = (TextView) findViewById(R.id.message);
        usernameBox = (EditText) findViewById(R.id.username);
        passwordBox = (EditText) findViewById(R.id.password);
        
        usernameBox.setText(username);
        message.setText(getMessage());
        
        usersettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        Spinner spnServer = (Spinner) findViewById(R.id.SpinnerServerList);
        
        ArrayList<ServerData> serverList=new ArrayList<ServerData>();
        
        //Creating the serverList
        //TODO retrive server from file or from a known-server's web service
        serverList.add(new ServerData("Localhost","10.0.2.2"));
        serverList.add(new ServerData("UniPD","serverpd.dyndns.org"));
        serverList.add(new ServerData("UniGE","zelda.openlab-dist.org"));
        
        
        
        ServerListItemAdapter serverListAdapter=new ServerListItemAdapter(this,serverList);
        
        spnServer.setAdapter(serverListAdapter);
        
        spnServer.setOnItemSelectedListener(new OnItemSelectedListener(){

    		@Override
    		public void onItemSelected(AdapterView<?> parent, View v, int pos,
    				long row) {
    			
    			String serverURI=((ServerData)parent.getItemAtPosition(pos)).serverURL;
    			Log.d(TAG,"Selected "+serverURI);
    			usersettings.edit().putString("ServerURI", serverURI);
    			NetworkUtilities.changeServerURI(serverURI);
    			Log.d(TAG, parent.toString());
    		}

    		@Override
    		public void onNothingSelected(AdapterView<?> arg0) {
    			// TODO Auto-generated method stub
    			
    		}
        	
        });
        
        /*spnServer.setSelection(DEFAULT_SERVER_POS_IN_LIST);
        

        serverURI=((ServerData)spnServer.getItemAtPosition(DEFAULT_SERVER_POS_IN_LIST)).serverURL;
        
        
        usersettings.edit().putString("ServerURI", serverURI);
        
        NetworkUtilities.changeServerURI(serverURI);
        */

    }
    
    private static class ServerListItemAdapter extends BaseAdapter {

    	private List<ServerData> serverList;
   	
    	private LayoutInflater mInflater;

	 
    	public ServerListItemAdapter(Context context,List<ServerData> _serverList) {
			 mInflater = LayoutInflater.from(context);
			 serverList=_serverList;
		 }
    	
		@Override
		public int getCount() {
			return serverList.size();
		}

		@Override
		public Object getItem(int pos) {
			return serverList.get(pos);
		}

		@Override
		public long getItemId(int pos) {
			return serverList.get(pos).hashCode();
		}

		private void printServerList(){
			Log.d(TAG, "PRINT - start");
			int pos=0;
			for(ServerData server:serverList){
				Log.d(TAG, pos+":"+server.serverName);
				pos++;
			}
			Log.d(TAG, "PRINT - end");
		}
		@Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "getView -start- pos:"+position);
			printServerList();
			 final ViewHolder holder;
			 if (convertView == null) {
				 convertView = mInflater.inflate(R.layout.select_server_list_item, null);
				 holder = new ViewHolder();
				 
				 holder.server=serverList.get(position);
				 holder.serverName=serverList.get(position).serverName;
				 Log.d(TAG,"Pos:"+position+", server:"+holder.server.serverName);

				 
				 holder.txtServerName=(TextView)convertView.findViewById(R.id.txt_server_name);
				 Log.d(TAG, "Set text for position "+ position+ ": "+ holder.server.serverName);
				 holder.txtServerName.setText(holder.serverName);
				 
				 convertView.setTag(holder);
			 } else {
				 Log.d(TAG, position+")Getting tag serverName"+((ViewHolder) convertView.getTag()).serverName);
				 holder = (ViewHolder) convertView.getTag();
			 }
			 Log.d(TAG, "getView -end- pos:"+position);
			 return convertView;
		 }
		
		 static class ViewHolder {
			 
			 TextView txtServerName;
			 String serverName;
			 ServerData server;
		 }
		 

    
    }
    
    
	private class ServerData{
		public String serverName="";
		public String serverURL="";
		
		public ServerData(String _serverName,String _serverURL){
			serverName=_serverName;
			serverURL=_serverURL;
		}
	}

    /**
     * get the message to be displayed at login box
     * @return <CharSequence containing the message to be displayed
     */
    private CharSequence getMessage() {
    	if (TextUtils.isEmpty(username) && TextUtils.isEmpty(password))
    		return getText(R.string.sign_in_new_account);    	
        if (TextUtils.isEmpty(username))               
            return getText(R.string.sign_in_no_username);
        if (TextUtils.isEmpty(password)) {
            return getText(R.string.sign_in_no_password);
        }
        return null;
    }
    
    public void signIn(View view){
    	if (usernameIsEmpty) username = usernameBox.getText().toString();
        password = passwordBox.getText().toString();
        
        //check for empty username or password field
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            message.setText(getMessage());
        } else {
            showDialog(0); //will call onCreateDialog method     
            Log.i(TAG,"Starting authenticationThread");
            authenticationThread = LoginResource.signIn(username, password, handler, CopyOfLogin.this);
            Log.i(TAG,"authenticationThread returned");
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.authenticating)+ NetworkUtilities.SERVER_URI);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Log.i(TAG, "dialog cancel has been invoked");
                if (authenticationThread != null) {
                    authenticationThread.interrupt();
                    finish();
                }
            }
        });
        return dialog;
    }
    
    /**
     * Called after authentication process is finished
     */
    public void showResult(LoginReply result) {
        dismissDialog(0); //disable the progress dialog
        if(result.status==404){
        	 message.setText(getText(R.string.tryConnectionFail));
        	 return;
        }
        
        if (result.status == 1) {
            if (!confirmCredentials) {
            	finishLogin(result);
            } else {
                finishConfirmCredentials(true);
            }
        } else {
            Log.e(TAG, "Authentication failed");
            if (usernameIsEmpty) {
                message.setText(getText(R.string.sign_in_failed_both));
            } else {
                message.setText(getText(R.string.sign_in_failed_password));
            }
        }
    }
    
    /**
     * 
     * Called when response is received from the server for authentication
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller. Also sets
     * the authToken in AccountManager for this account.
     * 
     * @param the confirmCredentials result.
     */

    protected void finishLogin(LoginReply result) {
        final Account account = new Account(username, com.thesisug.Constants.ACCOUNT_TYPE);
        Log.i(TAG, "finish login");
        Intent intent = getIntent();
        Log.i(TAG, "after intent");
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, com.thesisug.Constants.ACCOUNT_TYPE);
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, result.session);
        if (usernameIsEmpty) {
        	accountManager.addAccountExplicitly(account, password, intent.getExtras());
        	accountManager.setAuthToken(account, com.thesisug.Constants.AUTHTOKEN_TYPE, result.session);
        	// Set contacts sync for this account.
        	// ContentResolver.setSyncAutomatically(account,
        	//  ContactsContract.AUTHORITY, true);
        } else {
        	//this line has to be there to complete authorization process
        	accountManager.setPassword(account, password); 
        }
        setAccountAuthenticatorResult(intent.getExtras());
        AccountAuthenticatorResponse response = intent.getExtras().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        if (response!=null) response.onResult(intent.getExtras());
        setResult(RESULT_OK);
        finish();
    }
    
    /**
     * Called when response is received from the server for confirm credentials
     * request. See onAuthenticationResult(). Sets the
     * AccountAuthenticatorResult which is sent back to the caller.
     * 
     * @param the confirmCredentials result.
     */
    protected void finishConfirmCredentials(boolean result) {
        Log.i(TAG, "confirm credentials");
    	final Account account = new Account(username, com.thesisug.Constants.ACCOUNT_TYPE);
        accountManager.setPassword(account, password);
        final Intent intent = new Intent();
        intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result);
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    } 
}