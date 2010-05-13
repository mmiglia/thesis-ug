package com.thesisug.communication;

import java.util.List;

import com.thesisug.communication.valueobject.SingleTask;

public interface TaskResource {

	public void createTask(String userid,String sessionid, SingleTask toAdd) ;	
	public List<SingleTask> getAllTasks(String userid,	String sessionid) ;
	public List<SingleTask> getFirstTasks(String userid, String sessionid) ;
	public void updateTasks(String userid, String sessionid, SingleTask oldTask, SingleTask newTask) ;	
	public void removeTasks(String taskID,	String userid, String sessionid) ;
}
