package valueobject;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents the location hints that will be sent to client.
 * It includes a method (build) that create a string starting from the object passed as parameter.
 * This method is used to ovverride the toString method because of the complexity of the Hint class
 */
@XmlRootElement

public class Result {
	
	public Address_components[] address_components;
	
	public String formatted_address;
	
	public Geometry geometry;
	
	public String[] types;
	
	public Result(){}
	
	public Geometry getGeometry()
	{
		return geometry;
		
	}
	

	
	}
