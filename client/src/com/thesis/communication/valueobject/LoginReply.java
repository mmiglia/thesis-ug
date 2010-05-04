package com.thesis.communication.valueobject;


/**
 * value returned after client attempts to login.
 */
public class LoginReply {
	/**
	 * 1 is successful login, 0 is unsuccessful login
	 */
	public int status;
	/**
	 * String represents UUID of the session
	 */
	public String session;

	/**
	 * Constructor for the class
	 * 
	 * @param session
	 *            UUID of session
	 * @param status
	 *            1 for successful login, 0 for unsuccessful login
	 */
	public LoginReply(int status, String session) {
		this.status = status;
		this.session = session;
	}
	public LoginReply(){
		status= 0;
		session="0";
	}
}
