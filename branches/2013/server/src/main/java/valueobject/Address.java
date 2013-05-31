package valueobject;

public class Address {
	
	private String streetAddress;
	private String streetNumber;
	private String town_hall;
	
	private String city;
	private String region;
	private String state;
	private String cap;
	
	
	public Address() 
	{
		
	}

	public Address(String streetAddress,String streetNumber,String town_hall, String city,String region,String state,String cap) 
	{
		
		this.streetAddress = streetAddress;
		this.streetNumber = streetNumber;
		this.town_hall = town_hall;
		this.city = city;
		this.region = region;
		this.state = state;
		this.cap = cap;
	}
	
	public String getStreetAddress()
	{
		return streetAddress;
	}

	public String getStreetNumber()
	{
		return streetNumber;
	}
	
	public String getTownHall()
	{
		return town_hall;
	}
	
	public String getRegion()
	{
		return region;
	}
	
	public String getCity()
	{
		return city;
	}
	
	public String getState()
	{
		return state;
	}
	public String getCap()
	{
		return cap;
	}
	
	
	public void setStreetAddress(String streetAddress1)
	{
		streetAddress = streetAddress1 ;
	}

	public void setStreetNumber(String streetNumber1)
	{
		streetNumber=streetNumber1;
	}
	
	public void setTownHall(String townHall1)
	{
		town_hall=townHall1;
	}
	
	public void setRegion(String region1)
	{
		region=region1;
	}
	
	public void setCity(String city1)
	{
		city=city1;
	}
	
	public void setState(String state1)
	{
		 state=state1;
	}
	
	public void setCap(String cap1)
	{
		 cap=cap1;
	}
	
	
	
}