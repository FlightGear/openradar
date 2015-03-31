/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.util;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.location.IMapProjection;

public class TransformedShape implements Shape {
	protected final Shape originalShape;
	protected final IMapProjection deviceTransformation;

	public TransformedShape(Shape originalShape,
			IMapProjection deviceTransformation) {
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
