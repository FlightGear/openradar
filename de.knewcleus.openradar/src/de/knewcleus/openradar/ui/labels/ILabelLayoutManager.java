package de.knewcleus.openradar.ui.labels;

import java.awt.geom.Dimension2D;

public interface ILabelLayoutManager {
	public abstract void layout(LabelElementContainer element);
	public abstract Dimension2D getMinimumSize(LabelElementContainer element);
	public abstract double getAscent(LabelElementContainer element);
}
