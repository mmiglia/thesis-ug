package businessobject;

import valueobject.VersionReply;

public class CheckVerCompatibility {
	private static String appVersion=Configuration.getInstance().constants.getProperty("VERSION");
	private static String minClientVersion=Configuration.getInstance().constants.getProperty("MIN_CLIENT_VER");
	
	/**
	 * This method provide to check the compatibility of the client version
	 * with the server version
	 * 
	 * @param String clientVer - client version
	 * @return object VersionReply - the object that contain the version and server status code
	 */
	public static VersionReply checkCompatibility (String clientVersion) {
		int clientVer = Integer.parseInt(clientVersion.replace(".", ""));
		int minClientVer = Integer.parseInt(minClientVersion.replace(".", ""));
		
		if (clientVer >= minClientVer)
			return new VersionReply(0002, appVersion);
		else
			return new VersionReply(0001, appVersion);
	}
}
