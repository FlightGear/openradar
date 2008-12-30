package de.knewcleus.openradar.notify;

public class Notification implements INotification {
	protected final INotifier notifier;
	
	public Notification(INotifier notifier) {
		this.notifier = notifier;
	}

	@Override
	public INotifier getSource() {
		return notifier;
	}
}
