package com.thesisug.communication;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.xmlparser.SingleEventHandler;
import com.thesisug.ui.EditEvent;
import com.thesisug.ui.ShowEvent;
import com.thesisug.ui.Todo;

public class EventResource {
	private static final String TAG = new String("EventResource");
	private static final String ALL_EVENTS = "/event/all";
	private static final String TODAY_EVENTS = "/event/today";
	private static final String BETWEEN_EVENTS = "/event/between";
	private static final String UPDATE_EVENT = "/event/update";
	private static final String REMOVE_EVENT = "/event/erase";
	
	public void createEvent(String userid, String sessionid, SingleEvent toAdd) {
	}

	private static List<SingleEvent> runHttpGet(String userid,
			String sessionid, final String method,
			final ArrayList<NameValuePair> params) {
		List<SingleEvent> result = new LinkedList<SingleEvent>();
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ userid + method + query);
		// send the request to network
		HttpResponse response = NetworkUtilities
				.sendRequest(newClient, request);
		// if we cannot connect to the server
		if (response.getStatusLine().getStatusCode() != 200) {
			return result;
		}
		try { // parsing XML message
			result = SingleEventHandler
					.parse(response.getEntity().getContent());
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

	/*
	 * return true upon successful POST, false otherwise
	 */
	private static boolean runHttpPost(String userid,
			String sessionid, final String method,
			final ArrayList<NameValuePair> params, final String msgBody) {
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		HttpPost request = new HttpPost(NetworkUtilities.SERVER_URI + "/"
				+ userid + method + query);
		try {
			request.setHeader("Content-Type", "application/xml");
			request.setEntity(new StringEntity(msgBody));
			// send the request to network
			HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
			Log.i(TAG, "Status Code is "+response.getStatusLine().getStatusCode());
			return (response.getStatusLine().getStatusCode() == 204 )? true: false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static Thread updateEvent(final String username,
			final String sessionid, final SingleEvent newEvent, final Handler handler,
			final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = SingleEventHandler.format(newEvent);
				final boolean result = runHttpPost(username, sessionid,
						UPDATE_EVENT, null, body);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((EditEvent) context).finishSave(result);
					}
				});
			}
		};
		// start updating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread removeEvent(final String username,
			final String sessionid, final String eventID, final Handler handler,
			final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {				
				final boolean result = runHttpPost(username, sessionid,
						REMOVE_EVENT, null, eventID);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((ShowEvent) context).finishDeletion(result);
					}
				});
			}
		};
		// start deleting
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	/**
	 * Creates and run background thread to do authentication
	 * 
	 * @param username
	 *            username of the user
	 * @param sessionid
	 *            session token
	 * @param handler
	 *            handler for the thread
	 * @param context
	 *            activity that calls for authentication
	 * @return thread running authentication
	 */
	public static Thread getAllEvent(final String username,
			final String sessionid, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				List<SingleEvent> result = runHttpGet(username, sessionid,
						ALL_EVENTS, null);
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread getEventToday(final String username,
			final String sessionid, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				List<SingleEvent> result = runHttpGet(username, sessionid,
						TODAY_EVENTS, null);
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread getEvent(final String username,
			final String sessionid, final String DateFrom, final String DateTo,
			final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("s", DateFrom));
				params.add(new BasicNameValuePair("e", DateTo));
				List<SingleEvent> result = runHttpGet(username, sessionid,
						BETWEEN_EVENTS, params);
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	private static void sendResult(final List<SingleEvent> result,
			final Handler handler, final Context context) {
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
