package com.thesisug.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thesisug.R;

public class Register extends Activity {
	private static final String TAG = "thesisug - ui.RegisterActivity";
	
	public static final String PARAM_FIRSTNAME = "firstname";
	public static final String PARAM_LASTNAME = "lastn";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_NEWUSER = "newuser";
	public static final String PARAM_PWD1 = "newpwd1";
	public static final String PARAM_PWD2 = "newpwd2";
	public static final String PARAM_SUBMSG = "submit_message";
	private String firstn, lastn, mail, newuser, newpwd1, newpwd2, sub_msg;
	private EditText firstnbox, lastnbox, emailbox, newuserbox, newpwd1box, newpwd2box;
	private TextView message;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.register);
        
        message = (TextView) findViewById(R.id.submit_message);
        firstnbox = (EditText) findViewById(R.id.firstname);
        lastnbox = (EditText) findViewById(R.id.lastname);
        emailbox = (EditText) findViewById(R.id.email);
        newuserbox = (EditText) findViewById(R.id.newuser);
        newpwd1box = (EditText) findViewById(R.id.newpwd1);
        newpwd2box = (EditText) findViewById(R.id.newpwd2);
        
        Button sub = (Button) findViewById(R.id.submit);
        sub.setOnClickListener(new View.OnClickListener() {
           public void onClick(View arg0) {
        	   Log.i(TAG, "Creating new account");
        	   if ( newpwd1box.getText().toString() != newpwd2box.getText().toString() ) {
        		   message.setText(R.string.pwd_no_match);
        		   newpwd1box.setText("");
        		   newpwd2box.setText("");
        	   }
        	   else {
        		   // password value is matching
        	   }
           }
        });
        
        Button canc = (Button) findViewById(R.id.cancel);
        canc.setOnClickListener(new View.OnClickListener() {
           public void onClick(View arg0) {
        	   finish();
           }
        });
    }
}
