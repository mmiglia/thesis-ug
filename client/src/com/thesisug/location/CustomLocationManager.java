package com.thesisug.location;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;
import com.thesisug.R;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.notification.TaskNotification;
import com.thesisug.tracking.ActionTracker;
import com.thesisug.tracking.GpxBuilder;

/**
 * Implements methods for handling location updates in an intelligent way for this app 
 * to consume less battery possible.
 * 
 * @author Alberto Servetti
 *
 */
public class CustomLocationManager 
{
	private static String TAG = "thesisug - CustomLocationManager";
	private static LocationListener locationListener;
	private static LocationManager locationManager;
	private static Criteria criteria;
	private static String locationProvider;
	private static Context applicationContext;
	private static Location lastCheckedFix;
	private static Location userPosition;
	private static SharedPreferences usersettings;
	private static float minUpdateDistance=0;
	private static float maxHintDistance=0;
	private static long minUpdateTime=0;
	private static String appMode;
	private static long standingTime=0;
	private static int hintsShowed;
	private static Handler handler;
	private static Runnable requestUpdates;
	private static Runnable gpsTimeout;
	private static Runnable wifiRequestTimeout;
	private static Runnable gpsRequestTimeout;
	private static BroadcastReceiver broadCastReceiver;
	private static boolean isBatteryLow;
	private static boolean haveWifiConnection;
	//Wifi and Network fixes both belongs to NETWORK_PROVIDER,
	//so I need wifiToCellular to be aware when wifi fixes are no 
	//more avaiable and I pass to Network fixes.
	private static boolean wifiToCellular; 
	private static boolean wifiAlreadyAsked;
	private static boolean gpsAlreadyAsked;
	private static boolean waitingForWifiAnswer;
	private static int wifiInaccurateFixes;
	private static WifiManager wifiManager;
	private static int taskHintSearching;
	private static Thread evaluateThread;
	private static GpxBuilder gpxBuilder;
	private static long gpsIdleTime;
	//Time values
	@SuppressWarnings("unused")
	private static final long FIVESEC = 1000 * 5;
	private static final long TENSEC = 1000 * 10;
	private static final long HALFMIN = 1000 * 30;
	private static final long ONEMIN = 1000 * 60;
	private static final long TWOMINS = 1000 * 60 * 2;
	private static final long FIVEMINS = 1000 * 60 * 5;
	private static final long TENMINS = 1000 * 60 * 10;
	@SuppressWarnings("unused")
	private static final long FIFTMINS = 1000 * 60 * 15;
	@SuppressWarnings("unused")
	private static final long HALFHOUR = 1000 * 60 * 30;
	private static ConditionVariable evaluateThreadStop;
	private static Object wifiLock;
	private static Object gpsLock;
	private static boolean isAccuracyOk;
	private static class pendingHint
	{
		public String title;
		public List<Hint> result;
		public int priority;
		
		public pendingHint(String t,List<Hint> r, int p)
		{
			title=t;
			result=r;
			priority=p;
		}
	}
	
	private static ArrayList<pendingHint> pendingHints;
	private static final int LOCATIONCHANGED = 0;
	private static final int PROVIDERENABLED = 1;
	private static final int PROVIDERDISABLED = 2;
	private static final int STATUSCHANGED = 3;
		
	public CustomLocationManager(String owner,LocationListener listener,SharedPreferences settings,Context context)
	{
		TAG+=owner;
		Log.i(TAG,"New CustomLocationManager."); 
		locationListener=listener;
		usersettings = settings;
		minUpdateDistance = Float.parseFloat(usersettings.getString("queryperiod", "100"));
		maxHintDistance = Float.parseFloat(usersettings.getString("maxdistance", "100"));
		appMode = usersettings.getString("appmode","learning_mode");
		ResetMinUpdateTime();
		applicationContext=context; 
		wifiManager = (WifiManager) applicationContext.getSystemService(Context.WIFI_SERVICE);
		locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
		locationProvider = LocationManager.NETWORK_PROVIDER;
		criteria = new Criteria();
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_MEDIUM);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		hintsShowed = 0; 
		taskHintSearching = 0;
		gpxBuilder = new GpxBuilder(Calendar.getInstance().getTime(),applicationContext);
		handler = new Handler();
		wifiToCellular = false;
		wifiAlreadyAsked = false;
		gpsAlreadyAsked = false;
		waitingForWifiAnswer = false;
		requestUpdates = 	new Runnable() 
		{
			@Override
			public void run() 
			{
				RequestLocationUpdates("runnable");
			}
		};
		gpsIdleTime = 0;		
		gpsTimeout = new Runnable()
		{

			@Override
			public void run() 
			{
				Log.d(TAG,"GPS timeout.");
				gpsIdleTime += TWOMINS;
				if(gpsIdleTime>TENMINS)
				{
					locationProvider = LocationManager.NETWORK_PROVIDER;
					gpsIdleTime = 0;
				}
				handler.removeCallbacks(requestUpdates);
				RemoveUpdates();
				handler.postDelayed(requestUpdates, minUpdateTime);
			}
			
		};
		
