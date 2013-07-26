package com.thesisug.communication.xmlparser;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.thesisug.communication.valueobject.RegistrationReply;

public class RegistrationReplyHandler {
	private static final String TAG = "thesisug - Login Reply Handler";
	
    public static RegistrationReply parse(InputStream toParse) throws IOException, SAXException {
		RootElement root = new RootElement("registrationReply");
		final RegistrationReply result = new RegistrationReply();
        root.getChild("status").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                result.status = Integer.parseInt(body);
            }
        });
        Log.i(TAG, "parsing RegistrationReply XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return result;
    }
}
