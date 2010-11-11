package valueobject;
 
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Value returned after client attempts to login.
 */
@XmlRootElement
public class TestConnectionReply {
	/**
	 * 1 is successful login, 0 is unsuccessful login
	 */
	public int status;
	
	public String serverURI;

	/**
	 * Constructor for the class
	 * 
	 * @param status: 0 if connection is not available, 1 if is available 
	 *            

	 */
	public TestConnectionReply(int status,String serverURI) {
		this.status = status;
		this.serverURI=serverURI;
	}
	
	private TestConnectionReply(){
		this.status=0;
		this.serverURI="";
	}
	
}
