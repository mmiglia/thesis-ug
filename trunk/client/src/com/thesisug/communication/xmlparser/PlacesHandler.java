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

import com.thesisug.communication.valueobject.PlaceClient;
import com.thesisug.communication.valueobject.SingleActionLocation;
import com.thesisug.communication.valueobject.SingleItemLocation;

public class PlacesHandler {

private static final String TAG = "thesisug - PlacesHandler";
	
	public static List<PlaceClient> parseUserPrivatePlaces(InputStream toParse) throws IOException, SAXException {
		final List<PlaceClient> combine = new LinkedList<PlaceClient>();
    	final PlaceClient current = new PlaceClient();
		RootElement root = new RootElement("collection");
		Element privatePlace = root.getChild("placeClient");
		privatePlace.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		privatePlace.getChild("title").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.title = body;
            }
        });
		privatePlace.getChild("lat").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.lat = body;
            }
        });
		privatePlace.getChild("lng").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.lng = body;
            }
		});
		privatePlace.getChild("streetAddress").setEndTextElementListener(new EndTextElementListener(){
                public void end(String body) {
                	current.streetAddress = body;
                }
        });
		privatePlace.getChild("streetNumber").setEndTextElementListener(new EndTextElementListener(){
                    public void end(String body) {
                    	current.streetNumber = body;
                    }
         });
		privatePlace.getChild("cap").setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) {
                        	current.cap = body;
                        }  
        });
		privatePlace.getChild("city").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.city = body;
            }  
		});
		privatePlace.getChild("category").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.category = body;
            }  
		});
		Log.i(TAG, "parsing PlaceClient for private places XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        Log.i(TAG, "... parsing done, return "+combine.size()+" result");
        return combine;
    }
	
	public static List<PlaceClient> parseUserPublicPlaces(InputStream toParse) throws IOException, SAXException {
		final List<PlaceClient> combine = new LinkedList<PlaceClient>();
    	final PlaceClient current = new PlaceClient();
		RootElement root = new RootElement("collection");
		Element privatePlace = root.getChild("placeClient");
		privatePlace.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		privatePlace.getChild("title").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.title = body;
            }
        });
		privatePlace.getChild("lat").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.lat = body;
            }
        });
		privatePlace.getChild("lng").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.lng = body;
            }
		});
		privatePlace.getChild("streetAddress").setEndTextElementListener(new EndTextElementListener(){
                public void end(String body) {
                	current.streetAddress = body;
                }
        });
		privatePlace.getChild("streetNumber").setEndTextElementListener(new EndTextElementListener(){
                    public void end(String body) {
                    	current.streetNumber = body;
                    }
         });
		privatePlace.getChild("cap").setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) {
                        	current.cap = body;
                        }  
        });
		privatePlace.getChild("city").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.city = body;
            }  
		});
		privatePlace.getChild("category").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.category = body;
            }  
		});
		Log.i(TAG, "parsing PlaceClient for private places XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        Log.i(TAG, "... parsing done, return "+combine.size()+" result");
        return combine;
    }
	
	 public static String formatPlaceClient (PlaceClient place){
	    	XmlSerializer serializer = Xml.newSerializer();
	        StringWriter writer = new StringWriter();
	        try {
	            serializer.setOutput(writer);
	            serializer.startDocument("UTF-8", true);
	            serializer.startTag("", "placeClient");
	            Log.d(TAG, "start");
	            //The id of the task is taskID field, the id of the reminder is ID field 
	            serializer.startTag("", "title");
	            serializer.text(place.title);
	            serializer.endTag("", "title");
	            Log.d(TAG, "1");
	            serializer.startTag("", "streetAddress");
	            serializer.text(place.streetAddress);
	            serializer.endTag("", "streetAddress");
	            
	            serializer.startTag("", "streetNumber");
	            serializer.text(place.streetNumber);
	            serializer.endTag("", "streetNumber");
	            
	            serializer.startTag("", "city");
	            serializer.text(place.city);
	            serializer.endTag("", "city");
	            
	            serializer.startTag("", "cap");
	            serializer.text(place.cap);
	            serializer.endTag("", "cap");
	            
	            serializer.startTag("", "category");
	            serializer.text(place.category);
	            serializer.endTag("", "category");
	            
	            serializer.endTag("", "placeClient");
	            serializer.endDocument();
	            return writer.toString();
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        } 
	    }
	 
}
