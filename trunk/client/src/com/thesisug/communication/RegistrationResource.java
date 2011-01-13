package com.thesisug.communication;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("fn", ""+firstname));
				params.add(new BasicNameValuePair("ln", ""+lastname));
				params.add(new BasicNameValuePair("e", ""+email));
				params.add(new BasicNameValuePair("u", ""+username));
				RegistrationReply result = Registration(params, password);
				Log.v(TAG, (result.status==2)? "Successful registered": "Unsuccessful registered");
			}
		};
		// start registration
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	public static RegistrationReply Registration(final ArrayList<NameValuePair> params, String password) {
	       
    	RegistrationReply result = new RegistrationReply();
    	DefaultHttpClient newClient = NetworkUtilities.createClient();
    	
    	String query = (params == null) ? "" : URLEncodedUtils.format(params, "UTF-8");
    	String url=NetworkUtilities.SERVER_URI+BASE_REGISTER+"?"+query;
    	Log.d(TAG,url);
        HttpGet request = new HttpGet(url);
        request.addHeader("Cookie", "p="+password);
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
