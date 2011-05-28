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
public class ItemLocationList {
	
	public String item;
	public List<String> location;
	
	public ItemLocationList()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param item
	 */
	public ItemLocationList(String item,List<String> location) 
	{
		this.item = item;
		this.location = location;
		
	}

}

