package com.thesisug.widget;
import java.util.ArrayList;
import java.util.LinkedHashMap;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.thesisug.communication.valueobject.Hint;
import com.thesisug.notification.TaskNotification;
import com.thesisug.ui.HintList;
import com.thesisug.ui.ParentTab;
import com.thesisug.ui.Todo;
import com.thesisug.R;

public class ExampleAppWidgetProvider extends AppWidgetProvider {
	private static final String TAG="thesisug - Widget";
	public static final String ACTION_HINT_SEARCH = "SearchForHint";
	public static final String ACTION_OPEN_HINT_LIST="OpenHintList";
	public static final String ACTION_NEW_HINT_FOUND="NewHintFound";
	public static final String ACTION_NO_HINT_FOUND="NoHintsFound";
	public static final String ACTION_NEXT_TASK="NextTask";
	public static final String ACTION_PREV_TASK="PrevTask";
	public static final String ACTION_NEXT_HINT="NextHint";
	public static final String ACTION_PREV_HINT="PrevHint";
	
	private static Intent openHintListIntent;
	
	private static int tot=0;
	private long triggeFirstTime=1000;
	//Auto-message intervall
	private long triggeInterval=60000;
	
	private static ArrayList<Hint> hintList;
	//Used to read the hintList 
	private static int currHint=0;
	//Used to read the taskList
	private static String taskSentence;
	private static int currTask=0;
	
