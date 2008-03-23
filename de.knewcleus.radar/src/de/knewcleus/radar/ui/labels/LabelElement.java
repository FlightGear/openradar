package de.knewcleus.radar.ui.labels;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public class LabelElement {
	protected LabelElementContainer parent=null;
	protected Dimension2D minimumSize=null;
	protected boolean enabled=true;
	protected Rectangle2D bounds=new Rectangle(0,0,1,1);
	
	public LabelElementContainer getParent() {
		return parent;
	}
	
	public Component getDisplayComponent() {
		if (parent!=null)
			return parent.getDisplayComponent();
		return null;
	}

	public Dimension2D getMinimumSize() {
		return minimumSize;
	}
	
	public void setMinimumSize(Dimension2D minimumSize) {
		this.minimumSize = minimumSize;
	}
	
	public double getAscent() {
		return 0;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public Rectangle2D getBounds() {
		return bounds;
	}
	
	public void setBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}
	
	public void setPosition(double x, double y) {
		final double w, h;
		
		w=bounds.getWidth();
		h=bounds.getHeight();
		
		bounds=new Rectangle2D.Double(x,y,w,h);
	}
	
	public void paint(Graphics2D g2d) {
		paintElement(g2d);
	}
	
	public void paintElement(Graphics2D g2d) {
	}
	
	public void processMouseEvent(MouseEvent event) {	
	}
}
