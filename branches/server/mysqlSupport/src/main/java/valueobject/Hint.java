package valueobject;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * This class represents the location hints that will be sent to client.
 * It includes a method (build) that create a string starting from the object passed as parameter.
 * This method is used to ovverride the toString method because of the complexity of the Hint class
 */
@XmlRootElement
public class Hint {
	public String title; // title with HTML formatting
	public String url; //Google local business URL
	public String content; // snippet associated with KML result
	public String titleNoFormatting;//same like title, without HTML formatting
	public String lat;// latitude coordinate of the place
	public String lng;//longitude coordinate of the place
	public String streetAddress; //street address and number for a given result
	public String city;//city name of the result
	public String ddUrl;//url that can be used as a search direction from center of set of result to this result
	public String ddUrlToHere;//url that can be used as a search direction from url supplied location to this result
	public String ddUrlFromHere;//url that can be used as a search direction to url supplied location from this result
	public String staticMapUrl;//url to the static image representation of the current result
	public String listingType; //can be either 'local' for local search listing, or 'KML' for KML listing
	public String region; //region name of the result
	public String country; //country name of the result
	public List<PhoneNumber> phoneNumbers; //list of phone numbers associated with this result
	public List<String> addressLines; //an array consisting mailing address for this result, to support correct rendering of address

	public Hint(){}
	
	/*
	 * 16-5-2011
	 * Costructor for hint in database
	 * @author anuska
	 */
	public Hint(String title,String url,String content,String titleNoFormatting,
			String lat,String lng,String streetAddress,String city,String ddUrl,
			String ddUrlToHere,String ddUrlFromHere,String staticMapUrl,
			String listingType,String region,String country,List<PhoneNumber> phoneNumbers,
			List<String> addressLines)
	{
		this.title = title;
		this.url = url;
		this.content = content;
		this.titleNoFormatting = titleNoFormatting;
		this.lat = lat;
		this.lng = lng;
		this.streetAddress=streetAddress;
		this.city=city;
		this.ddUrl=ddUrl;
		this.ddUrlToHere=ddUrlToHere;
		this.ddUrlFromHere=ddUrlFromHere;
		this.staticMapUrl=staticMapUrl;
		this.listingType=listingType;
		this.region=region;
		this.country=country;
		this.phoneNumbers=phoneNumbers;
		this.addressLines=addressLines;
			
	}
	
	@Override
	public String toString() {
		return build(this);
	}

	/**
	 * Create a string representing the object passed as parameter.
	 * Here it's used in the toString() method of this class and also for PhoneNumbers
	 * @param obj the object to be converted as string (object and fields)
	 * @return a string representing the object and it's fields
	 */
	private static String build(Object obj) {
		StringBuilder builder = new StringBuilder();

		if (obj != null) {
			Class clazz = obj.getClass();
			Field[] fields = clazz.getDeclaredFields();

			if (fields != null) {
				try {
					AccessibleObject.setAccessible(fields, true);
					appendFields(builder, fields, obj);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		return builder.toString();
	}

	/** This is a method that supports the work of the build method .
	 * It appends fields to the string that the build method has to return
	 * 
	 * @param builder a reference to the StringBuilder of the build method, used to retreive the string were we have to append fields strings
	 * @param fields list of the filed that have to be converted to string
	 * @param obj a reference to the object that have to be converted to string by the build method
	 */
	private static void appendFields(StringBuilder builder, Field[] fields,
			Object obj) {
		Class clazz = obj.getClass();
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];

			if (Modifier.isStatic(f.getModifiers())) {
				continue;
			}

			try {
				Object value = f.get(obj);
				if (!f.getName().equalsIgnoreCase("class")) {
					builder.append(clazz.getSimpleName());
					builder.append(".");
					builder.append(f.getName());
					builder.append(": ");
					builder.append(String.valueOf(value));
					builder.append("\n");
				}
			} catch (Exception ignore) {
				// ignored
			}
		}
	}
	
	public static class PhoneNumber
	{
		public String type;
		public String number;
		
		/*
		 * 16-5-2011
		 * Costructor for hint in database
		 * @author anuska
		 */
		public PhoneNumber(String type,String number)
		{
			this.type=type;
			this.number=number;	
		}
		public PhoneNumber()
		{
		}
		
		public String toString()
		{
			return build(this);
		}
	}
	
	public Hint copy() {
		Hint newcopy = new Hint();
		newcopy.city = city;
		newcopy.content = content;
		newcopy.country = country;
		newcopy.ddUrl = ddUrl;
		newcopy.ddUrlFromHere = ddUrlFromHere;
		newcopy.ddUrlToHere = ddUrlToHere;
		newcopy.lat = lat;
		newcopy.listingType = listingType;
		newcopy.lng = lng;
		newcopy.region = region;
		newcopy.staticMapUrl = staticMapUrl;
		newcopy.streetAddress = streetAddress;
		newcopy.title = title;
		newcopy.titleNoFormatting = titleNoFormatting;
		newcopy.url = url;
		PhoneNumber tempphone ;
		for (PhoneNumber o : phoneNumbers){
			tempphone = new PhoneNumber();
			tempphone.type = o.type;
			tempphone.number = o.number;
			newcopy.phoneNumbers.add(tempphone);
		}
		for (String address : addressLines){
			newcopy.addressLines.add(new String(address));
		}
		return newcopy;
	}
	
	
}