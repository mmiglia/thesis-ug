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

import com.thesisug.communication.valueobject.ActionLocationList;
import com.thesisug.communication.valueobject.Item;
import com.thesisug.communication.valueobject.ItemLocationList;
import com.thesisug.communication.valueobject.SingleActionLocation;
import com.thesisug.communication.valueobject.SingleItemLocation;
import com.thesisug.communication.xmlparser.AssertionsHandler;
import com.thesisug.ui.Create_Assertion_action;
import com.thesisug.ui.Create_Assertion_item;
import com.thesisug.ui.Details_assertion_action;
import com.thesisug.ui.Details_assertion_item;
import com.thesisug.ui.ViewAssertions;
import com.thesisug.ui.ViewAssertions_Action;
import com.thesisug.ui.Vote_ont_db;

public class AssertionsResource {
	
	private static final String TAG = new String("thesisug - viewItemLocation");
	
	private static final String CREATE_ITEM_LOCATION = "/ontology/addItemInLocation";
	private static final String CREATE_ITEM_LOCATION_OBJECT = "/ontology/addItemInLocationObject";
	
	private static final String CREATE_ACTION_LOCATION = "/ontology/addActionInLocation";
	private static final String CREATE_ACTION_LOCATION_OBJECT = "/ontology/addActionInLocationObject";
	
	
	private static final String VIEW_ITEM_LOCATION = "/ontology/viewItemLocation";
	private static final String VIEW_ACTION_LOCATION = "/ontology/viewActionLocation";
	private static final String VIEW_ITEM_LOCATION_VOTED ="/ontology/viewItemLocationVoted";
	private static final String VIEW_ACTION_LOCATION_VOTED ="/ontology/viewActionLocationVoted";
	
	private static final String DELETE_ITEM = "/ontology/deleteVoteForItemLocation";
	private static final String DELETE_ACTION = "/ontology/deleteVoteForActionLocation";
	private static final String DELETE_ITEM_OBJECT="/ontology/deleteVoteForItemLocationObject";
	private static final String DELETE_ACTION_OBJECT="/ontology/deleteVoteForActionLocationObject";
	
	private static final String CHECK_IN_ONTOLOGY_DB = "/ontology/checkInOntologyDb";
	private static final String VOTE_ITEM_LIST = "/ontology/voteItemLocationList";
	private static final String VOTE_ACTION_LIST = "/ontology/voteActionLocationList";
	
	private static final String ADD_LOCATION = "/ontology/addLocation";
	
	
	
	
		
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
				
