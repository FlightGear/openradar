package de.knewcleus.radar.ui.plaf.refghmi;

import java.awt.Color;
import java.awt.Graphics;

public class REFGHMIUtils {
	public static void drawEtch(Graphics g, Color highlight, Color shadow, int x, int y, int w, int h, boolean lowered) {
		g.setColor(lowered?shadow:highlight);
		g.drawLine(x, y, x+w-1, y);
		g.drawLine(x, y, x, y+h-1);
		
		g.setColor(lowered?highlight:shadow);
		g.drawLine(x+w-1, y, x+w-1, y+h-1);
		g.drawLine(x, y+h-1, x+w-1, y+h-1);
	}
}
