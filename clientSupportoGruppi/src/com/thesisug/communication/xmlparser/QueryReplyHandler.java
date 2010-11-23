package com.thesisug.communication.xmlparser;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.thesisug.communication.valueobject.QueryReply;

public class QueryReplyHandler {
	private static final String TAG = "Query Reply Handler";
	
    public static QueryReply parse(InputStream toParse) throws IOException, SAXException {
		RootElement root = new RootElement("queryReply");
		final QueryReply result = new QueryReply();
        root.getChild("status").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                result.status = Boolean.parseBoolean(body);
            }
        });
        Log.i(TAG, "parsing QueryReply XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return result;
    }
}
