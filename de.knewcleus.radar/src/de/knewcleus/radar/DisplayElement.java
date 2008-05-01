package de.knewcleus.radar;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

public abstract class DisplayElement {
	protected DisplayElementContainer parent=null;
	protected JComponent displayComponent=null;

	public DisplayElementContainer getParent() {
		return parent;
	}

	public abstract void paint(Graphics2D g);

	public abstract void validate();

	public abstract Rectangle2D getBounds();
	
	public abstract boolean isHit(Point2D position);

	public JComponent getDisplayComponent() {
		if (displayComponent!=null) {
			return displayComponent;
		}
		if (parent!=null) {
			return parent.getDisplayComponent();
		}
		return null;
	}
	
	public void setDisplayComponent(JComponent displayComponent) {
		this.displayComponent = displayComponent;
		validate();
	}

	public void invalidate() {
		final Rectangle2D bounds=getBounds();
		if (bounds==null)
			return;
		final JComponent displayComponent=getDisplayComponent();
		if (displayComponent==null)
			return;
		displayComponent.repaint(bounds.getBounds());
	}
}
