package com.thesisug.communication.valueobject;




/**
 * Value returned after client attempts to login.
 */

public class TestConnectionReply {
	/**
	 * 1 is successful login, 0 is unsuccessful login
	 */
	public int status;
	
	public String serverURI;

	/**
	 * Constructor for the class
	 * 
	 * @param status: 0 if connection is not available, 1 if is available 
	 *            

	 */
	public TestConnectionReply(int status,String serverURI) {
		this.status = status;
		this.serverURI=serverURI;
	}
	
	public TestConnectionReply(){
		this.status=0;
		this.serverURI="";
	}
	
}
