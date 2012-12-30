package de.knewcleus.openradar.view.mouse;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

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
	public void focusChanged(FocusChangeNotification event, MouseEvent e);

	/**
	 * This method exist to deliver position information for Selection To Pointer info
	 * 
	 * @return
	 */
    public Point2D getCenterViewCoordinates() ;
    public double getMilesPerDot() ;
    public int getAirSpeed();
}