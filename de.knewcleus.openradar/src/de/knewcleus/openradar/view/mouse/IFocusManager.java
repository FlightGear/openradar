package de.knewcleus.openradar.view.mouse;

import java.awt.event.MouseEvent;

import de.knewcleus.openradar.notify.INotifier;

/**
 * The focus manager is responsible for assigning focus to focusable events
 * and informing them and listeners about changes in focus.
 * 
 * The focus is held by at most one {@link IFocusableView} at any time.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IFocusManager extends INotifier {
	/**
	 * Get the current focus owner. If the focus is held by no view,
	 * <code>null</code> is returned.
	 */
	public IFocusableView getCurrentFocusOwner();

	/**
	 * Force the focus to be moved to the given view.
	 */
	public void forceCurrentFocusOwner(IFocusableView newFocusOwner, MouseEvent e);
}
