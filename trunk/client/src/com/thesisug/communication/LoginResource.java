package com.thesisug.communication;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesisug.Constants;
import com.thesisug.communication.valueobject.LoginReply;
import com.thesisug.communication.valueobject.TestConnectionReply;
import com.thesisug.communication.xmlparser.LoginReplyHandler;
import com.thesisug.communication.xmlparser.TryConnectionReplyHandler;
import com.thesisug.ui.Login;
import com.thesisug.ui.Preferences;
import com.thesisug.ui.SystemStatus;
  

public class LoginResource{
	private static final String TAG = new String("thesisug - LoginResource");
	public static final String BASE_LOGIN = "/login";
	public static final String BASE_LOGOUT = "/logout";
	public static final String BASE_TRY_CONNECTION="/tryConn/";
	
	public static LoginReply Authenticate(String username, String password) {
       
        	LoginReply result = new LoginReply();
        	DefaultHttpClient newClient = NetworkUtilities.createClient();

        	// provide username and password in correct param
        	String url=NetworkUtilities.SERVER_URI+"/"+username+BASE_LOGIN;
        	Log.d(TAG,url);
            HttpGet request = new HttpGet(url);
            request.addHeader("Cookie", "p="+password);
            // send the request to network
            Log.d(TAG,"Start autenticate request");
            HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
            Log.d(TAG,"Autenticate request returned "+response);
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
	 * This method is used to logout the user.
	 * On the server it sets, in the User table, the sessionKey to '' and the active field to 0
	 * @param username
	 * 
	 */
	public static void Logout(String username){
		Log.i(TAG,"Starting Logout");
    	DefaultHttpClient newClient = NetworkUtilities.createClient();
    	// provide username and password in correct param
    	Log.i(TAG,"Client created, creating request");
        HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI+"/"+username+BASE_LOGOUT);
        Log.i(TAG,"Sending logout request");
        // send the request to network
        HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
        Log.i(TAG,"Logout request sent");
	}
	
	/**
	 * This method try if the connection is available to the server
	 * @return
	 */
	public static TestConnectionReply tryConnectionThreadBody(String serverURI){
		
		TestConnectionReply result=new TestConnectionReply();
		
		Log.i(TAG,"Starting try connection");
    	DefaultHttpClient newClient = NetworkUtilities.createClient();
    	// provide username and password in correct param
    	Log.i(TAG,"Client created, creating request");
    	String url="http://"+serverURI+":8080/"+Constants.PROGRAM_NAME+BASE_TRY_CONNECTION;
    	
        HttpGet request = new HttpGet(url);
        request.addHeader("Cookie", "uri="+serverURI);
        Log.i(TAG,"Sending tryConn request to " + url);
        // send the request to network
        HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
        Log.i(TAG,"TryConn request sent");		
        Log.i(TAG,"Status: "+response.getStatusLine());
        
        if (response == null || response.getStatusLine().getStatusCode()==404) {
        	result.serverURI=serverURI;
        	result.status=0;
        	return result;
        }     


        
        try { // parsing XML message
			result = TryConnectionReplyHandler.parse(response.getEntity().getContent());
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
				Log.v(TAG, (result.status == 1)? "Successful authentication": "Unsuccessful authentication");
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	    
	
	/**
	 * Creates and run background thread to do logout operation
	 * @param username username of the user
	 * @param password password of the user
	 * @param handler handler for the thread
	 * @param context activity that calls for authentication
	 * @return thread running authentication
	 */
	public static Thread logout(final String username) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Logout(username);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}	
	
	/**
	 * Creates and run background thread to try the connection
	 * @param username username of the user
	 * @param password password of the user
	 * @param handler handler for the thread
	 * @param context activity that calls for authentication
	 * @return thread running authentication
	 */
	public static Thread tryConnection(final String serverURI,
			final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				
				TestConnectionReply result = tryConnectionThreadBody(serverURI);
				Log.v(TAG, (result.status == 1)? "Connection available:YES": "Connection available:NO");
				sendTryConnResult(result, handler, context);
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
    private static void sendResult(final LoginReply result, final Handler handler,
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
    
    /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param result The boolean holding authentication result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendTryConnResult(final TestConnectionReply result, final Handler handler,
        final Context context) {
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
            	if(context instanceof Preferences){
                ((Preferences) context).changeServerURI(result);
            	}
            	if(context instanceof SystemStatus){
                    ((SystemStatus) context).tryConnection(result);
                }
            }
        });
    }
}
