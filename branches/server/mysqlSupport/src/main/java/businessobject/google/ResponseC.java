package businessobject.google;

import java.util.ArrayList;
import java.util.List;

import valueobject.Address;

/**
 * Class used to pack all the Response data given by the various http request
 * into a single object
 * 
 *
 */
public class ResponseC {
	private String json;
	private ResponseDataC responseDataC;
	private String responseDetails;
	private Integer responseStatus;

	public void setJson(String j) {
		this.json = j;
	}

	public ResponseDataC getResponseData() {
		return responseDataC;
	}
	
	

	public void setResponseData(ResponseDataC responseDataC) {
		this.responseDataC = responseDataC;
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

	public static class ResponseDataC {
		
		private List<Address> results;

		public List<Address> getResults() {
			if (results == null) {
				results = new ArrayList<Address>();
			}
			return results;
		}

		public void setResults(List<Address> resultList) {
			this.results = resultList;
		}
	}
}