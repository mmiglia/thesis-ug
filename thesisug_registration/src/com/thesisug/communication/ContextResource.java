package com.thesisug.communication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.xmlparser.HintHandler;
import com.thesisug.notification.TaskNotification;
import com.thesisug.ui.Map;


public class ContextResource{
	private static final String TAG = new String("thesisug - ContextResource");
	private static final String LOCATION_ALL = "/location/all";
	private static final String LOCATION_SINGLE = "/location/single";
	
	private static List<Hint> runHttpGet(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<Hint> result = new LinkedList<Hint>();
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
			return result;
		}
		try { // parsing XML message
			result = HintHandler.parse(response.getEntity().getContent());
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

	public static Thread checkLocationAll(final float lat, final float lon, final int distance, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("lat", ""+lat));
				params.add(new BasicNameValuePair("lon", ""+lon));
				params.add(new BasicNameValuePair("dist", ""+distance));
				List<Hint> result = runHttpGet(LOCATION_ALL, params, context);
				sendResult(result, "do some tasks", handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	public static Thread checkLocationSingle(final String sentence, final float lat, final float lon, final int distance, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("q", sentence));
				params.add(new BasicNameValuePair("lat", ""+lat));
				params.add(new BasicNameValuePair("lon", ""+lon));
				params.add(new BasicNameValuePair("dist", ""+distance));
				List<Hint> result = runHttpGet(LOCATION_SINGLE, params, context);
				sendResult(result, sentence, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	private static void sendResult(final List<Hint> result, final String sentence,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		handler.post(new Runnable() {
			public void run() {
				if (context instanceof TaskNotification) ((TaskNotification) context).afterHintsAcquired(sentence, result);
				else ((Map) context).afterHintsAcquired(result);
			}
		});
	}
}

