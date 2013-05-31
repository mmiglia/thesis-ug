package com.thesisug.communication.xmlparser;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import com.thesisug.communication.valueobject.VersionReply;

import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

public class VersionReplyHandler {
private static final String TAG = "thesisug - Version Reply Handler";
	
    public static VersionReply parse(InputStream toParse) throws IOException, SAXException {
		RootElement root = new RootElement("versionReply");
		final VersionReply result = new VersionReply();
        root.getChild("status").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                result.status = Integer.parseInt(body);
            }
        });
        root.getChild("serverVersion").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                result.serverVersion = body;
            }
        });
        Log.i(TAG, "parsing VersionReply XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return result;
    }
}
