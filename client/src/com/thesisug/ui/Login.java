package com.thesisug.ui;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.LoginResource;

/**
 * This activity is shown when the user add new account service in
 * an android device
 */
public class Login extends AccountAuthenticatorActivity {
	private static final String TAG = "ui.LoginActivity";
	public static final String PARAM_CONFIRMCREDENTIALS = "confirmCredentials";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_AUTHTOKEN_TYPE = "authtokenType";
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
            authenticationThread = LoginResource.signIn(username, password, handler, Login.this);
        }
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getText(R.string.authenticating));
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
    public void showResult(boolean result) {
        dismissDialog(0); //disable the progress dialog
        if (result) {
            if (!confirmCredentials) {
            	finishLogin();
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

    protected void finishLogin() {
        final Account account = new Account(username, com.thesisug.Constants.ACCOUNT_TYPE);
        Log.i(TAG, "finish login");
        if (usernameIsEmpty) {
            accountManager.addAccountExplicitly(account, password, null);
            // Set contacts sync for this account.
           // ContentResolver.setSyncAutomatically(account,
              //  ContactsContract.AUTHORITY, true);
        } else {
        	//this line has to be there to complete authorization process
            accountManager.setPassword(account, password); 
        }
        Intent intent = getIntent();
        Log.i(TAG, "after intent");
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, com.thesisug.Constants.ACCOUNT_TYPE);
        if (authtokenType != null && authtokenType.equals(com.thesisug.Constants.AUTHTOKEN_TYPE)) {
                intent.putExtra(AccountManager.KEY_AUTHTOKEN, password);
            }
        setAccountAuthenticatorResult(intent.getExtras());
        Log.i(TAG, "after intent haha");
        AccountAuthenticatorResponse response = intent.getExtras().getParcelable(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        response.onResult(intent.getExtras());
        Log.i(TAG, "after intent haha222");
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