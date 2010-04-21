package businessobject;
/**
*/
public interface MapInterface {

	/**
	 * @param Return
	 * @return
	 */
	public boolean openConnection() ;

	/**
	 * This method will search for any given 'keyword businees' around the given
	 * location
	 * 
	 * @param Return
	 * @param longitude
	 *            longitude coordinate from GPS
	 * @param latitude
	 *            latitude coordinate from GPS
	 * @param business
	 *            keyword of the business
	 */
	public void searchLocalBusiness(float latitude, float longitude,
			String business) ;
}

