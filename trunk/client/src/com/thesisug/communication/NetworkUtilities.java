package com.thesisug.communication;

import java.io.IOException;
 
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.thesisug.Constants;

import android.util.Log;

public class NetworkUtilities {
	private static final String TAG = new String("thesisug - NetworkUtilities");
	private static final int REGISTRATION_TIMEOUT = 10000;
	
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
