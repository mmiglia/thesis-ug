package dao.management.mysql;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.Configuration;

import dao.TaskDatabase;
import dao.management.IFDatabaseManagement;
import dao.management.QueryStatus;

public class MySQLDBManager implements IFDatabaseManagement {
	private static final Logger log = LoggerFactory.getLogger(MySQLDBManager.class);

	private static String dbConnectionString="jdbc:mysql://localhost:3306/mysql";
	//private static String dbConnectionString="jdbc:mysql://zelda.openlab-dist.org:3306/mysql";
	private static String user=Configuration.getInstance().constants.getProperty("Database_user");
	private static String password=Configuration.getInstance().constants.getProperty("Database_password");
	private static String databaseName= Configuration.getInstance().constants.getProperty("Database_name");
	private static String databaseCreationFile="/home/jaxer/comandiSql";
	
	@Override 
	public QueryStatus addRecord(Object conn,String table, ArrayList<String> fields,
			ArrayList<String> values)  {
		QueryStatus queryStatusOutput=new QueryStatus();
		
		if(table.equals("") || fields.size()==0 || values.size()==0){
			queryStatusOutput.execError=true;
			queryStatusOutput.execErrorID=1;
			return queryStatusOutput;
		}
		
		//Build query
		String query="INSERT INTO "+ table+ " (";

		int i=0;
		for(i=0;i<fields.size()-1;i++){
			query +=" "+fields.get(i)+ ",";
		}
		query +=" "+fields.get(i)+ ") ";
		
		query+=" values (";
		
		for(i=0;i<values.size()-1;i++){
			query +=" '"+values.get(i)+ "',";
		}
		query +=" '"+values.get(i)+ "');";
		
		System.out.println(query);

		Statement stmt=createStatement((Connection)conn);
		
		if(stmt==null){
			queryStatusOutput.execError=true;
			queryStatusOutput.execErrorID=2;
			return queryStatusOutput;
		}
		int output=-1;
		try {
			output=stmt.executeUpdate(query);
		} catch (SQLException e) {
			return this.sqlExceptionManagement(queryStatusOutput, e, 3);
		}
		
		try {
			if(!(((Connection)dbDisconnect(conn)).isClosed())){
				queryStatusOutput.execWarning=true;
				queryStatusOutput.execWarningID=1;
				
			}
		} catch (SQLException e) {
			return this.sqlExceptionManagement(queryStatusOutput, e, 4);
		}
		
		System.out.println("Fatto: "+output);
		
		return queryStatusOutput;
	}

	
	private Statement createStatement(Connection con){
		
		Statement stmt;
		try {
			stmt = con.createStatement();
		} catch (SQLException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return null;
		}
		return stmt;
		
	}
	
