package businessobject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide methods to do conversion between different formats of data
 * and standard
 */
public class Converter {
	private final static Logger log = LoggerFactory.getLogger(Converter.class);
	
	/**
	 * Convert from xs:DateTime format to simple Java Calendar
	 * @param xsDateTime xs:DateTime format
	 * @return java calendar object
	 */
	public static Calendar toJavaDate (String xsDateTime){
		log.debug("Converting xs:DateTime "+xsDateTime+" to Java Calendar");
		String toParse = new String(xsDateTime);
		int stringLength = toParse.length();
		// removes the colon ':' at the 3rd position from the end to match ISO 8601
		SimpleDateFormat ISO_8601_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//NOT thread safe
		toParse=toParse.substring(0, stringLength-3)+toParse.substring(stringLength-2,stringLength);
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(ISO_8601_DATE.parse(toParse));
			return now;
		} catch (ParseException e) {
			log.warn("Cannot parse string :"+toParse);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Convert from xs:Time format to time instance TODAY
	 * @param xsTime xs:Time format
	 * @return java calendar object TODAY, same time
	 */
	public static Calendar toJavaTime (String xsTime){
		log.debug("Converting xs:Time "+xsTime+" to Java Calendar");
		String toParse = new String(xsTime);
		int stringLength = toParse.length();
		// removes the colon ':' at the 3rd position from the end to match ISO 8601
		SimpleDateFormat ISO_8601_TIME = new SimpleDateFormat("HH:mm:ssZ");//NOT thread safe
		toParse=toParse.substring(0, stringLength-3)+toParse.substring(stringLength-2,stringLength);
		try {
			Calendar result = Calendar.getInstance();
			result.setTime(ISO_8601_TIME.parse(toParse));
			return result;
		} catch (ParseException e) {
			log.warn("Cannot parse string :"+toParse);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Convert from Calendar Date object to String
	 * @param xsDateTime Calendar Date object
	 * @return string representing ISO8601 Date
	 */
	public static String CalendarDatetoString (Calendar xsDateTime) {
		SimpleDateFormat ISO_8601_DATE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");//NOT thread safe
		log.debug("Converting Calendar Date to xs:Datetime string");
		String result = ISO_8601_DATE.format(xsDateTime.getTime());
		int stringLength = result.length();
		return result.substring(0, stringLength-2)+":"+result.substring(stringLength-2,stringLength);
	}
	
	/**
	 * Convert from Calendar Time object to String
	 * @param xsTime Calendar Time object
	 * @return string representing ISO8601 Time
	 */
	public static String CalendarTimetoString (Calendar xsTime) {
		SimpleDateFormat ISO_8601_TIME = new SimpleDateFormat("HH:mm:ssZ");//NOT thread safe
		log.debug("Converting Calendar Time to xs:Time string");
		String result = ISO_8601_TIME.format(xsTime.getTime());
		int stringLength = result.length();
		return result.substring(0, stringLength-2)+":"+result.substring(stringLength-2,stringLength);
	}
}
