package valueobject;

import javax.xml.bind.annotation.XmlRootElement;
/* 4-5-2011
 *
 * This is the basic Location Location object that will be used for communication
 * to clients.
 * 
 *  @author: Anuska
 */

@XmlRootElement
public class SingleLocationLocation {
	
	public String location1;
	public String location2;
	public String username;
	public int n_views;
	public int n_votes;
	public double vote;
	
	public SingleLocationLocation(){
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param action
	 * @param location :location where the action can be made
	 * @param username :username of the user that has entered the Action-Location couple
	 * @param n_visualizzazioni :number of time that the Action-Location has been visualized
	 * @param n_voti :number of people that have voted for this Action-Location
	 */
	public SingleLocationLocation(String location1,String location2, String username,
			int n_views,int n_votes,double vote) {
	
		this.location1 = location1;
		this.location2 = location2;
		this.username = username;
		this.n_views = n_views;
		this.n_votes = n_votes;
		this.vote = vote;
	}

/*	public SingleActionLocation copy() {
		SingleActionLocation newSingleActionLocation=new SingleActionLocation();
		newSingleActionLocation.action = this.action;
		newSingleActionLocation.location = this.location;
		newSingleActionLocation.username = this.username;
		newSingleActionLocation.n_visualizzazioni = this.n_visualizzazioni;
		newSingleActionLocation.n_voti = this.n_voti;
		return newSingleActionLocation;
	}
*/

}
