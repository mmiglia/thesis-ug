package businessobject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.RegisteredUsers;
import dao.SessionData;

import valueobject.LoginReply;


/**
 * Business Object that will handle transaction for login process.
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
		if (ID == null) {
			log.info("Username or password is wrong");
			return new LoginReply(0, "-1");
		} else {
			log.info("Found matching record, create new session key for user");
			return new LoginReply(1, SessionData.instance.createSessionforUser(ID));
		}
	}
}
