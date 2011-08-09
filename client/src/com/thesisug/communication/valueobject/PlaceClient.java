package com.thesisug.communication.valueobject;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;


/**
 * This is the basic object that is used to get group members's data
 * 
 */
@XmlRootElement
public class PlaceClient  implements Serializable{

	
	  public String title;
	  public String lat; 
	  public String lng; 
	  public String streetAddress;
	  public String streetNumber;
	  public String cap;
	  public String city;
	  public String category; //stringa che contiene la lista di categorie 
	                            //separate da una virgola

	  public PlaceClient()
	  {
	      super();
	  }
	  
	  /**
	   * Constructor for this class     
	   * @param Place
	   */
	  public PlaceClient(String title,String lat,String lng,String streetAddress,String streetNumber,String cap, String city,String category) 
	  {
	      this.title = title;
	      this.lat = lat;
	      this.lng = lng;
	      this.streetAddress = streetAddress;
	      this.streetNumber = streetNumber;
	      this.cap = cap;
	      this.city = city;
	      this.category = category;
	  }
	  
	  public PlaceClient(String title,String streetAddress,String streetNumber,String cap, String city,String category) 
	  {
	      this.title = title;
	      this.streetAddress = streetAddress;
	      this.streetNumber = streetNumber;
	      this.cap = cap;
	      this.city = city;
	      this.category = category;
	  }
	  
	  public PlaceClient copy(){
			PlaceClient place=new PlaceClient();
			
			place.title=this.title;
			place.lat=this.lat;
			place.lng=this.lng;
			place.streetAddress=this.streetAddress;
			place.streetNumber=this.streetNumber;
			place.cap=this.cap;
			place.city=this.city;
			place.category = this.category;
			return place;
		}
	
	
	
	
}
