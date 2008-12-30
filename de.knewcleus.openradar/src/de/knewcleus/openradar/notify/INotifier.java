package de.knewcleus.openradar.notify;

/**
 * A notifier sends out notifications of specific events to its listeners.
 * 
 * @author Ralf Gerlich
 *
 */
public interface INotifier {
	public void registerListener(INotificationListener listener);
	public void unregisterListener(INotificationListener listener);
}
