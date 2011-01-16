package com.thesisug.communication;

import java.io.IOException;
 
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import com.thesisug.Constants;
import com.thesisug.communication.valueobject.LoginReply;
import com.thesisug.communication.valueobject.TestConnectionReply;
import com.thesisug.communication.xmlparser.TryConnectionReplyHandler;
import com.thesisug.ui.Login;
import com.thesisug.ui.Preferences;
import com.thesisug.ui.SystemStatus;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class NetworkUtilities {
	private static final String TAG = new String("thesisug - NetworkUtilities");
	private static final int REGISTRATION_TIMEOUT = 10000;
	public static final String BASE_TRY_CONNECTION="/tryConn/";
	
	//Dati del file com.thesisug.Constants.java
	public static String SERVER_URI = Constants.DEFAULT_URL+":"+Constants.DEFAULT_HTTP_PORT+"/"+Constants.PROGRAM_NAME;
	

	//Padova
	//public static String SERVER_URI = "http://serverpd.dyndns.org:8080/ephemere-0.0.11";
	
	//Genova
	//public static String SERVER_URI = "http://zelda.openlab-dist.org:8080/ephemere-0.0.4";
	
	//Locale
	//public static String SERVER_URI = "http://10.0.2.2:8080/ephemere-0.0.4";
	
	public static String actUser="";
	public static String actPass="";

	 
	public static DefaultHttpClient createClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		final HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
		ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
		return client;
	}
	
	/**
	 * Creates and run background thread to try the connection
	 * @param handler handler for the thread
	 * @param context activity that calls for authentication
	 * @return thread running authentication
	 */
	public static Thread tryConnection(final String serverURI,final boolean uriComplete,
			final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String URI_to_check="";
				if(!uriComplete){
					URI_to_check="http://"+serverURI+":8080/"+Constants.PROGRAM_NAME;
				}else{
					URI_to_check=serverURI;
				}
				TestConnectionReply result = tryConnectionThreadBody(URI_to_check);
				result.serverURI=URI_to_check;
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
	
	/**
	 * This method try if the connection is available to the server
	 * @return
	 */
	public static TestConnectionReply tryConnectionThreadBody(String _serverURI){
		
		TestConnectionReply result=new TestConnectionReply();
		
		Log.i(TAG,"Starting try connection");
    	DefaultHttpClient newClient = NetworkUtilities.createClient();
    	// provide username and password in correct param
    	Log.i(TAG,"Client created, creating request to check "+_serverURI);
    	String url=_serverURI+BASE_TRY_CONNECTION;
    	
        HttpGet request = new HttpGet(url);
        request.addHeader("Cookie", "uri="+_serverURI);
        Log.i(TAG,"Sending tryConn request to " + url);
        // send the request to network
        HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
        Log.i(TAG,"TryConn request sent");		
        Log.i(TAG,"Status: "+response.getStatusLine());

        //Set the requested URI
    	result.serverURI=_serverURI;
        
        if (response == null || response.getStatusLine().getStatusCode()==404) {
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
	
	public static void saveActualCredentials(String user, String pass){		
		actUser=user;
		actPass=pass;		
	}
	
	public static HttpResponse sendRequest (HttpClient client, HttpUriRequest request){
		HttpResponse response = null;
		try {
			Log.i(TAG, "Before send request, method("+request.getMethod()+") to "+request.getURI());
			response = client.execute(request);			
			Log.i(TAG, "Successfully send request "+response.getStatusLine().getStatusCode());
			return response;
		} catch (ClientProtocolException e) {
			Log.e(TAG, "ClientProtocol exception catched");
			e.printStackTrace();
			return response;
		} catch (IOException e) {
			Log.e(TAG, "IOException catched");
			e.printStackTrace();
			//return SERVER NOT FOUND status if there's error during connection
			return new BasicHttpResponse(new ProtocolVersion("1.1", 0 , 0), 404,"");
		}
	}
	
	/**
	 * Execute runnable on background thread
	 * @param runnable the runnable instance
	 * @return a running thread
	 */
    public static Thread startBackgroundThread(final Runnable runnable) {
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
    
    
    public static boolean changeServerURI(String serverURI){
    	
    	//Use this for using https (change protocol and port)
		//NetworkUtilities.SERVER_URI="http://"+result.serverURI+":"+Constants.DEFAULT_HTTPS_PORT+"/"+Constants.PROGRAM_NAME;

		//Use this for using normal http (change protocol and port)
		NetworkUtilities.SERVER_URI="http://"+serverURI+":"+Constants.DEFAULT_HTTP_PORT+"/"+Constants.PROGRAM_NAME;
		
		Log.i(TAG,"NetworkUtilities.SERVER_URI changed to: "+NetworkUtilities.SERVER_URI);
		
		return true;
    	
    }
    
}
