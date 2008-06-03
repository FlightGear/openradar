package de.knewcleus.openradar.ui.core;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DisplayElementContainer extends DisplayElement {
	protected final List<DisplayElement> children=new ArrayList<DisplayElement>();
	protected Rectangle2D currentBounds=null;
	protected boolean isValidating=false;
	
	public synchronized boolean add(DisplayElement e) {
		boolean retval=children.add(e);
		if (!retval)
			return false;
		e.parent=this;
		updateBounds();
		return true;
	}

	public synchronized void add(int index, DisplayElement element) {
		children.add(index, element);
		element.parent=this;
		updateBounds();
	}

	public synchronized int indexOf(DisplayElement o) {
		return children.indexOf(o);
	}

	public synchronized DisplayElement remove(int index) {
		DisplayElement removedChild=children.remove(index);
		assert(removedChild!=null);
		removedChild.parent=null;
		updateBounds();
		return removedChild;
	}

	public synchronized boolean remove(DisplayElement o) {
		boolean retval=children.remove(o);
		if (!retval)
			return false;
		o.parent=null;
		updateBounds();
		return true;
	}

	@Override
	public synchronized Rectangle2D getBounds() {
		return currentBounds;
	}

	@Override
	public synchronized void paint(Graphics2D g) {
		paintElement(g);
		final Rectangle clipBounds=g.getClipBounds();
		for (DisplayElement child: children) {
			final Rectangle2D childBounds=child.getBounds();
			if (childBounds==null || !childBounds.intersects(clipBounds))
				continue;
			child.paint(g);
		}
	}

	@Override
	public void paintElement(Graphics2D g) {
		/* Containers are typically empty */
	}
	
	public synchronized void updateBounds() {
		if (isValidating) {
			/* Ignore possibly repeated calls during validation */
			return;
		}
		Rectangle2D bounds=null;
		for (DisplayElement child: children) {
			final Rectangle2D childBounds=child.getBounds();
			if (childBounds==null)
				continue;
			if (bounds==null) {
				bounds=(Rectangle2D)childBounds.clone();
			} else {
				Rectangle2D.union(bounds, childBounds, bounds);
			}
		}
		
		currentBounds=bounds;
	}

	@Override
	public synchronized void validate() {
		isValidating=true;
		for (DisplayElement child: children) {
			child.validate();
		}
		isValidating=false;
		updateBounds();
	}
	
	@Override
	public final boolean isHit(Point2D position) {
		/* Containers typically cannot be hit themselves */
		return false;
	}

	/**
	 * Provide the list of objects containing the given position, in order from bottom to top.
	 */
	public synchronized void getHitObjects(Point2D position, Collection<DisplayElement> elements) {
		/* The container is drawn below its children */
		if (isHit(position))
			elements.add(this);
		for (DisplayElement child: children) {
			final Rectangle2D bounds=child.getBounds();
			if (bounds==null || !bounds.contains(position))
				continue;
			if (child instanceof DisplayElementContainer) {
				((DisplayElementContainer)child).getHitObjects(position, elements);
			} else if (child.isHit(position)) {
				elements.add(child);
			}
		}
	}
}
