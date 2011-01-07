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

import com.thesisug.communication.valueobject.GroupData;
import com.thesisug.communication.valueobject.GroupInviteData;
import com.thesisug.communication.valueobject.SingleTask;

public class GroupHandler {
private static final String TAG = "thesisug - GroupHandler";
	
    public static List<GroupData> parseUserGroupRequest(InputStream toParse) throws IOException, SAXException {
		final List<GroupData> combine = new LinkedList<GroupData>();
    	final GroupData current = new GroupData();
		RootElement root = new RootElement("collection");
		Element singleGroupData = root.getChild("groupData");
		singleGroupData.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		singleGroupData.getChild("groupID").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.groupID = body;
            }
        });
		singleGroupData.getChild("groupName").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.groupName = body;
            }
        });
		singleGroupData.getChild("owner").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.owner = body;
            }
        });
		Log.i(TAG, "parsing Single GroupData XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        Log.i(TAG, "... parsing done, return "+combine.size()+" result");
        return combine;
    }
    
    public static List<GroupInviteData> parseGroupInvite(InputStream toParse) throws IOException, SAXException {
		final List<GroupInviteData> combine = new LinkedList<GroupInviteData>();
    	final GroupInviteData current = new GroupInviteData();
		RootElement root = new RootElement("collection");
		Element singleGroupData = root.getChild("groupInviteData");
		singleGroupData.setEndElementListener(new EndElementListener(){
            public void end() {
            	
                combine.add(current.copy());
            }
        });
		
		singleGroupData.getChild("requestID").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.requestID = body;
            }
        });
		
		singleGroupData.getChild("groupID").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.groupID = body;
            }
        });
		singleGroupData.getChild("groupName").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.groupName = body;
            }
        });
		singleGroupData.getChild("userToInvite").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.userToInvite = body;
            }
        });
		singleGroupData.getChild("message").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.message = body;
            }
        });
		singleGroupData.getChild("sender").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.sender = body;
            }
        });
		Log.i(TAG, "parsing Single GroupData XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        Log.i(TAG, "... parsing done, return "+combine.size()+" result");
        return combine;
    }
    
    public static String formatUserGroupRequest (GroupData group){
    	XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "groupData");
            

	            serializer.startTag("", "groupID");
	            serializer.text(group.groupID);
	            serializer.endTag("", "groupID");
	            
	            serializer.startTag("", "groupName");
	            serializer.text(group.groupName);
	            serializer.endTag("", "groupName");
	            
	            serializer.startTag("", "groupOwner");
	            serializer.text(group.owner);
	            serializer.endTag("", "groupOwner");
            
	        serializer.endTag("", "groupData");    
	            
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }

	public static String formatGroupInvite(GroupInviteData invite) {
		XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            
	            serializer.startTag("", "groupInviteData");	
	            	serializer.startTag("", "requestID");
	            		serializer.text(invite.requestID);
	            	serializer.endTag("", "requestID");
	            	
	            	serializer.startTag("", "groupID");
		            	serializer.text(invite.groupID);
	            	serializer.endTag("", "groupID");

	            	serializer.startTag("", "groupName");
	            		serializer.text(invite.groupName);
	            	serializer.endTag("", "groupName");	            	
	            	
		            serializer.startTag("", "userToInvite");
		            	serializer.text(invite.userToInvite);
		            serializer.endTag("", "userToInvite");
			                      
		            serializer.startTag("", "message");
		            	serializer.text(invite.message);
		            serializer.endTag("", "message"); 

		            serializer.startTag("", "sender");
	            		serializer.text(invite.sender);
	            	serializer.endTag("", "sender"); 
	            
		            serializer.endTag("", "groupInviteData");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
	}
	

	public static String formatAcceptUserGroupRequest(GroupInviteData invite) {
		XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            	Log.i(TAG,"1");
	            serializer.startTag("", "groupInviteData");	
	            Log.i(TAG,"1a");	
	            	serializer.startTag("", "requestID");
	            	Log.i(TAG,"1b"+invite.requestID);	
	            		serializer.text(invite.requestID);
	            		Log.i(TAG,"1c");	
	            	serializer.endTag("", "requestID");
	            	Log.i(TAG,"2");	
	            	serializer.startTag("", "groupID");
		            	serializer.text(invite.groupID);
	            	serializer.endTag("", "groupID");
	            	Log.i(TAG,"3");
	            	serializer.startTag("", "groupName");
	            		serializer.text(invite.groupName);
	            	serializer.endTag("", "groupName");	            	
	            	Log.i(TAG,"4");
		            serializer.startTag("", "userToInvite");
		            	serializer.text(invite.userToInvite);
		            serializer.endTag("", "userToInvite");
		            Log.i(TAG,"5");   
		            serializer.startTag("", "message");
		            	serializer.text(invite.message);
		            serializer.endTag("", "message"); 
		            Log.i(TAG,"6");
		            serializer.startTag("", "sender");
	            		serializer.text(invite.sender);
	            	serializer.endTag("", "sender"); 
	            	Log.i(TAG,"7");
		            serializer.endTag("", "groupInviteData");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
	}

	public static String formatRefuseUserGroupRequest(GroupInviteData invite) {
		XmlSerializer serializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            	Log.i(TAG,"1");
	            serializer.startTag("", "groupInviteData");	
	            Log.i(TAG,"1a");	
	            	serializer.startTag("", "requestID");
	            	Log.i(TAG,"1b"+invite.requestID);	
	            		serializer.text(invite.requestID);
	            		Log.i(TAG,"1c");	
	            	serializer.endTag("", "requestID");
	            	Log.i(TAG,"2");	
	            	serializer.startTag("", "groupID");
		            	serializer.text(invite.groupID);
	            	serializer.endTag("", "groupID");
	            	Log.i(TAG,"3");
	            	serializer.startTag("", "groupName");
	            		serializer.text(invite.groupName);
	            	serializer.endTag("", "groupName");	            	
	            	Log.i(TAG,"4");
		            serializer.startTag("", "userToInvite");
		            	serializer.text(invite.userToInvite);
		            serializer.endTag("", "userToInvite");
		            Log.i(TAG,"5");   
		            serializer.startTag("", "message");
		            	serializer.text(invite.message);
		            serializer.endTag("", "message"); 
		            Log.i(TAG,"6");
		            serializer.startTag("", "sender");
	            		serializer.text(invite.sender);
	            	serializer.endTag("", "sender"); 
	            	Log.i(TAG,"7");
		            serializer.endTag("", "groupInviteData");
            serializer.endDocument();
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
	}
}
