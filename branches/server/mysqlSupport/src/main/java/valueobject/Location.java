package valueobject;

import javax.xml.bind.annotation.XmlRootElement;

/* 6-5-2011
 * 
 * This is the basic Location object that will be used for communication
 * to clients.
 * 
 * @author: Anuska
 */

@XmlRootElement
public class Location {
	
	public String location;
	
	public Location()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param location
	 */
	public Location(String location) 
	{
		this.location = location;	
	}

}

