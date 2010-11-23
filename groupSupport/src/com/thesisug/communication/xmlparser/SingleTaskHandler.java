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

import com.thesisug.communication.valueobject.SingleTask;

public class SingleTaskHandler {
private static final String TAG = "Single Task Handler";
	
    public static List<SingleTask> parse(InputStream toParse) throws IOException, SAXException {
		final List<SingleTask> combine = new LinkedList<SingleTask>();
    	final SingleTask current = new SingleTask();
		RootElement root = new RootElement("collection");
		Element SingleTask = root.getChild("singleTask");
		SingleTask.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		SingleTask.getChild("ID").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.ID = body;
            }
        });
		SingleTask.getChild("priority").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.priority = Integer.parseInt(body);
            }
        });
		SingleTask.getChild("description").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.description = body;
            }
        });
		SingleTask.getChild("title").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.title = body;
            }
        });
		SingleTask.getChild("type").setEndTextElementListener(new EndTextElementListener(){
			public void end(String body) {
				current.type = Integer.parseInt(body);
			}
		});
		SingleTask.getChild("dueDate").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.dueDate = body;
            }
        });
		SingleTask.getChild("notifyTimeStart").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.notifyTimeStart = body;
            }
        });
		SingleTask.getChild("notifyTimeEnd").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.notifyTimeEnd= body;
            }
        });
        Log.i(TAG, "parsing Single Task XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return combine;
    }
    
    public static String format (SingleTask task){
    	XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "singleTask");
            
            serializer.startTag("", "ID");
            serializer.text(task.ID);
            serializer.endTag("", "ID");
            
            serializer.startTag("", "priority");
            serializer.text(Integer.toString(task.priority));
            serializer.endTag("", "priority");
            
            serializer.startTag("", "description");
            serializer.text(task.description);
            serializer.endTag("", "description");
            
            serializer.startTag("", "title");
            serializer.text(task.title);
            serializer.endTag("", "title");
            
            serializer.startTag("", "type");
            serializer.text(Integer.toString(task.type));
            serializer.endTag("", "type");
            
            serializer.startTag("", "description");
            serializer.text(task.description);
            serializer.endTag("", "description");
            
            serializer.startTag("", "gpscoordinate");
            	serializer.startTag("", "latitude");
            	serializer.text(Float.toString(task.gpscoordinate.latitude));
            	serializer.endTag("", "latitude");
            	
            	serializer.startTag("", "longitude");
            	serializer.text(Float.toString(task.gpscoordinate.longitude));
            	serializer.endTag("", "longitude");
            serializer.endTag("", "gpscoordinate");

            serializer.startTag("", "dueDate");
            serializer.text(task.dueDate);
            serializer.endTag("", "dueDate");
            
            serializer.startTag("", "notifyTimeStart");
            serializer.text(task.notifyTimeStart);
            serializer.endTag("", "notifyTimeStart");
            
            serializer.startTag("", "notifyTimeEnd");
            serializer.text(task.notifyTimeEnd);
            serializer.endTag("", "notifyTimeEnd");

            serializer.endTag("", "singleTask");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }
}
