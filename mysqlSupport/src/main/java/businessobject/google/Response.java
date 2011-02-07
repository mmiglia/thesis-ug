package businessobject.google;

import java.util.ArrayList;
import java.util.List;

import valueobject.Hint;

/**
 * Class used to pack all the Response data given by the various http request
 * into a single object
 * 
 *
 */
public class Response {
	private String json;
	private ResponseData responseData;
	private String responseDetails;
	private Integer responseStatus;

	public void setJson(String j) {
		this.json = j;
	}

	public ResponseData getResponseData() {
		return responseData;
	}

	public void setResponseData(ResponseData responseData) {
		this.responseData = responseData;
	}

	public String getJson() {
		return json;
	}

	public boolean isOK() {
		if (this.getResponseStatus() == null) {
			return false;
		} else {
			return this.getResponseStatus().intValue() == 200;
		}
	}

	public boolean isError() {
		return !isOK();
	}

	public String getResponseDetails() {
		return responseDetails;
	}

	public void setResponseDetails(String details) {
		this.responseDetails = details;
	}

	public Integer getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(Integer status) {
		this.responseStatus = status;
	}

	public static class ResponseData {
		private List<Hint> results;

		public List<Hint> getResults() {
			if (results == null) {
				results = new ArrayList<Hint>();
			}
			return results;
		}

		public void setResults(List<Hint> resultList) {
			this.results = resultList;
		}
	}
}