	@Override
	public QueryStatus updateRecord(Object conn,String table, ArrayList<String> fileds,
			ArrayList<String> values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryStatus deleteRecord(Object conn,String table, ArrayList<String> keyFields,
			ArrayList<String> keyValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QueryStatus selectRecordWithoutJoin(Object conn,String table,
			ArrayList<String> fieldToGet, ArrayList<String> whereFields,
			ArrayList<String> whereValues) {
		/*
		//Build query
		String query="Select ";
		int i=0;
		for(i=0;i<fields.size()-1;i++){
			query +=" "+fields.get(i)+ ",";
		}
		query +=" "+fields.get(i)+ " ";
		
		query+=" from "+ table + " ";
		
		
		query+=" where ";
		for(i=0;i<fields.size()-1;i++){
			query +=" "+fields.get(i)+ ",";
		}
		
		
		*/
		return null;
	}

	@Override
	public QueryStatus customSelect(Object conn,String sqlQuery) {
		QueryStatus qsOutput=new QueryStatus();
		
		Statement stmt=this.createStatement((Connection)conn);
		boolean outExecute=false;
		try {
			outExecute=stmt.execute(sqlQuery);
		} catch (SQLException e) {
			return this.sqlExceptionManagement(qsOutput, e, 7);
		}
		
		if(outExecute){
			try {
				qsOutput.customQueryOutput=stmt.getResultSet();
			} catch (SQLException e) {
				return this.sqlExceptionManagement(qsOutput, e, 8);
			}
		}else{
			qsOutput.execError=true;
			qsOutput.execErrorID=9;			
		}
		
		
		
		return qsOutput;
	}
 
	@Override
	public QueryStatus startTransaction(Object conn) {		
		QueryStatus qs=customQuery(conn,"SET autocommit=0;");
		if(qs.execError){
			return qs;
		}
		qs=customQuery(conn,"START TRANSACTION;");
		if(!qs.execError){
			log.debug("Start transaction correctly executed");
		}else{
			log.error("Error Start transaction rollback");
		}
		return qs;		
	}

	@Override
	public QueryStatus commitTransaction(Object conn) {
		
		QueryStatus qs=customQuery(conn,"COMMIT");
		if(!qs.execError){
			log.debug("Commit correctly executed");
		}else{
			log.error("Error during commit");
		}
		return qs;
	}

	@Override
	public QueryStatus rollbackTransaction(Object conn) {
		QueryStatus qs=customQuery(conn,"ROLLBACK");
		if(!qs.execError){
			log.debug("Rollback correctly executed");
		}else{
			log.error("Error during rollback");
		}
		return qs;
		// TODO GESTIONE LOG

	}

	@Override
	public Object dbConnect() {
		
		 try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("Error during db connection "+e.getLocalizedMessage());
			}

			String url =
	            "jdbc:mysql://localhost:3306/mysql";

			Connection con =null;
			try {
				con = DriverManager.getConnection(
						dbConnectionString ,user,password);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error("Error during db connection "+e.getMessage());
				return null;
			}
			log.debug("Connection established");
			
			this.chooseDatabase(this.databaseName, con);
			
			return con;
	}

	@Override
	public Object dbDisconnect(Object connectionObjToClose) {
		// TODO GESTIONE LOG
		try {
			((Connection)connectionObjToClose).close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("Error during database disconnection:" + e.getMessage());
			return null;
		}
		return connectionObjToClose;
	}
	
	@Override 
	public Object chooseDatabase(String dbName,Object conn) {
		// TODO GESTIONE LOG
		QueryStatus queryStatusOutput=new QueryStatus();
		
		
		Statement stmt=this.createStatement((Connection)conn);
		
		if(stmt==null){
			queryStatusOutput.execError=true;
			queryStatusOutput.execErrorID=2;
			log.error(queryStatusOutput.explainError());
			return queryStatusOutput;
		}
		
		try {
			stmt.execute("use "+dbName);
		} catch (SQLException e) {
			return this.sqlExceptionManagement(queryStatusOutput, e, 5);
		}
		
		
		return queryStatusOutput;
	}
	
	
	@Override
	public QueryStatus dbSetup() {
		// TODO GESTIONE LOG
		QueryStatus queryStatus=new QueryStatus();
		
		Connection conn=(Connection)this.dbConnect();
		
		
		Statement stmt=createStatement(conn);
		
		// TODO - Aggiungere l'uso del file con tutte le query di creazione delle tabelle, il file è databaseCreation, per ora uso il plugin di Eclipse
		
		
		
		return queryStatus;
	}
	
	@Override
	public QueryStatus customQuery(Object conn, String sqlQuery) {
		// TODO GESTIONE LOG
		QueryStatus qsOutput=new QueryStatus();
		
		Statement stmt=this.createStatement((Connection)conn);
		boolean outExecute=false;
		try {
			outExecute=stmt.execute(sqlQuery);
		} catch (SQLException e) {
			return this.sqlExceptionManagement(qsOutput, e, 7);
		}
		
		if(outExecute){
			try {
				qsOutput.customQueryOutput=stmt.getResultSet();
			} catch (SQLException e) {
				return this.sqlExceptionManagement(qsOutput, e, 8);
			}
		}else{
			try {
				qsOutput.customQueryOutput=stmt.getUpdateCount();
			} catch (SQLException e) {
				return this.sqlExceptionManagement(qsOutput, e, 10);
			}
			
			
		}
		
		
		
		return qsOutput;
	}

	
	
	

public static void main(String[] args){
		
		MySQLDBManager mng=new MySQLDBManager();
		
		QueryStatus q=new QueryStatus();
		
		q=mng.customQuery(mng.dbConnect(),"insert into Event (dueDate,startTime,endTime,latitude,longitude,ReminderId) values (\"10:10\",\"05:00\",\"11:00\",\"15,40\",\"20,30\",1);");
		
		if(q.execError){
			System.out.println(q.explainError());
			q.occourtedErrorException.printStackTrace();
			return;
		}
		
		q=mng.customQuery(mng.dbConnect(),"Select * from Event");
		
		if(q.execError){
			System.out.println(q.explainError());
			q.occourtedErrorException.printStackTrace();
			return;
		}
		
		ResultSet rs=(ResultSet)q.customQueryOutput;
		int counter=0;
		try{
		while(rs.next()){
			counter++;
			System.out.println(counter+")"+rs.getInt("id")+"|"+rs.getString("dueDate"));
			
		}
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
	}
	
	private QueryStatus sqlExceptionManagement(QueryStatus qs,Exception e,int errorID){
		qs.execError=true;
		qs.execErrorID=errorID;
		qs.occourtedErrorException=e;
		log.error(qs.explainError());
		log.error(qs.occourtedErrorException.getMessage());
		return qs;
		
	}

}
