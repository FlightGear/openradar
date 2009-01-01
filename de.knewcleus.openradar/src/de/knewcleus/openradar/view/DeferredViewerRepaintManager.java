package de.knewcleus.openradar.view;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * A deferred viewer repaint manager collects dirty regions and schedules them for
 * repaint as soon as the system is idle.
 * 
 * @author Ralf Gerlich
 *
 */
public class DeferredViewerRepaintManager implements IViewerRepaintManager, Runnable {
	protected final JComponent managedViewer;
	protected Rectangle2D dirtyRegion = new Rectangle2D.Double();
	protected final Set<IBoundedView> dirtyViews = new HashSet<IBoundedView>();
	protected boolean fullRepaint = false;
	protected boolean repaintScheduled = false;

	public DeferredViewerRepaintManager(JComponent managedViewer) {
		this.managedViewer = managedViewer;
	}
	
	@Override
	public void scheduleFullRepaint() {
		fullRepaint = true;
		scheduleRepaint();
	}

	@Override
	public void addDirtyRegion(Shape region) {
		final Rectangle2D regionBounds = region.getBounds2D();
		Rectangle2D.union(regionBounds, dirtyRegion, dirtyRegion);
		scheduleRepaint();
	}

	@Override
	public void addDirtyView(IView view) {
		if (view instanceof IBoundedView) {
			dirtyViews.add((IBoundedView)view);
		} else {
			fullRepaint = true;
		}
		scheduleRepaint();
	}

	protected void scheduleRepaint() {
		if (repaintScheduled)
			return;
		SwingUtilities.invokeLater(this);
	}
	
	@Override
	public void run() {
		if (fullRepaint) {
			managedViewer.repaint();
		} else {
			for (IBoundedView view: dirtyViews) {
				Rectangle2D.union(view.getDisplayExtents(), dirtyRegion, dirtyRegion);
			}
			managedViewer.repaint(dirtyRegion.getBounds());
		}
		dirtyViews.clear();
		dirtyRegion = new Rectangle2D.Double();
		repaintScheduled = false;
	}
}
