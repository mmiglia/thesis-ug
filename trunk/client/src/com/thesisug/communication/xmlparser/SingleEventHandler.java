package com.thesisug.communication.xmlparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;
import android.util.Xml;

import com.thesisug.communication.valueobject.SingleEvent;

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
		singleEvent.getChild("gpscoordinate").getChild("latitude").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.gpscoordinate.latitude = Float.parseFloat(body);
            }
        });
		singleEvent.getChild("gpscoordinate").getChild("longitude").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.gpscoordinate.longitude = Float.parseFloat(body);
            }
        });
        Log.i(TAG, "parsing Single Event XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return combine;
    }
    
    public static String format (SingleEvent ev){
    	XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "singleEvent");
            
            serializer.startTag("", "ID");
            serializer.text(ev.ID);
            serializer.endTag("", "ID");
            
            serializer.startTag("", "location");
            serializer.text(ev.location);
            serializer.endTag("", "location");
            
            serializer.startTag("", "priority");
            serializer.text(Integer.toString(ev.priority));
            serializer.endTag("", "priority");
            
            serializer.startTag("", "description");
            serializer.text(ev.description);
            serializer.endTag("", "description");
            
            serializer.startTag("", "title");
            serializer.text(ev.title);
            serializer.endTag("", "title");
            
            serializer.startTag("", "type");
            serializer.text(Integer.toString(ev.type));
            serializer.endTag("", "type");
            
            serializer.startTag("", "startTime");
            serializer.text(ev.startTime);
            serializer.endTag("", "startTime");
            
            serializer.startTag("", "endTime");
            serializer.text(ev.endTime);
            serializer.endTag("", "endTime");
            
            serializer.startTag("", "description");
            serializer.text(ev.description);
            serializer.endTag("", "description");
            
            serializer.startTag("", "gpscoordinate");
            serializer.startTag("", "latitude");
            serializer.text(Float.toString(ev.gpscoordinate.latitude));
            serializer.endTag("", "latitude");
            serializer.startTag("", "longitude");
            serializer.text(Float.toString(ev.gpscoordinate.longitude));
            serializer.endTag("", "longitude");
            serializer.endTag("", "gpscoordinate");
            
            serializer.endTag("", "singleEvent");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
