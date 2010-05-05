package businessobject.google;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.AllClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import valueobject.Hint;

import businessobject.MapSubscriber;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MapsClient extends MapSubscriber {
	private HttpClient httpClient;
	private static final String LOCAL_SEARCH_URI = "http://ajax.googleapis.com/ajax/services/search/local";
	private final static Logger log = LoggerFactory.getLogger(MapsClient.class);

	public MapsClient() {
		this(new DefaultHttpClient());
	}

	public MapsClient(HttpClient hClient) {
		super();
		this.httpClient = hClient;
		if (CONSTANTS.containsKey("http.proxyHost")
				&& CONSTANTS.containsKey("http.proxyPort")) {
			HttpHost proxy = new HttpHost(CONSTANTS.getProperty("HTTP_PROXY"),
					Integer.parseInt(CONSTANTS.getProperty("HTTP_PORT")));
			this.httpClient.getParams().setParameter(
					ConnRoutePNames.DEFAULT_PROXY, proxy);
		}
		this.httpClient.getParams().setParameter(
				AllClientPNames.USER_AGENT,
				"Mozilla/5.0 (Java) Gecko/20081007 "
						+ CONSTANTS.getProperty("APP_NAME") + "-"
						+ CONSTANTS.getProperty("COMPANY_NAME") + "-"
						+ CONSTANTS.getProperty("VERSION"));
		this.httpClient.getParams().setIntParameter(
				AllClientPNames.CONNECTION_TIMEOUT, 10 * 1000);
		this.httpClient.getParams().setIntParameter(AllClientPNames.SO_TIMEOUT,
				25 * 1000);
	}

	@Override
	public boolean openConnection() {
		// since AJAX API doesn't need handshake, we always return true
		return true;
	}

	@Override
	public List<Hint> searchLocalBusiness(float lat, float lon, String query) {
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("sll", lat + "," + lon);
		params.put("mrt", "localonly");
		if (query != null) {
			params.put("q", query);
		}

		Response r = sendSearchRequest(LOCAL_SEARCH_URI, params);
		return r.getResponseData().getResults();
	}

	private Response sendSearchRequest(String url, Map<String, String> params) {
		if (params.get("v") == null) {
			params.put("v", "1.0");
		}
		String json = sendHttpRequest("GET", url, params);
		log.info("JSON : " + json);
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Response r = gson.fromJson(json, Response.class);
		r.setJson(json);

		return r;
	}

	private String buildQueryString(Map<String, String> params) {
		StringBuffer query = new StringBuffer();

		if (params.size() > 0) {
			query.append("?");

			for (String key : params.keySet()) {
				query.append(key);
				query.append("=");
				query.append(encodeParameter(params.get(key)));
				query.append("&");
			}

			if (query.charAt(query.length() - 1) == '&') {
				query.deleteCharAt(query.length() - 1);
			}
		}
		return query.toString();
	}

	private String encodeParameter(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private String sendHttpRequest(String httpMethod, String url,
			Map<String, String> params) {
		HttpClient c = this.httpClient;
		HttpUriRequest request = null;

		if ("GET".equalsIgnoreCase(httpMethod)) {
			String queryString = buildQueryString(params);
			url = url + queryString;
			request = new HttpGet(url);
		} else {
			throw new RuntimeException("unsupported method: " + httpMethod);
		}

		HttpResponse response = null;
		HttpEntity entity = null;

		try {
			response = c.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				throw new RuntimeException(
						"unexpected HTTP response status code = " + statusCode);
			}
			entity = response.getEntity();
			return EntityUtils.toString(entity);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}