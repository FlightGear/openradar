package de.knewcleus.openradar.notify;

/**
 * A notification is an instance of an event sent by an INotifier.
 * 
 * @author Ralf Gerlich
 */
public interface INotification {
	public INotifier getSource();
}
