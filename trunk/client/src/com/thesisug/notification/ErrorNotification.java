package com.thesisug.notification;

import com.thesisug.notification.TaskNotification.LocalBinder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class ErrorNotification extends Service{

	private final IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder {
        public ErrorNotification getService() {
            return ErrorNotification.this;
        }
    }

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	
	public static int[] arrElemToNotify;
	
    private Runnable mainthread = new Runnable() {
        public void run() {
        	while (true){
        		
        	}
        }
    };
}
