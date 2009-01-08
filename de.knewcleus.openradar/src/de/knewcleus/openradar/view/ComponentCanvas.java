package de.knewcleus.openradar.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class ComponentCanvas implements ICanvas {
	protected final Component managedComponent;
	
	public ComponentCanvas(Component managedComponent) {
		this.managedComponent = managedComponent;
	}

	@Override
	public Graphics2D getGraphics(Rectangle2D region) {
		Graphics2D g2d = (Graphics2D)managedComponent.getGraphics();
		if (g2d==null) {
			return null;
		}
		g2d.clip(region);
		g2d.setBackground(managedComponent.getBackground());
		return g2d;
	}
	
	@Override
	public FontMetrics getFontMetrics(Font font) {
		return managedComponent.getFontMetrics(font);
	}

	@Override
	public void flushGraphics() {
		/* Nothing to do here */
	}
}