		wifiRequestTimeout = new Runnable()
				
		{
			@Override
			public void run()
			{
				synchronized(wifiLock)
				{
					Log.d(TAG,"Wifi Request timeout.");
					wifiAlreadyAsked=false;
					wifiLock.notify();
				}
				
			}
		};
		
		gpsRequestTimeout = new Runnable()
		
		{
			@Override
			public void run()
			{
				synchronized(gpsLock)
				{
					Log.d(TAG,"Gps Request timeout.");
					gpsAlreadyAsked=false;
					ActionTracker.gpsResponse(Calendar.getInstance().getTime(), lastCheckedFix, "TIMEOUT", applicationContext);
					gpsLock.notify();
				}
				
			}
		};
		
		broadCastReceiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent) 
			{
				final String action = intent.getAction();
			    /*wifiEnabled*/
			    if (action.equals(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)) 
			    {
			        if (intent.getBooleanExtra(WifiManager.EXTRA_SUPPLICANT_CONNECTED, false)) 
			        {
			        	//synchronized(wifiLock)
			        	//{
				        	Log.d(TAG,"Wifi enabled.");
				    		//wifiLock.notify();
			        	//}
			        }
			        else 
			        {
			        	Log.d(TAG,"Wifi disabled.");
			        	//I synchronize with gpsLock to avoid
			        	//havewifiConnection changes while  evaluating thread
			        	//is reasoning about gps.
			        	synchronized(gpsLock)
			    		{
				        	if(haveWifiConnection)
			        		{
			        			//If i had wifi connection I have to tell to 
			        			//isBetterThanPrevious that now wifi 
			        			//fix is no more valid.
				        		wifiToCellular = true;
			        			haveWifiConnection=false;
			        		}
			    		}
			        }
			    }
			    /*wifiConnection*/
			    if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) 
			    {
			    	if(wifiManager.getScanResults().size()>0)
			    	{
			    		Log.d(TAG,"Woah! "+wifiManager.getScanResults().size()+" wifi network detected!");
			    		
			    		synchronized(gpsLock)
			    		{
			            	if(locationProvider.equals(LocationManager.GPS_PROVIDER))
				            {
				            	//If gps is active and now I have wifi
				            	handler.removeCallbacks(requestUpdates);
				            	handler.removeCallbacks(gpsTimeout);
				            	RemoveUpdates();
				            	locationProvider = LocationManager.NETWORK_PROVIDER;
				            	handler.post(requestUpdates);
				            	gpxBuilder.newtrkSeg(Calendar.getInstance().getTime());
				            }
			            	haveWifiConnection = true; 

			    		}
			    		
			    	} 
			        else
			        {
			        	Log.d(TAG,"Damn! no wifi network detected!");
			        	
			        	synchronized(gpsLock)
			    		{
				        	if(haveWifiConnection)
				        		{
				        			//If i had wifi connection I have to tell to 
				        			//isBetterThanPrevious that now wifi 
				        			//fix is no more valid.
				        			
				        			wifiToCellular = true;
					        		
				        			haveWifiConnection=false;
				        		}
				    	}
			        }
			    	
			    	synchronized(wifiLock)
			    	{
			    		if(waitingForWifiAnswer) //To avoid useless .notify()
				    	{
				    		Log.d(TAG,"Scan finished, unlocking.");
				    		waitingForWifiAnswer=false;
				    		wifiLock.notify();
				    	}
			    	}
			    } 
			    /*gpsStateChange*/
			    if (action.equals(LocationManager.PROVIDERS_CHANGED_ACTION))
		        { 
		            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		            {
		            	synchronized(gpsLock)
			        	{
			            	Log.d(TAG,"Gps enabled.");
		            		gpsLock.notify();
			        	}
		            	/*if(!gpsActive && !haveWifiConnection)
		            	{
		            		//If gps is not active and don't have wifi
			            	handler.removeCallbacks(requestUpdates);
			            	RemoveUpdates();
			            	handler.post(requestUpdates);
		            	}*/
		            }
		            else
		            {
		            	synchronized(gpsLock)
			    		{
			            	Log.d(TAG,"Gps disabled.");
			            	if(locationProvider.equals(LocationManager.GPS_PROVIDER))
			            	{
			            		handler.removeCallbacks(requestUpdates);
			            		handler.removeCallbacks(gpsTimeout);
			            		isAccuracyOk=false;
				            	locationProvider = LocationManager.NETWORK_PROVIDER;
				            	RemoveUpdates();
				            	handler.post(requestUpdates);
				            	gpxBuilder.newtrkSeg(Calendar.getInstance().getTime());
			            	
			            	}
			    		}
		            }
		        }
			    /*batteryLow*/
			    if(action.equals(Intent.ACTION_BATTERY_LOW))
			    {

			    	Log.d(TAG,"Battery low!");
			    	isBatteryLow=true;
			    	synchronized(gpsLock)
		    		{
				    	if(locationProvider.equals(LocationManager.GPS_PROVIDER) && appMode.equals("battery_rec_mode"))
				    	{
	
				    		handler.removeCallbacks(requestUpdates);
				    		ResetMinUpdateTime();
				    		locationProvider = LocationManager.NETWORK_PROVIDER;
				    		RemoveUpdates();
				    		RequestLocationUpdates("batteryLow");
				    	}
		    		}
			    }
			    /*batteryOk*/
			    if(action.equals(Intent.ACTION_BATTERY_OKAY))
			    {
		    		isBatteryLow=false;
			    }
			}
		};
	
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
		applicationContext.registerReceiver(broadCastReceiver, intentFilter);
		wifiLock = new Object();
		gpsLock = new Object();
		evaluateThreadStop = new ConditionVariable(false);
		
		pendingHints = new ArrayList<pendingHint>();
		isAccuracyOk=false;
		RequestLocationUpdates("Init");
		wifiInaccurateFixes=0;
		evaluateThread = new Thread(null, thread, "EvaluateAccuracyService");
		evaluateThread.start();
		
	}
	
	/**
	 * Reset minUpdateTime to default value;
	 */
	private void ResetMinUpdateTime()
	{
		if(!(isBatteryLow && appMode.equals("battery_rec_mode")))
			minUpdateTime = HALFMIN/2;
		else
			minUpdateTime = ONEMIN;
	}
	/**
	 * Send broadcast message to registered BroadCastReceivers. 
	 * 
	 * @param type		Type of message: LOCATIONCHANGED, PROVIDERENABLED or PROVIDERDISABLED
	 * @param location	If LOCATIONCHANGED message, new location value.
	 * @param provider	If PROVIDERENABLED or PROVIDER DISABLE message, name of provider.
	 */
	public void SendBroadcast(int type,Location location,String provider, int status, Bundle extras)
	{
		Log.i(TAG, "sendBroadCast");
		
		Intent intent = new Intent("com.thesisug.location");
		String message = ""; 
		switch(type)
		{
		case LOCATIONCHANGED:
			intent.putExtra("messagetype", LOCATIONCHANGED);
			intent.putExtra("location", location);
			Log.d(TAG,"Sending location: " + Double.toString(location.getLatitude())+ " " + Double.toString(location.getLongitude()));
			message="Location changed.";
			break;
		case PROVIDERENABLED:
			intent.putExtra("type", PROVIDERENABLED);
			intent.putExtra("provider",provider);
			message="Provider enabled.";
			break;
		case PROVIDERDISABLED:
			intent.putExtra("type", PROVIDERDISABLED);
			intent.putExtra("provider", provider);
			message="Provider disabled.";
			break;		
		case STATUSCHANGED:
			intent.putExtra("type", STATUSCHANGED);
			intent.putExtra("provider", provider);
			intent.putExtra("status", status);
			intent.putExtra("extras", extras);
			message="Status changed.";
			break;		
		}
		Log.d(TAG,"Sending broadcast message of type:" + message);
		applicationContext.sendBroadcast(intent);
		
	}
	
	/**
	 * Increase minUpdateTime of half a minute.
	 * If minUpdateTime gets longer than five minutes,
	 * it's set to five minutes.
	 */
	
	private void increaseMinUpdateTime()
	{
		Log.d(TAG,"Increasing minUpdateTime.");
		
		setMinUpdateTime(minUpdateTime+HALFMIN);
	}
	
	/**
	 * Decrease minUpdateTime of half a minute.
	 * If minUpdateTime gets shorter than ten seconds,
	 * it's set to ten seconds.
	 */
	
	@SuppressWarnings("unused")
	private void decreaseMinUpdateTime()
	{
		Log.d(TAG,"Decreasing minUpdateTime.");
		
		setMinUpdateTime(minUpdateTime-HALFMIN);
	}	
	
	/**
	 * Get time delta between two updates.
	 * @return time delta.
	 */
	public long getMinUpdateTime()
	{
		return minUpdateTime;
	}
	/**
	 * Set time delta between two updates to newMinUpdateTime if it belongs to the allowed range.
	 * @param newMinUpdateTime	New time delta.
	 */
	private void setMinUpdateTime(long newMinUpdateTime)
	{
		Log.d(TAG,"setMinUpdateTime: "+newMinUpdateTime/1000);
		long min = HALFMIN/2;
		long max = FIVEMINS;
		
		if(appMode.equalsIgnoreCase("battery_rec_mode") && isBatteryLow)
		{
			min = ONEMIN;
			max = TENMINS;
		}
		else
		{
			min = HALFMIN/2;
			max = FIVEMINS;
		}
		
		minUpdateTime=newMinUpdateTime;
		
		if(minUpdateTime < min)
			minUpdateTime = min;
		
		if(minUpdateTime > max)
			minUpdateTime = max;
	}
	
	/**
	 * Remove locationListener registration to location updates.
	 */
	private void RemoveUpdates()
	{
		Log.i(TAG,"removeUpdates");
		locationManager.removeUpdates(locationListener);
	}
	
	/**
	 * If available, returns location's speed. 
	 * Otherwise speed is obtained with physics law of motion.
	 * 
	 * @param location		Final location.
	 * @param distance		Distance from previous location in meters.
	 * @param timeDelta		Time delta from previous location in milliseconds.
	 * @return				Value of speed in meters/milliseconds.
	 */
	
	private float getSpeed(Location location, float distance,float timeDelta)
	{
		Log.i(TAG,"getSpeed.");
		
		float speed;
		if(location.hasSpeed())
		{
			speed = location.getSpeed();
			Log.d(TAG,"hasSpeed:" + speed);
			return speed;
		}
		else
		{
			speed = distance / timeDelta;
			Log.d(TAG,"Location does not hasSpeed, calculated:" + speed*1000);
			return speed;
		}
	}
	
	/**
	 * Try to obtain a new locationManager.
	 * 
	 * @return	True if new locationManager is not null, false otherwise.
	 */
	private boolean checkLocationManager()
	{
		locationManager = (LocationManager) applicationContext.getSystemService(Context.LOCATION_SERVICE);
    	return locationManager!=null?true:false;
	}
	
	/**
	 * Shows a message to user informing of not finding a location provider.
	 */
	private void showNoProviderMessage()
	{
		Log.i(TAG,"showNoProviderMessage");
	    Toast.makeText(applicationContext, R.string.no_location_provider_found, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Checks locationManager existence.
	 * @return	True if locationManager is not null, false otherwise.
	 */
	private boolean checkLocationManagerNotNull()
	{
		Log.i(TAG,"checkLocationManagerNotNull");
		if(locationManager==null || !checkLocationManager())
		{
			Log.e(TAG,"locationManager null");
			return true;
		}
		return false;
	}
	
	/**
	 * Checks locationProvider existence.
	 * @return True if locationProvider is not null, false otherwise.
	 */
	
	private boolean checkLocationProviderNotNull()
	{
		Log.i(TAG,"checkLocationProviderNotNull");
		if(locationProvider==null)
			{
			Log.e(TAG,"locationProvider null");
			showNoProviderMessage();
			return true;
			}
		return false;
	}
	
	/**
	 * Subscribe locationListener to locationProvider updates.
	 */
	private void RequestLocationUpdates(String caller)
	{
		Log.i(TAG,"requestLocationUpdates by " + caller);
		Log.d(TAG,"Registering to " + locationProvider +" updates with frequency "+ minUpdateTime +".");
		if(locationProvider.equals(LocationManager.GPS_PROVIDER))
		{
			handler.postDelayed(gpsTimeout, TWOMINS);
		}
		locationManager.requestLocationUpdates(locationProvider, minUpdateTime, 0, locationListener);
		return;
	}
	
	/**
	 * Force to immediately obtain a new location fix.
	 */
	public void forceLocationFix()
	{
		synchronized(gpsLock)
		{
			Log.i(TAG,"forcing Location Fix.");
			handler.removeCallbacks(requestUpdates);
			isAccuracyOk=false;
			//lastCheckedFix=null;
			RemoveUpdates();
			handler.post(requestUpdates);	
		}
	}
	
	/**
	 * Returns last known user position.
	 * 
	 * @return Last known position
	 * .
	 */
	public Location getLastKnownPosition()
	{
		Log.i(TAG,"getLastKnownPosition");
		/*if(lastCheckedFix == null)
		{
			lastCheckedFix = locationManager.getLastKnownLocation(locationProvider);
		}*/
			return locationManager.getLastKnownLocation(locationProvider);
	}
	
	/**
	 * Checks if two Location objects came from same provider.
	 * 
	 * @param location1
	 * @param location2
	 * @return	True if provider is the same, false if is different or null.
	 */
	boolean isSameProvider(Location location1, Location location2)
	{
		if(location1 == null || location2 == null)
			return false;
		if(location1.getProvider() == null || location2.getProvider() == null)
			return false;
		else return location1.getProvider().equals(location2.getProvider());
	}
	/**
	 * Check if new location is acceptable.
	 * @param location	New location fix to check.
	 * @return			True if new location is acceptable, false if it is not.
	 */
	public boolean isBetterThanPrevious(Location location)
	{
		//TODO
		Log.i(TAG,"isBetterThanPrevious");
		 
		Log.d(TAG,"New location from: " + location.getProvider() + " Accuracy: " + location.getAccuracy());
		
		//If fix comes from gps, reset gpsIdleTime
		if(location.getProvider().equals(LocationManager.GPS_PROVIDER))
		{
			handler.removeCallbacks(gpsTimeout);
			if(gpsIdleTime > 0)
				gpsIdleTime = 0;
		}
		
		if(lastCheckedFix == null)
		{
			//Every fix is better than no fix 
			Log.d(TAG,"No previous fix, accepting this one.");
			lastCheckedFix = location;
			userPosition = location;
			return true;
		}
		//It makes sense to compare new fix with previous only if localization
		//is in a state where accuracy level is ok.
		if(isAccuracyOk)
		{	
			boolean isSameProvider = userPosition.getProvider().equals(location.getProvider());
			
			//Check if location is significantly newer than lastCheckedFix
			long timeDelta = ((location.getElapsedRealtimeNanos() - userPosition.getElapsedRealtimeNanos()))/1000000; //Nanoseconds to milliseconds
			//boolean isSignificantlyNewer = timeDelta >= minUpdateTime/1000-5; //Accept an error of 5 seconds
			Log.d(TAG,"timeDelta: "+timeDelta/1000);
			/*Log.d(TAG,"Checking if new location is significantly newer. MinUpdateTime: " + minUpdateTime/1000);
			if(!isSignificantlyNewer)
			{ 
				Log.d(TAG,"New location is not significantly newer.");
		
				return false;
			}
			Log.d(TAG, "New location is significantly newer.");
		    */
			
		    //Check if accuracy of new location is adequate.
		    Log.d(TAG, "Checking new location accuracy.");
		    
			float oldAccuracy = userPosition.getAccuracy();
			Log.d(TAG,"oldAccuracy: "+ oldAccuracy);
			float newAccuracy = location.getAccuracy();
			Log.d(TAG,"newAccuracy: "+ newAccuracy);
			int accuracyDelta = (int)(newAccuracy - oldAccuracy);
			boolean isSignificantlyLessAccurate = accuracyDelta > 200;
			boolean isSignificantlyMoreAccurate = accuracyDelta < 200;
			
			if(isSignificantlyLessAccurate && !wifiToCellular && isSameProvider)
			{
				Log.d(TAG,"Previous fix was more accurate.");
		    	return false;
			}
			//wifiToCellular is true if there are no more Wifi nets available.
			//It's necessary to use a boolean because location manager can't distinguish
			//between fixes from Wifi and fixes from network, because both belong to NETWORK_PROVIDER.
			//When wifiToCellular is true I know that less accuracy is caused by the
			//passage from wifi fixes to network fixes.
			if(isSignificantlyLessAccurate && wifiToCellular)
			{
				Log.d(TAG,"wifiToCellular");
				wifiToCellular = false;
				lastCheckedFix=location;
				userPosition=location;
				isAccuracyOk=false;
				return true;
				
			}
			
			Log.d(TAG, "New location accuracy is ok.");
			
			 //Check if new location respects minUpdateDistance
	    	float distanceFromLastCheck = lastCheckedFix.distanceTo(location);
	    	float distanceFromLastPosition= userPosition.distanceTo(location);
	    	Log.d(TAG,"Distance from last check: "+ distanceFromLastCheck +".");
	    	Log.d(TAG,"Distance from last position: "+ distanceFromLastPosition +".");
	    	boolean isDistantEnoughForUpdate = distanceFromLastCheck >= minUpdateDistance-15;
	    	boolean isDistantEnough = distanceFromLastPosition >= 15;
			Log.d(TAG,"Checking if new location is distant enough.");
			//Check if new location is distant enough from last one
			//I also check !isSignificantlyMoreAccurate and !isSignificantlyLessAccurate
			//because it makes no sense to compare distance when accuracies are highly different.
			if(!isDistantEnough && (!isSignificantlyMoreAccurate || !isSignificantlyLessAccurate))
		    {
				Log.d(TAG,"Location is very close to previous");
				standingTime += minUpdateTime; 
				//If user is in the same place for more than 10 mins, probably he is standing there
				if(standingTime > TENMINS)
				{
					Log.d(TAG,"User is standing.");
					handler.removeCallbacks(requestUpdates);
					RemoveUpdates();
					increaseMinUpdateTime();
					handler.postDelayed(requestUpdates,minUpdateTime);
					
			    }
				userPosition.setElapsedRealtimeNanos(location.getElapsedRealtimeNanos());
				return false;
		    }
			else
			{
		    	Log.d(TAG,"Location is not very close to previous.");
		    	
		    	if(standingTime > FIVEMINS)
			   	{
					Log.d(TAG,"User is no more standing.");
			   		standingTime = 0;
					ResetMinUpdateTime();
			   	}
		    	userPosition = location;
			}
			float speed = getSpeed(location, distanceFromLastPosition, timeDelta);//Speed of user movement

			Log.d(TAG,"Checking if new location is distant enough for a new update.");
			if(!isDistantEnoughForUpdate && (!isSignificantlyMoreAccurate || !isSignificantlyLessAccurate) )
			{

			    Log.d(TAG,"New location is not distant enough for a new update.");
			    
	    		
				if(speed>0)															
				{
					setMinUpdateTime((int)((minUpdateDistance-distanceFromLastCheck)/speed));//How long to exit from minUpdateDistance area at this speed?
				}
				Log.d(TAG,"Next location fix in: " + Long.toString(minUpdateTime/1000));
				handler.removeCallbacks(requestUpdates);
				RemoveUpdates();
				handler.postDelayed(requestUpdates, minUpdateTime);
				//If distance is not enough, I update Map but don't search for new hints
			    SendBroadcast(LOCATIONCHANGED, location, location.getProvider(),0,null);
			    return false;
			   
			    
			 }

		 	Log.d(TAG,"Location is distant enough for a new update!");
		   	
			if(speed>0)															
			{
				location.setSpeed(speed);
			}
			lastCheckedFix=location;
			return true;
			
			
			
		}
		else
		{
			Log.d(TAG,"Accuracy not Ok");
			lastCheckedFix = location;
			return true;
		}
	}
	
	/**
	 * Unused
	 */
	public void providerDisabled(String provider)
	{
		SendBroadcast(PROVIDERDISABLED, null, provider,0,null);
	}
	/**
	 * Unused
	 */
	public void providerEnabled(String provider)
	{
		SendBroadcast(PROVIDERENABLED, null, provider,0,null);
	}
	/**
	 * Unused
	 */
	public void statusChanged(String provider, int status, Bundle extras)
	{
		SendBroadcast(STATUSCHANGED, null, provider,status,extras);
	}
	
	/**
	 * Update location provider.
	 * @return	True if provider is avaiable, false otherwise.
	 */
	public boolean UpdateProvider()
    {
		//Change provider if there's one active
				Log.i(TAG, "updateProvider");
		if(!checkLocationManagerNotNull())
			return false;
		
		locationProvider=locationManager.getBestProvider(criteria,true);
		if(!checkLocationProviderNotNull())
			return false;
		
		RemoveUpdates();
		RequestLocationUpdates("update provider");
		
		return true;
    }
	
	
	/**
	 * Set number of hints actually avaiable.
	 * Input 0 to reset.
	 * @param hints		New hints number.
	 */
	public void SetHints(int hints)
	{
		
		hintsShowed = hints;
		if(hintsShowed>0)
		{

			Log.d(TAG,"Hints present: "+Integer.toString(hintsShowed));
		} 
		else
		{
			Log.d(TAG,"No hints present.");
		}
		
		
	}
	
	
/**
 * Update parameters from user settings.
 * 
 * @param arg0	Unused.
 * @param key	Key of changed preference.
 */
	public void onSharedPreferenceChanged(SharedPreferences arg0, String key) {
		/**
		 * Query period updated
		 */
		if(key.equals("queryperiod"))
		{
			Log.d(TAG,"Got query period updated, registering..");
			minUpdateDistance = Float.parseFloat(usersettings.getString("queryperiod", "100"));
			Log.d(TAG," done!");
		}
		if(key.equals("maxdistance"))
		{
			Log.d(TAG,"Got query max hint distance updated, registering..");
			maxHintDistance = Float.parseFloat(usersettings.getString("maxdistance", "100"));
			Log.d(TAG," done!");
		}
		if(key.equals("appmode"))
		{
			Log.d(TAG,"Got query app mode updated, registering..");
			appMode = usersettings.getString("appmode", "learning_mode");
			Log.d(TAG," done!");
			
		}
		
	}
	/**
	 * Obtain app running mode.
	 * @return	Learning mode or Battery Recovery Mode.
	 */
	public String appMode()
	{
		return appMode;
	}
	/**
	 * Returns last location fix avaiable.
	 * 
	 * @return		Last location fix.
	 */
	public Location GetlastCheckedFix()
	{
		return lastCheckedFix;
	}
	/**
	 * Signal to custom location manager that a new hint search started.
	 */
	public synchronized void NewTaskHintsSearch()
	{
		taskHintSearching++;
	}
	/**
	 * Signal to custom location manager that an hint search terminated.
	 * 
	 * @param title		Title of the task.
	 * @param result	List of hints for the task.
	 * @param priority	Priority of the task.
	 */
	public synchronized void TaskHintsSearchFinished(String title, List<Hint> result,int priority)
	{
		pendingHints.add(new pendingHint(title,result,priority));
		taskHintSearching--;
	}
	/**
	 * Check if there are hints searches running.
	 * @return	True if there are, false otherwise.
	 */
	public boolean MoreHintsSearch()
	{
		if(taskHintSearching==0)
			return false;
		else
			return true;
	}
	/**
	 * Obtain min update distance.
	 * @return min update distance.
	 */
	public float GetMinUpdateDistance()
	{
		return minUpdateDistance;
	}
	/**
	 * Obtain max hint distance.
	 * @return max hint distance.
	 */
	public float GetMaxHintDistance()
	{
		return maxHintDistance;
	}
	/**
	 * Thread that implements hierarchical localization
	 */
	private Runnable thread =new Runnable() 
    {
		//TODO
        public void run() 
        {
        	while(true)
        	{
        		Log.d(TAG,"evaluateThread stopped.");
        		evaluateThreadStop.block();
	        	Log.d(TAG,"evaluateThread started!");
	        	evaluateThreadStop.close();
	        	
	    		if(hintsShowed==0)
	    		{ 
	    			Log.d(TAG, "There are no hints in the area.");
	    			int lastCheckedFixAccuracy = (int)lastCheckedFix.getAccuracy();
	    			if(lastCheckedFixAccuracy < 50)
	    			{
	    				isAccuracyOk=true;
	    			}
	    			SendBroadcast(LOCATIONCHANGED, lastCheckedFix, lastCheckedFix.getProvider(),0,null);
	    			gpxBuilder.append(lastCheckedFix, Calendar.getInstance().getTime());
	    			//If there are no hints try to disable GPS
	    			synchronized(gpsLock)
		    		{
	    				ResetMinUpdateTime();
		    			if(locationProvider.equals(LocationManager.GPS_PROVIDER))
		    			{
		    				Log.d(TAG,"Gps enabled with no hints! Disabling.");
		    				handler.removeCallbacks(requestUpdates);
		    				handler.removeCallbacks(gpsTimeout);
		    				locationProvider = LocationManager.NETWORK_PROVIDER;
		    				RemoveUpdates();
		    				gpxBuilder.newtrkSeg(Calendar.getInstance().getTime());
		    			}
		    			handler.postDelayed(requestUpdates,minUpdateTime);
		    		}
	    		}
	    		else
	    		{
	    			Log.d(TAG,"There are hints in the Accuracy+MaxHintDistance area.");
	    			 
	    			int lastCheckedFixAccuracy = (int)lastCheckedFix.getAccuracy();
	    			 
	    			if(lastCheckedFixAccuracy > 100)
	    			{
	    				isAccuracyOk=false;
	    				
	    				ResetMinUpdateTime();
	    				
	    				Log.d(TAG,"Last location fix accuracy is not enough.");
	    				
	    				//if lastCheckedFixAccuracy > 50 should be always true, but not sure
	    				if(lastCheckedFix.getProvider().equals(LocationManager.NETWORK_PROVIDER))
	    				{
	    					synchronized(wifiLock)
    						{
		    					if(!wifiManager.isWifiEnabled() && !wifiAlreadyAsked)
		    					{
		    						
			    						Log.d(TAG,"Asking user to activate wifi.");
			    						wifiAlreadyAsked=true;
			    						handler.removeCallbacks(requestUpdates);
			    						RemoveUpdates();
			    						TaskNotification.getInstance().sendWifiNotification(applicationContext);
			    						Log.d(TAG,"Thread is going to stop waiting for user answer!");
			    						ActionTracker.wifiRequest(Calendar.getInstance().getTime(), lastCheckedFix, applicationContext);
			    						handler.postDelayed(wifiRequestTimeout, FIVEMINS);
			    						try 
			    						{
			    							waitingForWifiAnswer = true;
											wifiLock.wait();
										} 
			    						catch (InterruptedException e) 
			    						{
											e.printStackTrace();
										}
			    						Log.d(TAG,"Thread unlocked.");
			    						handler.removeCallbacks(wifiRequestTimeout);
		    						
		    						if(wifiManager.isWifiEnabled())
		    						{
		    							ActionTracker.wifiResponse(Calendar.getInstance().getTime(), lastCheckedFix, "POSITIVE", applicationContext);
		    							pendingHints.clear();
		    							handler.post(requestUpdates);
		    							continue;
		    						}
		    						else
		    						{
		    							ActionTracker.wifiResponse(Calendar.getInstance().getTime(), lastCheckedFix, "NEGATIVE", applicationContext);
		    						}
		    					} 
		    					else 
		    					{
		    						Log.d(TAG,"Wifi is enabled or already asked!");
		    						
		    					}
    						}
	    					
	    					synchronized(gpsLock)
    						{
		    					if(haveWifiConnection)
	    						{
	    							
	    							Log.d(TAG,"Have wifi connection!");
	    							//wifiInaccurateFixes++;
	    							//if(wifiInaccurateFixes < 5)
	    							//If there are wifi networks I don't need GPS
	    							continue;
	    							
	    						}
		    					
	    						
		    					if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		    					{
		    						
		    						if(!gpsAlreadyAsked)
		    						{
			    						Log.d(TAG,"Asking user to activate GPS!");
			    						gpsAlreadyAsked=true;
			    						handler.removeCallbacks(requestUpdates);
			    						RemoveUpdates();
			    						TaskNotification.getInstance().sendGpsNotification(applicationContext);
			    						Log.d(TAG,"Thread is going to stop waiting for user answer!");
			    						ActionTracker.gpsRequest(Calendar.getInstance().getTime(), lastCheckedFix, applicationContext);
			    						handler.postDelayed(gpsRequestTimeout,FIVEMINS);
			    						try 
			    						{
											gpsLock.wait();
										} 
			    						catch (InterruptedException e) 
			    						{
											e.printStackTrace();
										}
			    						Log.d(TAG,"Thread unlocked!");
			    						handler.removeCallbacks(gpsRequestTimeout);
		    						}
		    						
		    						if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		    						{
		    							ActionTracker.gpsResponse(Calendar.getInstance().getTime(), lastCheckedFix, "POSITIVE", applicationContext);
		    							pendingHints.clear();
		    							Log.d(TAG,"Gps is enabled!");
		    							locationProvider = LocationManager.GPS_PROVIDER;
		    							handler.post(requestUpdates);
		    							continue;
		    						}
		    						else
		    							ActionTracker.gpsResponse(Calendar.getInstance().getTime(), lastCheckedFix, "NEGATIVE", applicationContext);
		    						
		    					}
		    					else
		    					{
		    						Log.d(TAG,"Gps is enabled! Registering.");
		    						handler.removeCallbacks(requestUpdates);
		    						RemoveUpdates();
		    						locationProvider = LocationManager.GPS_PROVIDER;
		    						handler.post(requestUpdates);
		    						continue;
		    					}
	    					}
	    					
	    					
							Log.d(TAG,"Neither Wifi nor Gps enabled!");
							for(pendingHint p : pendingHints)
		    				{
		    					TaskNotification.getInstance().notifyHints(p.title, p.result, p.priority);
		    				}
		    				pendingHints.clear();
		    				SendBroadcast(LOCATIONCHANGED, lastCheckedFix, lastCheckedFix.getProvider(),0,null);
							
		    				handler.postDelayed(requestUpdates, minUpdateTime);
    						
	    					
    					}
	    			}
	    			else
	    			{ 
	    				isAccuracyOk = true;
	    				//if(locationProvider.equals(LocationManager.NETWORK_PROVIDER) && haveWifiConnection)
	    				//	wifiInaccurateFixes=0;
	    				Log.d(TAG,"Accuracy is enough!");
	    				SendBroadcast(LOCATIONCHANGED, lastCheckedFix, lastCheckedFix.getProvider(),0,null);
	    				gpxBuilder.append(lastCheckedFix, Calendar.getInstance().getTime());
	    				for(pendingHint p : pendingHints)
		    			{
		    				TaskNotification.getInstance().notifyHints(p.title, p.result, p.priority);
		    			}

		    			pendingHints.clear();
	    				if(lastCheckedFix.getSpeed()>0)
	    				{
	    					setMinUpdateTime((int)(minUpdateDistance/lastCheckedFix.getSpeed()));
	    					Log.d(TAG, "minUpdateTime: " + minUpdateTime/100);
	    					handler.removeCallbacks(requestUpdates);
	    					handler.postDelayed(requestUpdates,minUpdateTime);
	    				}
	    				
	    			}
	    		}
        	}
        }
    };
  
  /**
   * Unlock the thread when user answer to wifi activation request.
   */
    public void UnlockEvaluateThreadWifi()
    {
    	synchronized(wifiLock)
    	{
	    	Log.i(TAG,"unlockEvaluateThreadWifi");
	    	wifiLock.notify();
    	}
    }
    public boolean isAccuracyOk()
    {
    	return isAccuracyOk;
    }
    /**
     * Unlock the thread when user answer to gps activation request.
     */
    public void UnlockEvaluateThreadGps()
    {
    	synchronized(gpsLock)
    	{
	    	Log.i(TAG,"unlockEvaluateThreadGps");
	    	gpsLock.notify();
    	}
    }
    /**
     * Starts thread that performs evaluations
     */
	public void EvaluateAccuracy()
	{
		 evaluateThreadStop.open();
	}
	/**
	 * Perform operations when there are no tasks
	 */
	public void NoTasks()
	{
		Log.d(TAG,"noTasks");
		SendBroadcast(LOCATIONCHANGED, lastCheckedFix, lastCheckedFix.getProvider(),0,null);
		if(appMode.equals("battery_rec_mode"))
		{
			Log.d(TAG,"Battery Recovery mode and no tasks! Stopping updates untill a new Task is added!");
			RemoveUpdates();
			return;
		}
		else
			Log.d(TAG,"Learning mode.");
		gpxBuilder.append(lastCheckedFix, Calendar.getInstance().getTime());
		
		synchronized(gpsLock)
		{
			if(locationProvider.equals(LocationManager.GPS_PROVIDER))
			{
				Log.d(TAG,"GPS is not needed because there are no tasks!");
				locationProvider = LocationManager.NETWORK_PROVIDER;
				RemoveUpdates();
				handler.postDelayed(requestUpdates, minUpdateTime);
			}
		}
	}
	/**
	 * When customLocationManager is destroyed.
	 */
	public void onDestroy()
	{
		Log.d(TAG,"Destroy.");
		
		RemoveUpdates();
		gpxBuilder.closeFile(Calendar.getInstance().getTime());
	}
}
