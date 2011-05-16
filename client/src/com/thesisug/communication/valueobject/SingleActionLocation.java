package com.thesisug.communication.valueobject;



import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is the basic object that is used to get group members's data
 * 
 */
@XmlRootElement
public class SingleActionLocation {
	
	public String action;
	public String location;
	public String username;
	public String n_views;
	public String n_votes;
	public String vote;
	//public String CreatedDate;

	
	public SingleActionLocation(){
		super();
	}
	
	/*
	 * Basic constructor
	 */
	public SingleActionLocation(String action, String location, String username, String n_views,String n_votes, String vote) {

		this.action = action;
		this.location = location;
		this.username=username;
		this.n_views=n_views;
		this.n_votes=n_votes;
		this.vote=vote;
	}
	
	public SingleActionLocation(String action, String location) {

		this.action = action;
		this.location = location;
		this.username="";
		this.n_views="1";
		this.n_votes="1";
		this.vote="0.1";
	}
	
	public SingleActionLocation copy(){
		SingleActionLocation actionLocation=new SingleActionLocation();
		
		actionLocation.action=this.action;
		actionLocation.location=this.location;
		actionLocation.username=this.username;
		actionLocation.n_views=this.n_views;
		actionLocation.n_votes=this.n_votes;
		actionLocation.vote=this.vote;
		return actionLocation;
	}
	
	
}
