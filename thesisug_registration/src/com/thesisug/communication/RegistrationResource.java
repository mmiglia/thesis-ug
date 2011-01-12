package com.thesisug.communication;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesisug.communication.valueobject.RegistrationReply;
import com.thesisug.communication.xmlparser.RegistrationReplyHandler;

public class RegistrationResource {
	private static final String TAG = new String("thesisug - RegistrationResource");
	public static final String BASE_REGISTER = "/registration";
	public static final String BASE_TRY_CONNECTION="/tryConn/";
	
	/**
	 * Creates and run background thread to do registration
	 * @param firstname first name of the new user
	 * @param lastname last name of the new user
	 * @param email e-mail of the new user
	 * @param username username of the user
	 * @param password password of the user
	 * @return thread running authentication
	 */
	public static Thread register(final String firstname, final String lastname, final String email,
			final String username, final String password) {
		final Runnable runnable = new Runnable() {
			public void run() {
				RegistrationReply result = Registration(firstname, lastname, email, username, password);
				Log.v(TAG, (result.status==2)? "Successful registered": "Unsuccessful registered");
			}
		};
		// start registration
		return NetworkUtilities.startBackgroundThread(runnable);
	}
//	public static Thread signIn(final String username, final String password,
//	final Handler handler, final Context context) {
//final Runnable runnable = new Runnable() {
//	public void run() {
//		LoginReply result = Authenticate(username, password);
//		Log.v(TAG, (result.status == 1)? "Successful authentication": "Unsuccessful authentication");
//		sendResult(result, handler, context);
//	}
//};
//// start authenticating
//return NetworkUtilities.startBackgroundThread(runnable);
//}
	
	public static RegistrationReply Registration(String fn, String ln, String e, String u, String p) {
	       
    	RegistrationReply result = new RegistrationReply();
    	DefaultHttpClient newClient = NetworkUtilities.createClient();

    	// provide all variables in correct param
    	String url=NetworkUtilities.SERVER_URI+BASE_REGISTER;
    	Log.d(TAG,url);
        HttpGet request = new HttpGet(url);
        request.addHeader("Cookie", "fn="+fn);
        request.addHeader("Cookie", "ln="+ln);
        request.addHeader("Cookie", "e="+e);
        request.addHeader("Cookie", "u="+u);
        request.addHeader("Cookie", "p="+p);
        // send the request to network
        Log.d(TAG,"Start registration request");
        HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
        Log.d(TAG,"Registration request returned "+response);
        if (response == null) {
        	return result;
        }     
        
        //Try if server is on-line
        if(response.getStatusLine().getStatusCode()==404){
        	//Server unavailable, display it
        	result.status=404;
        	return result;
        }
        
        try { // parsing XML message
        	result = RegistrationReplyHandler.parse(response.getEntity().getContent());
			return result;
		} catch (IllegalStateException ex) {
			Log.i(TAG, "Illegal State");
			ex.printStackTrace();
			return result;
		} catch (IOException ex) {
			Log.i(TAG, "IOException");
			ex.printStackTrace();
			return result;
		} catch (SAXException ex) {
			Log.i(TAG, "SAXException");
			ex.printStackTrace();
			return result;
		}
	}
}
