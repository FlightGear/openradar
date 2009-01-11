package de.knewcleus.openradar.view.mouse;

import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.IView;

/**
 * A focusable view is a view which can gain the mouse focus.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IFocusableView extends IPickable, IView {
	/**
	 * This method is called whenever the view gains or loses the focus.
	 * 
	 * The notification is first sent to the previous focus owner,
	 * and then to the new focus owner.
	 */
	public void focusChanged(FocusChangeNotification event);
}
