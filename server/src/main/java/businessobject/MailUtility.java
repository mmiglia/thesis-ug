package businessobject;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.Configuration;

public class MailUtility {
	
	private final static Logger log = LoggerFactory.getLogger(MailUtility.class);
	
	private static String smtpHost=Configuration.getInstance().constants.getProperty("SMTP_SERVER");
	private static String smtpPort=Configuration.getInstance().constants.getProperty("SMTP_PORT");
	private static String user=Configuration.getInstance().constants.getProperty("SMTP_AUTH_USER");
	private static String pwd=Configuration.getInstance().constants.getProperty("SMTP_AUTH_PWD");
	private static String app_name=Configuration.getInstance().constants.getProperty("APP_NAME");
	private static String type=Configuration.getInstance().constants.getProperty("MAIL_TYPE");

    public static boolean sendMail(String recipient, String subject, String body) throws Exception{
    	boolean sent = false;
    	try {
    		Properties prop = new Properties();
    		prop.put("mail.smtp.port", smtpPort);
    		prop.put("mail.smtp.socketFactory.fallback", "false");
    		prop.put("mail.smtp.quitwait", "false");
    		prop.put("mail.smtp.host", smtpHost);
    		prop.put("mail.smtp.auth", "true");
    		prop.put("mail.smtp.starttls.enable", "true");
     
    		Session session = Session.getDefaultInstance(prop);
     
    		Message msg = new MimeMessage(session);
    		msg.setSubject(subject);
     
    		InternetAddress from = new InternetAddress(user, app_name);
    		InternetAddress to = new InternetAddress(recipient);
    		msg.addRecipient(Message.RecipientType.TO, to);
    		
    		msg.setFrom(from);
     
    		// set reply to email address
    		InternetAddress[] replyToAddress = new InternetAddress[1];
    		replyToAddress[0] = new InternetAddress(user);
    		msg.setReplyTo(replyToAddress);
     
    		Multipart multipart = new MimeMultipart("related");
     
    		BodyPart htmlPart = new MimeBodyPart();
    		if (type.equals("html")) htmlPart.setContent(body, "text/html");
    		else htmlPart.setContent(body, "text/plain");
     
    		multipart.addBodyPart(htmlPart);
    		msg.setContent(multipart);
     
    		log.info("Sending verification mail to: "+recipient);
    		Transport transport = session.getTransport("smtp");
    		transport.connect(user, pwd);
    		transport.sendMessage(msg, msg.getAllRecipients());
    		transport.close();
     
    		sent = true;
     
    	 } catch (Exception e) {
    		 log.info("Failed sending verification mail because:" + e.getMessage());
    		 e.printStackTrace();
    	 }
    	 log.info("Verification mail sent: " + sent);
    	 return sent;

    }
    
    

    
}
