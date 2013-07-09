package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;
import valueobject.Hint.PhoneNumber;
import businessobject.DateUtilsNoTime;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

/**
 * Singleton class that acts as a database that will save all the hint that arrive from Google, with this class
 * you can connect to the CachingDatabase (with openDatabase method) or create and manage
 * hint saved into the database
 * 13-05-2010
 * @author Anuska
 */


public enum CachingDatabase {
	
	istance;
	
	private static final Logger log = LoggerFactory.getLogger(CachingDatabase.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	public static boolean cachingListHint(String user, String sentence, float latitude, float longitude,int distance,List<Hint> list)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		
		DateUtilsNoTime today=new DateUtilsNoTime();
		String dateNow =today.now(); 
		String insertQuery;
		
		for (Hint h:list)
		{
			
			//Starting transaction
			QueryStatus qs=dbManager.startTransaction(conn);
			
			if(qs.execError){
				//TODO decide what to do in this case (transaction not started)
				log.error(qs.explainError());
				qs.occourtedErrorException.printStackTrace();
				System.out.println("Error during transaction starting...list<Hint> not added");
				log.error("Error during transaction starting...list<Hint> not added");
				dbManager.dbDisconnect(conn);
				continue;
			}
			insertQuery="insert into CachingGoogle (title,url,content,titleNoFormatting," +
					"lat,lng,streetAddress,city,ddUrl,ddUrlToHere,ddUrlFromHere,staticMapUrl," +
					"listingType,region,country,insertionDate,sentence,user) values (";
			insertQuery += "'"+h.title.replaceAll("'","")+"',";
			insertQuery += "'"+h.url.replaceAll("'","")+"',";
			insertQuery += "'"+h.content+"',";
			insertQuery += "'"+h.titleNoFormatting.replaceAll("'","")+"',";
			
			insertQuery += "'"+h.lat+"',";
			insertQuery += "'"+h.lng+"',";
			insertQuery += "'"+h.streetAddress.replaceAll("'","")+"',";
			insertQuery += "'"+h.city.replaceAll("'","")+"',";
			insertQuery += "'"+h.ddUrl.replaceAll("'","")+"',";
			insertQuery += "'"+h.ddUrlToHere.replaceAll("'","")+"',";
			insertQuery += "'"+h.ddUrlFromHere.replaceAll("'","")+"',";
			insertQuery += "'"+h.staticMapUrl.replaceAll("'","")+"',";
			insertQuery += "'"+h.listingType.replaceAll("'","")+"',";
			insertQuery += "'"+h.region.replaceAll("'","")+"',";
			insertQuery += "'"+h.country.replaceAll("'","")+"',";
			insertQuery += "'"+dateNow+"',";
			insertQuery += "'"+sentence+"',";
			insertQuery += "'"+user+"')";
			
			qs=dbManager.customQuery(conn, insertQuery);
			System.out.println(insertQuery);
			if(qs.execError){
				log.error(qs.explainError());
				//qs.occourtedErrorException.printStackTrace();
				//System.out.println("ERRORE: title-lat-lng già inseriti nel db");
				
				//Rolling back
				dbManager.rollbackTransaction(conn);
				
				log.error("Error during title-lat-lng adding... Assertion not added");
				//dbManager.dbDisconnect(conn);
				continue;
			}
			dbManager.commitTransaction(conn);
			if(h.phoneNumbers!=null)
			for (PhoneNumber p : h.phoneNumbers)
			{	
				qs=dbManager.startTransaction(conn);
				insertQuery = "insert into CachingGooglePhoneNumber (title,lat,lng,number,type,insertionDate)";
				insertQuery += "values( ";
				insertQuery += "'"+h.title.replaceAll("'","")+"',";
				insertQuery += "'"+h.lat+"',";
				insertQuery += "'"+h.lng+"',";
				insertQuery += "'"+p.number+"',";
				insertQuery += "'"+p.type.replaceAll("'","")+"',";
				insertQuery += "'"+dateNow+"')";
				
				qs=dbManager.customQuery(conn, insertQuery);
				System.out.println(insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					//qs.occourtedErrorException.printStackTrace();
					//System.out.println("ERRORE: title-lat-lng-number già inseriti nel db");
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					log.error("Error during title-lat-lng-number adding... Assertion not added");
					//dbManager.dbDisconnect(conn);
				}
				dbManager.commitTransaction(conn);
				
			}
			if(h.addressLines!=null)
			for (String a : h.addressLines)
			{	
				qs=dbManager.startTransaction(conn);
				insertQuery = "insert into CachingGoogleAddressLines (title,lat,lng,addressLine,insertionDate)";
				insertQuery += "values( ";
				insertQuery += "'"+h.title.replaceAll("'","")+"',";
				insertQuery += "'"+h.lat+"',";
				insertQuery += "'"+h.lng+"',";
				insertQuery += "'"+ a.replaceAll("'","") +"',";
				insertQuery += "'"+dateNow+"')";
				
				qs=dbManager.customQuery(conn, insertQuery);
				System.out.println(insertQuery);
				if(qs.execError){
					log.error(qs.explainError());
					//qs.occourtedErrorException.printStackTrace();
					//System.out.println("ERRORE: title-lat-lng-address già inseriti nel db");
					
					//Rolling back
					dbManager.rollbackTransaction(conn);
					
					log.error("Error during title-lat-lng-address adding... Assertion not added");
					//dbManager.dbDisconnect(conn);
				}
				dbManager.commitTransaction(conn);
				
			}
			
		}
		
		
		dbManager.dbDisconnect(conn);
		
		
		return true;
	}
	
