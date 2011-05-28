package valueobject;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/* 28-5-2011
 * 
 * This is the basic Item-LocationList object voted by a client that will be used for communication
 * to clients.
 * 
 * @author: Anuska
 */

@XmlRootElement
public class ActionLocationList {
	
	public String action;
	public List<String> location;
	
	public ActionLocationList()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param item
	 */
	public ActionLocationList(String action,List<String> location) 
	{
		this.action = action;
		this.location = location;
		
	}

}