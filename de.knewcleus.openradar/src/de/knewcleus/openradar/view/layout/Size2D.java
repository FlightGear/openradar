package de.knewcleus.openradar.view.layout;

import java.awt.geom.Dimension2D;

public class Size2D extends Dimension2D {
	protected double width, height;

	public Size2D() {
		this(0.0, 0.0);
	}
	
	public Size2D(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	public Size2D(Size2D s) {
		this(s.width, s.height);
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}
}
