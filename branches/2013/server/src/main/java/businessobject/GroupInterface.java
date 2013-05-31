package businessobject;

import java.util.List;

import valueobject.GroupData;
import valueobject.GroupInviteData;
import valueobject.GroupMember;


/**
 * This class provides abstraction of all the methods that needs to be
 * implemented in the group database service
 */
public interface GroupInterface {

	/**
	 * Add the group to the database and set the owner, finally add the owner as group member
	 */
	public GroupData createGroup(GroupData group);
	
	/**
	 * Add the invite_to_group request into the database
	 */
	public boolean  inviteToGroup(String sender,GroupInviteData invite);
	
	
	/**
	 * Get from the database the list of all the group in wich the user is member
	 */
	public List<GroupData> getUserGroups(String username);
	
	/**
	 * Accept the user group join request and cancel the join_to_group request
	 */
	public boolean acceptGroupInviteRequest(GroupInviteData invite);

	/**
	 * Refuse the user group join request by cancelling the join_to_group request from the GroupRequest table
	 */
	public boolean refuseGroupInviteRequest(GroupInviteData invite);

	/**
	 * Get all group members
	 * @param groupID
	 * @return a list of GroupMemeber object that describes members
	 */
	public List<GroupMember> getGroupMembers(String groupID);

	/**
	 * Delete the user from the group. If the user is the last member, also the group is deleted
	 * @param userID
	 * @param groupID
	 * @return a boolean that say if the delete operation has gone well
	 */
	public boolean deleteFromGroup(String userID, String groupID);
	
}
