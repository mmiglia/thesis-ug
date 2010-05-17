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

import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.xmlparser.SingleEventHandler;
import com.thesisug.ui.Todo;

public class EventResource {	
	private static final String TAG = new String("EventResource");
	private static final String ALL_EVENTS = "/event/all";
	private static final String TODAY_EVENTS = "/event/today";
	private static final String BETWEEN_EVENTS = "/event/between";
	
	public void createEvent(String userid,  String sessionid, SingleEvent toAdd) {}
	private static List<SingleEvent> getAllEvents(String userid, String sessionid) {
		return runHttpGet(userid, sessionid, ALL_EVENTS, null);
	}
	public List<SingleEvent> getEventToday(String userid, String sessionid) {
		return runHttpGet(userid, sessionid, TODAY_EVENTS, null);
	}
	public List<SingleEvent> getEvent(String DateFrom, String DateTo, String userid, String sessionid) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("s", DateFrom));
		params.add(new BasicNameValuePair("e", DateTo));
		return runHttpGet(userid, sessionid, BETWEEN_EVENTS, params);
	}	
	public void updateEvent(String userid, String sessionid, SingleEvent oldEvent, SingleEvent newEvent) {}	
	public void removeEvent(String eventID,	String userid,  String sessionid) {}

	private static List<SingleEvent> runHttpGet(String userid, String sessionid, final String method, final ArrayList<NameValuePair> params) {
		List<SingleEvent> result = new LinkedList<SingleEvent>();
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null)? "": URLEncodedUtils.format(params, "UTF-8");
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI+"/"+userid+method+query);
		// send the request to network
		HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
		if (response == null) {
			return result;
		}
		try { // parsing XML message
			result = SingleEventHandler.parse(response.getEntity().getContent());
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
 * @param sessionid session token
 * @param handler handler for the thread
 * @param context activity that calls for authentication
 * @return thread running authentication
 */
public static Thread getAllEvent(final String username, final String sessionid,
		final Handler handler, final Context context) {
	final Runnable runnable = new Runnable() {
		public void run() {
			List<SingleEvent> result = getAllEvents(username, sessionid);			
			sendResult(result, handler, context);
		}
	};
	// start authenticating
	return NetworkUtilities.startBackgroundThread(runnable);
}

public static Thread updateEvent(final String username, final String sessionid,
		final Handler handler, final Context context) {
	final Runnable runnable = new Runnable() {
		public void run() {
			List<SingleEvent> result = getAllEvents(username, sessionid);			
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
 * @param result The boolean holding authentication result
 * @param handler The main UI thread's handler instance.
 * @param context The caller Activity's context.
 */
private static void sendResult(final List<SingleEvent> result, final Handler handler,
    final Context context) {
    if (handler == null || context == null) {
        return;
    }
    handler.post(new Runnable() {
        public void run() {
            ((Todo) context).afterDataLoaded(result);
        }
    });
}
}
