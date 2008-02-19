package de.knewcleus.fgfs.location;

public class ReverseCoordinateTransformation implements ICoordinateTransformation {
	protected final ICoordinateTransformation original;

	public ReverseCoordinateTransformation(ICoordinateTransformation original) {
		this.original=original;
	}

	public Position backward(Position pos) {
		return original.forward(pos);
	}

	public Position forward(Position pos) {
		return original.backward(pos);
	}

}
