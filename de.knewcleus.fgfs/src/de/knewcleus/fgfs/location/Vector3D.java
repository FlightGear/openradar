package de.knewcleus.fgfs.location;

public class Vector3D {
	protected double x;
	protected double y;
	protected double z;
	
	public Vector3D() {
		x=y=z=0.0;
	}
	
	public Vector3D(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public Vector3D(Vector3D original) {
		this.x=original.x;
		this.y=original.y;
		this.z=original.z;
	}

	public static Vector3D crossProduct(Vector3D a, Vector3D b) {
		double x=a.y*b.z-a.z*b.y;
		double y=a.z*b.x-a.x*b.z;
		double z=a.x*b.y-a.y*b.x;
		
		return new Vector3D(x,y,z);
	}
	
	public Vector3D add(Vector3D b) {
		return new Vector3D(x+b.x,y+b.y,z+b.z);
	}
	
	public Vector3D subtract(Vector3D b) {
		return new Vector3D(x-b.x,y-b.y,z-b.z);
	}

	public void scale(double s) {
		x*=s;
		y*=s;
		z*=s;
	}
	
	public Vector3D normalise() {
		double len=getLength();
		
		if (len<1E-22) {
			return new Vector3D();
		}
		
		return new Vector3D(x/len,y/len,z/len);
	}

	public double getLength() {
		return Math.sqrt(x*x+y*y+z*z);
	}

	@Override
	public String toString() {
		return "("+x+","+y+","+z+")";
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

}