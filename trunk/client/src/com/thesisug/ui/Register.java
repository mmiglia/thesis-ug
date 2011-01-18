package com.thesisug.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.NetworkUtilities;
import com.thesisug.communication.RegistrationResource;

public class Register extends Activity {
	private static final String TAG = "thesisug - ui.RegisterActivity";
	
	private EditText firstnbox, lastnbox, emailbox, newuserbox, newpwd1box, newpwd2box;
	private TextView message;
	private TextView firstnok, lastnok, emailok, newuserok, newpwd1ok, newpwd2ok;
	private Thread registrationThread;
	private Handler handler = new MyHandler();
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.register);
    	
    	//Handler handler = new MyHandler();
        
        message = (TextView) findViewById(R.id.submit_message);
        firstnbox = (EditText) findViewById(R.id.firstname);
        firstnok = (TextView) findViewById(R.id.firstnameok);
        lastnbox = (EditText) findViewById(R.id.lastname);
        lastnok = (TextView) findViewById(R.id.lastnameok);
        emailbox = (EditText) findViewById(R.id.email);
        emailok = (TextView) findViewById(R.id.emailok);
        newuserbox = (EditText) findViewById(R.id.newuser);
        newuserok = (TextView) findViewById(R.id.newuserok);
        newpwd1box = (EditText) findViewById(R.id.newpwd1);
        newpwd1ok = (TextView) findViewById(R.id.newpwd1ok);
        newpwd2box = (EditText) findViewById(R.id.newpwd2);
        newpwd2ok = (TextView) findViewById(R.id.newpwd2ok);
        
        firstnok.setVisibility(View.INVISIBLE);
        lastnok.setVisibility(View.INVISIBLE);
        emailok.setVisibility(View.INVISIBLE);
        newuserok.setVisibility(View.INVISIBLE);
        newpwd1ok.setVisibility(View.INVISIBLE);
        newpwd2ok.setVisibility(View.INVISIBLE);
        
        Button canc = (Button) findViewById(R.id.cancel);
        canc.setOnClickListener(new View.OnClickListener() {
           public void onClick(View arg0) {
        	   finish();
           }
        });
    }
    
    private class MyHandler extends Handler {
    	@Override
        public void handleMessage(Message msg) {
    		Bundle bundle = msg.getData();
    		if(bundle.containsKey("status")) {
    			int value = bundle.getInt("status");
    			//Toast.makeText(getApplicationContext(), "status:"+value, Toast.LENGTH_SHORT).show();
    			if (value==0) {
    				Toast.makeText(getApplicationContext(), R.string.user_exist, Toast.LENGTH_SHORT).show();
    				newuserbox.setText("");
    				newuserok.setVisibility(View.VISIBLE);
    			}
				if (value==1) {
					Toast.makeText(getApplicationContext(), R.string.email_exist, Toast.LENGTH_SHORT).show();
					emailbox.setText("");
    				emailok.setVisibility(View.VISIBLE);
				}
				if (value==2) {
					Toast.makeText(getApplicationContext(), R.string.registration_ok, Toast.LENGTH_SHORT).show();
					finish();
				}
				if (value==404) {
					Toast.makeText(getApplicationContext(), R.string.tryConnectionFail, Toast.LENGTH_SHORT).show();
				}
    			//view.setText(value);
    		}
        }

    }
    
    public void register(View view) {
    	
    	firstnok.setVisibility(View.INVISIBLE);
        lastnok.setVisibility(View.INVISIBLE);
        emailok.setVisibility(View.INVISIBLE);
        newuserok.setVisibility(View.INVISIBLE);
        newpwd1ok.setVisibility(View.INVISIBLE);
        newpwd2ok.setVisibility(View.INVISIBLE);
        
 	   	Log.i(TAG, "Creating new account");
 	   	boolean fieldsok = false;
 	   	boolean fieldempty = false;        	   
 	   	if (!check(firstnbox.getText().toString(), ".*\\S.*")) {
 	   		fieldempty = true;
 	   		firstnok.setVisibility(View.VISIBLE);
 	   	}
 	   	if (!check(lastnbox.getText().toString(), ".*\\S.*")) {
 	   		fieldempty = true;
 	   		lastnok.setVisibility(View.VISIBLE);
 	   	}
 	   	if (!check(emailbox.getText().toString(), ".*\\S.*")) {
 	   		fieldempty = true;
 	   		emailok.setVisibility(View.VISIBLE);
 	   	}
 	   	if (!check(newuserbox.getText().toString(), ".*\\S.*")) {
 	   		fieldempty = true;
 	   		newuserok.setVisibility(View.VISIBLE);
 	   	}
 	   	if (!check(newpwd1box.getText().toString(), ".*\\S.*")) {
 	   		fieldempty = true;
 	   		newpwd1ok.setVisibility(View.VISIBLE);
 	   	}
 	   	if (!check(newpwd2box.getText().toString(), ".*\\S.*")) {
 	   		fieldempty = true;
 	   		newpwd2ok.setVisibility(View.VISIBLE);
 	   	}
 	   	if (fieldempty)
 	   		Toast.makeText(getApplicationContext(), R.string.no_fields, Toast.LENGTH_SHORT).show();
 	   	else {
 	   		// check correctness of email, username and password
 	   		if (check(emailbox.getText().toString(), "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}")) {
 	   			if (check(newuserbox.getText().toString(), "\\S{1,}")) {
 	   				if (check(newpwd1box.getText().toString(), "\\S{1,}") || 
 	   						check(newpwd2box.getText().toString(), "\\S{1,}")) {
 	   					// check pwd1=pwd2
 	   					if (newpwd1box.getText().toString().equals(newpwd2box.getText().toString())) {
 	   						fieldsok = true;
 	   					}
 	   					else {
 	   						newpwd1ok.setVisibility(View.VISIBLE);
 	   						newpwd2ok.setVisibility(View.VISIBLE);
 	   						newpwd1box.setText("");
 	   						newpwd2box.setText("");
 	   						Toast.makeText(getApplicationContext(), R.string.pwd_no_match, Toast.LENGTH_SHORT).show();
 	   					}   	         					   
 	   				}
 	   				else {
 	   					newpwd1ok.setVisibility(View.VISIBLE);
 	   					newpwd2ok.setVisibility(View.VISIBLE);
 	   					newpwd1box.setText("");
 	   					newpwd2box.setText("");
 	   					Toast.makeText(getApplicationContext(), R.string.pwd_invalid, Toast.LENGTH_SHORT).show();
 	   				}
 	   			}
 	   			else {
 	   				newuserok.setVisibility(View.VISIBLE);
 	   				Toast.makeText(getApplicationContext(), R.string.username_invalid, Toast.LENGTH_SHORT).show();
 	   			}
 	   		}
 	   		else {
 	   			emailok.setVisibility(View.VISIBLE);
 	   			Toast.makeText(getApplicationContext(), R.string.email_invalid, Toast.LENGTH_SHORT).show();
 	   		}
 	   	}
 	   
 	   	if (fieldsok) {
 	   		// all fields inserted are ok, start registration thread
 	   		message.setText("email: "+emailbox.getText().toString());
 	   		Log.i(TAG,"Starting registrationThread");
 	   		Toast.makeText(getApplicationContext(), "Sending registration request to "+NetworkUtilities.SERVER_URI, Toast.LENGTH_SHORT).show();
 	   		//Handler handler = new MyHandler();
            registrationThread = RegistrationResource.register(firstnbox.getText().toString(), 
         		   lastnbox.getText().toString(), 
         		   emailbox.getText().toString(), 
         		   newuserbox.getText().toString(), 
         		   newpwd2box.getText().toString(),
         		   handler);
            Log.i(TAG,"registrationThread returned");
            //finish();
 	   }
    }
    
    public boolean check(String input, String regex) {
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(input);
    	if (matcher.matches())
    		return true;
    	else
    		return false;
    }

}
