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

import com.thesisug.communication.valueobject.Item;
import com.thesisug.communication.valueobject.SingleActionLocation;
import com.thesisug.communication.valueobject.SingleItemLocation;
import com.thesisug.communication.valueobject.Hint.PhoneNumber;


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
	
	public static List<Item> parseUserItems(InputStream toParse) throws IOException, SAXException {
		final List<Item> combine = new LinkedList<Item>();
    	final Item current = new Item();
    	Log.d(TAG,"Sono DENTRO AL METODO PARSEUSERITEMS____________");
		RootElement root = new RootElement("collection");
		Element item = root.getChild("item");
		
		item.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
                current.ontologyList = new LinkedList<String>();
                current.dbList = new LinkedList<String>();
            }
        });
		
		item.getChild("name").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	Log.d(TAG,"Item name: "+body);
            	current.name = body;
            }
        });
		item.getChild("nScreen").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	Log.d(TAG,"Item nScreen: "+body);
            	current.nScreen = body;
            }
        });
		item.getChild("itemActionType").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	Log.d(TAG,"Item itemActionType: "+body);
            	current.itemActionType = body;
            }
		});
		item.getChild("ontologyList").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	Log.d(TAG,"Item ontologyList: "+body);
            	current.ontologyList.add(new String(body));
            }
        });

		item.getChild("dbList").setEndTextElementListener(new EndTextElementListener(){
                    public void end(String body) {
                    	Log.d(TAG,"Item dbList: "+body);
                    	current.dbList.add(new String(body));
                    	
                    }
         });
		
		
		Log.d(TAG,"FINE PARSE parseUserItems");
		Log.i(TAG, "parsing Single ItemsInLocation XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        Log.d(TAG,"DOPO XML PARSE parseUserItems");
		
        Log.i(TAG, "... parsing done, return "+combine.size()+" result");
        return combine;
    }
	
	
	public static List<SingleActionLocation> parseUserActionInLocation(InputStream toParse) throws IOException, SAXException {
		final List<SingleActionLocation> combine = new LinkedList<SingleActionLocation>();
    	final SingleActionLocation current = new SingleActionLocation();
		RootElement root = new RootElement("collection");
		Element singleActionLocation = root.getChild("singleActionLocation");
		singleActionLocation.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		singleActionLocation.getChild("action").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.action = body;
            }
        });
		singleActionLocation.getChild("location").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.location = body;
            }
        });
		singleActionLocation.getChild("username").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.username = body;
            }
		});
		singleActionLocation.getChild("n_views").setEndTextElementListener(new EndTextElementListener(){
                public void end(String body) {
                	current.n_views = body;
                }
        });
		singleActionLocation.getChild("n_votes").setEndTextElementListener(new EndTextElementListener(){
                    public void end(String body) {
                    	current.n_votes = body;
                    }
         });
		singleActionLocation.getChild("vote").setEndTextElementListener(new EndTextElementListener(){
                        public void end(String body) {
                        	current.vote = body;
                        }  
        });
		Log.i(TAG, "parsing Single ActionInLocation XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        Log.i(TAG, "... parsing done, return "+combine.size()+" result");
        return combine;
    }
	
	 public static String formatSingleItemLocation (SingleItemLocation itemLocation){
	    	XmlSerializer serializer = Xml.newSerializer();
	        StringWriter writer = new StringWriter();
	        try {
	            serializer.setOutput(writer);
	            serializer.startDocument("UTF-8", true);
	            serializer.startTag("", "singleItemLocation");
	            Log.d(TAG, "start");
	            //The id of the task is taskID field, the id of the reminder is ID field 
	            serializer.startTag("", "item");
	            serializer.text(itemLocation.item);
	            serializer.endTag("", "item");
	            Log.d(TAG, "1");
	            serializer.startTag("", "location");
	            serializer.text(itemLocation.location);
	            serializer.endTag("", "location");
	            
	            serializer.startTag("", "username");
	            serializer.text(itemLocation.username);
	            serializer.endTag("", "username");
	            
	            serializer.startTag("", "n_views");
	            serializer.text(itemLocation.n_views);
	            serializer.endTag("", "n_views");
	            
	            serializer.startTag("", "n_votes");
	            serializer.text(itemLocation.n_votes);
	            serializer.endTag("", "n_votes");
	            
	            serializer.startTag("", "vote");
	            serializer.text(itemLocation.vote);
	            serializer.endTag("", "vote");
	            
	            serializer.endTag("", "singleItemLocation");
	            serializer.endDocument();
	            return writer.toString();
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        } 
	    }
	 
	 public static String formatSingleActionLocation (SingleActionLocation actionLocation){
	    	XmlSerializer serializer = Xml.newSerializer();
	        StringWriter writer = new StringWriter();
	        try {
	            serializer.setOutput(writer);
	            serializer.startDocument("UTF-8", true);
	            serializer.startTag("", "singleActionLocation");
	            Log.d(TAG, "start");
	            //The id of the task is taskID field, the id of the reminder is ID field 
	            serializer.startTag("", "action");
	            serializer.text(actionLocation.action);
	            serializer.endTag("", "action");
	            Log.d(TAG, "1");
	            serializer.startTag("", "location");
	            serializer.text(actionLocation.location);
	            serializer.endTag("", "location");
	            
	            serializer.startTag("", "username");
	            serializer.text(actionLocation.username);
	            serializer.endTag("", "username");
	            
	            serializer.startTag("", "n_views");
	            serializer.text(actionLocation.n_views);
	            serializer.endTag("", "n_views");
	            
	            serializer.startTag("", "n_votes");
	            serializer.text(actionLocation.n_votes);
	            serializer.endTag("", "n_votes");
	            
	            serializer.startTag("", "vote");
	            serializer.text(actionLocation.vote);
	            serializer.endTag("", "vote");
	            
	            serializer.endTag("", "singleActionLocation");
	            serializer.endDocument();
	            return writer.toString();
	        } catch (Exception e) {
	            throw new RuntimeException(e);
	        } 
	    }
}
