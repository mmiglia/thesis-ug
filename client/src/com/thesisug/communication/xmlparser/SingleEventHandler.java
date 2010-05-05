package com.thesisug.communication.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.SAXException;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.thesis.communication.valueobject.SingleEvent;

public class SingleEventHandler {
private static final String TAG = "Single Event Handler";
	
    public static List<SingleEvent> parse(InputStream toParse) throws IOException, SAXException {
		final List<SingleEvent> combine = new LinkedList<SingleEvent>();
    	final SingleEvent current = new SingleEvent();
		RootElement root = new RootElement("collection");
		Element singleEvent = root.getChild("singleEvent");
		singleEvent.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		singleEvent.getChild("ID").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.ID = body;
            }
        });
		singleEvent.getChild("priority").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.priority = Integer.parseInt(body);
            }
        });
		singleEvent.getChild("description").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.description = body;
            }
        });
		singleEvent.getChild("title").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.title = body;
            }
        });
		singleEvent.getChild("type").setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				current.type = Integer.parseInt(body);
			}
		});
		singleEvent.getChild("startTime").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.startTime = body;
            }
        });
		singleEvent.getChild("endTime").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.endTime = body;
            }
        });
		singleEvent.getChild("location").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.location = body;
            }
        });
        Log.i(TAG, "parsing Single Event XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return combine;
    }
}
