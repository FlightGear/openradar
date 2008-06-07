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

	public TransformedShape(Shape originalShape,
			IDeviceTransformation deviceTransformation) {
		this.originalShape = originalShape;
		this.deviceTransformation = deviceTransformation;
	}

	@Override
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	@Override
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean contains(double x, double y) {
		return Path2D.contains(getPathIterator(null), x, y);
	}

	@Override
	public boolean contains(double x, double y, double w, double h) {
		return Path2D.contains(getPathIterator(null), x, y, w, h);
	}

	@Override
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	@Override
	public boolean intersects(double x, double y, double w, double h) {
		return Path2D.intersects(getPathIterator(null), x, y, w, h);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at) {
		return new TransformingPathIterator(at, originalShape
				.getPathIterator(null), deviceTransformation);
	}

	@Override
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new TransformingPathIterator(at, originalShape.getPathIterator(
				null, flatness), deviceTransformation);
	}

	@Override
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	@Override
	public Rectangle2D getBounds2D() {
		final double coords[]=new double[6];
		Rectangle2D bounds=new Rectangle2D.Double();
		for (PathIterator pi=getPathIterator(null);!pi.isDone();pi.next()) {
			final int type=pi.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_QUADTO:
				bounds.add(coords[4], coords[5]);
			case PathIterator.SEG_CUBICTO:
				bounds.add(coords[2], coords[3]);
			case PathIterator.SEG_MOVETO:
			case PathIterator.SEG_LINETO:
				bounds.add(coords[0], coords[1]);
			case PathIterator.SEG_CLOSE:
				break;
			}
		}
		return bounds;
	}
}
