package com.thesisug.communication;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class ClientFactory {
	private static final int REGISTRATION_TIMEOUT = 30000;

	public static HttpClient getClient() {
		HttpClient result = new DefaultHttpClient();
		final HttpParams params = result.getParams();
		HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, REGISTRATION_TIMEOUT);
		ConnManagerParams.setTimeout(params, REGISTRATION_TIMEOUT);
		return result;
	}
}
