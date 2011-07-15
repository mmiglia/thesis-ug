package businessobject.google;

import java.util.ArrayList;
import java.util.List;


import valueobject.Result;

/**
 * Class used to pack all the Response data given by the various http request
 * into a single object
 * 
 *
 */
public class ResponseC {
	//private String json;
	//private ResponseDataC responseDataC;
	private String status;
	private  Result[] results;
	
	public ResponseC() {}
	
	public Result[] getResult()
	{
		return results;
	}

}