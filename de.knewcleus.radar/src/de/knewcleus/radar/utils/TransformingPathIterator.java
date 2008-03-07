package de.knewcleus.radar.utils;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;

public class TransformingPathIterator implements PathIterator {
	protected final AffineTransform affineTransform;
	protected final PathIterator originalIterator;
	protected final IDeviceTransformation deviceTransformation;

	public TransformingPathIterator(AffineTransform affineTransform, PathIterator originalIterator, IDeviceTransformation deviceTransformation) {
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
			Point2D point=deviceTransformation.toDevice(position);
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
			Point2D point=deviceTransformation.toDevice(position);
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
