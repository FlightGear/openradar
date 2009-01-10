package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractUpdateManager implements IUpdateManager {
	protected IView rootView = null;
	protected final Set<IView> invalidViews = new HashSet<IView>();
	protected boolean validating = false;
	
	@Override
	public IView getRootView() {
		return rootView;
	}
	
	@Override
	public void setRootView(IView rootView) {
		this.rootView = rootView;
	}

	@Override
	public synchronized void markViewInvalid(IView view) {
		invalidViews.add(view);
		scheduleRevalidation();
	}
	
	/**
	 * Schedule a call to {@link #validate()}.
	 */
	protected abstract void scheduleRevalidation();
	
	/**
	 * Ensure that all views are valid.
	 */
	public synchronized void validate() {
		if (validating) {
			return;
		}
		try {
			validating = true;
			for (IView view: invalidViews) {
				view.validate();
			}
		} finally {
			validating = false;
		}
	}
	
	/**
	 * Repaint the view on the given graphics context.
	 */
	public void paint(Graphics2D g2d) {
		if (rootView==null) {
			return;
		}
		final Rectangle clipBounds = g2d.getClipBounds();
		g2d.clearRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		final ViewPaintVisitor paintVisitor = new ViewPaintVisitor(g2d);
		rootView.accept(paintVisitor);
	}
}
