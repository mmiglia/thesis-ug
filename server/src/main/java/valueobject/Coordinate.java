package valueobject;

public class Coordinate {
	
	private double lat;
	private double lng;
		
	
	public Coordinate() 
	{
		
	}

	public Coordinate(double lat,double lng) 
	{
		
		this.lat = lat;
		this.lng = lng;
	
	}
	
	public double getLat()
	{
		return lat;
	}

	public double getLng()
	{
		return lng;
	}

}
