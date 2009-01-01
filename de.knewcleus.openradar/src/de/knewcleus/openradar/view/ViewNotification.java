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

	public ViewNotification(IView notifyingElement) {
		this.source = notifyingElement;
	}

	@Override
	public IView getSource() {
		return source;
	}
}
