package de.knewcleus.openradar.map;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.notify.Notifier;

public class LayeredView extends Notifier implements IContainer {
	protected final IMapViewAdapter mapViewAdapter;
	protected final List<IView> views=new ArrayList<IView>();
	
	public LayeredView(IMapViewAdapter mapViewAdapter) {
		this.mapViewAdapter = mapViewAdapter;
	}

	public void pushView(IView view) {
		views.add(view);
		fireStructuralChange(new StructuralNotification(this,
				view,
				StructuralNotification.ChangeType.ADD));
	}

	public void removeView(IView view) {
		views.remove(view);
		fireStructuralChange(new StructuralNotification(this,
				view,
				StructuralNotification.ChangeType.REMOVE));
	}
	
	protected void fireStructuralChange(StructuralNotification notification) {
		notify(notification);
		mapViewAdapter.acceptNotification(notification);
		
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
	public void paint(Graphics2D g2d) {}
}
