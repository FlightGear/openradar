package de.knewcleus.openradar.view;

import de.knewcleus.openradar.notify.INotification;

/**
 * A view notification is sent whenever the graphical representation
 * of an element needs to be updated.
 * 
 * @author Ralf Gerlich
 *
 */
public class ViewNotification implements INotification {
	protected final IView source;
	protected final boolean invalidateBounds;

	public ViewNotification(IView notifyingElement, boolean invalidateBounds) {
		this.source = notifyingElement;
		this.invalidateBounds = invalidateBounds;
	}

	@Override
	public IView getSource() {
		return source;
	}
	
	public boolean invalidateBounds() {
		return invalidateBounds;
	}
}
