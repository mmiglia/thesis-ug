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
	public String title;
	public String url;
	public String content;
	public String titleNoFormatting;
	public String lat;
	public String lng;
	public String streetAddress;
	public String city;
	public String ddUrl;
	public String ddUrlToHere;
	public String ddUrlFromHere;
	public String staticMapUrl;
	public String listingType;
	public String region;
	public String country;
	public List<PhoneNumber> phoneNumbers;
	public List<String> addressLines;

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