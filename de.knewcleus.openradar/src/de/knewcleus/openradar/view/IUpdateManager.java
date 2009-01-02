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
	public abstract ICanvas getCanvas();
	public abstract void setCanvas(ICanvas canvas);
}
