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

import com.thesisug.communication.valueobject.SingleItemLocation;


public class AssertionsHandler {
	private static final String TAG = "thesisug - AssertionsHandler";
	
	public static List<SingleItemLocation> parseUserItemsInLocation(InputStream toParse) throws IOException, SAXException {
		final List<SingleItemLocation> combine = new LinkedList<SingleItemLocation>();
    	final SingleItemLocation current = new SingleItemLocation();
		RootElement root = new RootElement("collection");
		Element singleItemLocation = root.getChild("singleItemLocation");
		singleItemLocation.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		singleItemLocation.getChild("item").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.item = body;
            }
        });
		singleItemLocation.getChild("location").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.location = body;
            }
        });
		singleItemLocation.getChild("username").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.username = body;
            }
		});
        singleItemLocation.getChild("n_views").setEndTextElementListener(new EndTextElementListener(){
                public void end(String body) {
                	current.n_views = body;
                }
        });
        singleItemLocation.getChild("n_votes").setEndTextElementListener(new EndTextElementListener(){
                    public void end(String body) {
                    	current.n_votes = body;
                    }
         });
        singleItemLocation.getChild("vote").setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) {
                        	current.vote = body;
                        }  
        });
		Log.i(TAG, "parsing Single ItemsInLocation XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        Log.i(TAG, "... parsing done, return "+combine.size()+" result");
        return combine;
    }
	
}
