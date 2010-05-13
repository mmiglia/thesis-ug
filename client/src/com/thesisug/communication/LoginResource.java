package com.thesisug.communication;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesisug.communication.valueobject.LoginReply;
import com.thesisug.communication.xmlparser.LoginReplyHandler;
import com.thesisug.ui.Login;

public class LoginResource{
	private static final String TAG = new String("LoginResource");
	public static final String BASE_LOGIN = "/login";
	  
	public static LoginReply Authenticate(String username, String password) {
       
        	LoginReply result = new LoginReply();
        	DefaultHttpClient newClient = NetworkUtilities.createClient();
        	// provide username and password in correct param
            HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI+BASE_LOGIN+"/"+username+"");
            request.addHeader("Cookie", "p="+password);
            // send the request to network
            HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
            if (response == null) {
            	return result;
            }
            try { // parsing XML message
				result = LoginReplyHandler.parse(response.getEntity().getContent());
				return result;
			} catch (IllegalStateException e) {
				Log.i(TAG, "Illegal State");
				e.printStackTrace();
				return result;
			} catch (IOException e) {
				Log.i(TAG, "IOException");
				e.printStackTrace();
				return result;
			} catch (SAXException e) {
				Log.i(TAG, "SAXException");
				e.printStackTrace();
				return result;
			}
	}
	
	/**
	 * Creates and run background thread to do authentication
	 * @param username username of the user
	 * @param password password of the user
	 * @param handler handler for the thread
	 * @param context activity that calls for authentication
	 * @return thread running authentication
	 */
	public static Thread signIn(final String username, final String password,
			final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				LoginReply result = Authenticate(username, password);
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
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
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
}
