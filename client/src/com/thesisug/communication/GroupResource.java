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

import com.thesisug.communication.valueobject.GroupData;
import com.thesisug.communication.valueobject.GroupInviteData;
import com.thesisug.communication.valueobject.GroupMember;
import com.thesisug.communication.xmlparser.GroupHandler;
import com.thesisug.ui.EditTask;
import com.thesisug.ui.InviteToJoinGroup;
import com.thesisug.ui.ManageGroupMenu;
import com.thesisug.ui.ViewGroupMembers;

public class GroupResource {
	private static final String TAG = new String("thesisug - GroupResource");
	private static final String CREATE_GROUP = "/group/create";
	private static final String GET_GROUP_LIST = "/group/list";
	private static final String INVITE_USER_TO_GROUP = "/group/invite";
	private static final String GET_JOIN_TO_GROUP_REQUEST = "/group/getJoinGroupRequest";
	private static final String ACCEPT_INVITE="/group/accept";
	private static final String REFUSE_INVITE="/group/refuse";
	private static final String GET_GROUP_MEMBER_LIST_Part_1="/group";
	private static final String GET_GROUP_MEMBER_LIST_Part_2="/getGroupMember";
	
	private static final String DELETE_FROM_GROUP_Part_1="/group";
	private static final String DELETE_FROM_GROUP_Part_2="/deleteFromGroup";
	
	
	private static List<GroupData> runHttpGetUserGroups(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<GroupData> result = new LinkedList<GroupData>();
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
			result = GroupHandler.parseUserGroupRequest(response.getEntity().getContent());
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

	private static List<GroupInviteData> runHttpGetUserGroupInviteList(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<GroupInviteData> result = new LinkedList<GroupInviteData>();
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
			result = GroupHandler.parseGroupInvite(response.getEntity().getContent());
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
	
	private static List<GroupMember> runHttpGetUserGroupMemberList(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		List<GroupMember> result = new LinkedList<GroupMember>();
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
			result = GroupHandler.parseGroupMemberList(response.getEntity().getContent());
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

	private static boolean runHttpGetDeleteFromGroup(final String method,
			final ArrayList<NameValuePair> params, Context c) {
		boolean result = true;
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
		//TODO change this with a correct management of deleting output
		return result;
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
			return (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() ==200)? true: false;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static Thread createGroup(final GroupData group, final Handler handler, final Context context){
		
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = GroupHandler.formatUserGroupRequest(group);
				Log.i(TAG,body);
				final boolean result = runHttpPost(CREATE_GROUP, null, body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((ManageGroupMenu) context).finishCreateGroup(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	

	/**
	 * Creates and run background thread to get all group in wich the user is involved
	 * 
	 * @param username
	 *            username of the user
	 * @param sessionid
	 *            session token
	 * @param handler
	 *            handler for the thread
	 * @param context
	 *            activity that calls for authentication
	 * @return thread running the request
	 */
	public static Thread getUserGroup(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "Sending groupList request for the user");
				List<GroupData> result = runHttpGetUserGroups(GET_GROUP_LIST, null, context);
				
				sendResult(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	
	/**
	 * Creates and run background thread that get all informations about a group
	 * 
	 * @param groupId
	 *            the id of the group
	 * @param sessionid
	 *            session token
	 * @param handler
	 *            handler for the thread
	 * @param context
	 *            activity that calls for authentication
	 * @return thread running the request
	 */
	public static Thread viewGroup(final String groupID, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "Sending viewGroup request for the group "+ groupID);
				String GET_GROUP_MEMBER=GET_GROUP_MEMBER_LIST_Part_1+"/"+groupID+GET_GROUP_MEMBER_LIST_Part_2;
				List<GroupMember> result = runHttpGetUserGroupMemberList(GET_GROUP_MEMBER, null, context);
				
				sendResultGetGroupMember(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}

	/**
	 * Creates and run background thread to create on the server an invite to join to the group
	 * 
	 * @param userToInvite
	 *            username of the user to invite to join the group
	 * @param handler
	 *            handler for the thread
	 * @param context
	 *            activity that calls for authentication
	 * @return thread running the request
	 */
	public static Thread inviteUserToJoinTheGroup(final GroupInviteData invite,final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {				
				String body = GroupHandler.formatGroupInvite(invite);
				Log.i(TAG,body);
				final boolean result = runHttpPost(INVITE_USER_TO_GROUP, null, body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((InviteToJoinGroup) context).finishJoinGroupInvite(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}	

	/**
	 * Creates and run background thread to get all request to join to a group in wich the user is involved
	 * 
	 * @param username
	 *            username of the user
	 * @param sessionid
	 *            session token
	 * @param handler
	 *            handler for the thread
	 * @param context
	 *            activity that calls for authentication
	 * @return thread running the request
	 */
	public static Thread getUserJoinGroupRequest(final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "Sending get all join to group request to server");
				List<GroupInviteData> result = runHttpGetUserGroupInviteList(GET_JOIN_TO_GROUP_REQUEST, null, context);
				Log.i(TAG, "get all join to group request returned "+result.size() + " request");
				sendResultgetUserJoinGroupRequest(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
	}	
	
	public static Thread acceptJoinToGroupRequest(final GroupInviteData invite, final Handler handler, final Context context){
		
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = GroupHandler.formatAcceptUserGroupRequest(invite);
				Log.i(TAG,body);
				final boolean result = runHttpPost(ACCEPT_INVITE, null, body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((ManageGroupMenu) context).finishAcceptGroupInvite(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}
	

	public static Thread refuseJoinToGroupRequest(final GroupInviteData invite, final Handler handler,final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				String body = GroupHandler.formatRefuseUserGroupRequest(invite);
				Log.i(TAG,body);
				final boolean result = runHttpPost(REFUSE_INVITE, null, body, context);
				if (handler == null || context == null) {
					return;
				}
				handler.post(new Runnable() {
					public void run() {
						((ManageGroupMenu) context).finishRefuseGroupInvite(result);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
		
	}

	/**
	 * This method delete the current user from the group.
	 * If the group became empty, it delete also the group and all the JoinToGroup pending requests
	 * TODO this has to be done with POST
	 * @param groupID
	 * @param handler
	 * @param context
	 * @return
	 */
	public static Thread deleteFromGroup(final String groupID, final Handler handler,final Context context) {
		final Runnable runnable = new Runnable() {
			public void run() {
				Log.i(TAG, "Sending deleteFromGroup request for the group "+ groupID);
				String DELETE_FROM_GROUP_MEMBER=DELETE_FROM_GROUP_Part_1+"/"+groupID+DELETE_FROM_GROUP_Part_2;
				boolean result = runHttpGetDeleteFromGroup(DELETE_FROM_GROUP_MEMBER, null, context);
				
				sendResultDeleteFromGroupMember(result, handler, context);
			}
		};
		// start group list request
		return NetworkUtilities.startBackgroundThread(runnable);
		
	}
	


	private static void sendResult(final List<GroupData> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {
				if(context instanceof EditTask){
					((EditTask)context).afterGroupListLoaded(result);
					return;
				}
				if(context instanceof InviteToJoinGroup){
					((InviteToJoinGroup)context).afterGroupListLoaded(result);
					return;
				}
				if(context instanceof ViewGroupMembers){
					((ViewGroupMembers)context).afterGroupListLoaded(result);
					return;
				}
			}
		});
	}
	private static void sendResultgetUserJoinGroupRequest(final List<GroupInviteData> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {
				((ManageGroupMenu)context).afterGroupInviteListLoaded(result);
			}
		});
	}

	private static void sendResultGetGroupMember(final List<GroupMember> result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {

				if(context instanceof ViewGroupMembers){
					((ViewGroupMembers)context).afterGroupMemberListLoaded(result);
					return;
				}
			}
		});
	}
	private static void sendResultDeleteFromGroupMember(final boolean result,
			final Handler handler, final Context context) {
		if (handler == null || context == null) {
			return;
		}
		Log.i(TAG, "Sending message");
		handler.post(new Runnable() {
			public void run() {

				if(context instanceof ViewGroupMembers){
					((ViewGroupMembers)context).afterDeltingFromGroup(result);
					return;
				}
			}
		});
	}
	


	
}
