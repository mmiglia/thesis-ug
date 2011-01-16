package valueobject;



import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is the basic object that is used to create and retreive groups
 * 
 */
@XmlRootElement
public class GroupData{
	
	/**
	 * The id of the group into the database, is set to -1 when the client try to create a group because the correct
	 * id will be assigned from the DBMS after the group creation
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
	public GroupData(String groupName,String owner) {
		this("-1",groupName,owner);		
	}

	public GroupData copy(){
		GroupData newGroupData=new GroupData();
		
		newGroupData.groupID=this.groupID;
		newGroupData.groupName=this.groupName;
		newGroupData.owner=this.owner;
		
		return newGroupData;
	}
	
}

