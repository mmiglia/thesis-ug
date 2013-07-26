package com.thesisug.communication.valueobject;

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
	
	public boolean versionOk;
	
	public String serverURI;
	
	/**
	 * Constructor for the class
	 * 
	 * @param int status - compatibility: 0001 not compatible, 0002 compatible
	 * @param String version - server version 
	 */
	public VersionReply (int status, String ver, boolean verOk, String serverURI) {
		this.status = status;
		this.serverVersion = ver;
		this.versionOk = verOk;
		this.serverURI = serverURI;
	}
	public VersionReply(int status) {
		this.status = status;
		this.serverVersion = "N/A";
		this.versionOk = false;
		this.serverURI = null;
	}
	public VersionReply() {
		status = 0;
		serverVersion = "N/A";
		versionOk = false;
		serverURI = null;
	}
}
