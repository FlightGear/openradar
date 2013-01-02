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

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.fgfs.location.Position;

public class TransformingPathIterator implements PathIterator {
	protected final AffineTransform affineTransform;
	protected final PathIterator originalIterator;
	protected final IMapProjection deviceTransformation;

	public TransformingPathIterator(AffineTransform affineTransform, PathIterator originalIterator, IMapProjection deviceTransformation) {
		this.affineTransform=affineTransform;
		this.originalIterator=originalIterator;
		this.deviceTransformation=deviceTransformation;
	}

	public int currentSegment(double[] coords) {
		int type=originalIterator.currentSegment(coords);
		
		switch (type) {
		case SEG_CUBICTO:
			transformPoints(coords,0,3);
			break;
		case SEG_QUADTO:
			transformPoints(coords,0,2);
			break;
		case SEG_MOVETO:
		case SEG_LINETO:
			transformPoints(coords,0,1);
			break;
		}
		return type;
	}

	public int currentSegment(float[] coords) {
		int type=originalIterator.currentSegment(coords);
		
		switch (type) {
		case SEG_CUBICTO:
			transformPoints(coords,0,3);
			break;
		case SEG_QUADTO:
			transformPoints(coords,0,2);
			break;
		case SEG_MOVETO:
		case SEG_LINETO:
			transformPoints(coords,0,1);
			break;
		}
		return type;
	}
	
	protected void transformPoints(double[] coords, int offset, int numPts) {
		for (int i=0;i<numPts;i++) {
			int off=2*(offset+i);
			Position position=new Position(coords[off],coords[off+1],0.0);
			Point2D point=deviceTransformation.forward(position);
			coords[off]=point.getX();
			coords[off+1]=point.getY();
		}
		if (affineTransform!=null)
			affineTransform.transform(coords, offset, coords, offset, numPts);
	}
	
	protected void transformPoints(float[] coords, int offset, int numPts) {
		for (int i=0;i<numPts;i++) {
			int off=2*(offset+i);
			Position position=new Position(coords[off],coords[off+1],0.0);
			Point2D point=deviceTransformation.forward(position);
			coords[off]=(float)point.getX();
			coords[off+1]=(float)point.getY();
		}
		if (affineTransform!=null)
			affineTransform.transform(coords, offset, coords, offset, numPts);
	}

	public int getWindingRule() {
		return originalIterator.getWindingRule();
	}

	public boolean isDone() {
		return originalIterator.isDone();
	}

	public void next() {
		originalIterator.next();
	}
}
