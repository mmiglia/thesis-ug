package com.thesisug.notification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import com.thesisug.tracking.ActionTracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * Handles notification's snooze.
 * 
 * @author Alberto Servetti
 *
 */
public class SnoozeHandler 
{
	private static final String TAG = "thesisug - SnoozeHandler";

	private static HashMap<String,Date> snoozedNotifications;
	private static Context context;
	private static Object snoozedNotificationsSafe = new Object();
	private static BroadcastReceiver saveSnoozedNotifications = new BroadcastReceiver()
	    {
	        @Override
	        public void onReceive(Context context, Intent intent) 
	        {

	    		Log.i(TAG,"Phone is shutting down");
	    		saveToFile();
	        }
	     };
	     /**
	      * Init SnoozeHandler.
	      * @param c Context.
	      */
	     public static void Init(Context context)
	     {
	    	 Log.i(TAG,"Init");
	    	 SnoozeHandler.context = context;
	    	 if(!checkIfSnoozedFileIsPresent())
	    		 snoozedNotifications = new HashMap<String,Date>();
	    	 IntentFilter intentFilter = new IntentFilter();
	    	 intentFilter.addAction(Intent.ACTION_SHUTDOWN);
	    	 context.registerReceiver(saveSnoozedNotifications, intentFilter);
	     }
	     
