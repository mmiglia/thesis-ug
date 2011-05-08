package valueobject;

import javax.xml.bind.annotation.XmlRootElement;
/* 4-5-2011
 *
 * This is the basic Action Location object that will be used for communication
 * to clients.
 * 
 *  @author: Anuska
 */

@XmlRootElement
public class SingleActionLocation {
	
	public String action;
	public String location;
	public String username;
	public int n_views;
	public int n_votes;
	public double vote;
	
	public SingleActionLocation(){
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
	public SingleActionLocation(String action,String location, String username,
			int n_views,int n_votes,double vote)  {
	
		this.action = action;
		this.location = location;
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
