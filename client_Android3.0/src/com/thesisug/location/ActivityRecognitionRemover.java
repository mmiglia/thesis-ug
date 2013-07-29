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
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;

/**
 * Class for connecting to Location Services and removing activity recognition updates.
 * 
 * To use a ActityRecognitionRemover, instantiate it, then call removeUpdates().
 *	@author Alberto Servetti
 */
public class ActivityRecognitionRemover implements ConnectionCallbacks, OnConnectionFailedListener 
{
	private static final String TAG = "thesisug - ActivityRecognitionRemover";
    private Context context;
    private ActivityRecognitionClient activityRecognitionClient;
    private PendingIntent currentIntent;

    public ActivityRecognitionRemover(Context context) 
    {
        this.context = context;
        activityRecognitionClient = null;
    }

    /**
     * Remove the activity recognition updates associated with a PendIntent. The PendingIntent is 
     * the one used in the request to add activity recognition updates.
     *
     * @param requestIntent The PendingIntent used to request activity recognition updates.
     */
    public void removeUpdates(PendingIntent requestIntent) 
    {
        currentIntent = requestIntent;
        requestConnection();
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
     *
     * @return An ActivityRecognitionClient object
     */
    public ActivityRecognitionClient getActivityRecognitionClient() 
    {
        if (activityRecognitionClient == null) 
        {
            setActivityRecognitionClient(new ActivityRecognitionClient(context, this, this));
        }
        return activityRecognitionClient;
    }

    /**
     * Get a activity recognition client and disconnect from Location Services
     */
    private void requestDisconnection() 
    {
        getActivityRecognitionClient().disconnect();
        setActivityRecognitionClient(null);
    }

    /**
     * Set the global activity recognition client
     * @param client An ActivityRecognitionClient object
     */
    public void setActivityRecognitionClient(ActivityRecognitionClient client) 
    {
        activityRecognitionClient = client;
    }

    /**
     * Called by Location Services once the activity recognition client is connected.
     *
     * Continue by removing activity recognition updates.
     */
    @Override
    public void onConnected(Bundle connectionData) 
    {
        Log.d(TAG, "Connected.");
    
        continueRemoveUpdates();
    }

    /**
     * Once the connection is available, send a request to remove activity recognition updates. 
     */
    private void continueRemoveUpdates() 
    {
        // Remove the updates
        activityRecognitionClient.removeActivityUpdates(currentIntent);
        currentIntent.cancel();
        requestDisconnection();
    }

    /**
     * Called by Location Services once the activity recognition client is disconnected.
     */
    @Override
    public void onDisconnected() 
    {

        // In debug mode, log the disconnection
        Log.d(TAG, "Disconnected.");

        // Destroy the current activity recognition client
        activityRecognitionClient = null;
    }

    /**
     * Implementation of OnConnectionFailedListener.onConnectionFailed
     * If a connection or disconnection request fails, report the error
     * connectionResult is passed in from Location Services
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) 
    {

        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) 
        {

            try 
            {
                connectionResult.startResolutionForResult((Activity) context,ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } 
            catch (SendIntentException e) 
            {
               // display an error or log it here.
            }

        /*
         * If no resolution is available, display Google
         * Play service error dialog. This may direct the
         * user to Google Play Store if Google Play services
         * is out of date.
         */
        } 
        else 
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(
                            connectionResult.getErrorCode(),
                            (Activity) context,
                            ActivityUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (dialog != null) 
            {
                dialog.show();
            }
        }
    }
}