	public static List<Hint> searchLocalBusinessDB(float latitude, float longitude,String query,int distance)
	{
		ArrayList<Hint> hintList=new ArrayList<Hint>();
		System.out.println("Siamo in CachingDB- searchLocalBusinessDB");
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		String selectQuery="select * from CachingGoogle where sentence='"+query+"'";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				
				ArrayList<PhoneNumber> phoneNumberList=new ArrayList<PhoneNumber>();
				Connection conn2= (Connection) dbManager.dbConnect();
				
				selectQuery="select * from CachingGooglePhoneNumber where title='"+rs.getString("title")+"'" +
						" and lat ='"+rs.getString("lat")+"'"+
						" and lng ='"+rs.getString("lng")+"'";
				System.out.println(selectQuery);
				QueryStatus qsp=dbManager.customSelect(conn2, selectQuery);
				ResultSet rsp=(ResultSet)qsp.customQueryOutput;
				try{
					while(rsp.next()){
							phoneNumberList.add(
									new PhoneNumber(
											rsp.getString("type"),
											rsp.getString("number")
									)					
							);
					}
				}catch(SQLException sqlE){
					//TODO
					//return null;
					log.error("SQLException in retrieve phone numbers");
				}finally{	
					//dbManager.dbDisconnect(conn2);
				}
				dbManager.dbDisconnect(conn2);
				ArrayList<String> addressLinesList=new ArrayList<String>();
				Connection conn3= (Connection) dbManager.dbConnect();
				
				selectQuery="select * from CachingGoogleAddressLines where title='"+rs.getString("title")+"'" +
				" and lat ='"+rs.getString("lat")+"' "+
				" and lng ='"+rs.getString("lng")+"'";
				System.out.println(selectQuery);
				QueryStatus qsa=dbManager.customSelect(conn3, selectQuery);
				ResultSet rsa=(ResultSet)qsa.customQueryOutput;
				try{
					while(rsa.next()){
						addressLinesList.add(
										rsa.getString("addressLines")				
						);
					}
				}catch(SQLException sqlE){
					//TODO
					//return null;
					log.error("SQLException in retrieve address Lines");
				}finally{	
					//dbManager.dbDisconnect(conn3);
				}
				dbManager.dbDisconnect(conn3);
				System.out.println("Ho fatto le select number e address");
				
				//cambio ddUrl mettendo le coordinate dell'utente nel
				//momento in cui vengono richiesti gli hint
				String ddUrl = rs.getString("ddUrl");
				int index = ddUrl.indexOf("&saddr=");
				ddUrl = ddUrl.substring(0, index+7);
				ddUrl = ddUrl + +latitude+","+longitude;

				hintList.add(
						new Hint(
								rs.getString("title"),
								rs.getString("url") ,
								rs.getString("content") , 
								rs.getString("titleNoFormatting"),
								rs.getString("lat") ,
								rs.getString("lng") , 
								rs.getString("streetAddress") ,
								rs.getString("city"),
								ddUrl,
								rs.getString("ddUrlToHere") , 
								rs.getString("ddUrlFromHere") ,
								rs.getString("staticMapUrl"),
								rs.getString("listingType"),
								rs.getString("region"),
								rs.getString("country"),
								phoneNumberList,
								addressLinesList		
							)					
					);
			}
		}catch(SQLException sqlE){
			//TODO
			//return null;
			System.out.println("Sono nel catch della select principale");
			log.error("SQLException in retrieve Hint from cache");
		}finally{	
			
			System.out.println("Sono nel finally della select principale");
		}
		dbManager.dbDisconnect(conn);
		System.out.println("Sto per ritornare hintList");
		//System.out.println(hintList);
		return hintList;
	}


	
	/*
	 * Metodo per eliminare i record che sono da troppo tempo in cache
	 */
	public void cachingDelete()
	{	
		DateUtilsNoTime date = new DateUtilsNoTime();
		String nowDate = date.now();
		System.out.println(nowDate);
		
		cachingDeleteCachingGoogle(nowDate);
		cachingDeleteCachingGoogleAddressLines(nowDate);
		cachingDeleteCachingGooglePhoneNumber(nowDate);
		//cachingDeleteDateUpdate(nowDate);
	
	}
	
	public void cachingDeleteCachingGoogle(String nowDate)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...list<Hint> not added");
			log.error("Error during transaction starting...list<Hint> not added");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		String deleteQuery="delete from CachingGoogle "+
		"where insertionDate<>'"+nowDate+"'";
		qs=dbManager.customQuery(conn, deleteQuery);
		System.out.println(deleteQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("ERRORE: durante caching delete");
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("ERROR during caching delete");
			dbManager.dbDisconnect(conn);
			return;
		}
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
	}
	
	public void cachingDeleteCachingGoogleAddressLines(String nowDate)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...list<Hint> not added");
			log.error("Error during transaction starting...list<Hint> not added");
			dbManager.dbDisconnect(conn);
			return;
		}
	
		String deleteQuery="delete from CachingGoogleAddressLines "+
		"where insertionDate<>'"+nowDate+"'";
		qs=dbManager.customQuery(conn, deleteQuery);
		System.out.println(deleteQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("ERRORE: durante caching delete");
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("ERROR during caching delete");
			dbManager.dbDisconnect(conn);
			return;
		}
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
		
	}
	
	public void cachingDeleteCachingGooglePhoneNumber(String nowDate)
	{	
		Connection conn= (Connection) dbManager.dbConnect();
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...list<Hint> not added");
			log.error("Error during transaction starting...list<Hint> not added");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		String deleteQuery="delete from CachingGooglePhoneNumber "+
		"where insertionDate<>'"+nowDate+"'";
		qs=dbManager.customQuery(conn, deleteQuery);
		System.out.println(deleteQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("ERRORE: durante caching delete");
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("ERROR during caching delete");
			dbManager.dbDisconnect(conn);
			return;
		}
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
	}
	
	public void cachingDeleteDateUpdate(String nowDate)
	{
		Connection conn= (Connection) dbManager.dbConnect();
		//Starting transaction
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError)
		{
			//TODO decide what to do in this case (transaction not started)
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("Error during transaction starting...cancellation cache date not added");
			log.error("Error during transaction starting...cancellation cache date not added");
			dbManager.dbDisconnect(conn);
			return;
		}
		
		String deleteQuery="insert into CachingCancellationDate(date) values ("+
		"'"+nowDate+"')";
		qs=dbManager.customQuery(conn, deleteQuery);
		System.out.println(deleteQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			System.out.println("ERRORE: durante caching delete");
			
			//Rolling back
			dbManager.rollbackTransaction(conn);
			
			log.error("ERROR during caching delete");
			dbManager.dbDisconnect(conn);
			return;
		}
		dbManager.commitTransaction(conn);
		dbManager.dbDisconnect(conn);
	}
	
	public boolean isAlreadyDeleteCacheToday()
	{
		DateUtilsNoTime date = new DateUtilsNoTime();
		String nowDate = date.now();
		
		Connection conn= (Connection) dbManager.dbConnect();
		boolean toReturn=false;
		String selectQuery="Select * from CachingCancellationDate where date='"+nowDate+"'";
		System.out.println(selectQuery);
		QueryStatus qs=dbManager.customSelect(conn, selectQuery);
		
		ResultSet rs=(ResultSet)qs.customQueryOutput;

		try{
			while(rs.next()){
				toReturn= true;
			}
		}catch(SQLException sqlE){
			//TODO
			
		}finally{	
			dbManager.dbDisconnect(conn);
		}
		
		return toReturn;
	}
	
}
