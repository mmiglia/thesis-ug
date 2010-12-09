
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
import org.xml.sax.SAXException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesisug.communication.valueobject.SingleTask;
import com.thesisug.communication.xmlparser.SingleTaskHandler;
import com.thesisug.notification.TaskNotification;
import com.thesisug.ui.EditTask;
import com.thesisug.ui.ShowTask;
import com.thesisug.ui.Todo;

public class TaskResource {
	private static final String TAG = new String("TaskResource");
	private static final String ALL_TASKS = "/task/all";
	private static final String FIRST_TASKS = "/task/first";
	private static final String UPDATE_TASK = "/task/update";
	private static final String REMOVE_TASK = "/task/erase";
	private static final String CREATE_TASK = "/task/add";

	private static List<SingleTask> runHttpGet(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<SingleTask> result = new LinkedList<SingleTask>();
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
			return null; // return null if there's problem with connection
		}
		try { // parsing XML message
			result = SingleTaskHandler
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


	public static Thread createTask(final SingleTask toAdd, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = SingleTaskHandler.format(toAdd);
				Log.i(TAG,body);
				final boolean result = runHttpPost(CREATE_TASK, null, body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((EditTask) context).finishSave(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread updateTask(final SingleTask newTask, final Handler handler,
			final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = SingleTaskHandler.format(newTask);
				final boolean result = runHttpPost(UPDATE_TASK, null, body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((EditTask) context).finishSave(result);
					}
				});
			}
		};
		// start updating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread removeTask(final String taskID, final Handler handler,
			final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {				
				final boolean result = runHttpPost(REMOVE_TASK, null, taskID, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((ShowTask) context).finishDeletion(result);
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
	public static Thread getAllTask(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				List<SingleTask> result = runHttpGet(ALL_TASKS, null, context);
				if(result!=null){
					for (SingleTask o : result){
						Log.i(TAG, "Task id retreived:"+o.groupId);
					}
				}
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	public static Thread getFirstTask(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {				
				List<SingleTask> result = runHttpGet(FIRST_TASKS, null, context);
				if(result!=null){
					for (SingleTask o : result){
						Log.i(TAG, "Task id retreived:"+o.groupId);
					}
				}
				sendResult(result, handler, context);
			}
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	private static void sendResult(final List<SingleTask> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		handler.post(new Runnable() {
			public void run() {
				if (context instanceof Todo)
				 ((Todo)context).afterTaskLoaded(result);
				else ((TaskNotification)context).afterTaskLoaded(result);
			}
		});
	}
}

