package de.knewcleus.fgfs.geodata;

public class NullShape extends Geometry {
	protected final static NullShape instance=new NullShape();
	
	private NullShape() {
		super();
	}
	
	public static NullShape getInstance() {
		return instance;
	}
	
	@Override
	public double getXMax() {
		return 0;
	}

	@Override
	public double getXMin() {
		return 0;
	}

	@Override
	public double getYMax() {
		return 0;
	}

	@Override
	public double getYMin() {
		return 0;
	}

	@Override
	public double getZMax() {
		return 0;
	}

	@Override
	public double getZMin() {
		return 0;
	}
	
	@Override
	public void accept(IGeometryVisitor visitor) throws GeometryException {
		visitor.visit(this);
	}
}
