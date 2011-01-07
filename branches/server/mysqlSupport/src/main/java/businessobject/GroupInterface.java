package businessobject;

import java.util.List;

import valueobject.GroupData;
import valueobject.GroupInviteData;


/**
 * This class provides abstraction of all the methods that needs to be
 * implemented in the group database service
 */
public interface GroupInterface {

	/*
	 * Add the group to the database and set the owner, finally add the owner as group member
	 */
	public GroupData createGroup(GroupData group);
	
	/*
	 * Add the invite to group request to the database
	 */
	public boolean  inviteToGroup(String sender,GroupInviteData invite);
	
	
	/*
	 * Get from the database the list of all the group in wich the user is involved
	 */
	public List<GroupData> getUserGroups(String username);
	
	/*
	 * Accept the user group join request
	 */
	public boolean acceptGroupInviteRequest(GroupInviteData invite);
	
}
