package com.thesisug.caching;

import android.provider.BaseColumns;

/**
 * Interface for LocalHintCachingAddressLines
 * @author Alberto Servetti
 *
 */
public interface LocalHintCachingAddressLines extends BaseColumns
{

	String TABLE_NAME = "LocalHintCachingAddressLines";
	 
	String TITLE = "title";
 	String LAT = "lat";
 	String LNG = "lng";
 	String ADDRESSLINE = "addressLine";
 	String INSERTIONDATE = "insertionDate";

	String[] COLUMNS = new String[]
	{ 
			TITLE,
			LAT,
			LNG,
			ADDRESSLINE,
			INSERTIONDATE
	};
}
