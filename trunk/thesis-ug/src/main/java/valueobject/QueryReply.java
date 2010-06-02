package valueobject;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class QueryReply {
	Boolean status;
	private QueryReply(){
		status = false;
	}
	public QueryReply(boolean stat){
		status = stat;
	}
}
