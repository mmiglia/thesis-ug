package com.thesisug.communication.valueobject;



import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is the basic object that is used to get group members's data
 * 
 */
@XmlRootElement
public class SingleItemLocation {
	
	public String item;
	public String location;
	public String username;
	public String n_views;
	public String n_votes;
	public String vote;
	//public String CreatedDate;

	
	public SingleItemLocation(){
		super();
	}
	
	/*
	 * Basic constructor
	 */
	public SingleItemLocation(String item, String location, String username, String n_views,String n_votes, String vote) {

		this.item = item;
		this.location = location;
		this.username=username;
		this.n_views=n_views;
		this.n_votes=n_votes;
		this.vote=vote;
	}
	
	public SingleItemLocation(String item, String location) {

		this.item = item;
		this.location = location;
		this.username="";
		this.n_views="1";
		this.n_votes="1";
		this.vote="0.1";
	}
	
	
	public SingleItemLocation copy(){
		SingleItemLocation ItemLocation=new SingleItemLocation();
		
		ItemLocation.item=this.item;
		ItemLocation.location=this.location;
		ItemLocation.username=this.username;
		ItemLocation.n_views=this.n_views;
		ItemLocation.n_votes=this.n_votes;
		ItemLocation.vote=this.vote;
		return ItemLocation;
	}
	
	
}
