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

import android.util.Log;

public class NetworkUtilities {
	private static final String TAG = new String("NetworkUtilities");
	private static final int REGISTRATION_TIMEOUT = 30000;
	public static final String SERVER_URI = "http://10.0.2.2:8080/ephemere";
	
	public static DefaultHttpClient createClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		final HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
		ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
		return client;
	}
	
	public static HttpResponse sendRequest (HttpClient client, HttpUriRequest request){
		HttpResponse response = null;
		try {
			Log.i(TAG, "Before send request");
			response = client.execute(request);			
			Log.i(TAG, "Successfully send request");
			return response;
		} catch (ClientProtocolException e) {
			Log.i(TAG, "ClientProtocol exception catched");
			e.printStackTrace();
			return response;
		} catch (IOException e) {
			Log.i(TAG, "IOException catched");
			e.printStackTrace();
			//return SERVER NOT FOUND status if there's error during connection
			return new BasicHttpResponse(new ProtocolVersion("1.1", 0 , 0), 404,"");
		} finally {
			Log.i(TAG, "finally");
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
}
