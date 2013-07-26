package com.thesisug.communication.valueobject;

public class RegistrationReply {
	/**
	 * 1 is successful registration, 0 is unsuccessful registration
	 */
	public int status;
	
	/**
	 * Constructor for the class
	 * 
	 * @param status
	 *            1 for successful registration, 0 for unsuccessful registration
	 */
	public RegistrationReply(int status) {
		this.status = status;
	}
	public RegistrationReply(){
		status= 0;
	}
}
