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

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.thesisug.communication.valueobject.PlaceClient;
import com.thesisug.communication.xmlparser.AssertionsHandler;
import com.thesisug.communication.xmlparser.PlacesHandler;
import com.thesisug.ui.Create_new_place;
import com.thesisug.ui.PrivatePlaces;
import com.thesisug.ui.PublicPlaces;
import com.thesisug.ui.SelectCategory;


public class PlacesResource {
	
private static final String TAG = new String("thesisug - PlaceResource");
	
	private static final String GET_PRIVATE_PLACES = "/places/privatePlaces";
	private static final String GET_PUBLIC_PLACES = "/places/publicPlacesVoted";
	private static final String ADD_PRIVATE_PLACE = "/places/addPrivatePlace";
	
	private static List<PlaceClient> runHttpGetPrivatePlaces(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<PlaceClient> result = new LinkedList<PlaceClient>();
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
		if (!HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode())) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return null; // return null if there's problem with connection
		}
		try { // parsing XML message
			
			
			result = PlacesHandler.parseUserPrivatePlaces(response.getEntity().getContent());
			
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
			return HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode());

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	private static List<PlaceClient> runHttpGetPublicPlaces(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<PlaceClient> result = new LinkedList<PlaceClient>();
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method + query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
		// send the request to network
		HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
		
		// if we cannot connect to the server
		if (!HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode())) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return null; // return null if there's problem with connection
		}
		try { // parsing XML message
			
			
			result = PlacesHandler.parseUserPublicPlaces(response.getEntity().getContent());
			
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
	
	
	public static Thread createPrivatePlace(final PlaceClient toAdd, final Handler handler, final Context context) 
	{
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = PlacesHandler.formatPlaceClient(toAdd);
				Log.i(TAG,body);
				final boolean result = runHttpPost(ADD_PRIVATE_PLACE, null,body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						
							
							((Create_new_place) context).finishSaveToAdd(result,toAdd);
							
							 
							 
						}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	
	public static Thread getPrivatePlaces(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request private places for the user");
				List<PlaceClient> result = runHttpGetPrivatePlaces(GET_PRIVATE_PLACES, null, context);	
				sendResult_PrivatePlaces(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	public static Thread getPublicPlaces(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request public places for the user");
				List<PlaceClient> result = runHttpGetPublicPlaces(GET_PUBLIC_PLACES, null, context);	
				sendResult_PublicPlaces(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	private static void sendResult_PrivatePlaces(final List<PlaceClient> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {
				
					((PrivatePlaces)context).afterPrivatePlacesLoaded(result);
					
				
			}
		});
	}
	private static void sendResult_PublicPlaces(final List<PlaceClient> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {
				
					((PublicPlaces)context).afterPublicPlacesLoaded(result);
					
				
			}
		});
	}
	

}
