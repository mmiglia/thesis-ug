package valueobject;


import javax.xml.bind.annotation.XmlRootElement;

/* 26-4-2011
 * 
 * This is the basic Item Location object that will be used for communication
 * to clients.
 * 
 * @author: Anuska
 */

@XmlRootElement
public class SingleItemLocation {
	
	public String item;
	public String location;
	public String username;
	public int n_views;
	public int n_votes;
	public double vote;
	
	public SingleItemLocation(){
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param item
	 * @param location :location where the item can be found in
	 * @param username :username of the user that has entered the Item-Location couple
	 * @param n_visualizzazioni :number of time that the Item-Location has been visualized
	 * @param n_voti :number of people that have voted for this Item-Location
	 */
	public SingleItemLocation(String item,String location, String username,
			int n_views,int n_votes,double vote) {
	
		this.item = item;
		this.location = location;
		this.username = username;
		this.n_views = n_views;
		this.n_votes = n_votes;
		this.vote = vote;
	}

	/*
	public SingleItemLocation copy() {
		SingleItemLocation newSingleItemLocation=new SingleItemLocation();
		newSingleItemLocation.item = this.item;
		newSingleItemLocation.location = this.location;
		newSingleItemLocation.username = this.username;
		newSingleItemLocation.n_views = this.n_views;
		newSingleItemLocation.n_votes = this.n_votes;
		newSingleItemLocation.vote = this.vote;
		return newSingleItemLocation;
	}
*/	
}