	     public static void saveToFile()
	     {
	    	 synchronized(snoozedNotificationsSafe)
	        	{
		        	if(snoozedNotifications != null && !snoozedNotifications.isEmpty())
		        	{
		        		Log.d(TAG,"Saving snoozedNotifications");;
		        		File file = new File(context.getDir("data", Context.MODE_PRIVATE), "snoozedNotifications");    
		        		ObjectOutputStream outputStream;
		        		try 
		        		{
		        			outputStream= new ObjectOutputStream(new FileOutputStream(file));
							outputStream.writeObject(snoozedNotifications);

			        		outputStream.flush();
			        		outputStream.close();
						}
		        		catch (IOException e) 
		        		{
							e.printStackTrace();
						}
		        	}
	        	}
	     }
	     /**
	      * Check is snooze backup file is present in app_data. If present, it's loaded in snoozedNotifications.
	      * @return True if it is present, false otherwise.
	      */
	     @SuppressWarnings("unchecked")
		private static boolean checkIfSnoozedFileIsPresent()
	     {
	    	 File file = new File(context.getDir("data", Context.MODE_PRIVATE), "snoozedNotifications");
	    	 if(file.length()>0)
	    	 {
	    		 Log.d(TAG,"File already exists, copying values.");
	    		 ObjectInputStream inputStream ;
	        		try 
	        		{
	        			inputStream= new ObjectInputStream(new FileInputStream(file));
	        			synchronized(snoozedNotificationsSafe)
	        			{
	        				snoozedNotifications = (HashMap<String, Date>) inputStream.readObject();
	        			}

	        			inputStream.close();
	        			file.delete();
					}
	        		catch (IOException e) 
	        		{
						e.printStackTrace();
					} catch (ClassNotFoundException e) 
					{
						e.printStackTrace();
					}
	    		 return true;
	    	 }
	    	 else
	    		 return false;
	     }
		 /**
		  * Check if there are expired snoozes.   
		  */
	     public static void checkExpiredSnoozes()
	     {
	    	 Log.i(TAG,"checkExpiredSnoozes");
	    	 synchronized(snoozedNotificationsSafe)
		     {
	    		 if(snoozedNotifications == null)
	     		{
	     			Log.d(TAG,"SnoozedNotifications null");
	     			return;
	     		}
	    		 Set<String> keys = snoozedNotifications.keySet();
	    		 for(String s:keys)
	    		 {
	    			 if(Calendar.getInstance().getTime().after(snoozedNotifications.get(s)))
		 				{
		 					Log.d(TAG,"Snooze delay expired for : "+ s);
		 					//If snoozed delay expired I remove the entry
		 					snoozedNotifications.remove(s);
		 				}
	    		 }
		     }
	    	 
	     }
	     /**
	      * Check if a task/event has been snoozed by the user. 
	      * If it has been snoozed and snooze time expired, 
	      * the entry is removed from snoozed tasks list.
	      * @param sentence	Title of the task,
	      * @return
	      */
	     public static boolean checkIfTaskIsSnoozed(String sentence)
	     {
	     	Log.i(TAG,"checkIfTaskIsSnoozed");
	     	boolean ret;
	     	synchronized(snoozedNotificationsSafe)
	     	{
	     		if(snoozedNotifications == null)
	     		{
	     			Log.d(TAG,"SnoozedNotifications null");
	     			return true;
	     		}
	     		if(snoozedNotifications.containsKey(sentence))
	 			{
	 			Log.d(TAG,"There is an entry for "+ sentence + " in snoozedNotifications.");
	 				if(Calendar.getInstance().getTime().after(snoozedNotifications.get(sentence)))
	 				{
	 					Log.d(TAG,"Snooze delay expired.");
	 					//If snoozed delay expired I remove the entry
	 					snoozedNotifications.remove(sentence);
	 					ret=false;
	 				}
	 				else
	 				{
	 					Log.d(TAG,"Task/Event " + sentence +" has been snoozed.");
	 					ret=true;
	 				}
	 			}
	     		else
	     		{
	     			Log.d(TAG,"Task/Event " + sentence + " has not been snoozed.");
	     			ret=false;
	     		}
	     	}
	     	return ret;
	     }
	     /**
	      * Memorize a snooze for a task.
	      * 
	      * @param sentence		Title of the task to be snoozed.
	      * @param delayDate		Date from which task can be notified again.
	      */
	     public static void snoozeTask(String sentence,String type,int delay)
	     {
	     	Log.i(TAG,"snoozeTask");
	     	Date delayDate = getDelayedDate(Calendar.getInstance().getTime(),delay);
	     	synchronized(snoozedNotificationsSafe)
	     	{
	 	    	if(!snoozedNotifications.containsKey(sentence))
	 	    	{
	 	    		Log.d(TAG,sentence + " is going to be added to snooze list.");
	 	    		snoozedNotifications.put(sentence, delayDate);
	 	    		ActionTracker.notificationSnooze(Calendar.getInstance().getTime(), sentence, context, type, delay);
	 	    	}
	 	    	else
	 	    	{
	 	    		//This is necessary because at this moment two different tasks 
	 	    		//can have the same sentence. In future this should be avoided.
	 	    		Log.e(TAG,sentence + " is already present.");
	 	    		snoozedNotifications.remove(sentence);
	 	    		snoozedNotifications.put(sentence, delayDate);
	 	    	}
	     	}
	     }
	     /**
	      * Returns a new date which is input date plus delay.
	      * @param date		Starting date.
	      * @param delay		Delay to add in minutes.
	      * @return			New delayed Date.
	      */
	     private static Date getDelayedDate(Date date,int delay)
	     {
	     	Calendar calInstance = new GregorianCalendar(); 
	 		calInstance.setTime(date);
	 		calInstance.add(Calendar.MINUTE, delay);
	 		return calInstance.getTime();
	     }
	     
	     /**
	      * Returns delayed data formatted as dd/mm/yyyy HH:mm:ss.
	      * @param date		Starting date.
	      * @param delay		Delay to add in minutes.
	      * @return			New delayed Date.
	      */
	     public static String getStringFormattedDelayedDate(String sentence)
	     {
	    	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ITALY);;
	     	
	 		return dateFormat.format(snoozedNotifications.get(sentence));
	     }
	     /**
	      * Dismiss snooze setting for a task.
	      * @param sentence	Title of the task
	      */
	     public static void removeSnooze(String sentence)
	     {
	     	Log.i(TAG,"removeSnooze");
	     	synchronized(snoozedNotificationsSafe)
	     	{
	 	    	if(snoozedNotifications.containsKey(sentence))
	 	    	{
	 	    		Log.d(TAG,sentence + " snooze setting is going to be dismissed.");
	 	    		snoozedNotifications.remove(sentence);
	 	    	}
	     	}
	     }
	     
	     public static void unregister()
	     {
	    	 context.unregisterReceiver(saveSnoozedNotifications);
	     }
	     
}
