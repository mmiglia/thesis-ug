package com.thesisug;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Login extends AccountAuthenticatorActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.sign);
        button.setOnClickListener(buttonListener);
    }
    
    private OnClickListener buttonListener = new OnClickListener() {
        public void onClick(View v) {
            Intent intent = new Intent().setClass(Login.this, ParentTab.class);
            startActivity(intent);
        }
    };
}