	private static LinkedHashMap<String,ArrayList<Hint>> taskHintsMap=new LinkedHashMap<String,ArrayList<Hint>>();
	
	
	@Override
	 public void onReceive(Context context, Intent intent) {
	  // TODO Auto-generated method stub
	  super.onReceive(context, intent);

	  Log.d(TAG,"Widget onReceive...start, intent:"+intent.hashCode());
	   
	    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
	    ComponentName thisAppWidget = new ComponentName(context.getPackageName(), ExampleAppWidgetProvider.class.getName());
	    int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
	    final int N = appWidgetIds.length;
	    String status="";
	    /*
	    if(TaskNotification.userLocation==null){
	    	status="No location available";
	    }else{
	    	status="Lat:"+TaskNotification.userLocation.getLatitude()+" Lon:"+TaskNotification.userLocation.getLongitude();
	    }

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateText(status,context,appWidgetManager,appWidgetId);
        }
        */
        final String action = intent.getAction();
        if(action==null){
        	Log.d(TAG, "NO ACTION!");
        	return;
        }else{
        	Log.d(TAG, "Action:"+action);
        }
        
        if (action.equals(ACTION_HINT_SEARCH)) {
        	startHintSearch(context);
        }        
        
        if (action.equals(ACTION_NEW_HINT_FOUND)) {      	
        	taskSentence=intent.getExtras().getString("task");
        	
        	Log.d(TAG, "received taskSentence:"+taskSentence);
        	hintList = intent.getExtras().getParcelableArrayList("hints");
        	
        	//Put data into the map
        	taskHintsMap.put(taskSentence, hintList);
        	/*
        	Log.d(TAG, "taskHintsMap size:"+taskHintsMap.size());
        	Log.d(TAG, "hintList:"+hintList.size());
        	if(hintList==null){
        		Toast.makeText(context.getApplicationContext(),"no hints!",Toast.LENGTH_SHORT).show();
        	}else{
        		Toast.makeText(context.getApplicationContext(),"YES we got "+hintList.size()+" hints!",Toast.LENGTH_SHORT).show();
        	}
        	*/
        	updateText(context,R.id.widget_txt_task,taskSentence,appWidgetManager,appWidgetIds);

        	//Setting the first hint text for the current task
        	String hintSentence=getFirstHintForTheTask(taskSentence);
        	//Update hint txt
        	updateText(context,R.id.widget_txt_hint,hintSentence,appWidgetManager,appWidgetIds);
        	
        }
        
        if (action.equals(ACTION_NO_HINT_FOUND)) {

        	
        }

        
        if(action.equals(ACTION_NEXT_TASK)){
        	//Setting the task text
        	Log.d(TAG, "# Task in taskHintsMap:"+taskHintsMap.size());
        	Log.d(TAG, "currTask:"+currTask);
        	if(taskHintsMap.isEmpty()){
        		updateText(context,R.id.widget_txt_hint,"No hints for tasks",appWidgetManager,appWidgetIds);        		
        		return;
        	}
        	
        	//If the map has 1 elem it's the number 0 so the last task available is the taskHintMap.size-1
        	if(currTask<taskHintsMap.size()-1){
        		currTask++;
        	}else{
        		currTask=0;
        	}
        	Log.d(TAG, "now currTask is "+currTask);
        	String currTaskSentence=taskHintsMap.keySet().toArray()[currTask].toString();
        	Log.d(TAG, "currTaskSentence:"+currTaskSentence);
        	updateText(context,R.id.widget_txt_task,currTaskSentence,appWidgetManager,appWidgetIds);

        	//Setting the first hint text for the current task
        	String hintSentence=getFirstHintForTheTask(currTaskSentence);
        	//Update hint txt
        	updateText(context,R.id.widget_txt_hint,hintSentence,appWidgetManager,appWidgetIds);
        	
        	//Update openHintListButton
        	setOpenHintListButtonElements(context,currTaskSentence,getHintListForTheTask(currTaskSentence));
        }
        
        
        
        if(action.equals(ACTION_PREV_TASK)){
        	Log.d(TAG, "Task in list:"+taskHintsMap.size());
        	if(taskHintsMap.isEmpty()){
        		updateText(context,R.id.widget_txt_hint,"No hints for tasks",appWidgetManager,appWidgetIds);        		
        		return;
        	}
        	
        	
        	if(currTask>0){
        		currTask--;
        	}else{
        		currTask=taskHintsMap.size()-1;
        	}
        	
        	
        	Log.d(TAG, "now  currTask="+currTask);
        	
        	Log.d(TAG,"We got"+taskHintsMap.keySet().toArray().length+" tasks in the keySet(), we choose the number "+ currTask);
        	
        	String currTaskSentence=getCurrTaskSentence();
        	//Update task txt
        	updateText(context,R.id.widget_txt_task,currTaskSentence,appWidgetManager,appWidgetIds);
        	
        	//Setting the first hint text for the current task
        	String hintSentence=getFirstHintForTheTask(currTaskSentence);
        	//Update hint txt
        	updateText(context,R.id.widget_txt_hint,hintSentence,appWidgetManager,appWidgetIds);

        	//Update openHintListButton
        	setOpenHintListButtonElements(context,currTaskSentence,getHintListForTheTask(currTaskSentence));
        	
        }

        if(action.equals(ACTION_NEXT_HINT)){
        	
        	String currTaskSentence=getCurrTaskSentence();
        	if(currTaskSentence==null){ 
        		updateText(context,R.id.widget_txt_hint,"No Hints",appWidgetManager,appWidgetIds);
        	}else{
	        	//Setting the first hint text for the current task
	        	String hintSentence=getNextHintForTask(currTaskSentence);
	        	updateText(context,R.id.widget_txt_hint,hintSentence,appWidgetManager,appWidgetIds);
        	}        	
        }
        
        if(action.equals(ACTION_PREV_HINT)){
        	
        	String currTaskSentence=getCurrTaskSentence();
        	if(currTaskSentence==null){ 
        		updateText(context,R.id.widget_txt_hint,"No Hints",appWidgetManager,appWidgetIds);
        	}else{
        		//Setting the first hint text for the current task
        		String hintSentence=getPrevHintForTask(currTaskSentence);
        		updateText(context,R.id.widget_txt_hint,hintSentence,appWidgetManager,appWidgetIds);
        	}
        	
        }

       
        Log.d(TAG,"Widget onReceive...done");  
	 }
	
