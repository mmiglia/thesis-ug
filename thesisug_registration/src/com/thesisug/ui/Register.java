package com.thesisug.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thesisug.R;
import com.thesisug.communication.LoginResource;
import com.thesisug.communication.RegistrationResource;

public class Register extends Activity {
	private static final String TAG = "thesisug - ui.RegisterActivity";
	
	private EditText firstnbox, lastnbox, emailbox, newuserbox, newpwd1box, newpwd2box;
	private TextView message;
	private TextView firstnok, lastnok, emailok, newuserok, newpwd1ok, newpwd2ok;
	private Thread registrationThread;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	setContentView(R.layout.register);
        
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
        
        Button sub = (Button) findViewById(R.id.submit);
        sub.setOnClickListener(new View.OnClickListener() {
           public void onClick(View arg0) {
        	   firstnok.setVisibility(View.INVISIBLE);
               lastnok.setVisibility(View.INVISIBLE);
               emailok.setVisibility(View.INVISIBLE);
               newuserok.setVisibility(View.INVISIBLE);
               newpwd1ok.setVisibility(View.INVISIBLE);
               newpwd2ok.setVisibility(View.INVISIBLE);
               
        	   Log.i(TAG, "Creating new account");
        	   boolean fieldsok = false;
        	   boolean pwdok = false;
        	   
        	   if (firstnbox.getText().toString().length()==0) {
        		   fieldsok = false;
        		   firstnok.setVisibility(View.VISIBLE);
        	   }
        	   else
        		   fieldsok = true;
        	   
        	   if (lastnbox.getText().toString().length()==0) {
        		   fieldsok = false;
        		   lastnok.setVisibility(View.VISIBLE);
        	   }
        	   else
        		   fieldsok = true;
        	   
        	   if (emailbox.getText().toString().length()==0) {
        		   fieldsok = false;
        		   emailok.setVisibility(View.VISIBLE);
        	   }
        	   else
        		   fieldsok = true;
        	   
        	   if (newuserbox.getText().toString().length()==0) {
        		   fieldsok = false;
        		   newuserok.setVisibility(View.VISIBLE);
        	   }
        	   else
        		   fieldsok = true;
        	   
        	   if (newpwd1box.getText().toString().length()==0) {
        		   fieldsok = false;
        		   newpwd1ok.setVisibility(View.VISIBLE);
        	   }
        	   else
        		   fieldsok = true;
        	   
        	   if (newpwd2box.getText().toString().length()==0) {
        		   fieldsok = false;
        		   newpwd2ok.setVisibility(View.VISIBLE);
        	   }
        	   else {
        		   fieldsok = true;
        		   if ( newpwd1box.getText().toString().equals(newpwd2box.getText().toString()) )
        			   pwdok = true;
        	   }
        	   
        	   if (!fieldsok || !pwdok) {
        		   if (!fieldsok)
        			   message.setText(R.string.no_fields);
        		   else {
        			   message.setText(R.string.pwd_no_match);
        			   newpwd1ok.setVisibility(View.VISIBLE);
        			   newpwd2ok.setVisibility(View.VISIBLE);
        		   }
        	   }
        	   else {
        		   //si può far partire il thread di registrazione
        		   message.setText("thread registrazione starting");
        		   Log.i(TAG,"Starting registrationThread");
                   registrationThread = RegistrationResource.register(firstnbox.getText().toString(), 
                		   lastnbox.getText().toString(), 
                		   emailbox.getText().toString(), 
                		   newuserbox.getText().toString(), 
                		   newpwd2box.getText().toString());
                   Log.i(TAG,"registrationThread returned");
                   finish();
                   // TODO 
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
