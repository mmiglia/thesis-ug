package com.thesisug.location;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Service that receives ActivityRecognition updates. It receives updates
 * in the background, even if the main Activity is not visible.
 */

public class ActivityRecognitionIntentService extends IntentService 
{
	
	private static final String TAG = "thesisug - ActivityRecognitionService";

    // Store the app's shared preferences repository
    private static SharedPreferences sharedPreferences;
    private static String actualStatus;
  
    public ActivityRecognitionIntentService() 
    {
        // Set the label for the service's background thread
        super("ActivityRecognitionIntentService");
        actualStatus = "UNKNOWN";
    }
  
    /**
     * Called when a new activity detection update is available.
     */
    @Override
    protected void onHandleIntent(Intent intent) 
    {
    	Log.i(TAG,"New activity detection avaiable.");
        // Get a handle to the repository
        sharedPreferences = getApplicationContext().getSharedPreferences(ActivityUtils.SHARED_PREFERENCES, Context.MODE_PRIVATE);

        // If the intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) 
        {
        	Log.d(TAG,"New ActivityRecognitionResult.");
        	
        	ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        	DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            int confidenceLevel = mostProbableActivity.getConfidence();
            int activityType = mostProbableActivity.getType();

            // Check to see if the repository contains a previous activity
            if (!sharedPreferences.contains(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE)) 
            {
            	Log.d(TAG,"First time an activity is detected, storing type.");
                Editor editor = sharedPreferences.edit();
                editor.putInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE, activityType);
                editor.commit();
            
            } 
            else if ((confidenceLevel >= 50)) 
            {
            	SetActualStatus(getNameFromType(activityType));
            	Log.d(TAG,actualStatus);
                // Notify the user
              	
            	if(activityChanged(activityType))
            	{
            		Log.d(TAG,"User activity changed");
            		//TODO Mo che me ne fo?
            	}
            }
        }
    }
    
    private void SetActualStatus (String newStatus)
    {
    		actualStatus = newStatus;
    }
    
    public String GetActualStatus ()
    {
    		return actualStatus; 
    }

    /**
     * Check if activity has changed from previous.
     *
     * @param currentType 		Current Activity type.
     * @return 					True if activity changed, false otherwise.
     */
    private boolean activityChanged(int currentActivityType) 
    {
        int previousActivityType = sharedPreferences.getInt(ActivityUtils.KEY_PREVIOUS_ACTIVITY_TYPE,DetectedActivity.UNKNOWN);
        
        if (previousActivityType != currentActivityType) 
        {
        	Log.d(TAG,"Activity changed.");
            return true;
        } 
        else 
        {
        	Log.d(TAG,"Activity not changed");
            return false;
        }
    }


    /**
     * Map detected activity types to strings
     *
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    private String getNameFromType(int activityType) 
    {
        switch(activityType) 
        {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
}
