package valueobject;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RegistrationReply {
	/**
	 * 0 is unsuccessful registration because username already exist
	 * 1 is unsuccessful registration bacause email already in use for another user
	 * 2 is successful registration
	 */
	public int status;
	
	/**
	 * Constructor for the class
	 * 
	 * @param status
	 *            2 for successful login, 0 or 1 for unsuccessful login
	 */
	public RegistrationReply(int status) {
		this.status = status;
	}
	private RegistrationReply(){
		status= 0;
	}
}
