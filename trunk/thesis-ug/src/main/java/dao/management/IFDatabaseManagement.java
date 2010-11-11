package dao.management;

import java.util.ArrayList;

public interface IFDatabaseManagement {

	/**
	 * Creation of startup database (to use only once at setup time)
	 * @return
	 */
	public QueryStatus dbSetup();
	
	
	/**
	 * Connect to the database. 
	 * @return
	 */
	public Object dbConnect();
	
	/**
	 * Close the connection with the database
	 * @param connectionObjToClose this object specify wich connection has to be closed
	 * @return
	 */
	public Object dbDisconnect(Object connectionObjToClose);
	
	
	/**
	 * This method implements the USE dbName command
	 * @param dbName
	 * @param conn
	 * @return
	 */
	public Object chooseDatabase(String dbName,Object conn);
	
	/**
	 * SQL Query Builder method - actually not implemented
	 */
	public QueryStatus addRecord(Object conn,String table, ArrayList<String> fields, ArrayList<String> values);
	
	/**
	 * SQL Query Builder method - actually not implemented
	 */
	public QueryStatus updateRecord(Object conn,String table, ArrayList<String> fields, ArrayList<String> values);
	
	/**
	 * SQL Query Builder method - actually not implemented
	 */
	public QueryStatus deleteRecord(Object conn,String table, ArrayList<String> keyFields, ArrayList<String> keyValues);
	
	/**
	 * SQL Query Builder method - actually not implemented
	 */
	public QueryStatus selectRecordWithoutJoin(Object conn,String table, ArrayList<String> fieldToGet,ArrayList<String> whereFields, ArrayList<String> whereValues);
	
	/**
	 * This method is used to permit user to make a custom select to the system. 
	 * For custom we intend that sql syntax is made by hand and put into the sqlQuery parameter
	 * If the query isn't a select, the system return an error (see QueryStatus object)
	 */
	public QueryStatus customSelect(Object conn,String sqlQuery);
	
	/**
	 * This method is used to permit user to make a custom query to the system. 
	 * For custom we intend that sql syntax is made by hand and put into the sqlQuery parameter
	 */
	public QueryStatus customQuery(Object conn,String sqlQuery);
	

	
	//Database backup
	// TODO define methods for backup
	
	
	//Transaction management
	
	/**
	 * This method starts, when required, a transaction by injecting the 
	 * correct query for the related DBMS
	 */
	public QueryStatus startTransaction(Object conn);
	public QueryStatus commitTransaction(Object conn);
	public QueryStatus rollbackTransaction(Object conn);
	
	
	
}
