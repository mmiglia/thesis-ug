package com.thesisug.caching;

import android.provider.BaseColumns;

/**
 * Interface for LocalHintCaching table.
 * @author Alberto Servetti
 *
 */
public interface LocalHintCaching extends BaseColumns
{
	String TABLE_NAME = "LocalHintCaching";
 
	String TITLE = "title";
 	String URL = "url";
 	String CONTENT = "content";
 	String TITLENOFORMATTING = "titleNoFormatting";
 	String LAT = "lat";
 	String LNG = "lng";
 	String STREETADDRESS = "streetAddress";
 	String CITY = "city";
 	String DDURL = "ddUrl";
 	String DDURLTOHERE = "ddlUrlToHere";
 	String DDURLFROMHERE = "ddlUrlFromHere";
 	String STATICMAPURL = "staticMapUrl";
 	String LISTINGTYPE = "listingType";
 	String REGION = "region";
 	String COUNTRY = "country";
 	String INSERTIONDATE = "insertionDate";
 	String SENTENCE = "sentence";
 	String USER = "user";
 	
	String[] COLUMNS = new String[]
	{ 
			TITLE, 
			URL, 
			CONTENT,
			TITLENOFORMATTING,
			LAT,
			LNG,
			STREETADDRESS,
			CITY,
			DDURL,
			DDURLTOHERE,
			DDURLFROMHERE,
			STATICMAPURL,
			LISTINGTYPE,
			REGION,
			COUNTRY,
			INSERTIONDATE,
			SENTENCE,
			USER};
}
