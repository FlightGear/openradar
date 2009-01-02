package de.knewcleus.openradar.view;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class ComponentCanvas implements ICanvas {
	protected final Component managedComponent;
	
	public ComponentCanvas(Component managedComponent) {
		this.managedComponent = managedComponent;
	}

	@Override
	public void flushGraphics() {
		/* Nothing to do here */
	}

	@Override
	public Graphics2D getGraphics(Rectangle2D region) {
		Graphics2D g2d = (Graphics2D)managedComponent.getGraphics();
		if (g2d==null) {
			return null;
		}
		g2d.clip(region);
		return g2d;
	}
}