	private void setOpenHintListButtonElements(Context context,String taskSentence,ArrayList<Hint> hintList){
    	openHintListIntent.putParcelableArrayListExtra("hints", hintList);
    	openHintListIntent.putExtra("tasktitle", taskSentence);
    	openHintListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	
	}
	
	
	private String getCurrTaskSentence(){
		if(!taskHintsMap.keySet().isEmpty()){
			return taskHintsMap.keySet().toArray()[currTask].toString();
		}else{
			return null;
		}
	}
	
	private ArrayList<Hint> getHintListForTheTask(String currTaskSentence){
		return taskHintsMap.get(currTaskSentence);
	}

	private String getFirstHintForTheTask(String currTaskSentence){
    	
    	currHint=0;
    	String hintSentence="";
    	ArrayList<Hint> currentTaskHintList=getHintListForTheTask(currTaskSentence);
    	//There are hints for the task?
    	if(!currentTaskHintList.isEmpty()){
    		hintSentence=currentTaskHintList.get(currHint).title;
    	}else{
    		hintSentence="No hints for the task";
    	}
    	
    	return hintSentence;
	}
	
	/**
	 * 
	 * @param currTaskSentence
	 * @return the hint title
	 */
	private String getNextHintForTask(String currTaskSentence){
		String hintSentence="No hint found";
		ArrayList<Hint> hintList=taskHintsMap.get(currTaskSentence);
		
		//Are we at the end of the list?
		if(currHint+1>hintList.size()-1){
			//Return the first element
			currHint=0;
		}else{
			currHint++;
		}
		
		
		
    	//There are hints for the task?
    	if(!taskHintsMap.get(currTaskSentence).isEmpty()){
    		hintSentence=hintList.get(currHint).title;
    	}else{
    		hintSentence="No hints for the task";
    	}
    	
    	return hintSentence;
		
	}
	
