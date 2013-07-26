package com.thesisug.communication.valueobject;

import javax.xml.bind.annotation.XmlRootElement;




/**
 * This is the basic object that is used to manage (send, receive, accept, refuse) join_to_gropu request
 * 
 */
@XmlRootElement
public class GroupInviteData{
	
	//Is set to -1 if the invite is sent from the client to the server
	public String requestID;
	public String groupID;
	public String groupName;
	//username of the user that has to be invited
	public String userToInvite;
	//An optional message that is sent within the invite
	public String message;
	//username of the user that send the invite
	public String sender;
	
	//Default value used as constant to know if an element has been set
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
