package businessobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.RegisteredUsers;
import dao.SessionData;

import valueobject.LoginReply;



/**
 * Business Object that will handle transaction for login process.
 * 
 */
public class LoginManager {
	private final static Logger log = LoggerFactory.getLogger(LoginManager.class);

	/**
	 * This class checks the database for matching username and password, and
	 * returns new LoginReply object with session number if user credentials is
	 * correct. returns LoginReply Object with status=1 upon successful login,
	 * or 0 upon unsuccessful login. This method will check for registered user
	 * in database, if there is a match, it will create a new session ID and
	 * save it in a SessionData.
	 * 
	 * @param username unique UUID of the user
	 * @param password password of the user
	 * @return LoginReply object
	 */
	public static LoginReply login(String username, String password) {
		// put code to check in database here
		log.info("Check for match in database for username ="+username+" password = "+password);
		String ID = RegisteredUsers.instance.checkMatch(username, password);
		log.info("Found id ("+ID + ") for the user with username "+username);
		int trial_login = RegisteredUsers.instance.checkTrialLogin(ID);
		if (trial_login>0) {
			if (ID == null) {
				log.info("Username or password is wrong");
				return new LoginReply(0, "");
			} else {
				//it would be better, instead of bluntly creating new session token, 
				//to check for session token validity in the database first
				//if there's no valid token, then create new token.
				log.info("Found matching record, create new session key for user");
				return new LoginReply(1, SessionData.instance.createSessionforUser(ID));
			}
		}
		else {
			return new LoginReply(0,"");
			//TODO ritornare un valore che sia compatibile con i messaggi di stato
			//in questo caso deve ritornare un messaggio "controlla la mail e verifica la registrazione prima di procedere"
		}
	}
	
	public static void logout(String username) {
		// put code to check in database here
		log.info("Starting logout for user "+username);
		RegisteredUsers.instance.logout(username);
		log.info("User "+username+" successfully logged out");
		
		
	}
}
