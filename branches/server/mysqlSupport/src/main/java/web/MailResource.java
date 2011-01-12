package web;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import businessobject.MailUtility;

@Path("/mail")
public class MailResource {
	private static Logger log = LoggerFactory.getLogger(MailResource.class);
	
	@GET
	@Produces("application/xml")
	public void sendMail(@QueryParam("mitt")String destinatario, @QueryParam("dest")String oggetto, 
			@QueryParam("testo")String testo) throws Exception {
//	public void getEvent(@QueryParam("mitt")String mittente, @QueryParam("dest")String destinatario, 
//			@QueryParam("subj")String oggetto, @QueryParam("testo")String testo) {
		//System.out.println("Invio mail from: "+mittente+" To: "+destinatario);
		//System.out.println("Oggetto: "+oggetto);
		//System.out.println("Testo mail: "+testo);
		//MailUtility.sendMail(mittente, destinatario, oggetto, testo);
		//MailUtility.sendMail(mittente, destinatario);
		boolean sent = MailUtility.sendMail(destinatario, oggetto, testo);

		//System.out.println("Mail inviata con successo!");
	}
}
