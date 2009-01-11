package de.knewcleus.openradar.view.mouse;

import de.knewcleus.openradar.notify.Notifier;

public class FocusManager extends Notifier implements IFocusManager {
	protected IFocusableView currentFocusOwner = null;

	@Override
	public void forceCurrentFocusOwner(IFocusableView newFocusOwner) {
		if (newFocusOwner==currentFocusOwner) {
			return;
		}
		final FocusChangeNotification notification = new FocusChangeNotification(
				this,currentFocusOwner, newFocusOwner);
		currentFocusOwner = newFocusOwner;
		/* First notify the old focus owner ... */
		if (notification.getPreviousOwner()!=null) {
			notification.getPreviousOwner().focusChanged(notification);
		}
		/* ... then the new one */
		if (newFocusOwner!=null) {
			newFocusOwner.focusChanged(notification);
		}
		notify(notification);
	}

	@Override
	public IFocusableView getCurrentFocusOwner() {
		return currentFocusOwner;
	}

}
