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
public class GroupInviteData{
	
	public String requestID;
	public String groupID;
	public String groupName;
	public String userToInvite;
	public String message;
	public String sender;
	
	private static final String ELEMENT_NOT_SET="#element_not_set#";
	
	//public List<String> groupMember;
	
	public GroupInviteData(){
		super();
	}

	/*
	 * Basic constructor
	 */
	public GroupInviteData(String requestID,String groupID,String groupName,String userToInvite,String message,String sender) {

		this.groupID=groupID;
		this.userToInvite=userToInvite;
		this.message = message;
		this.sender=sender;
		this.groupName=groupName;
		this.requestID=requestID;
		
	}
	


	public GroupInviteData copy() {
		GroupInviteData newGroupInvite=new GroupInviteData();
		newGroupInvite.groupID=this.groupID;
		newGroupInvite.userToInvite=this.userToInvite;
		newGroupInvite.message=this.message;
		newGroupInvite.sender=this.sender;
		newGroupInvite.groupName=this.groupName;
		newGroupInvite.requestID=this.requestID;
		return newGroupInvite;
	}


	
}
