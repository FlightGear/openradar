package de.knewcleus.openradar.view.layout;

import java.awt.geom.Dimension2D;

public interface ILayoutManager {
	public Dimension2D getMinimumSize();
	public Dimension2D getPreferredSize();
	public Dimension2D getMaximumSize();
	
	public void invalidate();
	
	public void layout();
}
