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
	public int n_views;
	public int n_votes;
	public double vote;
	//public String CreatedDate;

	
	public SingleItemLocation(){
		super();
	}
	
	/*
	 * Basic constructor
	 */
	public SingleItemLocation(String item, String location, String username) {

		this.item = item;
		this.location = location;
		this.username=username;
		n_views=0;
		n_votes=0;
		vote=0;
	}
	
	public SingleItemLocation copy(){
		SingleItemLocation ItemLocation=new SingleItemLocation();
		
		ItemLocation.item=this.item;
		ItemLocation.location=this.location;
		ItemLocation.username=this.username;
		ItemLocation.n_views=0;
		ItemLocation.n_votes=0;
		ItemLocation.vote=0;
		return ItemLocation;
	}
	
	
}
