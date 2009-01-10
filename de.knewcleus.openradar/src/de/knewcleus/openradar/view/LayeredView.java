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

	public void pushView(IView view) {
		views.add(view);
		if (view instanceof IBoundedView) {
			viewAdapter.getUpdateManager().markRegionDirty(((IBoundedView)view).getDisplayExtents());
		} else {
			viewAdapter.getUpdateManager().markViewportDirty();
		}
	}

	public void removeView(IView view) {
		if (view instanceof IBoundedView) {
			viewAdapter.getUpdateManager().markRegionDirty(((IBoundedView)view).getDisplayExtents());
		} else {
			viewAdapter.getUpdateManager().markViewportDirty();
		}
		views.remove(view);
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		if (visible==this.visible) {
			return;
		}
		this.visible = visible;
		viewAdapter.getUpdateManager().markViewportDirty();
	}
	
	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitContainer(this);
	}
	
	@Override
	public void traverse(IViewVisitor visitor) {
		for (IView view: views) {
			view.accept(visitor);
		}
	}
	
	@Override
	public void validate() {}

	@Override
	public void paint(Graphics2D g2d) {}
}
