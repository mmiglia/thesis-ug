package com.thesisug.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.thesisug.tracking.ActionTracker;
import com.thesisug.tracking.GpxBuilder;

/**
 * Methods to upload track files.
 * 
 * @author Alberto Servetti
 *
 */
public class TrackingResource 
{
	private static final String TAG = "thesisug - UploadFileResource";
	private static final String UPLOAD_ACTION_TRACK = "/tracking/action";
	private static final String UPLOAD_PATH_TRACK = "/tracking/path";
	
	/**
	 * HttpPost request to upload tracking file.
	 * 
	 * @param method	UPLOAD_ACTION_TRACK for action tracks, UPLOAD_PATH_TRACK for path tracks.
	 * @param fileName	Name of the file to be uploaded.
	 * @param context	Application context.
	 * @return			true if HttpPost request succeeds, false if does not.
	 */
	private static boolean runHttpPostUpload(final String method,String fileName, Context context) 
	{
		File file = new File(context.getFilesDir(),fileName);
		DefaultHttpClient newClient = NetworkUtilities.createClient();
		AccountUtil util = new AccountUtil();
		HttpPost request = new HttpPost(NetworkUtilities.SERVER_URI + "/"
				+ util.getUsername(context) + method);
		request.addHeader("Cookie", "sessionid="+util.getToken(context));
		request.addHeader("filename", fileName);
		
		try 
		{
			
			//request.setHeader("Content-Type", "binary/octet-stream");
			
			InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(file), -1);
		    reqEntity.setContentType("binary/octet-stream");
		    reqEntity.setChunked(true); // Send in multiple parts if needed
		    request.setEntity(reqEntity);
			// send the request to network
			HttpResponse response = NetworkUtilities.sendRequest(newClient, request);
			Log.i(TAG, "Status Code is "+response.getStatusLine().getStatusCode());
			return HttpResponseStatusCodeValidator.isValidRequest(response.getStatusLine().getStatusCode());

		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Starts a new background thread to upload a track file to server. 
	 * 
	 * @param type			0 for uploading action track, 1 for uploading path track.
	 * @param fileName		name of the track file.
	 * @param handler		Handler for uploading result.
	 * @param context		Application context.
	 * @return				Thread started.
	 */
	
	public static Thread uploadTrack(final int type,final String fileName, final Handler handler, final Context context) {
		final Runnable runnable = new Runnable() 
		{
			public void run() 
			{
				Log.i(TAG,fileName);
				String method;
				if(type==0)
					method = UPLOAD_ACTION_TRACK;
				else
					method = UPLOAD_PATH_TRACK;
				final boolean result = runHttpPostUpload(method, fileName, context);
				if (handler == null || context == null) 
				{
					return;
				}
				handler.post(new Runnable() 
				{
					public void run() 
					{
						if(type==0)
							ActionTracker.finishSave(result,context);
						else
							GpxBuilder.finishSave(fileName,result,context);
					}
				});
			}
		};
		return NetworkUtilities.startBackgroundThread(runnable);
	}

}