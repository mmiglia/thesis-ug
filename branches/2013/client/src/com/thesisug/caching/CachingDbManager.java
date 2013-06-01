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
	//private static SQLiteDatabase db;
	private static Context context;
	
	public static void Init(Context c)
	{
		cachingDb = new CachingDb(c);
		context = c;
	}
	
	
	public static boolean insertHints(Area actualArea,String sentence, List<Hint> hints)
	{
		Log.i(TAG,"insertHints");
		boolean ret;
		SQLiteDatabase db = cachingDb.getWritableDatabase();
		cachingDb.startTransaction(db);
		ret=cachingDb.insertHints(sentence, hints,db);
		if(ret)
		{
			cachingDb.commitTransaction(db);
			if(!dbNeedsDownsize(db))
			{
				Log.d(TAG,"Database is too big, need resize.");
				cachingDb.downsizeDb(actualArea,db);
			}
			else
				Log.d(TAG,"Size is ok.");
		}
		//db.close();
		return ret;
	}
	
	public static List<Hint> searchLocalBuisnessInCache(String sentence,float latitude, float longitude,int distance)
	{
		Log.i(TAG,"searchLocalBuisnessInCache");

		SQLiteDatabase db = cachingDb.getWritableDatabase();
		List<Hint> ret = cachingDb.searchLocalBuisnessInCache(sentence, latitude, longitude, distance,db);
		//db.close();
		return ret;
	}
	
	public static Area checkArea(Area areaToCheck,String sentence)
	{
		Log.i(TAG,"checkArea");
		SQLiteDatabase db = cachingDb.getReadableDatabase();
		Area ret = cachingDb.checkArea(areaToCheck, sentence,db);
		//db.close();
		return ret;
	}
	
	public static boolean deleteArea(Area areaToClean,String sentence)
	{
		Log.i(TAG,"cleanArea");
		boolean ret;
		SQLiteDatabase db = cachingDb.getWritableDatabase();
		cachingDb.startTransaction(db);
		ret=cachingDb.deleteArea(areaToClean, sentence,db);
		if(ret)
		{
			cachingDb.commitTransaction(db);
		}
		//db.close();
		return ret;
		
	}
	
	public static void startCacheUpdate()
	{
		Log.i(TAG,"updateCache");
		SQLiteDatabase db = cachingDb.getWritableDatabase();
		CachingDb.startCacheUpdate(db);
		//db.close();
	}
	
	public static void updateArea(Area area,String sentence,List<Hint> update)
	{
		Log.i(TAG,"updateArea");

		SQLiteDatabase db = cachingDb.getWritableDatabase();
		cachingDb.updateArea(area,sentence,update,db);
		//db.close();
	}
	
	public static void cleanCache()
	{
		Log.i(TAG,"cleanCache");

		SQLiteDatabase db = cachingDb.getWritableDatabase();
		cachingDb.cachingClean(db);
		//db.close();
	}
	
	public static boolean dbNeedsDownsize(SQLiteDatabase db)
	{
		Log.i(TAG,"dbNeedsDownsize");
		return cachingDb.dbNeedsDownsize(db);
	}
}
