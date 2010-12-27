package com.thesisug.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Handles account authentication. Initiates the Authenticator.
 */
public class AuthenticationService extends Service {
	private static final String TAG = "thesisug - AuthenticationService";
	private Authenticator authenticator;

	@Override
	public void onCreate() {
		if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.v(TAG, "Authentication Service started.");
		authenticator = new Authenticator(this);
	}

	@Override
	public void onDestroy() {
		if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.v(TAG, "Authentication Service stopped.");
	}

	@Override
	public IBinder onBind(Intent intent) {
		if (Log.isLoggable(TAG, Log.VERBOSE))
			Log.v(TAG, "Binding from Authentication Service.");
		return authenticator.getIBinder();
	}
}
