package de.knewcleus.fgfs.geodata;

public class Point extends Geometry {
	protected final double x, y, z, m;
	protected final boolean hasZ;
	protected final boolean hasM;
	
	public Point(double x, double y) {
		this.x=x;
		this.y=y;
		this.z=0;
		this.m=0;
		this.hasZ=false;
		this.hasM=false;
	}
	
	public Point(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.m=0;
		this.hasZ=true;
		this.hasM=false;
	}
	
	public Point(double x, double y, double z, double m, boolean hasZ, boolean hasM) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.m=m;
		this.hasZ=hasZ;
		this.hasM=hasM;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getM() {
		return m;
	}
	
	public boolean hasZ() {
		return hasZ;
	}
	
	public boolean hasM() {
		return hasM;
	}
	
	@Override
	public double getXMax() {
		return x;
	}

	@Override
	public double getXMin() {
		return x;
	}

	@Override
	public double getYMax() {
		return y;
	}

	@Override
	public double getYMin() {
		return y;
	}

	@Override
	public double getZMax() {
		return z;
	}

	@Override
	public double getZMin() {
		return z;
	}
}
