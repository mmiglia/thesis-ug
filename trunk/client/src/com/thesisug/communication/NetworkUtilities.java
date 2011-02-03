package com.thesisug.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
 
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.xml.sax.SAXException;

import sun.util.logging.resources.logging;

import com.thesisug.Constants;
import com.thesisug.communication.valueobject.LoginReply;
import com.thesisug.communication.valueobject.RegistrationReply;
import com.thesisug.communication.valueobject.TestConnectionReply;
import com.thesisug.communication.valueobject.VersionReply;
import com.thesisug.communication.xmlparser.RegistrationReplyHandler;
import com.thesisug.communication.xmlparser.TryConnectionReplyHandler;
import com.thesisug.communication.xmlparser.VersionReplyHandler;
import com.thesisug.ui.Login;
import com.thesisug.ui.Preferences;
import com.thesisug.ui.SystemStatus;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class NetworkUtilities {
	private static final String TAG = new String("thesisug - NetworkUtilities");
	private static final int REGISTRATION_TIMEOUT = 10000;
	public static final String BASE_TRY_CONNECTION="/tryConn/";
	public static final String BASE_VERSION_CHECK="/checkVer";
	
	//Dati del file com.thesisug.Constants.java
	public static String SERVER_URI = Constants.DEFAULT_URL+":"+Constants.DEFAULT_HTTP_PORT+"/"+Constants.PROGRAM_NAME;
	
	// this variable indicate if server and client are compatible
	// false = client version is compatible with server version but not vice versa
	// true = client version is compatible with server version and vice versa
	public static boolean VERSION_OK = false;

	//Padova
	//public static String SERVER_URI = "http://serverpd.dyndns.org:8080/ephemere-0.0.11";
	
	//Genova
	//public static String SERVER_URI = "http://zelda.openlab-dist.org:8080/ephemere-0.0.4";
	
	//Locale
	//public static String SERVER_URI = "http://10.0.2.2:8080/ephemere-0.0.4";
	
	public static String actUser="";
	public static String actPass="";
	
	
	public static boolean check(String input, String regex) {
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(input);
    	if (matcher.matches())
    		return true;
    	else
    		return false;
    }
	 
	public static DefaultHttpClient createClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		final HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
		ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
		return client;
	}
	
	public static Thread checkVersion(final String serverURI, final String clientVersion, 
			final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("ver", clientVersion));
				VersionReply result = checkVersionThreadBody(serverURI, params);
				sendResult(result, handler);
				
				Log.v(TAG, (result.status == 2)? "Client version compatible with server": "client version not compatible with server");

				//sendCheckVersionResult(result,handler,context);
			}
		};
		//return NetworkUtilities.startBackgroundThread(runnable);
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	

	
	public static VersionReply checkVersionThreadBody (final String serverURI, final ArrayList<NameValuePair> params) {
		// eventualmente fare il test della connessione prima!
		//TestConnectionReply result=new TestConnectionReply();
		NetworkUtilities.VERSION_OK = false;
		
		VersionReply result = new VersionReply();
		
		Log.i(TAG,"Verifying client/server version compatibility");
    	DefaultHttpClient newClient = NetworkUtilities.createClient();
    	// provide username and password in correct param
    	Log.i(TAG,"Client created, creating request to check version of --> "+serverURI+" <--");
    	String query = (params == null) ? "" : URLEncodedUtils.format(params, "UTF-8");
    	String url;
    	if (check(serverURI, "http\\://+[a-zA-Z0-9\\-\\.]+[\\.[a-zA-Z]{2,4}]*+\\S*")) {
    		url = serverURI+BASE_VERSION_CHECK+"?"+query;
    	}
    	else {
    		url="http://"+serverURI+":"+Constants.DEFAULT_HTTP_PORT+"/"+Constants.PROGRAM_NAME+BASE_VERSION_CHECK+"?"+query;
    	}
    	Log.d(TAG,url);
    	HttpGet request = new HttpGet(url);
    	Log.d(TAG,"Start check version request to "+url);
    	HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
    	Log.d(TAG,"Version check request returned: "+response);
    	if (response == null) {
        	return result;
        }
    	
    	//Try if server is on-line
        if(response.getStatusLine().getStatusCode()==404){
        	//Server unavailable, display it
        	Log.i(TAG, "Resource not found.");
        	result.status=404;
        	return result;
        }
        
        try { // parsing XML message
        	result = VersionReplyHandler.parse(response.getEntity().getContent());        	
        	int minServerVer = Integer.parseInt(Constants.MIN_SERVER_VER.replace(".", ""));
        	if (Integer.parseInt(result.serverVersion.replace(".", "")) >= minServerVer ) {
        		// versione server ok
        		result.versionOk = true;
        		result.serverURI = serverURI;
        	}
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
	
	/**
     * Sends the Version verification response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param result The boolean holding VersionReply result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendResult(final VersionReply result, final Handler handler) {
    	Message msg = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putInt("status", result.status);
        b.putString("serverVersion", result.serverVersion);
        b.putBoolean("versionOk", result.versionOk);
        b.putString("serverURI", result.serverURI);
        msg.setData(b);
        handler.sendMessage(msg);
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
				TestConnectionReply result = tryConnectionThreadBody(serverURI);
				result.serverURI=serverURI;
				Log.v(TAG, (result.status == 1)? "Connection available: YES": "Connection available: NO");
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
	public static TestConnectionReply tryConnectionThreadBody(String serverURI){
		
		Log.d(TAG, "tryConnectionThreadBody("+serverURI+")");
		TestConnectionReply result=new TestConnectionReply();
		
		Log.i(TAG,"Starting try connection");
    	DefaultHttpClient newClient = NetworkUtilities.createClient();
    	// provide username and password in correct param
    	Log.i(TAG,"Client created, creating request to check --> "+serverURI+" <--");
    	String url="http://"+serverURI+":"+Constants.DEFAULT_HTTP_PORT+"/"+Constants.PROGRAM_NAME+BASE_TRY_CONNECTION;
    	
        HttpGet request = new HttpGet(url);
        request.addHeader("Cookie", "uri="+serverURI);
        Log.i(TAG,"Sending tryConn request to " + url);
        // send the request to network
        HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
        Log.i(TAG,"TryConn request sent");		
        Log.i(TAG,"Status: "+response.getStatusLine());

        //Set the requested URI
    	result.serverURI=serverURI;
        
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
		
		// EFFETTUARE LA VERIFICA DELLA VERSIONE QUA!!!
		// E RITORNARE UN CAMPO NEL TESTCONNECTIONREPLY CON IL RISULTATO DELLA VERIFICA VERSIONE
		// boolean -> false (client version compatibile con server version
		// boolean -> true (server version compatibile con client version
//		if (result.status == 1) {
//			//effettuare il test versione e mettere la variabile sull'oggetto TestConnectionReply
//			NetworkUtilities.checkVersion(serverURI, Constants.VERSION, handler, NetworkUtilities.this);
//		}
        
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
			Log.i(TAG, "Successfully send request , response status code:"+response.getStatusLine().getStatusCode());
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
