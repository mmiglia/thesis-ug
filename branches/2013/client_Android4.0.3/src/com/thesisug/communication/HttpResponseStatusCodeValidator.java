package com.thesisug.communication;

/**
 * This class is used to understand if the status code received from the client
 * after an HTTP request is valid or not
 * @author jaxer
 *
 */
public class HttpResponseStatusCodeValidator {

	
	
	public static boolean isValidRequest(int statusCode){
		
		
		if(statusCode< 200 ||statusCode > 300){
			return false;
		}else{
			return true;
		}
		
	}
}
