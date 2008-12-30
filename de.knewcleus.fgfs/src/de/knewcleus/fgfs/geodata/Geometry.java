package de.knewcleus.fgfs.geodata;

public abstract class Geometry {
	public abstract double getXMin();
	public abstract double getXMax();
	public abstract double getYMin();
	public abstract double getYMax();
	public abstract double getZMin();
	public abstract double getZMax();
	
	public abstract void accept(IGeometryVisitor visitor);
}
