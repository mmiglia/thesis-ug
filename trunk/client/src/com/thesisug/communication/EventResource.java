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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesisug.communication.valueobject.SingleEvent;
import com.thesisug.communication.xmlparser.SingleEventHandler;
import com.thesisug.authenticator.Authenticator;
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
	private static final String CREATE_EVENT = "/event/add";
	
	private static List<SingleEvent> runHttpGet(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<SingleEvent> result = new LinkedList<SingleEvent>();
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method + query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
		// send the request to network
		HttpResponse response = NetworkUtilities
				.sendRequest(newClient, request);
		// if we cannot connect to the server
		if (response.getStatusLine().getStatusCode() != 200) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return null; // return null if there's a problem with connection
		}
		try { // parsing XML message
			Log.d(TAG,"Starting parse");
			
			result = SingleEventHandler.parse(response.getEntity().getContent());
			
			Log.d(TAG,"Parse finish, " + result.size() +" event parsed");
			
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
	private static boolean runHttpPost(final String method,
			final ArrayList<NameValuePair> params, final String msgBody, Context c) {
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpPost request = new HttpPost(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method + query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
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


	public static Thread createEvent(final SingleEvent toAdd, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = SingleEventHandler.format(toAdd);
				final boolean result = runHttpPost(CREATE_EVENT, null, body, context);
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
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread updateEvent(final SingleEvent newEvent, final Handler handler,
			final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG,"Request to update event with id="+newEvent.eventID);
				String body = SingleEventHandler.format(newEvent);
				Log.i(TAG,"End formatting");
				final boolean result = runHttpPost(UPDATE_EVENT, null, body, context);
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

	public static Thread removeEvent(final String eventID, final Handler handler,
			final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {				
				final boolean result = runHttpPost(REMOVE_EVENT, null, eventID, context);
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
	public static Thread getAllEvent(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				List<SingleEvent> result = runHttpGet(ALL_EVENTS, null, context);
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread getEventToday(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				List<SingleEvent> result = runHttpGet(TODAY_EVENTS, null, context);
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread getEvent(final String DateFrom, final String DateTo,
			final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("s", DateFrom));
				params.add(new BasicNameValuePair("e", DateTo));
				List<SingleEvent> result = runHttpGet(BETWEEN_EVENTS, params, context);
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
				((Todo) context).afterEventLoaded(result);
			}
		});
	}
}
