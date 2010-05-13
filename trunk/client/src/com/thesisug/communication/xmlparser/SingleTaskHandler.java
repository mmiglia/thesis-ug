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

import com.thesisug.communication.valueobject.SingleTask;

public class SingleTaskHandler {
private static final String TAG = "Single Task Handler";
	
    public static List<SingleTask> parse(InputStream toParse) throws IOException, SAXException {
		final List<SingleTask> combine = new LinkedList<SingleTask>();
    	final SingleTask current = new SingleTask();
		RootElement root = new RootElement("collection");
		Element SingleTask = root.getChild("SingleTask");
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
}
