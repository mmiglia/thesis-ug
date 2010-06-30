package valueobject;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
/**
 * This class represents the location hints that will be sent to client
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
	
	@Override
	public String toString() {
		return build(this);
	}

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