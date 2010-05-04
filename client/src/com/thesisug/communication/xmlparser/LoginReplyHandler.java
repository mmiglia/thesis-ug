package com.thesisug.communication.xmlparser;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.thesis.communication.valueobject.LoginReply;

public class LoginReplyHandler {
	private static final String TAG = "Login Reply Handler";
	
    public static LoginReply parse(InputStream toParse) throws IOException, SAXException {
		RootElement root = new RootElement("loginReply");
		final LoginReply result = new LoginReply();
        root.getChild("status").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                result.status = Integer.parseInt(body);
            }
        });
        root.getChild("session").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                result.session = body;
            }
        });
        Log.i(TAG, "parsing LoginReply XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return result;
    }
}
