package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

/**
 * A deferred viewer repaint manager collects dirty regions and schedules them for
 * repaint as soon as the system is idle.
 * 
 * @author Ralf Gerlich
 *
 */
public class DeferredUpdateManager implements IUpdateManager, Runnable {
	protected final IViewerAdapter viewerAdapter;
	
	protected Rectangle2D dirtyRegion = new Rectangle2D.Double();
	protected final Set<IView> invalidViews = new HashSet<IView>();
	protected boolean fullRepaint = false;
	protected boolean repaintScheduled = false;
	
	public DeferredUpdateManager(IViewerAdapter viewerAdapter) {
		this.viewerAdapter = viewerAdapter;
	}
	
	@Override
	public synchronized void markViewportDirty() {
		fullRepaint = true;
		scheduleUpdate();
	}
	
	@Override
	public synchronized void markRegionDirty(Rectangle2D bounds) {
		Rectangle2D.union(bounds, dirtyRegion, dirtyRegion);
		scheduleUpdate();
	}
	
	@Override
	public synchronized void markViewInvalid(IView view) {
		invalidViews.add(view);
	}

	protected void scheduleUpdate() {
		if (repaintScheduled)
			return;
		SwingUtilities.invokeLater(this);
		repaintScheduled=true;
	}
	
	protected synchronized void performRevalidation() {
		for (IView view: invalidViews) {
			view.revalidate();
		}
		invalidViews.clear();
	}
	
	protected synchronized void repairDamage() {
		if (viewerAdapter.getCanvas()!=null) {
			if (fullRepaint) {
				dirtyRegion = viewerAdapter.getViewerExtents();
			}
			final Graphics2D g2d = viewerAdapter.getCanvas().getGraphics(dirtyRegion);
			if (g2d!=null) {
				final Rectangle clipRectangle = dirtyRegion.getBounds();
				g2d.clearRect(clipRectangle.x, clipRectangle.y, clipRectangle.width, clipRectangle.height);
				final ViewPaintVisitor viewPaintVisitor = new ViewPaintVisitor(g2d);
				viewerAdapter.getRootView().accept(viewPaintVisitor);
				viewerAdapter.getCanvas().flushGraphics();
			}
		}
		dirtyRegion = new Rectangle2D.Double();
		fullRepaint = false;
		repaintScheduled = false;
	}
	
	@Override
	public void run() {
		performRevalidation();
		repairDamage();
	}
}
