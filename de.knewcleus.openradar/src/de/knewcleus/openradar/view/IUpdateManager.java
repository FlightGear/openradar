package de.knewcleus.openradar.view;

/**
 * A viewer repaint manager schedules dirty regions for repaint.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IUpdateManager {
	public void invalidateView(IView view);
	public void addDirtyView(IView view);
}
