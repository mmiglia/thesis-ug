package valueobject;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Place {
	
	public String title;
	public String lat; 
	public String lng; 
	public String streetAddress;
	public String streetNumber;
	public String city;
	

	public Place()
	{
		super();
	}
	
	/**
	 * Constructor for this class	 
	 * @param Place
	 */
	public Place(String title,String lat,String lng,String streetAddress,String streetNumber, String city) 
	{
		this.title = title;
		this.lat = lat;
		this.lng = lng;
		this.streetAddress = streetAddress;
		this.streetNumber = streetNumber;
	}
}
