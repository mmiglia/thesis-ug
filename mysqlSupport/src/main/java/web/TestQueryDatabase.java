package web;

import java.sql.Connection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import dao.GroupDatabase;
import dao.management.QueryStatus;
import dao.management.mysql.MySQLDBManager;

import valueobject.Stringa;

/*
 * Classe creata per testare l'inserimento di qualcosa nel database
 * (dato che riesco a leggere ma non a inserire)
 */
@Path("/{username}/testDB")
public class TestQueryDatabase 
{
	private static final MySQLDBManager dbManager=new MySQLDBManager();
	private static final Logger log = LoggerFactory.getLogger(TestQueryDatabase.class);
	
	@GET
	@Path("/insertItemLocation")	
	//@Consumes("application/xml")
	public  static boolean  insertItemLocation(@PathParam("username") String userID)
	{
		Connection conn= (Connection) dbManager.dbConnect();
	
		String insertQuery="Insert into Item_foundIn_Loc (Item,Location,Username,N_visualizzazioni,N_voti) values ('biscotti','alimentari','anuska',1,1)";
		QueryStatus qs=dbManager.customQuery(conn, insertQuery);
		System.out.println(insertQuery);
		if(qs.execError){
			log.error(qs.explainError());
			qs.occourtedErrorException.printStackTrace();

			dbManager.dbDisconnect(conn);
			return false;
		}
		System.out.println("Nessun errore");
		
		return true;
	
	
	}
	
	@GET
	@Path("/serverOk")	
	@Produces("application/xml")
	public  static Stringa  serverOK(@PathParam("username") String userID)
	{
		log.info("Server is ok");
		Stringa ret = new Stringa("Tomcat is running!!!");
		return ret;
	}
	
}
