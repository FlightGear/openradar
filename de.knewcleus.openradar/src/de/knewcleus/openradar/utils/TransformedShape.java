package de.knewcleus.openradar.utils;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.location.IDeviceTransformation;

public class TransformedShape implements Shape {
	protected final Shape originalShape;
	protected final IDeviceTransformation deviceTransformation;
	
	public TransformedShape(Shape originalShape, IDeviceTransformation deviceTransformation) {
		this.originalShape=originalShape;
		this.deviceTransformation=deviceTransformation;
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(),p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(),r.getY(),r.getWidth(),r.getHeight());
	}

	@Override
	public boolean contains(double x, double y) {
		return Path2D.contains(getPathIterator(new AffineTransform()), x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return Path2D.contains(getPathIterator(new AffineTransform()), x, y, w, h);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(),r.getY(),r.getWidth(),r.getHeight());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return Path2D.intersects(getPathIterator(new AffineTransform()), x, y, w, h);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new TransformingPathIterator(at,originalShape.getPathIterator(new AffineTransform()),deviceTransformation);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new TransformingPathIterator(at,originalShape.getPathIterator(new AffineTransform(), flatness),deviceTransformation);
	}

	@Override
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		// TODO Auto-generated method stub
		return null;
	}
}
