package valueobject;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VersionReply {
	/**
	 * 0001 client version incompatible with server version
	 * 0002 client version compatible with server version
	 */
	public int status;
	/**
	 * the server version
	 */
	public String serverVersion;
	
	/**
	 * Constructor for the class
	 * 
	 * @param int status - compatibility: 0001 not compatible, 0002 compatible
	 * @param String version - server version 
	 */
	public VersionReply (int status, String ver) {
		this.status = status;
		this.serverVersion = ver;
	}
	private VersionReply(int status) {
		this.status = status;
		this.serverVersion = "N/A";
	}
	private VersionReply() {
		status = 0;
		serverVersion = "N/A";
	}
}
