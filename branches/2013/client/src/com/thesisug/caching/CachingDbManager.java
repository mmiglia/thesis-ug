package com.thesisug.caching;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.caching.CachingDb;

public class CachingDbManager 
{
	private static final String TAG = "thesisug - CachingDbManager";
	private static CachingDb cachingDb;
	private static final int FIVEHUNDREDMEGA = 524288000;
	
	public static void Init(Context c)
	{
		cachingDb = new CachingDb(c);
	}
	
	
	public static boolean insertHints(String sentence, List<Hint> hints)
	{
		Log.i(TAG,"insertHints");
		boolean ret;
		SQLiteDatabase db = cachingDb.getWritableDatabase();
		cachingDb.startTransaction(db);
		ret=cachingDb.insertHints(sentence, hints,db);
		if(ret)
		{
			cachingDb.commitTransaction(db);
			long dbSize = new File(db.getPath()).length();
			Log.d(TAG,"Database size after insert: " + dbSize);
			if(dbSize>FIVEHUNDREDMEGA)
			{
				Log.d(TAG,"Database is too big, need resize.");
				cachingDb.downsizeDb(db);
			}
		}
		db.close();
		return ret;
	}
	
	public static List<Hint> searchLocalBuisnessInCache(String sentence,float latitude, float longitude,int distance)
	{
		Log.i(TAG,"searchLocalBuisnessInCache");
		return cachingDb.searchLocalBuisnessInCache(sentence, latitude, longitude, distance);
	}
	
	public static Area checkArea(Area areaToCheck,String sentence)
	{
		Log.i(TAG,"checkArea");
		return cachingDb.checkArea(areaToCheck, sentence);
	}
	
	public static boolean cleanArea(Area areaToClean,String sentence)
	{
		Log.i(TAG,"cleanArea");
		boolean ret;
		SQLiteDatabase db = cachingDb.getWritableDatabase();
		cachingDb.startTransaction(db);
		ret=cachingDb.cleanArea(areaToClean, sentence,db);
		if(ret)
		{
			cachingDb.commitTransaction(db);
		}
		db.close();
		return ret;
		
	}
	
	public static void startCacheUpdate()
	{
		Log.i(TAG,"updateCache");
		cachingDb.startCacheUpdate();
	}
	
	public static void updateArea(Area area,String sentence,List<Hint> update)
	{
		Log.i(TAG,"updateArea");
		cachingDb.updateArea(area,sentence,update);
	}
}
