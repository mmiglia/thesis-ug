package valueobject;

import javax.xml.bind.annotation.XmlRootElement;

/* 6-5-2011
 * 
 * This is the basic Action object that will be used for communication
 * to clients.
 * 
 * @author: Anuska
 */

@XmlRootElement
public class Action {
	
	public String action;
	
	public Action()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param action
	 */
	public Action(String action) 
	{
		this.action = action;	
	}

}
