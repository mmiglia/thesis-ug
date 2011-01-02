package businessobject;

import java.util.List;

import valueobject.GroupData;


/**
 * This class provides abstraction of all the methods that needs to be
 * implemented in the group database service
 */
public interface GroupInterface {

	/*
	 * Add the group to the database and set the owner, finally add the owner as group member
	 */
	public int createGroup(String groupName, String owner);
	
	/*
	 * Add the invite to group request to the database
	 */
	public boolean  inviteToGroup(String sender,String username,String groupID, String message);
	
	
	/*
	 * Get from the database the list of all the group in wich the user is involved
	 */
	public List<GroupData> getUserGroups(String username);
	
	/*
	 * Accept the user group join request
	 */
	public boolean acceptGroupInviteRequest(String requestId);
	
}
