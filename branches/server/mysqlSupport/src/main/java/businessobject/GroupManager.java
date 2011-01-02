package businessobject;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.GroupDatabase;


import valueobject.GroupData;

public class GroupManager implements GroupInterface{
	private final static Logger log = LoggerFactory
	.getLogger(GroupManager.class);
	
	private static class InstanceHolder {
		private static final GroupManager INSTANCE = new GroupManager();
	}
	
	public static GroupManager getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	@Override
	public int createGroup(String groupName, String owner) {
		return GroupDatabase.instance.createGroup(groupName, owner);
		
	}

	@Override
	public boolean inviteToGroup(String sender,String receiver, String groupID, String message) {
		return GroupDatabase.instance.inviteToGroup(sender,receiver, groupID, message);
		
	}

	@Override
	public List<GroupData> getUserGroups(String username) {
		return GroupDatabase.instance.getUserGroups(username);
		
	}

	@Override
	public boolean acceptGroupInviteRequest(String requestId) {
		
		return GroupDatabase.instance.acceptGroupInviteRequest(requestId);
	}	
	
	public static void main(String[] args){
		GroupManager manager=new GroupManager();
		String groupName="the_group2";
		String userName="the_owner";
		
		String sender="Babbuino";
		String userToInvite="ZioBibbo";
		
		/*
		 * Create group
		

		
		int groupID=manager.createGroup(groupName,userName);
		System.out.println("Created group "+ groupID);
		*/
		
		/*
		 * Join to group 

		int groupID=1;
		
		manager.inviteToGroup(sender,userToInvite, String.valueOf(groupID) , "Bella "+userToInvite+" ti unisci a noi? By "+sender);
		
		System.out.println("User "+userToInvite+ " invited to the group "+ groupID);
		*/
		
		/*
		 * Get group list

		
		List<GroupData> gruppi=manager.getUserGroups(userName);
		System.out.println(gruppi.size()+" gruppi per l'utente "+userName);
		*/
		
		/*
		 * Accept join group request
		
		manager.acceptGroupInviteRequest("1");
		 */
	}


	
}
