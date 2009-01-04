package de.knewcleus.openradar.view.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

public interface ILayoutPart {
	/**
	 * @return the minimum size required by this layout part
	 */
	public Dimension2D getMinimumSize();
	
	/**
	 * @return the preferred size for this layout part
	 */
	public Dimension2D getPreferredSize();
	
	/**
	 * @return the {@link ILayoutPartContainer} of this layout part
	 */
	public ILayoutPartContainer getLayoutPartContainer();
	
	/**
	 * @return the baseline offset from the origin of the part.
	 * @param size	The assumed size for which to express the baseline offset.
	 */
	public double getBaselineOffset(Dimension2D size);
	
	/**
	 * @return <code>true</code>, if and only if this layout part is visible.
	 */
	public boolean isVisible();
	
	/**
	 * Set the bounds of this layout part.
	 */
	public void setBounds(Rectangle2D bounds);
}
