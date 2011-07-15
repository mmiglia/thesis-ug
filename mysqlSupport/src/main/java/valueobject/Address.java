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

public class Address {
	
	public ArrayList<Add_Comp_element> address_components;
	
	public String formatted_address;
	
	public Geometry geometry;
	
	public List<String> types;
	
	public Address(){}
	
	public static class Add_Comp_element {
		
		public String long_name;
		public String short_name;
		public List<String> types;
		
		public Add_Comp_element(){}

	}
	
	public static  class Geometry {

		public Bounds bounds;
		
		public Coordinate location;
		
		public String location_type;
		
		public Viewport viewport;
		
		public Geometry(){}
		
		public static class Bounds {
			public Coordinate northeast;
			public Coordinate southwest;
			
			public Bounds(){}
			
		}
		
		public static class Viewport {
			public Coordinate northeast;
			public Coordinate southwest;
			
			public Viewport(){}
		}
		
		

	}
	
}
