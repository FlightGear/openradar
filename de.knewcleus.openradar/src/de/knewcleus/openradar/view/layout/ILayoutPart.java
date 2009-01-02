package de.knewcleus.openradar.view.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public interface ILayoutPart {
	public Dimension2D getMinimumSize();
	public Dimension2D getPreferredSize();
	public Dimension2D getMaximumSize();
	public double getBaselineOffset(Dimension2D size);
	
	public void setBounds(Rectangle2D bounds);
	
	public void accept(ILayoutPartVisitor visitor);
}
