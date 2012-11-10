package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class LayeredView implements IContainer {
	protected final IViewerAdapter viewAdapter;
	protected final List<IView> views=new ArrayList<IView>();
	protected boolean visible = true;
	
	public LayeredView(IViewerAdapter mapViewAdapter) {
		this.viewAdapter = mapViewAdapter;
	}

	public synchronized void pushView(IView view) {
		views.add(view);
		if (view instanceof IBoundedView) {
			viewAdapter.getUpdateManager().markRegionDirty(((IBoundedView)view).getDisplayExtents());
		} else {
			viewAdapter.getUpdateManager().markViewportDirty();
		}
	}

	public synchronized void removeView(IView view) {
		if (view instanceof IBoundedView) {
			viewAdapter.getUpdateManager().markRegionDirty(((IBoundedView)view).getDisplayExtents());
		} else {
			viewAdapter.getUpdateManager().markViewportDirty();
		}
		views.remove(view);
	}
	
	@Override
	public synchronized boolean isVisible() {
		return visible;
	}
	
	public synchronized void setVisible(boolean visible) {
		if (visible==this.visible) {
			return;
		}
		this.visible = visible;
		viewAdapter.getUpdateManager().markViewportDirty();
	}
	
	@Override
	public synchronized void accept(IViewVisitor visitor) {
		visitor.visitContainer(this);
	}
	
	@Override
	public synchronized void traverse(IViewVisitor visitor) {
		for (IView view: views) {
			view.accept(visitor);
		}
	}
	
	@Override
	public synchronized void validate() {}

	@Override
	public synchronized void paint(Graphics2D g2d) {}
}
