package dao;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

public class OntologyDatabase {
	
	private static final Logger log = LoggerFactory.getLogger(OntologyDatabase.class);
	
	//MySQL database manager
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	
	public static String addItemInLocation(String user, String item,
			String location) {
		
		Connection conn= (Connection) dbManager.dbConnect();
		log.info("Connected to the db");
		QueryStatus qs=dbManager.startTransaction(conn);
		
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			log.error("Error during transaction starting... Assertion not added");
			dbManager.dbDisconnect(conn);
			return "Error during transaction starting... Assertion not added";
		}
		
		String insertQuery="Insert into item_founIn_Loc (Item,Location,Username,N_visualizzazioni,N_voti) values ('"+item+"','"+location+"','"+user+"',1,1)";
	
		qs=dbManager.customQuery(conn, insertQuery);
		
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();
			dbManager.rollbackTransaction(conn);
			log.error("Error during Ontology adding... Assertion not added");
			dbManager.dbDisconnect(conn);
			return "Error during transaction starting... Assertion not added";
		}
		
		log.info("Assertion added!");	
			
		dbManager.dbDisconnect(conn);
	
		return "Assertion added";
	}

}
