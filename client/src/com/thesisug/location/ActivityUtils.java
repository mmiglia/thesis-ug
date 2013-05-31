package com.thesisug.location;

/**
 * Utilities for Activity Recognition Service
 */
public final class ActivityUtils 
{
	//Request code to send to Google Play services 
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Intent actions and extras for sending information from the IntentService to the Activity
    public static final String ACTION_CONNECTION_ERROR =
            "com.example.android.activityrecognition.ACTION_CONNECTION_ERROR";

    public static final String ACTION_REFRESH_STATUS_LIST =
                    "com.example.android.activityrecognition.ACTION_REFRESH_STATUS_LIST";

    public static final String CATEGORY_LOCATION_SERVICES =
            "com.example.android.activityrecognition.CATEGORY_LOCATION_SERVICES";

    public static final String EXTRA_CONNECTION_ERROR_CODE =
            "com.example.android.activityrecognition.EXTRA_CONNECTION_ERROR_CODE";

    public static final String EXTRA_CONNECTION_ERROR_MESSAGE =
            "com.example.android.activityrecognition.EXTRA_CONNECTION_ERROR_MESSAGE";

    // Constants used to establish the activity update interval
    public static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int DETECTION_INTERVAL_SECONDS = 10;

    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;

    // Shared Preferences repository name
    public static final String SHARED_PREFERENCES =
            "com.example.android.activityrecognition.SHARED_PREFERENCES";

    // Key in the repository for the previous activity
    public static final String KEY_PREVIOUS_ACTIVITY_TYPE =
            "com.example.android.activityrecognition.KEY_PREVIOUS_ACTIVITY_TYPE";

}