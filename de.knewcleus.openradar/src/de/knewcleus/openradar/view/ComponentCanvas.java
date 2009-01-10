package de.knewcleus.openradar.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;

public class ComponentCanvas implements ICanvas {
	protected final Component managedComponent;
	
	public ComponentCanvas(Component managedComponent) {
		this.managedComponent = managedComponent;
	}
	
	@Override
	public FontMetrics getFontMetrics(Font font) {
		return managedComponent.getFontMetrics(font);
	}
}
