package de.knewcleus.openradar.autolabel;

public class PotentialGradient {
	protected final double dx,dy;
	
	public PotentialGradient() {
		dx=dy=0.0;
	}
	
	public PotentialGradient(double dx, double dy) {
		this.dx=dx;
		this.dy=dy;
	}
	
	public double getDx() {
		return dx;
	}
	
	public double getDy() {
		return dy;
	}
	
	public PotentialGradient add(PotentialGradient a) {
		return new PotentialGradient(dx+a.dx,dy+a.dy);
	}
	
	public PotentialGradient scale(double s) {
		return new PotentialGradient(s*dx,s*dy);
	}
	
	public double magntitude() {
		return Math.sqrt(dx*dx+dy*dy);
	}
	
	public PotentialGradient normalise() {
		return scale(1/magntitude());
	}
	
	@Override
	public String toString() {
		return "["+dx+","+dy+"]";
	}
}
