package de.knewcleus.fgfs.geodata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class GeometryContainer<T extends Geometry> extends Geometry implements Iterable<T> {
	protected double xMin, xMax, yMin, yMax, zMin, zMax;
	
	protected final List<T> containedGeometry=new ArrayList<T>();
	
	public List<T> getContainedGeometry() {
		return Collections.unmodifiableList(containedGeometry);
	}
	
	public void add(T geometry) {
		if (containedGeometry.isEmpty()) {
			xMin=geometry.getXMin();
			xMax=geometry.getXMax();
			yMin=geometry.getYMin();
			yMax=geometry.getYMax();
			zMin=geometry.getZMin();
			zMax=geometry.getZMax();
		} else {
			xMin=Math.min(xMin, geometry.getXMin());
			xMax=Math.max(xMax, geometry.getXMax());
			yMin=Math.min(yMin, geometry.getYMin());
			yMax=Math.max(yMax, geometry.getYMax());
			zMin=Math.min(zMin, geometry.getZMin());
			zMax=Math.max(zMax, geometry.getZMax());
		}
		containedGeometry.add(geometry);
	}
	
	@Override
	public double getXMax() {
		return xMax;
	}

	@Override
	public double getXMin() {
		return xMin;
	}

	@Override
	public double getYMax() {
		return yMax;
	}

	@Override
	public double getYMin() {
		return yMin;
	}

	@Override
	public double getZMax() {
		return zMax;
	}

	@Override
	public double getZMin() {
		return zMin;
	}
	
	@Override
	public Iterator<T> iterator() {
		return getContainedGeometry().iterator();
	}
	
	public void traverse(IGeometryVisitor visitor) throws GeometryException {
		for (T child: containedGeometry) {
			child.accept(visitor);
		}
	}
}
