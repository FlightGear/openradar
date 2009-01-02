package de.knewcleus.openradar.view.layout;

import java.awt.geom.Rectangle2D;

public interface ILayoutPartContainer {
	public void traverse(ILayoutPartVisitor visitor);
	
	public Insets2D getBorderPadding();
	public Insets2D getPadding();
	public Rectangle2D getBounds();
	
	public ILayoutManager getLayoutManager();
}
