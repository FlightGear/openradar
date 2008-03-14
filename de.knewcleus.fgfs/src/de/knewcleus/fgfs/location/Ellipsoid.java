package de.knewcleus.fgfs.location;

import de.knewcleus.fgfs.Units;

public class Ellipsoid {
	protected final double a,b;
	protected final double f;
	protected final double esquared;
	protected final double M0;
	
	public static final Ellipsoid WGS84=new Ellipsoid(6378137*Units.M,6356752.315*Units.M);
	
	public Ellipsoid(double a, double b) {
		this.a=a;
		this.b=b;
		
		f=1.0-b/a;
		esquared=f*(2.0-f);
		
		M0=Math.PI/2*(1-esquared*(1/4+esquared*(3/64 + esquared*5/256)));
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
	
	public double getM0() {
		return M0;
	}
}
