package de.knewcleus.radar.ui.plaf;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.plaf.ComponentUI;

public abstract class ListMenuUI extends ComponentUI {
	public abstract void ensureIndexIsVisible(int index);
	public abstract Rectangle getCellBounds(int index0, int index1);
	public abstract Dimension getPreferredScrollableViewportSize();
}
