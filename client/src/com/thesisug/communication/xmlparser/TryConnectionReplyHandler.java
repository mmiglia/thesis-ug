package com.thesisug.communication.xmlparser;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.thesisug.Constants;
import com.thesisug.communication.valueobject.LoginReply;
import com.thesisug.communication.valueobject.TestConnectionReply;

public class TryConnectionReplyHandler {
	private static final String TAG = "thesisug - TryConnection Reply Handler";
	
    public static TestConnectionReply parse(InputStream toParse) throws IOException, SAXException {
		RootElement root = new RootElement("testConnectionReply");
		final TestConnectionReply result = new TestConnectionReply();
        root.getChild("status").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                result.status = Integer.parseInt(body);
            }
        });
        root.getChild("serverURI").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	
                result.serverURI = body;
            }
        });

        Log.i(TAG, "parsing TestConnectionReply XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return result;
    }
}
