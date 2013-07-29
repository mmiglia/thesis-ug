package com.thesisug.location;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

/**
 * Class to request Activity Recognition updates.
 * 
 * To use a ActivityRecognitionRequester, instantiate it and call requestUpdates().
 * automatically.
 * 
 * @author Alberto Servetti
 */
public class ActivityRecognitionRequester implements ConnectionCallbacks, OnConnectionFailedListener 
{
	private static final String TAG = "thesisug - ActivityRecognitionRequester";
    private Context context;
    private PendingIntent activityRecognitionPendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;

    public ActivityRecognitionRequester(Context context) 
    {
        this.context = context;
        activityRecognitionPendingIntent = null;
        activityRecognitionClient = null;
    }
    
    /**
     * Returns the current PendingIntent to the caller.
     *
     * @return The PendingIntent used to request activity recognition updates.
     */
    public PendingIntent getRequestPendingIntent() 
    {
        return activityRecognitionPendingIntent;
    }

    /**
     * Sets the PendingIntent used to make activity recognition update requests
     * @param intent The PendingIntent used to request activity recognition updates.
     */
    public void setRequestPensdingIntent(PendingIntent intent) 
    {
        activityRecognitionPendingIntent = intent;
    }

    /**
     * Start the activity recognition update request process by
     * getting a connection.
     */
    public void requestUpdates() 
    {
    	Log.i(TAG,"requestUpdates");
        requestConnection();
    }

    /**
     * Make the actual update request. This is called from onConnected().
     */
    private void continueRequestActivityUpdates() 
    {
        /*
         * Request updates, using the default detection interval.
         * The PendingIntent sends updates to ActivityRecognitionIntentService
         */
        getActivityRecognitionClient().requestActivityUpdates(
                ActivityUtils.DETECTION_INTERVAL_MILLISECONDS,
                createRequestPendingIntent());

        // Disconnect the client
        requestDisconnection();
    }

    /**
     * Request a connection to Location Services. This call returns immediately,
     * but the request is not complete until onConnected() or onConnectionFailure() is called.
     */
    private void requestConnection() 
    {
        getActivityRecognitionClient().connect();
    }

    /**
     * Get the current activity recognition client, or create a new one if necessary.
     * This method facilitates multiple requests for a client, even if a previous
     * request wasn't finished. Since only one client object exists while a connection
     * is underway, no memory leaks occur.
     *
     * @return An ActivityRecognitionClient object
     */
    private ActivityRecognitionClient getActivityRecognitionClient()
    {
        if (activityRecognitionClient == null) 
        {
        	Log.d(TAG,"activityRecognitionClient null, creating new one.");
            activityRecognitionClient =
                    new ActivityRecognitionClient(context, this, this);
        }
        return activityRecognitionClient;
    }

    /**
     * Get the current activity recognition client and disconnect from Location Services
     */
    private void requestDisconnection() 
    {
        getActivityRecognitionClient().disconnect();
    }

    /*
     * Called by Location Services once the activity recognition client is connected.
     *
     * Continue by requesting activity updates.
     */
    @Override
    public void onConnected(Bundle arg0) 
    {
        Log.d(TAG, "Connected.");
        continueRequestActivityUpdates();
    }

    /*
     * Called by Location Services once the activity recognition client is disconnected.
     */
    @Override
    public void onDisconnected() 
    {
        Log.d(TAG,"Disconnected.");

        // Destroy the current activity recognition client
        activityRecognitionClient = null;
    }

    /**
     * Get a PendingIntent to send with the request to get activity recognition updates. Location
     * Services issues the Intent inside this PendingIntent whenever a activity recognition update
     * occurs.
     *
     * @return A PendingIntent for the IntentService that handles activity recognition updates.
     */
    private PendingIntent createRequestPendingIntent() 
    {
    	Log.i(TAG,"ActivityRecognitionRequester");
    	
        if (activityRecognitionPendingIntent != null) 
        {
        	Log.d(TAG,"activityRecognitionPendingIntent already exists.");
            return activityRecognitionPendingIntent;
        } 
        else 
        {
        	Log.d(TAG,"activityRecognitionPendingIntent is null, creating new one!");
            Intent intent = new Intent(context, ActivityRecognitionIntentService.class);
            
            PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
   
            activityRecognitionPendingIntent = pendingIntent;
            
            return activityRecognitionPendingIntent;
        }

    }

    /*
     * Implementation of OnConnectionFailedListener.onConnectionFailed
     * If a connection or disconnection request fails, report the error
     * connectionResult is passed in from Location Services
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) 
    {
    	Log.i(TAG,"Connection failed");
        if (connectionResult.hasResolution()) 
        {
        	/*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
            try 
            {
            	connectionResult.startResolutionForResult((Activity) context,ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

            } catch (SendIntentException e) 
            {
               e.printStackTrace();
            }
        } 
        else 
        {
        	 /*
             * If no resolution is available, display Google
             * Play service error dialog. This may direct the
             * user to Google Play Store if Google Play services
             * is out of date.
             */
        	Log.d(TAG,"No solution avaiable.");
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                            connectionResult.getErrorCode(),
                            (Activity) context,
                            ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (dialog != null) {
                dialog.show();
            }
        }
    }
}