	private String getPrevHintForTask(String currTaskSentence){
		String hintSentence="No hint found";
		ArrayList<Hint> hintList=taskHintsMap.get(currTaskSentence);
		
		//Are we at the end of the list?
		if(currHint-1<0){
			//Return the first element
			currHint=hintList.size()-1;
		}else{
			currHint--;
		}
		
    	//There are hints for the task?
    	if(!taskHintsMap.get(currTaskSentence).isEmpty()){
    		hintSentence=hintList.get(currHint).title;
    	}else{
    		hintSentence="No hints for the task";
    	}
    	
    	return hintSentence;
		
	}
	
	
	public void startHintSearch(Context context){
		String sentence=(String) context.getText(R.string.searching_around_here);
		Todo.speakIt(sentence);
		boolean checkMinDistanceInHintSearch=false;
		TaskNotification.getInstance().startHintSearch(null,checkMinDistanceInHintSearch);
		Toast.makeText(context.getApplicationContext(), R.string.hint_search_started, Toast.LENGTH_SHORT).show();
	}

	
	public void updateText(Context context,int widget_txt_id,String value,AppWidgetManager appWidgetManager,int[] appWidgetIds){
        // Get the layout for the App Widget and attach an on-click listener to the button
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
        //views.setOnClickPendingIntent(R.id.button, pendingIntent);
        views.setTextViewText(widget_txt_id, value);
        
        
        // Tell the AppWidgetManager to perform an update on the current App Widget
        appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
	

	
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        Log.d(TAG, "onUpdate");
       
        if(tot==0){
        	Log.d(TAG, "Setup");
        	
	        Intent intent = new Intent(context, ExampleAppWidgetProvider.class);
	        PendingIntent pIntend=PendingIntent.getBroadcast(context, 0, intent, 0);      
	        AlarmManager am = (AlarmManager)context.getSystemService(android.content.Context.ALARM_SERVICE);        
	        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggeFirstTime, triggeInterval, pIntend);
	        
	        
	        //ButtonSearchForHints
	        Intent searchHintIntent = new Intent(context, ExampleAppWidgetProvider.class);
	        searchHintIntent.setAction(ACTION_HINT_SEARCH);
	        PendingIntent btnSearchHintPendingIntent = PendingIntent.getBroadcast(context, 0, searchHintIntent, 0);
	        
	        //Button todoList
	        Intent todoListIntent = new Intent(context, ParentTab.class);
	        PendingIntent btnTodoListPendingIntent = PendingIntent.getActivity(context, 0, todoListIntent, 0);
	        
	        
	        //Button nextTask
	        Intent nextTaskIntent = new Intent(context, ExampleAppWidgetProvider.class);
	        nextTaskIntent.setAction(ACTION_NEXT_TASK);
	        PendingIntent btnNextTaskIntentPendingIntent = PendingIntent.getBroadcast(context, 0, nextTaskIntent, 0);
	        
	        //Button prevTask
	        Intent prevTaskIntent = new Intent(context, ExampleAppWidgetProvider.class);
	        prevTaskIntent.setAction(ACTION_PREV_TASK);
	        PendingIntent btnPrevTaskIntentPendingIntent = PendingIntent.getBroadcast(context, 0, prevTaskIntent, 0);
	              
	        
	        //Button nextHint
	        Intent nextHintIntent = new Intent(context, ExampleAppWidgetProvider.class);
	        nextHintIntent.setAction(ACTION_NEXT_HINT);
	        PendingIntent btnNextHintIntentPendingIntent = PendingIntent.getBroadcast(context, 0, nextHintIntent, 0);
	        		
	        		
	        //Button prevHint
	    	Intent prevHintIntent = new Intent(context, ExampleAppWidgetProvider.class);
	    	prevHintIntent.setAction(ACTION_PREV_HINT);
	        PendingIntent btnPrevHintIntentPendingIntent = PendingIntent.getBroadcast(context, 0, prevHintIntent, 0);

	        //Button Hint list
        	openHintListIntent = new Intent(context.getApplicationContext(), HintList.class);
        	openHintListIntent.putParcelableArrayListExtra("hints", null);
        	openHintListIntent.putExtra("tasktitle", "No task selected");
        	openHintListIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);	    	
        	PendingIntent btnHintListPendingIntent = PendingIntent.getActivity(context, 0, openHintListIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	        
	        //SetOnClickPendingIntent for each button
	        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
	        views.setOnClickPendingIntent(R.id.widget_btn_search_hint, btnSearchHintPendingIntent);
	        views.setOnClickPendingIntent(R.id.widget_btn_hint_list, btnHintListPendingIntent);
	        views.setOnClickPendingIntent(R.id.widget_btn_next_task, btnNextTaskIntentPendingIntent);
	        views.setOnClickPendingIntent(R.id.widget_btn_prev_task, btnPrevTaskIntentPendingIntent);
	        views.setOnClickPendingIntent(R.id.widget_btn_next_hint, btnNextHintIntentPendingIntent);
	        views.setOnClickPendingIntent(R.id.widget_btn_prev_hint, btnPrevHintIntentPendingIntent);
	        views.setOnClickPendingIntent(R.id.widget_btn_todo_list, btnTodoListPendingIntent);
	        
	        
	        //Set text
	        views.setTextViewText(R.id.widget_txt_task, "TaskList");
	        views.setTextViewText(R.id.widget_txt_hint, "HintList");
	        
	        
	        appWidgetManager.updateAppWidget(appWidgetIds, views);

	        Toast.makeText(context, "Widget setup done", Toast.LENGTH_SHORT).show();
	        Log.d(TAG, "..done");
        }
        tot++;
    }
    
    public void setButtonAction(AppWidgetManager appWidgetManager,Context context, int btnID,PendingIntent btnIntent,int[] appWidgetIds){
    	RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.example_appwidget);
        views.setOnClickPendingIntent(btnID, btnIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}