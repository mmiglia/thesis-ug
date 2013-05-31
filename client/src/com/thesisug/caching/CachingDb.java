package com.thesisug.caching;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.thesisug.communication.AccountUtil;
import com.thesisug.communication.ContextResource;
import com.thesisug.communication.valueobject.Hint;
import com.thesisug.communication.valueobject.Hint.PhoneNumber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.os.Handler;
import android.util.Log;

/**
 * This class manages db creation, update and connections.
 * 
 * @author Alberto Servetti
 *
 */
public class CachingDb extends SQLiteOpenHelper
{
	private static final String TAG = "thesisug - CachingDb";
	private static final String DATABASE_NAME = "thesisug.db";
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);;
	public static final int AREA_IN = 0;
	public static final int AREA_OUT = 1;
	public static final int AREA_OVERLAY = 2;
	private static Handler handler = new Handler();
	private Context context;
	private static Date creationDate;
	//Starting version is 1
	private static final int SCHEMA_VERSION = 1;
	
	/**
	 * Create a helper object to create, open, and/or manage a database.
	 * 
	 * @param context
	 */
	public CachingDb(Context context)
	{
		super(context, DATABASE_NAME, null, SCHEMA_VERSION);
		Log.i(TAG,"new CachingDbHelper");
		creationDate = Calendar.getInstance().getTime();
		this.context=context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		Log.i(TAG,"new SQLite Database");
		
		String createTable =
				"CREATE TABLE IF NOT EXISTS LocalHintCachingDelete (" +
				"deleteDate date NOT NULL," +
				"PRIMARY KEY(deleteDate))";
		
		db.execSQL(createTable);
		
		ContentValues container = new ContentValues();
		container.put("deleteDate",dateFormat.format(creationDate));
		
		if(db.insert("LocalHintCachingDelete", null, container)==-1)
		{
			Log.e(TAG,"Failed inserting in LocalHintCachingDelete");
		}
		
		Log.d(TAG,"LocalHintCachingDelete table created");
		
		createTable = 
				"CREATE TABLE IF NOT EXISTS {0} ( " +		
				"{1} varchar(100) NOT NULL," +
				"{2} varchar(20) NOT NULL," +
				"{3} varchar(20) NOT NULL," +
				"{4} varchar(40) NOT NULL," +
				"{5} date NOT NULL," +
				"PRIMARY KEY ( {6},{7},{8},{9}));"
				;
				
		db.execSQL(MessageFormat.format(createTable,LocalHintCachingAreas.TABLE_NAME, LocalHintCachingAreas.SENTENCE,LocalHintCachingAreas.CENTRELAT,LocalHintCachingAreas.CENTRELNG,LocalHintCachingAreas.RADIUS,LocalHintCachingAreas.LASTUPDATE,LocalHintCachingAreas.CENTRELAT,LocalHintCachingAreas.CENTRELNG,LocalHintCachingAreas.RADIUS,LocalHintCachingAreas.SENTENCE));
		
		Log.d(TAG,"LocalHintCachingAreas table created.");
		
		createTable = 
				"CREATE TABLE IF NOT EXISTS {0} ( " +
				"{1} varchar(100) NOT NULL," +
				"{2} varchar(400) NOT NULL," +
				"{3} varchar(100) NOT NULL," +
				"{4} varchar(100) NOT NULL," +
				"{5} varchar(20) NOT NULL," +
				"{6} varchar(20) NOT NULL," +
				"{7} varchar(100) NOT NULL," +
				"{8} varchar(100) NOT NULL," +
				"{9} varchar(400) NOT NULL," +
				"{10} varchar(400) NOT NULL," +
				"{11} varchar(400) NOT NULL," +
				"{12} varchar(400) NOT NULL," +
				"{13} varchar(400) NOT NULL," +
				"{14} varchar(100) NOT NULL," +
				"{15} varchar(100) NOT NULL," +
				"{16} date NOT NULL," +
				"{17} varchar(30) NOT NULL," +
				"{18} varchar(30) NOT NULL,"+
				"PRIMARY KEY ( {19},{20},{21},{22}));"
				;
		
				db.execSQL(MessageFormat.format(createTable,LocalHintCaching.TABLE_NAME, LocalHintCaching.TITLE,LocalHintCaching.URL,LocalHintCaching.CONTENT,LocalHintCaching.TITLENOFORMATTING,LocalHintCaching.LAT,LocalHintCaching.LNG,LocalHintCaching.STREETADDRESS,LocalHintCaching.CITY,LocalHintCaching.DDURL,LocalHintCaching.DDURLTOHERE,LocalHintCaching.DDURLFROMHERE,LocalHintCaching.STATICMAPURL,LocalHintCaching.LISTINGTYPE,LocalHintCaching.REGION,LocalHintCaching.COUNTRY,LocalHintCaching.INSERTIONDATE,LocalHintCaching.SENTENCE,LocalHintCaching.USER,LocalHintCaching.TITLE,LocalHintCaching.LAT,LocalHintCaching.LNG,LocalHintCaching.SENTENCE));
				
				Log.d(TAG,"LocalHintCaching table created.");
				
				createTable=
					"CREATE TABLE IF NOT EXISTS {0} ( " +
					"{1} varchar(100) NOT NULL," +
					"{2} varchar(20) NOT NULL," +
					"{3} varchar(20) NOT NULL," +
					"{4} varchar(100) NOT NULL," +
					"{5} date NOT NULL," +
					"PRIMARY KEY ( {6},{7},{8},{9}));"
					;
				
				db.execSQL(MessageFormat.format(createTable,LocalHintCachingAddressLines.TABLE_NAME, LocalHintCachingAddressLines.TITLE,LocalHintCachingAddressLines.LAT,LocalHintCachingAddressLines.LNG,LocalHintCachingAddressLines.ADDRESSLINE,LocalHintCachingAddressLines.INSERTIONDATE,LocalHintCachingAddressLines.TITLE,LocalHintCachingAddressLines.LAT,LocalHintCachingAddressLines.LNG,LocalHintCachingAddressLines.ADDRESSLINE));
				
				Log.d(TAG,"LocalHintCachingAddressLines table created.");
				
				createTable=
						"CREATE TABLE IF NOT EXISTS {0} ( " +
						"{1} varchar(100) NOT NULL," +
						"{2} varchar(20) NOT NULL," +
						"{3} varchar(20) NOT NULL," +
						"{4} varchar(30) NOT NULL," +
						"{5} varchar(30) NOT NULL," +
						"{6} date NOT NULL," +
						"PRIMARY KEY ( {7},{8},{9},{10}));"
						;
				
				db.execSQL(MessageFormat.format(createTable,LocalHintCachingPhoneNumber.TABLE_NAME, LocalHintCachingPhoneNumber.TITLE,LocalHintCachingPhoneNumber.LAT,LocalHintCachingPhoneNumber.LNG,LocalHintCachingPhoneNumber.NUMBER,LocalHintCachingPhoneNumber.TYPE,LocalHintCachingPhoneNumber.INSERTIONDATE,LocalHintCachingPhoneNumber.TITLE,LocalHintCachingPhoneNumber.LAT,LocalHintCachingPhoneNumber.LNG,LocalHintCachingPhoneNumber.NUMBER));
				Log.d(TAG,"LocalHintCachingPhoneNumber table created.");
				
	}
	/**
	 * Start a new SQLite transaction.
	 */
	public void startTransaction(SQLiteDatabase db)
	{
		db.execSQL("BEGIN TRANSACTION;");
		Log.d(TAG,"Begin transaction.");
	}
	

	/**
	 * Rollback a SQLlite transaction.
	 */
	
	private void rollbackTransaction(SQLiteDatabase db)
	{
		db.execSQL("ROLLBACK;");
		Log.d(TAG,"Transaction aborted.");
	}
	
	/**
	 * Commit a SQLite transaction.
	 */
	public void commitTransaction(SQLiteDatabase db)
	{
		db.execSQL("COMMIT;");
		Log.d(TAG,"Commit Transaction.");
	}
	
	/**
	 * Check if an area is overlayed or nested with others for the same task.
	 * 
	 * @param areaToCheck	Area to check.
	 * @param sentence		Sentence describing task.
	 * @return				An Area object which contains result of search.
	 */
	public Area checkArea(Area areaToCheck, String sentence)
	{
		//TODO
		Log.i(TAG,"Going to check areas for " + sentence+".");
		Log.d(TAG,"areaToCheck:");
		Log.d(TAG,"lat: " + areaToCheck.lat);
		Log.d(TAG,"lng: " + areaToCheck.lng);
		Log.d(TAG,"rad:" + areaToCheck.rad);
		Cursor queryResult;
		
		SQLiteDatabase db = getReadableDatabase();
		//Query database for areas containing hints for the same task
		//Equivalent to SELECT * FROM TABLE_NAME WHERE SENTENCE = sentence;
		queryResult = db.query
				(
				LocalHintCachingAreas.TABLE_NAME,
				LocalHintCachingAreas.COLUMNS, 
				LocalHintCachingAreas.SENTENCE +" = '"+sentence+"' ",
				null, 
				LocalHintCaching.SENTENCE, 
				null, 
				null, 
				null
				);
		if (!(queryResult.moveToFirst()) || queryResult.getCount() == 0)
		{
			//If there are no areas for this sentence, query server on the starting area
		    Log.d(TAG,"No area present in Cache for "+ sentence +".");
		    Area area = new Area(areaToCheck.lat,areaToCheck.lng,areaToCheck.rad,AREA_OUT);
		    insertAreaEntry(area,sentence);
		    return area;
		}
		Log.d(TAG,"There are areas already in cache for "+sentence+".");
		ArrayList<Area> toDelete = new ArrayList<Area>();
		queryResult.moveToFirst();
		//For each area corresponding to the same task
		do
		{
			Log.d(TAG,"Stored area:");
			Log.d(TAG,"sentence: " + queryResult.getString(0));
			float lat = queryResult.getFloat(1);
			Log.d(TAG,"lat: " + lat);
			float lng = queryResult.getFloat(2);
			Log.d(TAG,"lng: " + lng);
			float rad = queryResult.getFloat(3);
			Log.d(TAG,"rad:" + rad);
			float[] distance = new float[1];
			//Distance between centre of the area I've to search and the existing area for the same task.
			Location.distanceBetween(lat, lng, areaToCheck.lat, areaToCheck.lng, distance);
			if(distance[0]>=0)
			{
				if(distance[0]<rad+areaToCheck.rad)
				{
					//If distance between centres is less than sum of two radius, 
					//areas are surely overlayed or nested.
					Log.d(TAG, "Areas are nested or overlayed.");
					if(distance[0]<=rad)
					{
						//If distance is less than stored area's radius,
						//It means that search area's centre is inside stored area.
						Log.d(TAG,"areaToCheck centre is inside stored area.");
						if(areaToCheck.rad<(rad-distance[0]))
						{

							db.close();
							queryResult.close();
							Log.d(TAG, "Search area is nested in existing area.");
							//If search area's radius is less than stored area's radius minus distance,
							//search area is nested inside stored area, so I can query db for hints.
							return new Area(0,0,0,AREA_IN);
						}
						else
						{
							//If search area's radius is more than stored area's radius minus distance,
							//there are two possibilities: areas are overlayed or stored area is nested
							//in search area.
							if(areaToCheck.rad<(rad+distance[0]))
							{

								db.close();
								queryResult.close();
								Log.d(TAG, "Search area is overlayed with existing area.");
								//If search area's radius is less than stored area's radius plus distance,
								//areas are overlayed, so return their union.
								return getAreaUnion(new Area(lat,lng,rad),areaToCheck,distance[0],sentence);
							}
							
							else
							{

								db.close();
								queryResult.close();
								Log.d(TAG, "Existing area is nested in search area.");
								//If search area's radius is more or equal than stored area's radius plus distance,
								//stored area is nested inside search area, so I delete it from database (mantaining hints).
								toDelete.add(new Area(lat,lng,rad));
							}
						}
					}
					else
					{
						//If distance is more than stored area's radius,
						//It means that search area's centre is outside stored area.
						Log.d(TAG,"areaToCheck centre is outside stored area.");
						if(areaToCheck.rad>distance[0]-rad)
						{
							//If search area's radius is more than stored area's radius minus distance,
							//there are two possibilities: areas are overlayed or stored area is nested
							//in search area.
							if(areaToCheck.rad<(rad+distance[0]))
							{

								db.close();
								queryResult.close();

								Log.d(TAG, "Search area is overlayed with existing area.");
								//If search area's radius is less than stored area's radius plus distance,
								//areas are overlayed, so return their union.
								return getAreaUnion(new Area(lat,lng,rad),areaToCheck,distance[0],sentence);
							}
							else
							{

								db.close();
								queryResult.close();

								Log.d(TAG, "Existing area is nested in search area.");
								//If search area's radius is more than stored area's radius plus distance,
								//stored area is nested inside, so I delete it from database (mantaining hints).
								toDelete.add(new Area(lat,lng,rad));
							}
						}
					}
				}
				
			}
		}
		while(queryResult.moveToNext());

		db.close();
		queryResult.close();
		db = getWritableDatabase();
		if(toDelete.size()>0)
		{	
			for(Area area:toDelete)
			{
				deleteAreaEntry(area, sentence,db);
			}	
		}
		db.close();
		Area area = new Area(areaToCheck.lat,areaToCheck.lng,areaToCheck.rad,AREA_OUT);
		insertAreaEntry(area,sentence);
		return area;
		
	}
	
	/**
	 * Merge two overlayed areas. If, during merge, other areas are overlayed,
	 * iterate.
	 * 
	 * @param area			First area to merge.
	 * @param areaToCheck	Second area to merge.
	 * @param distance		Distance between two areas' centres.
	 * @param sentence		Sentence describing areas' task. 
	 * @return				Union of areas.
	 */
	private Area getAreaUnion(Area storedArea, Area areaToCheck, float distance,String sentence) 
	{
		Log.i(TAG,"getAreaUnion");
		Area union = null;
		//Check which area is bigger
		if(storedArea.rad>areaToCheck.rad)
		{
			Log.d(TAG,"storedArea is bigger.");
			//If area is the bigger, use storedArea centre and distance+areaToCheck radius.
			union = new Area(storedArea.lat,storedArea.lng,areaToCheck.rad+distance);
		}
		else
		{
			Log.d(TAG,"areaToCheck is bigger.");
			//If area is the bigger, use areaToCheck centre and distance+storedArea radius.
			union = new Area(areaToCheck.lat,areaToCheck.lng,storedArea.rad+distance);
		}
		//Check union area for two reason: the union could overlay or neste other areas.
		//In any case this call to checkArea will delete storedArea because is nested.
		return checkArea(union,sentence);
	}
	/**
	 * Insert new area in cached areas.
	 * @param areaToInsert		Area to insert in database.
	 * @param sentence			Sentence describing task searched in this area.
	 */
	private void insertAreaEntry(Area areaToInsert,String sentence)
	{
		Log.i(TAG,"insertArea for " + sentence +".");
		Log.d(TAG,"lat " + areaToInsert.lat);
		Log.d(TAG,"lng " + areaToInsert.lng);
		Log.d(TAG,"radius " + areaToInsert.rad);
		SQLiteDatabase db = getWritableDatabase();
		/*
		//Before inserting area, checks if already present
		String whereClause = LocalHintCachingAreas.CENTRELAT + " = ? AND "+LocalHintCachingAreas.CENTRELNG +" = ? AND "+LocalHintCachingAreas.RADIUS + " = ? AND " + LocalHintCachingAreas.SENTENCE + " = ? ";
		Cursor c =db.query(LocalHintCachingAreas.TABLE_NAME, LocalHintCachingAreas.COLUMNS, whereClause, new String[]{Float.toString(areaToInsert.lat),Float.toString(areaToInsert.lng), Integer.toString(areaToInsert.rad), sentence}, null, null, LocalHintCachingAreas.SENTENCE);
		if (!(c.moveToFirst()) || c.getCount() ==0)
		{
		    Log.d(TAG,"Area not present.");
		}
		else
		{
			Log.e(TAG,"Area already present.");
			c.close();
			return;
		}
		*/
		ContentValues container = new ContentValues();
		container.put(LocalHintCachingAreas.SENTENCE, sentence);
		container.put(LocalHintCachingAreas.CENTRELAT, Float.toString(areaToInsert.lat));
		container.put(LocalHintCachingAreas.CENTRELNG, Float.toString(areaToInsert.lng));
		container.put(LocalHintCachingAreas.RADIUS, Float.toString(areaToInsert.rad));
		container.put(LocalHintCachingAreas.LASTUPDATE,dateFormat.format(Calendar.getInstance().getTime()));
		

		if(db.insert(LocalHintCachingAreas.TABLE_NAME, null, container)==-1)
			Log.e(TAG,"Error inserting LocalHintCaching.");
		else
			Log.d(TAG,"Area successfully inserted for " + sentence +".");			
		return;
	}
	
	public boolean cleanArea(Area areaToClean,String sentence,SQLiteDatabase db )
	{
		Log.i(TAG,"Going to clean an area for " + sentence +".");
		Log.d(TAG,"Area to clean = lat: "+ areaToClean.lat + " lng: " + areaToClean.lng + " rad: " + areaToClean.rad + ".");
		//SQLiteDatabase db = getWritableDatabase();
		String whereClause = LocalHintCachingAreas.CENTRELAT + " = ? AND "+LocalHintCachingAreas.CENTRELNG +" = ? AND "+LocalHintCachingAreas.RADIUS + " = ? AND " + LocalHintCachingAreas.SENTENCE + " = ? ";
		Cursor c =db.query(LocalHintCachingAreas.TABLE_NAME, LocalHintCachingAreas.COLUMNS, whereClause, new String[]{Float.toString(areaToClean.lat),Float.toString(areaToClean.lng), Float.toString(areaToClean.rad), sentence}, null, null, LocalHintCachingAreas.SENTENCE);
		if (!(c.moveToFirst()) || c.getCount() ==0)
		{
		    Log.e(TAG,"Area not present.");
		    return true;
		}
		else
		{	
			
			Log.d(TAG,"Area present.");
			//startTransaction(db);
			if(!deleteAreaEntry(areaToClean,sentence,db))
			{
				rollbackTransaction(db);
				return false;
			}
			if(!deleteHintsInArea(areaToClean,sentence,db))
			{
				rollbackTransaction(db);
				return false;
			}
			Log.d(TAG,"Area successfully cleaned.");
			//commitTransaction(db);
			//db.close();
			return true;
		}
	}

	/**
	 * Delete an area entry from LocalHintCachingAreas.
	 * @param areaToDelete	Area to be deleted.
	 * @param sentence		Sentence corresponding to the area to be deleted.
	 */
	private boolean deleteAreaEntry(Area areaToDelete, String sentence, SQLiteDatabase db)
	{
		String whereClause = LocalHintCachingAreas.SENTENCE + " = '"+sentence+"' AND " + LocalHintCachingAreas.CENTRELAT + " = '"+Float.toString(areaToDelete.lat)+"' AND " +LocalHintCachingAreas.CENTRELNG + " = '"+Float.toString(areaToDelete.lng)+"' AND "+ LocalHintCachingAreas.RADIUS + " = '"+Float.toString(areaToDelete.rad)+"' ";

		Log.d(TAG,"Going to delete areas where " + whereClause +".");
		int deleted =db.delete(LocalHintCachingAreas.TABLE_NAME,whereClause , null);
		if(deleted==0)
		{
			Log.e(TAG,"Error in deleting area from " + LocalHintCachingAreas.TABLE_NAME +" table.");
			return false;
		}
		else
		{
			Log.d(TAG,"Deleted "+ deleted+ " from "+ LocalHintCachingAreas.TABLE_NAME +" table.");
			return true;
		}
	
	}
	/**
	 * Clean all hints present in an area for a task.
	 * 
	 * @param areaToClean	Area to be cleaned.
	 * @param sentence		Sentence describing task.
	 */
	/**
	 * Delete all hints for a task present in a specific area.
	 * @param areaToClean	Area to be cleaned.
	 * @param sentence		Task whose hints have to be cleaned.
	 */
	private boolean deleteHintsInArea(Area areaToClean, String sentence,SQLiteDatabase db) 
	{
		List<Hint> hintsToDelete =
		searchLocalBuisnessInCache(sentence,areaToClean.lat, areaToClean.lng,areaToClean.rad);
		if(hintsToDelete.size()==0)
		{
			Log.e(TAG,"No hints for "+sentence + "in this area.");
			return false;
		}
		return deleteHints(hintsToDelete,sentence,db);
	}
	
	private boolean deleteHints(List<Hint> hintsToDelete,String sentence,SQLiteDatabase db)
	{
		int deletedHints = 0;
		for(Hint hint:hintsToDelete)
		{
			String whereClause = LocalHintCaching.TITLE + "  = ? AND "+LocalHintCaching.LAT+" = ? AND "+ LocalHintCaching.LNG +" = ? AND " + LocalHintCaching.SENTENCE + " = ? ";
			
			int deletedRows = db.delete(LocalHintCaching.TABLE_NAME, whereClause, new String[]{hint.title,hint.lat,hint.lng,sentence});
			if(deletedRows==0)
			{
				Log.e(TAG,"Hint not present in LocalHintCaching!");
				return false;
			}
			deletedHints+=deletedRows;
			
			int deletedAddressRows =0;
			for(String addressLine: hint.addressLines)
			{

				whereClause = LocalHintCachingAddressLines.TITLE + "  = ? AND "+LocalHintCachingAddressLines.LAT+" = ? AND "+ LocalHintCachingAddressLines.LNG +" = ? AND " + LocalHintCachingAddressLines.ADDRESSLINE + " = ? ";
				
				deletedRows = db.delete(LocalHintCachingAddressLines.TABLE_NAME, whereClause, new String[]{hint.title,hint.lat,hint.lng,addressLine});
				if(deletedRows==0)
				{
					Log.e(TAG,"Address line not present in LocalHintCaching!");
					return false;
				}
				deletedAddressRows+=deletedRows;
			}
			Log.d(TAG,"Deleted "+ deletedAddressRows+ " from " + LocalHintCachingAddressLines.TABLE_NAME);
			int deletedPhoneRows = 0;
			for(PhoneNumber phoneNumber:hint.phoneNumbers)
			{
				whereClause = LocalHintCachingPhoneNumber.TITLE + "  = ? AND "+LocalHintCachingPhoneNumber.LAT+" = ? AND "+ LocalHintCachingPhoneNumber.LNG +" = ? AND " + LocalHintCachingPhoneNumber.NUMBER + " = ? ";
				
				deletedRows = db.delete(LocalHintCachingPhoneNumber.TABLE_NAME, whereClause, new String[]{hint.title,hint.lat,hint.lng,phoneNumber.number});
				if(deletedRows==0)
				{
					Log.e(TAG,"Phone number not present in LocalHintCaching!");
					return false;
				}
				deletedPhoneRows+=deletedRows;
			}

			Log.d(TAG,"Deleted "+ deletedPhoneRows + " from " + LocalHintCachingPhoneNumber.TABLE_NAME);
		}

		Log.d(TAG,"Deleted "+ deletedHints + " from " + LocalHintCaching.TABLE_NAME);
		return true;
	}
	
	/**
	 * Insert into Db a list of hints for a task.
	 * 
	 * @param sentence	sentence used by user to describe task.
	 * @param result	list of Hint objects containing the hints to store.	
	 */
	public boolean insertHints(String sentence, List<Hint> hints,SQLiteDatabase db)
	{
		Log.i(TAG,"insertHints");
		
		
		
		/*
		Log.d(TAG,"Checking if cache has already been cleaned.");
		if(!cacheAlreadyCleanedToday())
		{
			Log.d(TAG,"Cache has not been cleaned today.");
			cachingClean();
		}
		else
			Log.d(TAG,"Cache has already been cleaned today.");
		*/
		//SQLiteDatabase db = getWritableDatabase();
		
		if(hints.size()==0)
		{
			Log.i(TAG,"No hints to insert.");
			return true;
		}
		else
			Log.i(TAG,"Inserting "+ hints.size() + " hints for " + sentence + " .");
		
		AccountUtil util = new AccountUtil();
		//startTransaction(db);
		for(Hint h:hints)
		{
			Log.d(TAG,"Inserting hint: " + h.titleNoFormatting);
			
			//Before inserting hint, checks if already present
			Cursor c =db.query(LocalHintCaching.TABLE_NAME, LocalHintCaching.COLUMNS, LocalHintCaching.TITLE +" = '"+h.title+"' AND "+LocalHintCaching.LAT+" ='"+h.lat+"' AND "+LocalHintCaching.LNG +" ='"+h.lng+"' AND "+ LocalHintCaching.SENTENCE+ " ='"+sentence+"'", null, null, null, LocalHintCaching.TITLE);
			if (!(c.moveToFirst()) || c.getCount() ==0)
			{
			    Log.d(TAG,"Hint not present.");
			}
			else
			{
				Log.d(TAG,"Hint already present.");
				c.close();
				continue;
			}
			c.close();
			ContentValues container = new ContentValues();
			container.put(LocalHintCaching.TITLE, h.title);
			container.put(LocalHintCaching.URL, h.url);
			container.put(LocalHintCaching.CONTENT, h.content);
			container.put(LocalHintCaching.TITLENOFORMATTING, h.titleNoFormatting);
			container.put(LocalHintCaching.LAT, h.lat);
			container.put(LocalHintCaching.LNG, h.lng);
			container.put(LocalHintCaching.STREETADDRESS, h.streetAddress);
			container.put(LocalHintCaching.CITY, h.city);
			container.put(LocalHintCaching.DDURL, h.ddUrl);
			container.put(LocalHintCaching.DDURLTOHERE, h.ddUrlToHere);
			container.put(LocalHintCaching.DDURLFROMHERE, h.ddUrlFromHere);
			container.put(LocalHintCaching.STATICMAPURL,h.staticMapUrl);
			container.put(LocalHintCaching.LISTINGTYPE,h.listingType);
			container.put(LocalHintCaching.REGION,h.region);
			container.put(LocalHintCaching.COUNTRY,h.country);
			container.put(LocalHintCaching.INSERTIONDATE, dateFormat.format(Calendar.getInstance().getTime()));
			container.put(LocalHintCaching.SENTENCE, sentence);
			container.put(LocalHintCaching.USER,util.getUsername(context));
			
			if(db.insert(LocalHintCaching.TABLE_NAME, null, container)==-1)
			{
				Log.e(TAG,"Error inserting LocalHintCaching");
				rollbackTransaction(db);
				return false;
			}
			
			Log.d(TAG,h.addressLines.size() + " addresses.");
			for(String addressLine: h.addressLines)
			{
				Cursor temp =db.query(LocalHintCachingAddressLines.TABLE_NAME, LocalHintCachingAddressLines.COLUMNS, LocalHintCachingAddressLines.TITLE +" = '"+h.title+"' AND "+LocalHintCachingAddressLines.LAT+" ='"+h.lat+"' AND "+LocalHintCachingAddressLines.LNG +" ='"+h.lng+"' AND "+ LocalHintCachingAddressLines.ADDRESSLINE+ " ='"+addressLine+"'", null, null, null, LocalHintCaching.TITLE);
				if (!(temp.moveToFirst()) || temp.getCount() ==0)
				{
				    Log.d(TAG,"Address not present.");
				}
				else
				{
					Log.d(TAG,"Address already present.");
					temp.close();
					continue;
				}
				temp.close();
				container.clear();
				container.put(LocalHintCachingAddressLines.TITLE, h.title);
				container.put(LocalHintCachingAddressLines.LAT, h.lat);
				container.put(LocalHintCachingAddressLines.LNG, h.lng);
				container.put(LocalHintCachingAddressLines.ADDRESSLINE,addressLine);
				container.put(LocalHintCachingAddressLines.INSERTIONDATE, dateFormat.format(Calendar.getInstance().getTime()));
				
				if(db.insert(LocalHintCachingAddressLines.TABLE_NAME, null,container)==-1)
				{
					Log.e(TAG,"Error inserting LocalHintCachingAddressLines");
					rollbackTransaction(db);
					return false;
				}
			}
			
			Log.d(TAG,h.phoneNumbers.size() + " phone numbers.");
			for(PhoneNumber phoneNumber : h.phoneNumbers)
			{
				Cursor temp =db.query(LocalHintCachingPhoneNumber.TABLE_NAME, LocalHintCachingPhoneNumber.COLUMNS, LocalHintCachingPhoneNumber.TITLE +" = '"+h.title+"' AND "+LocalHintCachingPhoneNumber.LAT+" ='"+h.lat+"' AND "+LocalHintCachingPhoneNumber.LNG +" ='"+h.lng+"' AND "+ LocalHintCachingPhoneNumber.NUMBER+ " ='"+phoneNumber.number+"'", null, null, null, LocalHintCaching.TITLE);
				if (!(temp.moveToFirst()) || temp.getCount() ==0)
				{
				    Log.d(TAG,"Phone number not present.");
				}
				else
				{
					Log.d(TAG,"Phone number already present.");
					temp.close();
					continue;
				}
				container.clear();
				container.put(LocalHintCachingPhoneNumber.TITLE, h.title);
				container.put(LocalHintCachingPhoneNumber.LAT, h.lat);
				container.put(LocalHintCachingPhoneNumber.LNG, h.lng);
				container.put(LocalHintCachingPhoneNumber.NUMBER,phoneNumber.number);
				container.put(LocalHintCachingPhoneNumber.TYPE, phoneNumber.type);
				container.put(LocalHintCachingPhoneNumber.INSERTIONDATE, dateFormat.format(Calendar.getInstance().getTime()));
				
				if(db.insert(LocalHintCachingPhoneNumber.TABLE_NAME, null,container)==-1)
				{
					Log.e(TAG,"Error inserting LocalHintCachingPhoneNumber");
					rollbackTransaction(db);
					return false;
				}
				else
					Log.d(TAG,"Phonenumber inserted.");
			
			}
		}
		//commitTransaction(db);
		//db.close();
		return true;
	}
	/**
	 * Clean old cache records.
	 * 
	 * @return	True if clean succeeds, false otherwise.
	 */
	private boolean cachingClean()
	{
		String today = dateFormat.format(Calendar.getInstance().getTime());
		Log.i(TAG,"cachingClean is going to delete all records antecendent " + today +". ");
		cachingCleanLocalHintCaching();
		cachingCleanLocalHintCachingAddressLines();
		cachingCleanLocalHintCachingPhoneNumber();
		setNewDeleteDate(today);
		
		return true;
	}
	/**
	 * Clean cache values in LocalHintCaching table.
	 * 
	 * @param today	String format of actual day.
	 */
	private void cachingCleanLocalHintCaching() 
	{
		Log.i(TAG,"cachingCleanLocalHintCaching.");

		SQLiteDatabase db = getWritableDatabase();
		
		//String whereClause = LocalHintCaching.INSERTIONDATE + "  <  date('now') ";
		
		int deletedRows = db.delete(LocalHintCaching.TABLE_NAME, null, null);
		
		Log.d(TAG,"Deleted "+ deletedRows + " from " + LocalHintCaching.TABLE_NAME);
		
		db.close();
		
	}
	/**
	 * Clean old cache values in LocalHintCachingPhoneNumber table.
	 * 
	 * @param today	String format of actual day.
	 */
	private void cachingCleanLocalHintCachingPhoneNumber() 
	{
		Log.i(TAG,"cachingCleanLocalHintCachingPhoneNumber.");
		
		SQLiteDatabase db = getWritableDatabase();
	
		String whereClause = LocalHintCachingPhoneNumber.INSERTIONDATE + "  <  date('now') ";
		
		int deletedRows = db.delete(LocalHintCachingPhoneNumber.TABLE_NAME, whereClause, null);
		
		Log.d(TAG,"Deleted "+ deletedRows + " from " + LocalHintCachingPhoneNumber.TABLE_NAME);
		
		db.close();
		
	}
	/**
	 * Clean old cache values in LocalHintCachingAddressLines table.
	 * 
	 * @param today	String format of actual day.
	 */
	private void cachingCleanLocalHintCachingAddressLines() 
	{
		Log.i(TAG,"cachingCleanLocalHintCachingAddressLines.");

		SQLiteDatabase db = getWritableDatabase();
		
		String whereClause = LocalHintCachingAddressLines.INSERTIONDATE + "  < date('now') ";
		
		int deletedRows = db.delete(LocalHintCachingAddressLines.TABLE_NAME, whereClause, null);
		
		Log.d(TAG,"Deleted "+ deletedRows + " from " + LocalHintCachingAddressLines.TABLE_NAME);
		
		db.close();
	}
	/**
	 * Search for local hints records in cache.
	 * 
	 * @param sentence	sentence of the task.
	 * @param latitude	latitude of user.
	 * @param longitude	longitude of user.
	 * @param distance	max distance for hints.
	 * @return	a list of local hints for the task.
	 */
	public List<Hint> searchLocalBuisnessInCache(String sentence,float latitude, float longitude,float distance)
	{
		Log.i(TAG,"Searching local hints for " + sentence + " in local cache.");
		//ArrayList where hints will be saved
		List<Hint> hintList=new ArrayList<Hint>();
		//SQLLite queries return a Cursors object
		Cursor queryResult;
		
		SQLiteDatabase db = getReadableDatabase();
		
		//Equivalent to SELECT * FROM TABLE_NAME WHERE SENTENCE = sentence;
		queryResult = db.query
				(
				LocalHintCaching.TABLE_NAME,
				LocalHintCaching.COLUMNS, 
				LocalHintCaching.SENTENCE +" = ? ",
				new String[]{sentence}, 
				LocalHintCaching.TITLE, 
				null, 
				null, 
				null
				);
		
		if (!(queryResult.moveToFirst()) || queryResult.getCount() == 0)
		{
		    Log.d(TAG,"No hints present in Cache.");
		    return hintList;
		}
		
		Log.d(TAG,"Found "+queryResult.getCount()+" hints for "+ sentence + " in local cache.");
		
		
		/* TODO
		 * In my opinion, filtering by distance before combining result with 
		 * LocalHintCachingPhoneNumber and LocalHintCachingAddressList would
		 * avoid to perform useless query to the database. 
		 * Alberto Servetti
		 */
		
		hintList = filterResultsByDistance(queryResult,distance,latitude,longitude);
		
		Log.d(TAG,hintList.size() + " hints after filtering.");
		
		queryResult.close();
		
		if(hintList.size() == 0)
		{
			Log.d(TAG,"No valid hints found in Cache.");
			return hintList;
		}
		
		Log.d(TAG,"Going to add phone numbers and addresses for each filtered hint.");
		
		for(Hint hint:hintList)
		{
			Log.d(TAG,"Checking phone numbers for " + hint.title);
			
			//Equivalent to SELECT * FROM TABLE_NAME WHERE TITLE = title AND LAT = lat AND LNG = lng;
			queryResult = db.query
					(
					LocalHintCachingPhoneNumber.TABLE_NAME, 
					LocalHintCachingPhoneNumber.COLUMNS, 
					LocalHintCachingPhoneNumber.TITLE + " = ? AND " + LocalHintCachingPhoneNumber.LAT + " = ? AND " + LocalHintCachingPhoneNumber.LNG + " = ?",
					new String[]{hint.title,hint.lat,hint.lng},
					null,
					null, 
					null
					);
			
			if ((queryResult.moveToFirst()) && queryResult.getCount() != 0)
			{
			   Log.d(TAG,"We have phone numbers to add.");
			   ArrayList<PhoneNumber> phoneNumberList=new ArrayList<PhoneNumber>();
			   do
			   {
				   PhoneNumber number = new PhoneNumber();
				   number.number = queryResult.getString(3);
				   number.type = queryResult.getString(4);
				   phoneNumberList.add(number);
			   }
			   while(queryResult.moveToNext());
			   
			   hint.phoneNumbers = phoneNumberList;
			   Log.d(TAG,phoneNumberList.size() +" phone numbers added to " + hint.title);
			   queryResult.close();
			}
			else 
				Log.d(TAG,"No phone numbers for "+ hint.title);
			
			
			
			Log.d(TAG,"Checking addresses for " + hint.title);
			
			//Equivalent to SELECT * FROM TABLE_NAME WHERE TITLE = title AND LAT = lat AND LNG = lng;
			queryResult = db.query
					(
					LocalHintCachingAddressLines.TABLE_NAME, 
					LocalHintCachingAddressLines.COLUMNS, 
					LocalHintCachingAddressLines.TITLE + " = ? AND " + LocalHintCachingAddressLines.LAT + " = ? AND " + LocalHintCachingAddressLines.LNG + " = ?",
					new String[]{hint.title,hint.lat,hint.lng},
					null,
					null, 
					null
					);
			
			if ((queryResult.moveToFirst()) && queryResult.getCount() != 0)
			{
				Log.d(TAG,"We have address line to add.");
				List<String> addressLines = new ArrayList<String>();
				
				do
				{
					addressLines.add(queryResult.getString(3));
				}
				while(queryResult.moveToNext());
				
				hint.addressLines = addressLines;
				Log.d(TAG,addressLines.size() +" address lines added to " + hint.title);
				
				queryResult.close();
			}
			
		}
		
		Log.d(TAG,"Finished adding phone numbers and address lines, returning hint list.");
		
		return hintList;
	}

	private List<Hint> filterResultsByDistance(Cursor result, float distance, float latitude, float longitude)
	{
		Log.i(TAG,"filterResultsByDistance.");
		ArrayList<Hint> hintList=new ArrayList<Hint>();
		Log.d(TAG,"Starting.");
		result.moveToFirst();
		do
		{
			if(calculateDistance(latitude,longitude,Float.parseFloat(result.getString(4)),Float.parseFloat(result.getString(5)))>distance)
			{
				Log.d(TAG,"Discarding an hint too far.");
				continue;
			}
			Log.d(TAG,"Hint distance is Ok.");
			Hint h = new Hint();
			h.title = result.getString(0);
			h.url = result.getString(1);
			h.content = result.getString(2);
			h.titleNoFormatting = result.getString(3);
			h.lat = result.getString(4);
			h.lng = result.getString(5);
			h.streetAddress = result.getString(6);
			h.city = result.getString(7);
			/*update ddUrl with current user position*/
			String ddUrl = result.getString(8);
			int index = ddUrl.indexOf("&saddr=");
			ddUrl = ddUrl.substring(0, index+7);
			ddUrl = ddUrl + +latitude+","+longitude;
			h.ddUrl = ddUrl;
			h.ddUrlToHere= result.getString(9);
			h.ddUrlFromHere = result.getString(10);
			h.staticMapUrl = result.getString(11);
			h.listingType = result.getString(12);
			h.region = result.getString(13);
			h.country = result.getString(14);
			
			hintList.add(h);
			
			Log.d(TAG,"New hint added.");
		}
		while(result.moveToNext());
		return hintList;
	}
	
	/**
	 * Returns the distance in meter between two GPS location
	 * 
	 * @param A	Point A.
	 * @param B	Point B.
	 * @return distance in meter
	 */
	private double calculateDistance(double latitudeA,double longitudeA,double latitudeB, double longitudeB) 
	{
		final double EARTH_RADIUS = 6378.14; // in kilometer, according to
		// WolframAlpha
		double cosaob = Math.cos((double) latitudeA) * Math.cos((double) latitudeB)
				* Math.cos((double) longitudeB - (double) longitudeA)
				+ Math.sin((double) latitudeA) * Math.sin((double) latitudeB);
		return Math.toRadians(Math.acos(cosaob)) * EARTH_RADIUS * 1000;
	}
	/**
	 * Start cache update for each area.
	 */
	public void startCacheUpdate()
	{
		Log.i(TAG,"updateCache");
		Cursor queryResult;
		
		SQLiteDatabase db = getReadableDatabase();
		queryResult = db.query(LocalHintCachingAreas.TABLE_NAME, LocalHintCachingAreas.COLUMNS, null, null, null, null, null);
		if (!(queryResult.moveToFirst()) || queryResult.getCount() == 0)
		{
			Log.d(TAG,"Cache is empty");
			return;
		}
		do
		{
			Log.d(TAG,"Updating an area.");
			String sentence = queryResult.getString(0);
			Area area = new Area(queryResult.getFloat(1),queryResult.getFloat(2),queryResult.getFloat(3));
			Log.d(TAG,"Area: lat " + area.lat + " lng " + area.lng + "radius " + area.rad);
			ContextResource.updateCache(sentence, 0, area.lat, area.lng, area.rad, handler, context);
			
		}while(queryResult.moveToNext());
		db.close();
	}
	/**
	 * Update a specific area.
	 * 
	 * @param area		Area to be updated.
	 * @param sentence	Sentence describing task to be updated.
	 * @param update	List of new hints to insert in cache for the area.
	 */
	public void updateArea(Area area,String sentence, List<Hint> update) 
	{
		Log.i(TAG,"updateArea");
		SQLiteDatabase db = getWritableDatabase();
		startTransaction(db);
		if(deleteHintsInArea(area, sentence,db))
		{
			Log.d(TAG,"Area cleaned.");
			if(insertHints(sentence, update,db))
			{
				Log.d(TAG,"Hints inserted.");
				if(updateAreaLastUpdate(area,sentence,db))
				{
					commitTransaction(db);
					Log.d(TAG,"Area updated.");
				}
			}
			else
				Log.e(TAG,"Failed to insert hints");
		}
		else
			Log.e(TAG,"Failed to clean area.");
		
	}
	
	public boolean updateAreaLastUpdate(Area area, String sentence,SQLiteDatabase db)
	{
		Log.i(TAG,"updateAreaLastUpdate");
		ContentValues updatedContent = new ContentValues();
		updatedContent.put(LocalHintCachingAreas.LASTUPDATE,dateFormat.format(Calendar.getInstance().getTime()));
		String whereClause = LocalHintCachingAreas.CENTRELAT + " = ? AND "+LocalHintCachingAreas.CENTRELNG +" = ? AND "+LocalHintCachingAreas.RADIUS + " = ? AND " + LocalHintCachingAreas.SENTENCE + " = ? ";
		if(db.update(LocalHintCachingAreas.TABLE_NAME, updatedContent,whereClause, new String[]{Float.toString(area.lat),Float.toString(area.lng), Float.toString(area.rad), sentence})==0)
		{
			rollbackTransaction(db);
			return false;
		}
		return true;
	}
	/**
	 * Checks if cache has been already cleaned today.
	 * 
	 * @return true if cache has been cleaned, false otherwise.
	 */
	
	private boolean cacheAlreadyCleanedToday()
	{
		Log.i(TAG,"cacheAlreadyCleanedToday");
		SQLiteDatabase db = getReadableDatabase();
		boolean ret = true;
		Cursor deleteResult = db.query("LocalHintCachingDelete", null, "deleteDate = date('now')", null, null, null, null, null);
		if (!(deleteResult.moveToFirst()) || deleteResult.getCount() == 0)
		{
			Log.d(TAG,"Cache must be cleaned.");
			ret = false;
		}
		else 
			Log.d(TAG,"Cache already cleaned today.");
		db.close();
		return ret;
	}
	/**
	 * Set new cache delete date.
	 * 
	 * @param newDate	
	 */
	private void setNewDeleteDate(String newDate)
	{
		SQLiteDatabase db = getWritableDatabase();
		startTransaction(db);
		if(db.delete("LocalHintCachingDelete", null,null)==1)
		{
			Log.d(TAG,"Old delete date cleaned.");
			ContentValues content = new ContentValues();
			content.put("deleteDate",newDate);
			if(db.insert("LocalHintCachingDelete", null, content)!=-1)
			{
				Log.d(TAG,"New date correctly inserted.");
				commitTransaction(db);
			}
			else
			{
				Log.e(TAG,"Error in inserting new date.");
				rollbackTransaction(db);
			}
		}
		else
		{
			Log.e(TAG,"Error in deleting old date.");
			rollbackTransaction(db);
		}
		db.close();
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		
		
	}

	public void downsizeDb(SQLiteDatabase db) 
	{
		Log.i(TAG,"downSizeDb");
		
		
		
		
	}

	private List<Area> getAllAreas(SQLiteDatabase db)
	{
		Cursor queryResult;
		queryResult = db.query
				(
				LocalHintCachingAreas.TABLE_NAME,
				LocalHintCachingAreas.COLUMNS, 
				null,
				null, 
				LocalHintCaching.SENTENCE, 
				null, 
				null, 
				null
				);
		
		if(!queryResult.moveToFirst() || queryResult.getCount() == 0)
		{
			Log.e(TAG,"No areas in db! Clean all hints.");
			cachingCleanLocalHintCaching();
			return null;
		}
		List<Area> allAreas = new ArrayList<Area>();
		
		return null;
	}
	
}
