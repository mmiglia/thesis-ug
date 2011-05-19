package valueobject;

import java.util.List;

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
	
	public String name;
	public int nScreen; //il tipo di schermata
	public int itemActionType; //1=item,0=action
	public List<String> ontologyList;
	public List<String> dbList;

	
	public Item()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param item
	 */
	public Item(String name,int nScreen,List<String> ontologyList,List<String> dbList) 
	{
		this.name = name;
		this.nScreen = nScreen;
		this.ontologyList = ontologyList;
		this.dbList = dbList;
	}

}
