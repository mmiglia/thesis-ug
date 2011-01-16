package web;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.GroupData;
import valueobject.GroupInviteData;
import valueobject.GroupMember;
import businessobject.GroupManager;

/**
 * GroupResource is responsible for manage the group into the server.
 * This class uses mainly method of GroupManager class
 */
@Path("/{username}/group")
public class GroupResource {
	private static Logger log = LoggerFactory.getLogger(TaskResource.class);

	/**
	 * Create new group into the database, the id of the new group is set to -1 because will be the DBMS that will
	 * assign it when the group will be stored into the database
	 * 
	 * @param userid the username of the user (will became owner of the group) and will be put into the GroupMember table
	 * @param group is the description of the group that is going to be created (see GroupData class) 
	 * 
	 */
	@POST
	@Path("/create")	
	@Consumes("application/xml")
	public void createGroup(@PathParam("username") String userID,
			@CookieParam("sessionid") String sessionid, GroupData group) {
		log.info("Request to create group from user " + userID + ", session "+ sessionid+ " groupName "+group.groupName);
		group.owner=userID;
		GroupManager.getInstance().createGroup(group);
	}

	/**
	 * Invite a user to join the group
	 * @param senderUserID the username of the user that send the invite
	 * @param invite the description of the join_to_group message, see GroupInviteData for more details
	 * 
	 * 
	 * @return a boolean that reflect the invite status
	 */
	@POST
	@Path("/invite")	
	@Consumes("application/xml")
	public boolean inviteToGroup(@PathParam("username") String senderUserID, GroupInviteData invite,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request from "+senderUserID+" to add the user "+invite.userToInvite+" to the group with id "+ invite.groupID);
		return GroupManager.getInstance().inviteToGroup(senderUserID,invite);
	}	
	
	/**
	 * Get all group where this user is involved to by selecting all the groupID from the GroupMember table
	 * @param userid user id of the user
	 * @param sessionid session token acquired by login
	 * @return list of tasks from this user
	 */
	@GET
	@Path("/list")
	@Produces("application/xml")
	public List<GroupData> getUserGroup(@PathParam("username") String userID,
			@CookieParam("sessionid") String sessionid) {
		log.info("Request to get all group  where this user is involved to from user " + userID + ", session "
				+ sessionid);
		return GroupManager.getInstance().getUserGroups(userID);
	}

	/**
	 * Get all group invite request for the user
	 * @param userID the username that has to be controlled
	 */
	@GET
	@Path("/getJoinGroupRequest")
	@Produces("application/xml")
	public List<GroupInviteData> getJoinGroupRequest(@PathParam("username") String userID,
			@CookieParam("sessionid") String sessionid){
		log.info("Retrieving all join to group request for the user "+ userID);
		return GroupManager.getInstance().getGroupInviteRequest(userID);
	}	
	
	/**
	 * Get all group member
	 * @param groupID the group of witch get the members
	 */
	@GET
	@Path("/{groupID}/getGroupMember")
	@Produces("application/xml")
	public List<GroupMember> getGroupMember(@PathParam("username") String userID,
			@PathParam("groupID") String groupID,@CookieParam("sessionid") String sessionid){
		log.info("User "+ userID+" ask to retrieve all group members for the group "+ groupID);
		return GroupManager.getInstance().getGroupMembers(groupID);
	}	

	/**
	 * Delete from group
	 * @param groupID the group of witch get the members
	 */
	@GET
	@Path("/{groupID}/deleteFromGroup")
	@Produces("application/xml")
	public boolean deleteFromGroup(@PathParam("username") String userID,
			@PathParam("groupID") String groupID,@CookieParam("sessionid") String sessionid){
		log.info("User "+ userID+" ask to be deleted from the group "+ groupID);
		return GroupManager.getInstance().deleteFromGroup(userID,groupID);
	}		
	
	/**
	 * Accept a group invite request and put the user into the GroupMember table, than cancel the join_to_group request
	 */
	@POST
	@Path("/accept")
	@Consumes("application/xml")
	public boolean acceptGroupRequest(@PathParam("username") String userID,
			@CookieParam("sessionid") String sessionid, GroupInviteData invite){
		log.info("Accepting group request to join the group "+invite.groupID+" received by "+userID+" from user "+ invite.sender);
		return GroupManager.getInstance().acceptGroupInviteRequest(invite);
	}
	
	/**
	 * Refuse a group invite request by cancelling the join_to_group request
	 */
	@POST
	@Path("/refuse")
	@Consumes("application/xml")
	public boolean refuseGroupRequest(@PathParam("username") String userID,
			@CookieParam("sessionid") String sessionid, GroupInviteData invite){
		log.info("Refusing group request to join the group "+invite.groupID+" received by "+userID+" from user "+ invite.sender);
		return GroupManager.getInstance().refuseGroupInviteRequest(invite);
	}
	
}
