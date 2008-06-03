package de.knewcleus.openradar.ui.labels;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LabelElementContainer extends LabelElement {
	protected final List<LabelElement> children=new ArrayList<LabelElement>();
	protected ILabelLayoutManager layoutManager;
	protected Component displayComponent;
	
	public void setLayoutManager(ILabelLayoutManager layoutManager) {
		this.layoutManager = layoutManager;
	}
	
	public ILabelLayoutManager getLayoutManager() {
		return layoutManager;
	}
	
	public Component getDisplayComponent() {
		if (displayComponent!=null)
			return displayComponent;
		return super.getDisplayComponent();
	}
	
	public void setDisplayComponent(Component displayComponent) {
		this.displayComponent = displayComponent;
	}
	
	@Override
	public Dimension2D getMinimumSize() {
		if (minimumSize!=null)
			return minimumSize;
		if (layoutManager!=null)
			return layoutManager.getMinimumSize(this);
		return null;
	}
	
	@Override
	public double getAscent() {
		if (layoutManager!=null)
			return layoutManager.getAscent(this);
		return super.getAscent();
	}
	
	public void add(LabelElement element) {
		addImpl(children.size(),element);
	}
	
	public void add(int index, LabelElement element) {
		addImpl(index,element);
	}
	
	public List<LabelElement> getChildren() {
		return Collections.unmodifiableList(children);
	}
	
	public void remove(LabelElement element) {
		assert(element.parent==this);
		element.parent=null;
		children.remove(element);
	}
	
	public void removeAll() {
		for (LabelElement child: children) {
			assert(child.parent==this);
			child.parent=null;
		}
		children.clear();
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		super.paint(g2d);
		paintChildren(g2d);
	}
	
	public void paintChildren(Graphics2D g2d) {
		Rectangle clipBounds=g2d.getClipBounds();
		for (LabelElement child: children) {
			Rectangle childBounds=child.getBounds2D().getBounds();
			if (childBounds.intersects(clipBounds)) {
				child.paint(g2d);
			}
		}
	}
	
	@Override
	public void setBounds2D(Rectangle2D bounds) {
		super.setBounds2D(bounds);
		layoutManager.layout(this);
	}
	
	@Override
	public void setPosition(double x, double y) {
		final double dx,dy;
		final Rectangle2D bounds=getBounds2D();
		
		dx=x-bounds.getMinX();
		dy=y-bounds.getMinY();
		
		for (LabelElement child: children) {
			final Rectangle2D childBounds=child.getBounds2D();
			child.setPosition(childBounds.getMinX()+dx, childBounds.getMinY()+dy);
		}
		super.setPosition(x, y);
	}
	
	public void pack() {
		Dimension2D minSize=getMinimumSize();
		Rectangle2D oldBounds=getBounds2D();
		setBounds2D(new Rectangle2D.Double(oldBounds.getMinX(),oldBounds.getMinY(),minSize.getWidth(),minSize.getHeight()));
	}
	
	protected void addImpl(int index, LabelElement element) {
		if (children.contains(element))
			return;
		if (element.parent!=null) {
			element.parent.children.remove(element);
		}
		element.parent=this;
		children.add(index, element);
	}
	
	@Override
	public void processMouseEvent(MouseEvent event) {
		for (LabelElement element: children) {
			final Rectangle2D bounds2d=element.getBounds2D();
			if (!bounds2d.contains(event.getX(), event.getY()))
				continue;
			if (!element.isEnabled()) {
				event.consume();
				return;
			}
			element.processMouseEvent(event);
			if (event.isConsumed())
				return;
		}
		super.processMouseEvent(event);
	}
}
