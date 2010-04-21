package businessobject;

/**
 * This class defines all the method that need to be implemented by actual map /
 * geolocation service (e.g. Google Maps)
 */
public interface MapInterface {

	/**
	 * Set up connection here. If the service need initialization, it can be
	 * done here
	 * 
	 * @return false if unsuccessful, true if successful
	 */
	public boolean openConnection();

	/**
	 * This method will search for any given 'keyword business' around the given
	 * location
	 * 
	 * @param longitude
	 *            longitude coordinate from GPS
	 * @param latitude
	 *            latitude coordinate from GPS
	 * @param business
	 *            keyword of the business
	 */
	public void searchLocalBusiness(float latitude, float longitude,
			String business);
}
