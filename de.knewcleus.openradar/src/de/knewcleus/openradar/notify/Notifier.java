package de.knewcleus.openradar.notify;

import java.util.HashSet;
import java.util.Set;

public class Notifier implements INotifier {
	protected final Set<INotificationListener> listeners=new HashSet<INotificationListener>();

	@Override
	public void registerListener(INotificationListener listener) {
		assert(!listeners.contains(listener));
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(INotificationListener listener) {
		assert(listeners.contains(listener));
		listeners.remove(listener);
	}
	
	/**
	 * Send out a notification to all listeners.
	 */
	public void notify(INotification notification) {
		for (INotificationListener listener: listeners) {
			listener.acceptNotification(notification);
		}
	}
}
