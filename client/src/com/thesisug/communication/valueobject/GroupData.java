package com.thesisug.communication.valueobject;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;




/**
 * This is the basic task object that will be used for communications to client.
 * Fiels are:
 * dueDate,notifyTimeStart,notifyTimeEnd 
 * 
 */
@XmlRootElement
public class GroupData{
	
	/**
	 * The id of the group into the database
	 */
	public String groupID;
	public String groupName;
	public String owner;
	
	//public List<String> groupMember;
	
	public GroupData(){
		super();
	}

	/*
	 * Basic constructor
	 */
	public GroupData(String groupID,String groupName,String owner) {

		this.groupID=groupID;
		this.groupName=groupName;
		this.owner = owner;

	}

	public GroupData copy() {
		GroupData newGroup=new GroupData();
		newGroup.groupID=this.groupID;
		newGroup.groupName=this.groupName;
		newGroup.owner=this.owner;
		return newGroup;
	}


	
}
