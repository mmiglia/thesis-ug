package com.thesisug.communication;

import com.thesis.communication.valueobject.LoginReply;

public interface LoginResource {

	public LoginReply Authenticate(String username, String password);

}
