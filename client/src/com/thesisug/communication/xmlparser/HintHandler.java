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

import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.valueobject.Hint.PhoneNumber;

public class HintHandler {
private static final String TAG = "Hint Handler";
	
    public static List<Hint> parse(InputStream toParse) throws IOException, SAXException {
		final List<Hint> combine = new LinkedList<Hint>();
    	final Hint current = new Hint();
    	final PhoneNumber phone = new PhoneNumber();
		RootElement root = new RootElement("collection");
		Element hint = root.getChild("hint");
		hint.setEndElementListener(new EndElementListener(){
            public void end() {
                combine.add(current.copy());
            }
        });
		hint.getChild("title").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.title = body;
            }
        });
		hint.getChild("url").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.url = body;
            }
        });
		hint.getChild("content").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.content = body;
            }
        });
		hint.getChild("url").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.url = body;
            }
        });
		hint.getChild("titleNoFormatting").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.titleNoFormatting = body;
            }
        });
		hint.getChild("lat").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.lat = body;
            }
        });
		hint.getChild("lng").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.lng = body;
            }
        });
		hint.getChild("streetAddress").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.streetAddress = body;
            }
        });
		hint.getChild("city").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.city = body;
            }
        });
		hint.getChild("ddUrl").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.ddUrl = body;
            }
        });
		hint.getChild("ddUrlToHere").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.ddUrlToHere = body;
            }
        });
		hint.getChild("ddUrlFromHere").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.ddUrlFromHere = body;
            }
        });
		hint.getChild("staticMapUrl").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.staticMapUrl = body;
            }
        });
		hint.getChild("listingType").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.listingType = body;
            }
        });
		hint.getChild("region").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
            	current.region = body;
            }
        });
		hint.getChild("country").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.country = body;
            }
        });
		Element phonenum = hint.getChild("phoneNumbers");
		phonenum.setEndElementListener(new EndElementListener(){
            public void end() {
                current.phoneNumbers.add(phone.copy());
            }
        });
		phonenum.getChild("type").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                phone.type = body;
            }
        });
		phonenum.getChild("number").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                phone.number = body;
            }
        });
		hint.getChild("addressLines").setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                current.addressLines.add(new String(body));
            }
        });
        Log.i(TAG, "parsing Hints XML message");
        Xml.parse(toParse, Xml.Encoding.UTF_8, root.getContentHandler());
        return combine;
    }
}
