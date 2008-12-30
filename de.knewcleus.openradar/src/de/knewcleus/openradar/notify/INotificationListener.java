package de.knewcleus.openradar.notify;

/**
 * A listener listens for notifications from an INotifier.
 * 
 * @author Ralf Gerlich
 */
public interface INotificationListener {
	/**
	 * Accept a notification.
	 */
	public void acceptNotification(INotification notification);
}
