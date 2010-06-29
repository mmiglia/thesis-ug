package valueobject;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class is used as a reply upon receiving natural language query
 * from user
 */
@XmlRootElement
public class QueryReply {
	public boolean status;
	private QueryReply(){
		status = false;
	}
	public QueryReply(boolean stat){
		status = stat;
	}
}
