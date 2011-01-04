package businessobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.RegisteredUsers;

import valueobject.RegistrationReply;

public class RegistrationManager {
	private final static Logger log = LoggerFactory.getLogger(RegistrationManager.class);
	
	public static RegistrationReply register(String firstname, String lastname, String email, 
			String username, String password) {
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
		log.info("Add new user ("+firstname+", "+lastname+", "+email+", "+username+") in db");
		RegisteredUsers.addUsers(firstname, lastname, email, username, password);
		return new RegistrationReply(2);
	}
}
