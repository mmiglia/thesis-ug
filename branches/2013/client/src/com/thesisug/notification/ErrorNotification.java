package com.thesisug.notification;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ErrorNotification extends Service{
	private static final String TAG = "thesisug - ErrorNotificationService";
	private final IBinder mBinder = new LocalBinder();
	
    @Override
    public void onCreate() {
    	
    	Thread notifyingThread = new Thread(null, mainthread, "ErrorNotificationService");
    }
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
        		Log.i(TAG, "eCCOMI!");
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    };
}
