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

import com.thesisug.communication.valueobject.QueryReply;
import com.thesisug.communication.xmlparser.QueryReplyHandler;
import com.thesisug.ui.Input;

public class InputResource {
	private static final String TAG = new String("thesisug - InputResource");
	private static final String INPUT = "/input";
	
	private static boolean runHttpGet(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		QueryReply result = new QueryReply();
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method + "?" + query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
		// send the request to network
		HttpResponse response = NetworkUtilities
				.sendRequest(newClient, request);
		// if we cannot connect to the server
		if (response.getStatusLine().getStatusCode() != 200) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return false;
		}
		try {
			result = QueryReplyHandler.parse(response.getEntity().getContent());
			return result.status;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Thread input(final String toParse, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("q", ""+toParse));
				boolean result = runHttpGet(INPUT, params, context);
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	 /**
     * Sends the authentication response from server back to the caller main UI
     * thread through its handler.
     * 
     * @param result The boolean holding parsing result
     * @param handler The main UI thread's handler instance.
     * @param context The caller Activity's context.
     */
    private static void sendResult(final boolean result, final Handler handler,
        final Context context) {
    	Log.i(TAG, "result is "+result);
        if (handler == null || context == null) {
            return;
        }
        handler.post(new Runnable() {
            public void run() {
                ((Input) context).showResult(result);
            }
        });
    }
}
