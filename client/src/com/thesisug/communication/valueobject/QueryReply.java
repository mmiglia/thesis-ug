package com.thesisug.communication.valueobject;


/**
 * This class is used as a reply upon receiving natural language query
 * from user
 */
public class QueryReply {
	public boolean status;
	public QueryReply(){
		status = false;
	}
	public QueryReply(boolean stat){
		status = stat;
	}
}
