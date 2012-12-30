package de.knewcleus.openradar.view.mouse;

import java.awt.event.MouseEvent;

import de.knewcleus.openradar.notify.Notifier;

public class FocusManager extends Notifier implements IFocusManager {
	protected IFocusableView currentFocusOwner = null;

	@Override
	public void forceCurrentFocusOwner(IFocusableView newFocusOwner, MouseEvent e) {
//		if (newFocusOwner==currentFocusOwner) {
//			return;
//		}
		final FocusChangeNotification notification = new FocusChangeNotification(
				this,currentFocusOwner, newFocusOwner);
		currentFocusOwner = newFocusOwner;
		/* First notify the old focus owner ... */
		if (notification.getPreviousOwner()!=null) {
			notification.getPreviousOwner().focusChanged(notification,e);
		}
		/* ... then the new one */
		if (newFocusOwner!=null) {
			newFocusOwner.focusChanged(notification,e);
		}
		notify(notification);
	}

	@Override
	public IFocusableView getCurrentFocusOwner() {
		return currentFocusOwner;
	}

}
