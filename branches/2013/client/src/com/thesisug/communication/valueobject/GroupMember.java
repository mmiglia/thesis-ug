package com.thesisug.communication.valueobject;



import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is the basic object that is used to get group members's data
 * 
 */
@XmlRootElement
public class GroupMember{
	
	public String username;
	public String joinDate;
	
	
	//public List<String> groupMember;
	
	public GroupMember(){
		super();
	}

	/*
	 * Basic constructor
	 */
	public GroupMember(String username,String joinDate) {

		this.username=username;
		this.joinDate=joinDate;

	}


	public GroupMember copy(){
		GroupMember newGroupMember=new GroupMember();
		
		newGroupMember.username=this.username;
		newGroupMember.joinDate=this.joinDate;
		
		return newGroupMember;
	}
	
}

