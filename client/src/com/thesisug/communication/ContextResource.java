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

import com.thesisug.caching.Area;
import com.thesisug.caching.CachingDb;
import com.thesisug.caching.CachingDbManager;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.xmlparser.HintHandler;
import com.thesisug.notification.TaskNotification;


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
		if (!HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode())){
			Log.e(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return null;
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

	/**
	 * Used only in MapView 
	 * @param lat
	 * @param lon
	 * @param distance
	 * @param handler
	 * @param context
	 * @return
	 */
	/*
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
	*/
	public static Thread checkLocationSingle(final String sentence, final int priority, final float lat, final float lon, final int distance, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() 
		{
			public void run() 
			{
				//Logica:
				//Quest'area l'ho già cachata per questo task?
				//Si -> cerca in cache
				//No -> la metto in cache
				//Sovrapposto -> dammi area che le ingloba e la cacho tutta

				List<Hint> result;
				
				Area area = CachingDbManager.checkArea(new Area(lat,lon,distance),sentence);
				
				
				switch(area.checkResult)
				{
				case CachingDb.AREA_IN:
					result = CachingDbManager.searchLocalBuisnessInCache(sentence, lat, lon, distance);
					sendResultSingleHint(result, sentence, priority, handler, context,true);
					break;
				case CachingDb.AREA_OUT:
					//If cache doesn't contain records query server
					Log.d(TAG,Integer.toString((int)Math.ceil(area.rad)));
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("q", sentence));
					params.add(new BasicNameValuePair("lat", ""+area.lat));
					params.add(new BasicNameValuePair("lon", ""+area.lng));
					params.add(new BasicNameValuePair("dist", ""+(int)Math.ceil(area.rad)));
					result = runHttpGet(LOCATION_SINGLE, params, context);
					sendResultSingleHint(result, sentence, priority, handler, context,false);
					break;
				}
				
				/*
				Log.d(TAG,"Checking hints in cache for " + sentence +".");
				//First check for results in local cache
				result = CachingDbManager.searchLocalBuisnessInCache(sentence, lat, lon, distance);
				
				if(result.size()==0)
				{
					Log.d(TAG,"No hints in cache for "+sentence+", query server!");
					//If cache doesn't contain records query server
					ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("q", sentence));
					params.add(new BasicNameValuePair("lat", ""+lat));
					params.add(new BasicNameValuePair("lon", ""+lon));
					params.add(new BasicNameValuePair("dist", ""+distance));
					result = runHttpGet(LOCATION_SINGLE, params, context);
					
				}
				sendResultSingleHint(result, sentence, priority, handler, context,fromCache);
				*/
			}
			
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	public static Thread updateCache(final String sentence, final int priority, final float lat, final float lon, final float distance, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() 
		{ 
			public void run() 
			{
				Log.d(TAG,"lat "+ lat + " long "+ lon + "radius "+ distance);
				List<Hint> result;

				Log.d(TAG,Integer.toString((int)Math.ceil(distance)));
				ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("q", sentence));
				params.add(new BasicNameValuePair("lat", ""+lat));
				params.add(new BasicNameValuePair("lon", ""+lon));
				params.add(new BasicNameValuePair("dist", ""+(int)Math.ceil(distance)));
				result = runHttpGet(LOCATION_SINGLE, params, context);
				sendResultCacheUpdate(new Area(lat,lon,distance),result, sentence, priority, handler, context);
				
			}
			
		};
		// start authenticating
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	/*
	private static void sendResult(final List<Hint> result, final String sentence, 
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		handler.post(new Runnable() {
			public void run() {
					Log.d(TAG,"Executing  Map.afterHintsAcquired. "+Integer.toString(result.size())+" results" );
					//TODO Decide how to use map
					((Map) context).afterHintsAcquired(sentence,result,null);
					
					
					
			}
		});
		
	}
	*/
	private static void sendResultSingleHint(final List<Hint> result, final String sentence, final int priority,
			final Handler handler, final Context context, final boolean fromCache) 
	{
		if (handler == null || context == null) 
		{
			return;
		}
		handler.post(new Runnable() 
		{
			public void run() 
			{
				if (context instanceof TaskNotification)
				{
					
					if(fromCache)
					{
						Log.d(TAG,"Executing  TaskNotification.afterHintsAcquiredFromCache");
						((TaskNotification) context).afterHintsAcquiredFromCache(sentence, result,priority);
					}
					else
					{
						Log.d(TAG,"Executing  TaskNotification.afterHintsAcquired");
						((TaskNotification) context).afterHintsAcquired(sentence, result,priority);
					}
					
				}
				
			}
		});
	}
	
	private static void sendResultCacheUpdate(final Area area,final List<Hint> result, final String sentence, final int priority,
			final Handler handler, final Context context) 
	{
		if (handler == null || context == null) 
		{
			return;
		}
		handler.post(new Runnable() 
		{
			public void run() 
			{
				if (context instanceof TaskNotification)
				{
					
						Log.d(TAG,"Executing  TaskNotification.afterHintsAcquiredFromCache");
						((TaskNotification) context).afterHintsAcquiredCacheUpdate(area,sentence, result,priority);

					
				}
				
			}
		});
	}
}

