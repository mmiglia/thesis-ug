package com.thesisug.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.thesisug.R;
import com.thesisug.communication.InputResource;

public class Input extends Activity implements OnClickListener{
	private static final String TAG = new String("thesisug - Input Activity");
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private EditText commandbox;
    private static SharedPreferences userSettings;
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        // Inflate our UI from its XML layout description.
        setContentView(R.layout.input);
        
        userSettings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        // Get display items for later interaction
        Button speakButton = (Button) findViewById(R.id.btn_speak);
        Button sendButton = (Button) findViewById (R.id.btn_send);
        commandbox =(EditText) findViewById (R.id.input_text);
        sendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(getApplicationContext(), "parser language selezionato:"+userSettings.getString("parserlang", "en.lang"), Toast.LENGTH_SHORT).show();
				String text = commandbox.getText().toString();
				// filter trash command
				if (text.length()<5) return;
				Thread sendcommand = InputResource.input(text, 
						userSettings.getString("parserlang", "en.lang"), 
						new Handler(), Input.this);
				setResult(RESULT_OK);
				finish();
			}
		});
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            speakButton.setOnClickListener(this);
        } else {
            speakButton.setEnabled(false);
        }
    }

    /**
     * Handle the click on the start recognition button.
     */
    public void onClick(View v) {
        if (v.getId() == R.id.btn_speak) {
            startVoiceRecognitionActivity();
        }
    }

    /**
     * Fire an intent to start the speech recognition activity.
     */
    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    /**
     * Handle the results from the recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            commandbox.setText(matches.get(0));
        }

    }
    /**
     * Called after authentication process is finished
     */
    public void showResult(boolean result) {
    	Log.i(TAG, "in showResult "+result);
    	if (result) Toast.makeText(getApplicationContext(), R.string.parsing_success,
                Toast.LENGTH_LONG).show();
		else Toast.makeText(getApplicationContext(), R.string.parsing_error,
                Toast.LENGTH_LONG).show();
    }

}
