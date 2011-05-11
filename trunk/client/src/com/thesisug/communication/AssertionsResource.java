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

import com.thesisug.R;
import com.thesisug.communication.valueobject.GroupData;
import com.thesisug.communication.valueobject.SingleItemLocation;
import com.thesisug.communication.valueobject.GroupInviteData;
import com.thesisug.communication.valueobject.GroupMember;
import com.thesisug.communication.xmlparser.AssertionsHandler;
import com.thesisug.ui.ViewAssertions;
import com.thesisug.ui.InviteToJoinGroup;
import com.thesisug.ui.ManageGroupMenu;
import com.thesisug.ui.ViewGroupMembers;

public class AssertionsResource {
	
	private static final String TAG = new String("thesisug - viewItemLocation");
	
	private static final String VIEW_ITEM_LOCATION = "/ontology/viewItemLocation";
		
	private static List<SingleItemLocation> runHttpGetUserSingleItemLocation(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<SingleItemLocation> result = new LinkedList<SingleItemLocation>();
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
			
			
			result = AssertionsHandler.parseUserItemsInLocation(response.getEntity().getContent());
			
			/*
			if(result==null || result.size()==0){
				Log.d(TAG,"lista itemfoundinloc VUOTA");
				Log.i(TAG,"lista itemfoundinloc VUOTA");
				SingleItemLocation num1 = new SingleItemLocation("item1","location1","anuska","1","1","1");
				SingleItemLocation num2 = new SingleItemLocation("item2","location2","anuska","1","1","1");
				SingleItemLocation num3 = new SingleItemLocation("item3","location3","anuska","1","1","1");
				result.add(num1);
				result.add(num2);
				result.add(num3);
			}else{
				*/
			//---------------------------------
			 for (SingleItemLocation o : result){
		         	Log.d(TAG,"item : " + o.item.toString() + "->" + o.location.toString());
					}
			//---------------------------------
			//}
			
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
	
	public static Thread getAssertions(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request assertionsList for the user");
				List<SingleItemLocation> result = runHttpGetUserSingleItemLocation(VIEW_ITEM_LOCATION, null, context);	
				
				sendResult(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	private static void sendResult(final List<SingleItemLocation> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {
				
					((ViewAssertions)context).afterAssertionsListLoaded(result);
				
			}
		});
	}

	

}
