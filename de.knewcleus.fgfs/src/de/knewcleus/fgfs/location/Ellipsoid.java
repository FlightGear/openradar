package de.knewcleus.fgfs.location;

import de.knewcleus.fgfs.Units;

public class Ellipsoid {
	protected final double a,b;
	protected final double f;
	protected final double esquared;
	
	public static final Ellipsoid WGS84=new Ellipsoid(6378137*Units.M,6356752.315*Units.M);
	
	public Ellipsoid(double a, double b) {
		this.a=a;
		this.b=b;
		
		f=1.0-b/a;
		esquared=1.0-b*b/a/a;
	}
	
	public double getA() {
		return a;
	}
	
	public double getB() {
		return b;
	}
	
	public double getF() {
		return f;
	}
	
	public double getEsquared() {
		return esquared;
	}
}
