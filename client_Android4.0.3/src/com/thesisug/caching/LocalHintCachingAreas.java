package com.thesisug.caching;

import android.provider.BaseColumns;
/**
 * Interface for LocalHintCachingAreas table.
 * 
 * @author Alberto Servetti
 *
 */
public interface LocalHintCachingAreas extends BaseColumns
{
	String TABLE_NAME = "LocalHintCachingAreas";
	
	String SENTENCE = "sentence";
	String CENTRELAT = "centreLat";
	String CENTRELNG = "centreLng";
	String RADIUS = "radius";
	String LASTUPDATE = "lastUpdate";
	
	String[] COLUMNS = new String[]
	{
			SENTENCE,
			CENTRELAT,
			CENTRELNG,
			RADIUS,
			LASTUPDATE
	};
	
}
