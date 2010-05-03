package com.thesisug.communication;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.HttpClient;
import org.jboss.resteasy.client.ClientResponseFailure;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesis.communication.valueobject.LoginReply;
import com.thesisug.ui.Login;

public class Authentication implements LoginResource{
	private static final String TAG = new String("communication.Authentication");
	/**
	 * Creates and run background thread to do authentication
	 * @param username username of the user
	 * @param password password of the user
	 * @param handler handler for the thread
	 * @param context activity that calls for authentication
	 * @return thread running authentication
	 */
	public static Thread signIn(final String username,
	        final String password, final Handler handler, final Context context) {
	        final Runnable runnable = new Runnable() {
	            public void run() {
	                authenticate(username, password, handler, context);
	            }
	        };
	        // start authenticating
	        return startBackgroundThread(runnable);
	    }
	
	private static void authenticate(String username, String password,
	        Handler handler, final Context context) {
		HttpClient client = ClientFactory.getClient();
		try {
			LoginReply result = client.Authenticate(username, password);
			if (result.status == 1) {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "Successful authentication");
				}
				sendResult(true, handler, context);
			} else {
				if (Log.isLoggable(TAG, Log.VERBOSE)) {
					Log.v(TAG, "Unsuccessful authentication");
				}
				sendResult(false, handler, context);
			}			
		} catch (final ClientResponseFailure failure) {
		} catch (final RuntimeException e) {
		} finally {
			sendResult(false, handler, context);
		}
	}
	
	
	/**
	 * Execute runnable on background thread
	 * @param runnable the runnable instance
	 * @return a running thread
	 */
    private static Thread startBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }
    
    /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param result The boolean holding authentication result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendResult(final Boolean result, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((Login) context).showResult(result);
            }
        });
    }
    
    
	public LoginReply Authenticate(String username, String password) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            RssHandler handler = new RssHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getMessages();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 

	}
	
	private class 
}
