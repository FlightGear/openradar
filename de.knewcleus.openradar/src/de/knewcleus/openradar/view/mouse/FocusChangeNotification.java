package de.knewcleus.openradar.view.mouse;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotifier;

/**
 * A focus change notification is issued by the {@link IFocusManager}
 * whenever the focus changes.
 * 
 * @author Ralf Gerlich
 * @see IFocusableView#focusChanged(FocusChangeNotification)
 *
 */
public class FocusChangeNotification implements INotification {
	protected final IFocusManager source;
	protected final IFocusableView previousOwner;
	protected final IFocusableView newOwner;

	public FocusChangeNotification(IFocusManager source,
			IFocusableView previousOwner, IFocusableView newOwner) {
		this.source = source;
		this.previousOwner = previousOwner;
		this.newOwner = newOwner;
	}

	@Override
	public INotifier getSource() {
		return source;
	}
	
	public IFocusableView getPreviousOwner() {
		return previousOwner;
	}
	
	public IFocusableView getNewOwner() {
		return newOwner;
	}
}
