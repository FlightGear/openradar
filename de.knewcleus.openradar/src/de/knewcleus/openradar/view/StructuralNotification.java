package de.knewcleus.openradar.view;

import de.knewcleus.openradar.notify.INotification;

/**
 * A structural notification is sent whenever an element is added or removed.
 * @author Ralf Gerlich
 *
 */
public class StructuralNotification implements INotification {
	protected final IContainer container;
	protected final IView element;
	protected final ChangeType changeType;
	
	public enum ChangeType { ADD, REMOVE };
	
	public StructuralNotification(IContainer container, IView element, ChangeType changeType) {
		this.container = container;
		this.element = element;
		this.changeType = changeType;
	}

	@Override
	public IContainer getSource() {
		return container;
	}
	
	/**
	 * @return the container in which the change occurs.
	 */
	public IContainer getContainer() {
		return container;
	}
	
	/**
	 * @return the element added or removed.
	 */
	public IView getElement() {
		return element;
	}

	/**
	 * @return the type of change (ADD or REMOVE)
	 */
	public ChangeType getChangeType() {
		return changeType;
	}
}
