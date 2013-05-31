package web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.CheckVerCompatibility;

import valueobject.VersionReply;

@Path("/checkVer")
/**
 * Resource class for testing compatibility version with client
*/
public class CheckVersionResource {
	private static Logger log = LoggerFactory.getLogger(LogoutResource.class);
	/**
	 * This method will be used to check the server version
	 * @param username unique ID of the user
	*/
	@GET
	@Produces("application/xml")
	public VersionReply getVersion(@QueryParam("ver") String clientVersion) {	
	    log.info("Checking if the server version in compatible with the client version ("+clientVersion+")");
	    return CheckVerCompatibility.checkCompatibility(clientVersion);		
	}
}
