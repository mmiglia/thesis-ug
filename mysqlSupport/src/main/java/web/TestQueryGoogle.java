package web;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import valueobject.Hint;

import businessobject.Configuration;
import businessobject.LocationAwareManager;
import businessobject.MapManager;
import businessobject.OntologyReasoner;

import businessobject.google.MapsClient;
import businessobject.google.Response;
import businessobject.google.Response.ResponseData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/*
 * Classe per testare le query di Google e i metodi che in questo progetto 
 * si richiamano per invocarle, in modo da vedere con jconsole quale occupa più
 * memoria.
 */
@Path("/test")
public class TestQueryGoogle {
	
	private static String googleApiKey=Configuration.getInstance().constants.getProperty("GOOGLE_API_KEY");
	private static final String LOCAL_SEARCH_URI = "http://ajax.googleapis.com/ajax/services/search/local";
	final int N=10;
	/*
	 * Per testare le api di google per ottenere la lista di suggerimenti date
	 *  le coordinate, con 100 interrogazioni.
	 *  Ritorna una stringa e il tempo in secondi che il metodo impiega per
	 *  fare 100 interrogazioni.
	 */
	@GET
	@Path("/googleapi")	
	@Consumes("application/xml")
	public String tryConnection()
	{	
		HttpUriRequest request = null;
		HttpClient c = new DefaultHttpClient();
		
		String url = "http://ajax.googleapis.com/ajax/services/search/local?sll=45.783733%2C11.87303&mrt=localonly&key=ABQIAAAAO_vXN4b2QPsxymrn7XXy2RRdqdQGkvyvReiMijBZHBdeXkehNxREVXpWe03KkedlpKVrws2dl0cHjA&q=panetteria&v=1.0";
		String json = "";
		double inizio = System.currentTimeMillis();

		for (int n = 1; n <= N; n++)
		{
		
			request = new HttpGet(url);
			HttpResponse response = null;
			HttpEntity entity = null;
			
			try 
			{
				
				response = c.execute(request);			
				int statusCode = response.getStatusLine().getStatusCode();			
				if (statusCode != HttpStatus.SC_OK) 
				{
					throw new RuntimeException(
							"unexpected HTTP response status code = " + statusCode);
				}
				entity = response.getEntity();
				json = json + " QUERY "+n+": "+EntityUtils.toString(entity);
			
			} catch (Exception ex) 
			  {
				throw new RuntimeException(ex);
			   }
		
		}
		double fine = System.currentTimeMillis();
		double tempo = (fine-inizio)/1000;
		return json+ " TEMPO TOTALE PER ESEGUIRE LE QUERY: "+tempo+ "sec";
	}
/*
	Per testare le api di google per ottenere la lista di suggerimenti date 
	le coordinate,con 100 interrogazioni. 
	Tra un'interrogazione e l'altra è inserito un delay.
	Ritorna una stringa e il tempo in secondi che il metodo impiega per
	fare 100 interrogazioni.
*/
	@GET
	@Path("/googleapiDelay")	
	@Consumes("application/xml")
	public String tryConnectionDelay()
	{	
		HttpUriRequest request = null;
		HttpClient c = new DefaultHttpClient();
		
		String url = "http://ajax.googleapis.com/ajax/services/search/local?sll=45.783733%2C11.87303&mrt=localonly&key=ABQIAAAAO_vXN4b2QPsxymrn7XXy2RRdqdQGkvyvReiMijBZHBdeXkehNxREVXpWe03KkedlpKVrws2dl0cHjA&q=panetteria&v=1.0";
		String json = "";
		double inizio = System.currentTimeMillis();

		for (int n = 1; n <= N; n++)
		{
		
			request = new HttpGet(url);
			HttpResponse response = null;
			HttpEntity entity = null;
			long delays[] = {200, 300, 400, 500, 600, 700, 800, 900, 1000};
			Random generator = new Random();

			try 
			{
				//introduce a random delay (taken from the values in delays[] - 200-1000ms
				//to prevent too many query 
				long delay = delays[generator.nextInt(delays.length)];
				Thread.sleep(delay);
				response = c.execute(request);			
				int statusCode = response.getStatusLine().getStatusCode();			
				if (statusCode != HttpStatus.SC_OK) 
				{
					throw new RuntimeException(
							"unexpected HTTP response status code = " + statusCode);
				}
				entity = response.getEntity();
				json = json + " QUERY "+n+": "+EntityUtils.toString(entity);
			} catch (Exception ex) 
			  {
				throw new RuntimeException(ex);
			   }
		
		}
		double fine = System.currentTimeMillis();
		double tempo = (fine-inizio)/1000;
		return json+ " TEMPO TOTALE PER ESEGUIRE LE QUERY: "+tempo+ "sec";
	}

/*	//DA ERRORI
	Per testare le api di google per ottenere la lista di suggerimenti date 
	le coordinate,con 100 interrogazioni. 
	Tra un'interrogazione e l'altra è inserito un delay.
	 Inoltre usa gson e ritorna una lista di Hint.
*/
	@GET
	@Path("/googleapiDelayListHint")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> tryConnectionDelayListHint()
	{	
		HttpUriRequest request = null;
		HttpClient c = new DefaultHttpClient();
		
		String url = "http://ajax.googleapis.com/ajax/services/search/local?sll=45.783733%2C11.87303&mrt=localonly&key=ABQIAAAAO_vXN4b2QPsxymrn7XXy2RRdqdQGkvyvReiMijBZHBdeXkehNxREVXpWe03KkedlpKVrws2dl0cHjA&q=panetteria&v=1.0";
		String json = "";
		double inizio = System.currentTimeMillis();
		List<Hint> result = new LinkedList<Hint>();
		for (int n = 1; n <= N; n++)
		{
		
			request = new HttpGet(url);
			HttpResponse response = null;
			HttpEntity entity = null;
			long delays[] = {200, 300, 400, 500, 600, 700, 800, 900, 1000};
			Random generator = new Random();

			try 
			{
				//introduce a random delay (taken from the values in delays[] - 200-1000ms
				//to prevent too many query 
				long delay = delays[generator.nextInt(delays.length)];
				Thread.sleep(delay);
				response = c.execute(request);			
				int statusCode = response.getStatusLine().getStatusCode();			
				if (statusCode != HttpStatus.SC_OK) 
				{
					throw new RuntimeException(
							"unexpected HTTP response status code = " + statusCode);
				}
				entity = response.getEntity();
				//String json = EntityUtils.toString(entity);
				json = json + " QUERY "+n+": "+EntityUtils.toString(entity);
		
				
				GsonBuilder builder = new GsonBuilder();
				Gson gson = builder.create();
				Response r = gson.fromJson(json, Response.class);
				r.setJson(json);
				ResponseData respData=r.getResponseData();
			
				result.addAll(respData.getResults());
				 
			} catch (Exception ex) 
			  {
				throw new RuntimeException(ex);
			   }
		
		}
		double fine = System.currentTimeMillis();
		double tempo = (fine-inizio)/1000;
		return result;
	}
	
