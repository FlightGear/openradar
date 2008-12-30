package de.knewcleus.fgfs.geodata;

import java.util.Iterator;


public class Ring extends LineString {
	public void closeRing() {
		if (!isClosed()) {
			final Point firstPoint=getContainedGeometry().get(0);
			add(firstPoint);
		}
	}
	
	public boolean isClosed() {
		if (getContainedGeometry().size()<2)
			return false;
		final Point firstPoint=getContainedGeometry().get(0);
		final Point lastPoint=getContainedGeometry().get(getContainedGeometry().size()-1);
		
		/* Yes, we actually want to compare for equality, not only within a given epsilon range */
		return (lastPoint.getX()==firstPoint.getX() &&
				lastPoint.getY()==firstPoint.getY() &&
				lastPoint.getZ()==firstPoint.getZ());
	}
	
	public double getEnclosedArea() {
		if (getPoints().size()<(2+(isClosed()?1:0)))
			return 0;
		double area=0.0;
		final Iterator<Point> pointIterator=iterator();
		final Point firstPoint=pointIterator.next();
		Point previousPoint=firstPoint;
		
		while (pointIterator.hasNext()) {
			final Point thisPoint=pointIterator.next();
			area+=(thisPoint.getX()-previousPoint.getX())*(thisPoint.getY()+previousPoint.getY())/2.0;
			previousPoint=thisPoint;
		}
		
		if (!isClosed()) {
			area+=(firstPoint.getX()-previousPoint.getX())*(firstPoint.getY()+previousPoint.getY())/2.0;
		}
		
		return area;
	}
	
	public boolean isClockWise() {
		return getEnclosedArea()>0.0;
	}
	
	@Override
	public void accept(IGeometryVisitor visitor) throws GeometryException {
		visitor.visit(this);
	}
}
