package valueobject;

import javax.xml.bind.annotation.XmlRootElement;

/* 6-5-2011
 * 
 * This is the basic Item object that will be used for communication
 * to clients.
 * 
 * @author: Anuska
 */

@XmlRootElement
public class Item {
	
	public String item;
	
	public Item()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param item
	 */
	public Item(String item) 
	{
		this.item = item;	
	}

}
