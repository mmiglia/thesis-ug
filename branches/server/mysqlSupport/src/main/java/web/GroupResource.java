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
import businessobject.GroupManager;

/**
 * Task GroupResource is responsible for manage the group into the server.
 * This class uses mainly method of GroupManager class
 */
@Path("/{username}/group")
public class GroupResource {
	private static Logger log = LoggerFactory.getLogger(TaskResource.class);

	/**
	 * Create new group into the database
	 * @param userid unique UUID of the user (will became owner of the group)
	 * @return the id of the group, -1 if the creation fails
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
	 * @param userID unique UUID of the user to be invited
	 * @param groupID the id of the group to join
	 * @param message the message to send to the user (optional)
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
	 * Get all group where this user is involved to
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
	 * Get all group invite request
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
	 * Accept a group invite request
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
	 * Refuse a group invite request
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
