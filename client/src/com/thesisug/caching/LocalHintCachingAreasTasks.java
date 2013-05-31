package com.thesisug.caching;

import android.provider.BaseColumns;
/**
 * Interface for LocalHintCachingAreasTasks table.
 * 
 * @author Alberto Servetti
 */
public interface LocalHintCachingAreasTasks  extends BaseColumns
{
	String TABLE_NAME = "LocalHintCachingAreas";
	
	String TITLE = "title";
	
	String[] COLUMNS = new String[]
	{ 
			_ID,
			TITLE
	};
	
}
