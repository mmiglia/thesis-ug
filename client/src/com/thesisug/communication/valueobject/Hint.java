package com.thesisug.communication.valueobject;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * This class represents the location hints that will be sent to client
 */
public class Hint implements Parcelable 
{
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
	public String searchRadius;//Radius of the search area that gave this hint

	public Hint(){
		phoneNumbers = new LinkedList<PhoneNumber>();
		addressLines = new LinkedList<String>();
	}
	
	@Override
	public String toString() {
		return build(this);
	}

	private static String build(Object obj) 
	{
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
		public PhoneNumber copy(){
			PhoneNumber newcopy = new PhoneNumber();
			newcopy.type = type;
			newcopy.number = number;
			return newcopy;
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
		newcopy.searchRadius=searchRadius;
		return newcopy;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		out.writeString(title);
		out.writeString(url);
		out.writeString(content);
		out.writeString(titleNoFormatting);
		out.writeString(lat);
		out.writeString(lng);
		out.writeString(streetAddress);
		out.writeString(city);
		out.writeString(ddUrl);
		out.writeString(ddUrlToHere);
		out.writeString(ddUrlFromHere);
		out.writeString(staticMapUrl);
		out.writeString(listingType);
		out.writeString(region);
		out.writeString(country);
		out.writeInt(phoneNumbers.size());
		for (PhoneNumber o : phoneNumbers){
			out.writeString(o.type);
			out.writeString(o.number);
		}
		out.writeInt(addressLines.size());
		for (String s : addressLines) out.writeString (s);
		out.writeString(searchRadius);
	}
	private void readFromParcel(Parcel in) {
		title = in.readString();
		url = in.readString();
		content = in.readString();
		titleNoFormatting = in.readString();
		lat = in.readString();
		lng = in.readString();
		streetAddress = in.readString();
		city = in.readString();
		ddUrl = in.readString();
		ddUrlToHere = in.readString();
		ddUrlFromHere = in.readString();
		staticMapUrl = in.readString();
		listingType = in.readString();
		region = in.readString();
		country = in.readString();
		int count = in.readInt();
		for (int i=0; i<count;i++){
			PhoneNumber phone = new PhoneNumber();
			phone.type = in.readString();
			phone.number = in.readString();
			phoneNumbers.add(phone);
		} 
		count = in.readInt();
		for (int i=0; i<count; i++)	addressLines.add(in.readString());
		searchRadius=in.readString();
    }

	private Hint(Parcel in){
		this();
		readFromParcel(in);
	}

	public static final Parcelable.Creator<Hint> CREATOR = new Parcelable.Creator<Hint>() {
        public Hint createFromParcel(Parcel in) {
            return new Hint(in);
        }

        public Hint[] newArray(int size) {
            return new Hint[size];
        }
    };
}