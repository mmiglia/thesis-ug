package businessobject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provide methods to do conversion between different formats of data
 * and standard
 */
public class Converter {
	private final static Logger log = LoggerFactory.getLogger(Converter.class);
	/**
	 * Convert from xs:DateTime format to simple Java Date
	 * @param xsDateTime xs:DateTime format
	 * @return java calendar object
	 */
	public static Calendar toJavaDate (String xsDateTime){
		log.debug("Converting xs:DateTime "+xsDateTime+" to Java Date");
		String toParse = new String(xsDateTime);
		int stringLength = toParse.length();
		// removes the colon ':' at the 3rd position from the end to match ISO 8601
		toParse=toParse.substring(0, stringLength-3)+toParse.substring(stringLength-2,stringLength);
		SimpleDateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		try {
			Calendar now = Calendar.getInstance();
			now.setTime(ISO_8601.parse(toParse));
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
		log.debug("Converting xs:Time "+xsTime+" to Java Date");
		Date parsedDate;		
		String toParse = new String(xsTime);
		int stringLength = toParse.length();
		// removes the colon ':' at the 3rd position from the end to match ISO 8601
		toParse=toParse.substring(0, stringLength-3)+toParse.substring(stringLength-2,stringLength);
		SimpleDateFormat ISO_8601 = new SimpleDateFormat("HH:mm:ssZ");
		try {
			parsedDate= ISO_8601.parse(toParse);
			Calendar now = Calendar.getInstance();
			Calendar result = Calendar.getInstance();
			result.setTime(parsedDate);	
			result.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
			return now;
		} catch (ParseException e) {
			log.warn("Cannot parse string :"+toParse);
			e.printStackTrace();
			return null;
		}
	}
}
