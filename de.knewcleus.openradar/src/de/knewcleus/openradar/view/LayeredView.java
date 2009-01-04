package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

public class LayeredView implements IContainer {
	protected final IViewerAdapter viewAdapter;
	protected final List<IView> views=new ArrayList<IView>();
	
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
	public void revalidate() {}

	@Override
	public void paint(Graphics2D g2d) {}
}
