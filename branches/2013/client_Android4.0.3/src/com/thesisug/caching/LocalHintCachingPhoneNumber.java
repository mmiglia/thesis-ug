package com.thesisug.caching;

import android.provider.BaseColumns;

/**
 * Interface for LocalHintCachingPhoneNumber table.
 * @author Alberto Servetti
 *
 */
public interface LocalHintCachingPhoneNumber extends BaseColumns 
{
	String TABLE_NAME = "LocalHintCachingPhoneNumber";
	 
	String TITLE = "title";
 	String LAT = "lat";
 	String LNG = "lng";
 	String NUMBER = "number";
 	String TYPE = "type";
 	String INSERTIONDATE = "insertionDate";

	String[] COLUMNS = new String[]
	{ 
			TITLE,
			LAT,
			LNG,
			NUMBER,
			TYPE,
			INSERTIONDATE
	};
}