			 for (SingleItemLocation o : result){
		         	Log.d(TAG,"item : " + o.item.toString() + "->" + o.location.toString());
					}
			}*/
			
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
	

	private static boolean runHttpGetUserDeleteItem(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method +"?"+ query);
		Log.i(TAG, NetworkUtilities.SERVER_URI + "/" + util.getUsername(c) + method +"?"+ query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
		// send the request to network
		HttpResponse response = NetworkUtilities
				.sendRequest(newClient, request);
		Log.i(TAG, NetworkUtilities.SERVER_URI + "/" + util.getUsername(c) + method +"?"+ query + "FATTO!");
		// if we cannot connect to the server
		if (!HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode())) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return false; // return null if there's problem with connection
		}
			return true;
	}
	
	private static boolean runHttpUserAddLocation(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method +"?"+ query);
		Log.i(TAG, NetworkUtilities.SERVER_URI + "/" + util.getUsername(c) + method +"?"+ query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
		// send the request to network
		HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
		Log.i(TAG, NetworkUtilities.SERVER_URI + "/" + util.getUsername(c) + method +"?"+ query + "FATTO!");
		// if we cannot connect to the server
		if (!HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode())) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return false; // return null if there's problem with connection
		}
			return true;
	}
	
	
	private static List<SingleActionLocation> runHttpGetUserSingleActionLocation(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<SingleActionLocation> result = new LinkedList<SingleActionLocation>();
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

			result = AssertionsHandler.parseUserActionInLocation(response.getEntity().getContent());
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
	
	private static List<Item> runHttpGetUsercheckInOntologyDb(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<Item> result = new LinkedList<Item>();
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method + "?"+ query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
		Log.i(TAG, "RICHIESTA RUNHTTPGETUSER CHECK ONOTOLGY DB RITORNATA");
		// send the request to network
		HttpResponse response = NetworkUtilities
				.sendRequest(newClient, request);
		// if we cannot connect to the server
		if (!HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode())) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return null; // return null if there's problem with connection
		}
		try { // parsing XML message
			Log.i(TAG, "INVIO RICHIESTA RUNHTTPGETUSER CHECK ONOTOLGY DB RITORNATA");
			result = AssertionsHandler.parseUserItems(response.getEntity().getContent());
			Log.i(TAG, "FATTO IL PARSER DI RICHIESTA RUNHTTPGETUSER CHECK ONOTOLGY DB RITORNATA");
			
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
	

	
	

	private static boolean runHttpAddUserSingleItemLocation(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		String query = (params == null) ? "" : URLEncodedUtils.format(params,
				"UTF-8");
		AccountUtil util = new AccountUtil();
		HttpGet request = new HttpGet(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method +"?" + query);
		Log.i(TAG,"Richiesta CREATE: "+ NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(c) + method + "?" +  query);
		request.addHeader("Cookie", "sessionid="+util.getToken(c));
		// send the request to network
		HttpResponse response = NetworkUtilities
				.sendRequest(newClient, request);
		
		// if we cannot connect to the server
		if (!HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode())) {
			Log.i(TAG, "Cannot connect to server with code "+ response.getStatusLine().getStatusCode());
			return false; // return null if there's problem with connection
		}
		try { // parsing XML message
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
				
			 for (SingleItemLocation o : result){
		         	Log.d(TAG,"item : " + o.item.toString() + "->" + o.location.toString());
					}
			}*/
			
			return true;
		} catch (IllegalStateException e) {
			Log.i(TAG, "Illegal State");
			e.printStackTrace();
			return false;
		} 
	
	}

	
	public static Thread addLocation(final String title, final String location,final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request addLocation for title : " + title + " e location: "+ location +" for the user");
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("title",title));
		        nameValuePairs.add(new BasicNameValuePair("location",location));
				Boolean result = runHttpUserAddLocation(ADD_LOCATION, nameValuePairs, context);	
				sendResult_Loc(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	
	public static Thread getAssertions(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request assertionsList_itemlocation for the user");
				List<SingleItemLocation> result = runHttpGetUserSingleItemLocation(VIEW_ITEM_LOCATION_VOTED, null, context);	
				sendResult(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	
	

	public static Thread getAssertions_action(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request assertionsList_actionlocation for the user");
				List<SingleActionLocation> result = runHttpGetUserSingleActionLocation(VIEW_ACTION_LOCATION_VOTED, null, context);	
				sendResult_action(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	public static Thread checkInOntologyDb(final String title, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "SONO ALLINTERNO DI CHECKONOTOLOGYDB____________");
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("title",title));
		        
				
				List<Item> result = runHttpGetUsercheckInOntologyDb(CHECK_IN_ONTOLOGY_DB, nameValuePairs, context);	
				Log.i(TAG, "SONO ALLINTERNO DI CHECKONOTOLOGYDB____________RISULTATI OTTENUTI");
				sendResult_item(result, handler, context);
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
					//((Vote_ont_db)context).afterAssertionsListLoaded(result);
				
			}
		});
	}
	
	
	private static void sendResult_Loc(final Boolean result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {
				
				((Vote_ont_db)context).finishSave_Loc(result);
					//((Vote_ont_db)context).afterAssertionsListLoaded(result);
				
			}
		});
	}
	
	private static void sendResult_item(final List<Item> result, final Handler handler, final Context context) {
		
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			
			public void run() {
				
					
					((Vote_ont_db)context).afterAssertionsListLoaded(result);
				
			}
		});
	}
	
	private static void sendResult_action(final List<SingleActionLocation> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {
				
					((ViewAssertions_Action)context).afterAssertionsListLoaded(result);
				
			}
		});
	}

	public static Thread createItemLocation(final SingleItemLocation toAdd, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = AssertionsHandler.formatSingleItemLocation(toAdd);

				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("item",toAdd.item));
		        nameValuePairs.add(new BasicNameValuePair("location",toAdd.location));
	
				Log.i(TAG,body);
				//final boolean result = runHttpAddUserSingleItemLocation(CREATE_ITEM_LOCATION, nameValuePairs, context);
				final boolean result = runHttpPost(CREATE_ITEM_LOCATION_OBJECT, null,body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						
						//((Create_Assertion_item) context).finishSave(result);
						if (context instanceof Create_Assertion_item)
							((Create_Assertion_item) context).finishSave(result);
						else
							((Vote_ont_db) context).finishSave(result);
						
						
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	
	public static Thread voteList(final ItemLocationList toVoted, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				
				
				String body = AssertionsHandler.formatItemLocationList(toVoted);
				

				/*ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("item",toAdd.item));
		        nameValuePairs.add(new BasicNameValuePair("location",toAdd.location));*/
	
				Log.i(TAG,body);
				//final boolean result = runHttpAddUserSingleItemLocation(CREATE_ITEM_LOCATION, nameValuePairs, context);
				final boolean result = runHttpPost(VOTE_ITEM_LIST, null,body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						
					
							((Vote_ont_db) context).finishSave_voted(result);
						
						
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	
	public static Thread voteList_action(final ActionLocationList toVoted, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				
				
				String body = AssertionsHandler.formatActionLocationList(toVoted);
				

				/*ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("item",toAdd.item));
		        nameValuePairs.add(new BasicNameValuePair("location",toAdd.location));*/
	
				Log.i(TAG,body);
				//final boolean result = runHttpAddUserSingleItemLocation(CREATE_ITEM_LOCATION, nameValuePairs, context);
				final boolean result = runHttpPost(VOTE_ACTION_LIST, null,body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						
					
							((Vote_ont_db) context).finishSave_voted(result);
						
						
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	public static Thread createActionLocation(final SingleActionLocation toAdd, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = AssertionsHandler.formatSingleActionLocation(toAdd);
				Log.i(TAG,body);
				final boolean result = runHttpPost(CREATE_ACTION_LOCATION_OBJECT, null,body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						
						if (context instanceof Create_Assertion_item)
							((Create_Assertion_item) context).finishSave(result);
						else
							((Vote_ont_db) context).finishSave(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	
	public static Thread deleteAssertions_item(final SingleItemLocation toDelete, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = AssertionsHandler.formatSingleItemLocation(toDelete);
				Log.i(TAG,body);
				final boolean result =runHttpPost(DELETE_ITEM_OBJECT, null,body, context);
				Log.i(TAG,"ok DELETE_ITEM_OBJECT");
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						Log.i(TAG,"ok DELETE_ITEM_OBJECT 2");
						((Details_assertion_item) context).finishSave(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	public static Thread deleteAssertions_action(final SingleActionLocation toDelete, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = AssertionsHandler.formatSingleActionLocation(toDelete);
				Log.i(TAG,body);
				final boolean result =runHttpPost(DELETE_ACTION_OBJECT, null,body, context);
				Log.i(TAG,"ok DELETE_ACTION_OBJECT");
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						Log.i(TAG,"ok DELETE_ACTION_OBJECT 2");
						((Details_assertion_action) context).finishSave(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	
/*public static Thread deleteAssertions_item(final SingleItemLocation toDelete, final Handler handler, final Context context) {
		
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request deleteAssertions_item for the user");
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("item",toDelete.item));
		        nameValuePairs.add(new BasicNameValuePair("location",toDelete.location));
		        Log.i(TAG, "request deleteAssertions_item for the user:nameValuePairs OK! ");
		        boolean result = runHttpGetUserDeleteItem(DELETE_ITEM, nameValuePairs, context);	
				Log.i(TAG, "ritornato " + result + "dalla richiesta di cancellazione!");
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						Log.i(TAG, "alla prox avvio della funzione finishSave!");
						((Details_assertion_item) context).finishSave(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
public static Thread deleteAssertions_item(final SingleItemLocation toDelete, final Handler handler, final Context context) {
		
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "request deleteAssertions_item for the user");
				
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		        nameValuePairs.add(new BasicNameValuePair("item",toDelete.item));
		        nameValuePairs.add(new BasicNameValuePair("location",toDelete.location));
		        Log.i(TAG, "request deleteAssertions_item for the user:nameValuePairs OK! ");
		       
				boolean result = runHttpGetUserDeleteItem(DELETE_ITEM, nameValuePairs, context);
				sendResult_delete_item(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}

private static void sendResult_delete_item(final boolean result,final Handler handler, final Context context) {
	if (handler == null || context == null) {
		return;
	}
	Log.i(TAG, "Sending message");
	handler.post(new Runnable() {
		public void run() {
			
				((Details_assertion_item)context).afterAssertionsListLoaded(result);
			
		}
	});
}*/
	
	
	
}


