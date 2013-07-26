package com.thesisug.notification;

import android.app.Notification;
/**
 * Bare container for a notification and some extra info. Constructor is
 * provided to allow initialization of all fields with a single statement.
 * @author lorenzo
 *
 */
public class NotificationContainer {

	public final String TAG = "thesisug - NotificationContainer";
	public String sentence;
	public Notification notification;
	public long duration;
	/**
	 * Constructor, allows to initialize all fields with a single statement.
	 * @param n notification object
	 * @param s text displayed both as ticker-text and expanded notification text
	 * @param d duration of the notification vibration pattern (for morse notifications)
	 */
	public NotificationContainer(Notification n, String s, long d) {
		notification = n;
		sentence = s;
		duration = d;
	}
}