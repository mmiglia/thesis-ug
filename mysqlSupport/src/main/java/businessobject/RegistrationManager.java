package businessobject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.RegisteredUsers;

import valueobject.RegistrationReply;

public class RegistrationManager {
	
	private final static Logger log = LoggerFactory.getLogger(RegistrationManager.class);
	
	private static String app_name=Configuration.getInstance().constants.getProperty("APP_NAME");
	
	public static RegistrationReply register(String firstname, String lastname, String email, 
			String username, String password){
		log.info("Check for match in database for username ="+username);
		boolean userexist = RegisteredUsers.usernameExist(username);
		if (userexist) {
			log.info("Username '"+username+"' already exist in the db");
			return new RegistrationReply(0);
		}
		log.info("Check for match in database for email ="+email);
		boolean emailexist = RegisteredUsers.useremailExist(email);
		if (emailexist) {
			log.info("Email '"+email+"' already exist in the db");
			return new RegistrationReply(1);
		}
		GregorianCalendar gc = new GregorianCalendar();
		String year = ""+gc.get(Calendar.YEAR);
		int m = gc.get(Calendar.MONTH)+1;
		String month = ""+m;
		if (m<10)
			month="0"+month;
		int d = gc.get(Calendar.DAY_OF_MONTH);
		String day = ""+d;
		if (d<10)
			day="0"+day;
		int h = gc.get(Calendar.HOUR_OF_DAY);
		String hour = ""+h;
		if (h<10)
			hour="0"+hour;
		int min = gc.get(Calendar.MINUTE);
		String minute = ""+min;
		if (min<10)
			minute="0"+minute;
		int s = gc.get(Calendar.SECOND);
		String second = ""+s;
		if (s<10)
			second="0"+second;
		// the verification string is md5 of: thesisug-YYYY-MM-DDThh:mm:ss
		String verification = "thesisug-"+year+"-"+month+"-"+day+"T"+hour+":"+minute+":"+second;
		System.out.println("data:"+verification);
		verification = MD5(verification);
		System.out.println("md5:"+verification);
		System.out.println("parameters: "+firstname+" - "+lastname+" - "+email+" - "+username+" - "+verification);
		log.info("Add new user ("+firstname+", "+lastname+", "+email+", "+username+", "+verification+") in db");
		RegisteredUsers.addUsers(firstname, lastname, email, username, password, verification);
		
		// send account verification email
		String subject = app_name+" registration";
		String body = "Congratulations, the "+app_name+" account has been created.<br />" +
				"Your registration needs to be activated.<br />" +
				"Please click on the link below, or copy it and paste it in your web browser.<br />" +
				"http://serverge.dyndns.org:8080/registration/verification?code="+verification+"&email="+email+"<br />" +
				"<br />" +
				"You have 5 trial login to try the application, after than you must validate your registration to continue using it.<br />" +
				"<br />" +
				"If you didn't register at "+app_name+" please ingnore this message.<br />" +
				"<br />" +
				"Best Regards<br />" +
				""+app_name+" Staff";
		try {
			MailUtility.sendMail(email, subject, body);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new RegistrationReply(2);
	}
	
	public static String MD5(String text) { 
        MessageDigest md = null;
        try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
        byte[] md5hash = new byte[32];
        try {
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        md5hash = md.digest();
        return convertToHex(md5hash);
    } 

    private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    }
    
    public static String verify(String verificationcode, String email) {
    	String result = null;
    	boolean verified = false;
    	verified = RegisteredUsers.verificationExist(verificationcode, email);
    	if (verified) {
    		result = "Registration successful verified.<br />" +
    				"You can now login to "+app_name+" with your username and password.<br />" +
    				"Enjoy with "+app_name+".";
    	}
    	else {
    		result = "Registration verification failed.<br />" +
    				"Verification code not found. Please check your verification url and try again!<br />";
    	}
    	return result;
    }
}
