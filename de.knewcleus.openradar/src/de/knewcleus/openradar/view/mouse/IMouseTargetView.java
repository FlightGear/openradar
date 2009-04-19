package de.knewcleus.openradar.view.mouse;

import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.IView;

/**
 * <p>A mouse target view is a view with which the user can interact by
 * means of the mouse.</p>
 * 
 * <p>Two distinct mouse buttons are available, the action button and the
 * information button. With each of these the user can click, press and hold or
 * press and drag on a mouse target view.</p>
 * 
 * <p>A <strong>click</strong> is performed by pressing and releasing the mouse button within
 * a time limit called the <strong>hold delay</strong>. Multiple successive clicks
 * are counted (e.g. double-clicks) if the time between the releases does not exceed
 * the <strong>multi-click delay</strong>.</p>
 * 
 * <p><strong>Press and hold</strong> is performed by pressing and holding the button without moving the
 * mouse for at least the time given by the <strong>hold delay</strong>.</p>
 * 
 * <p><strong>Press and drag</strong> is performed by pressing and holding the button and moving the
 * mouse cursor.
 * Press and holding followed by dragging without releasing the button inbetween is
 * interpreted the same as if the button had been released in the mean time.</p>
 * 
 * @author Ralf Gerlich
 * @see MouseButtonManager
 * @see ButtonType
 */
public interface IMouseTargetView extends IPickable, IView {
	public void processMouseInteractionEvent(MouseInteractionEvent e);
}
