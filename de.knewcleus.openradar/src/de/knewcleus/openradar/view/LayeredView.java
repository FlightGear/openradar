package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.notify.Notifier;

public class LayeredView extends Notifier implements IContainer {
	protected final IViewerAdapter viewAdapter;
	protected final List<IView> views=new ArrayList<IView>();
	
	public LayeredView(IViewerAdapter mapViewAdapter) {
		this.viewAdapter = mapViewAdapter;
	}

	public void pushView(IView view) {
		views.add(view);
	}

	public void removeView(IView view) {
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
