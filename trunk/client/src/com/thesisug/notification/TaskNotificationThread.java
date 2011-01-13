package com.thesisug.notification;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.thesisug.communication.ContextResource;
import com.thesisug.communication.TaskResource;
import com.thesisug.communication.valueobject.SingleTask;

public class TaskNotificationThread extends Thread {
	/*
	private Object lockObject;
	
	public TaskNotificationThread(ThreadGroup group, String threadName,Object _lockObject){
		//null, mainthread, "NotifyingService"
		super(group,threadName);
		lockObject=_lockObject;
	}

	
	public void run() {
    	while (true){
    		// get preference on query period, return default 5 min if not set
    		//int delay = Integer.parseInt(usersettings.getString("queryperiod", "300")) * 1000;
    		int delay = Integer.parseInt(usersettings.getString("queryperiod", "100"));
    		if (delay == 0) {
    			Log.i(TAG, "mainthread - Thread blocked for 60000 millis");
    			condvar.block(60000); 
    			continue;
    		}
    		//Log.i(TAG, "mainthread - distance query is "+delay+" mt");
    		Log.i(TAG, "mainthread is going to block on "+stopThreadObject.hashCode());
    		//condvargps.block();
    		try {
    			synchronized(stopThreadObject){
					stopThreadObject.wait();
    			}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		Log.i(TAG, "mainthread can go on!");
    		
    		//if (condvargps.block(delay)) break;
    		// get preference on distance, return default 0 (dont filter distance) if not set
    		int distance = Integer.parseInt(usersettings.getString("maxdistance", "0"));
    		List<Thread> threads = new LinkedList<Thread>();

		
    		if (userLocation == null) continue;
    		//asynchronous operation to download thread
    		downloadTaskThread = TaskResource.getFirstTask(handler, TaskNotification.this);
    		//block execution to make it synchronous
    		downloadlock.block();
    		for (SingleTask o : tasks){
    			// if user has chose not to be reminded for this task
    			if (!usersettings.getBoolean(o.title, true)) continue; 
    			// dispatch thread to get hints
				threads.add(ContextResource.checkLocationSingle(o.title,
						new Float(userLocation.getLatitude()),
						new Float(userLocation.getLongitude()),
						distance, handler, TaskNotification.this));
				
			}
    		condvargps.close();
    	}
    }
    */
}
