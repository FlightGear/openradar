package de.knewcleus.openradar.view;

import java.awt.Font;
import java.awt.FontMetrics;

public interface ICanvas {
	/**
	 * Get font metrics for the given font.
	 */
	public FontMetrics getFontMetrics(Font font);
}