	/*
	 Per testare il metodo SearchLocalBusiness nella classe MapsClient in 
	 businessobject.google, questa classe poi richiama sendSearchRequest 
	 in cui c'è gson 
	 */
	@GET
	@Path("/MapsClientSearchLocalBusiness")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> MapsClientSearchLocalBusiness()
	{
		MapsClient google = new MapsClient();
		float lat = 45;
		float lon = 11;
		String query = "panetteria";
		List<Hint> result = new LinkedList<Hint>();
		for (int n = 1; n <= N; n++)
		{
			result.addAll(google.searchLocalBusiness(lat, lon, query));
		}
		return result;
	
	}
	
	/*
	 Per testare il metodo getSearchQuery nella classe OntologyReasoner in 
	 businessobject
	 */
	@GET
	@Path("/OntologyReasonerGetSearchQuery")	
	@Consumes("application/xml")
	//@Produces("application/xml")
	//public List<String> OntologyReasonerGetSearchQuery()
	public String OntologyReasonerGetSearchQuery()
	{
		String need = "latte";
		//List<String> result = new LinkedList<String>();
		String result = "";
		for (int n = 1; n <= N; n++)
		{
			//result.addAll(OntologyReasoner.getInstance().getSearchQuery(need));
			result = result + OntologyReasoner.getInstance().getSearchQuery(need);
		}
		return result;
	}
	
	/*
	 Per testare il metodo SearchLocalBusiness nella classe MapManager in 
	 businessobject
	 */
	@GET
	@Path("/MapManagerSearchLocalBusiness")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> MapManagerSearchLocalBusiness()
	{
		float lat = 45;
		float lon = 11;
		String query = "panetteria";
		List<Hint> result = new LinkedList<Hint>();
		for (int n = 1; n <= N; n++)
		{
			result.addAll(MapManager.getInstance().searchLocalBusiness(lat, lon, query));
		}
		return result;
	}
	
	/* 
	 Per testare il metodo SearchLocalBusiness nella classe MapManager in 
	 businessobject
	 */
	@GET
	@Path("/ContextListenerCheckLocationSingle")	
	@Consumes("application/xml")
	@Produces("application/xml")
	public List<Hint> ContextListenerCheckLocationSingle()
	{
		String q = "andare a prendere il pane";
		String userid = "anuska";
		float lat=45;
		float lon=11;
		int dist=0;
		List<Hint> result = new LinkedList<Hint>();
		for (int n = 1; n <= N; n++)
		{
			result.addAll(LocationAwareManager.checkLocationSingle(userid, q, lat, lon, dist));
		}
		return result;
	}	
	
}
