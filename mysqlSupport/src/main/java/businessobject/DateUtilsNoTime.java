package businessobject;

import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateUtilsNoTime {
	
	 public static final String DATE_FORMAT_NOW = "yyyy-MM-dd";

	  public DateUtilsNoTime()
		{
			super();
		}
	  public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());

	  }

}
