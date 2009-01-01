package de.knewcleus.openradar.view;

import java.awt.Shape;

/**
 * A viewer repaint manager schedules dirty regions for repaint.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IViewerRepaintManager {
	public void scheduleFullRepaint();
	public void addDirtyRegion(Shape region);
	public void addDirtyView(IView view);
}
