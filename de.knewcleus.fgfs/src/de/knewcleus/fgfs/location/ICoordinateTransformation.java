package de.knewcleus.fgfs.location;


public interface ICoordinateTransformation {
	public Position forward(Position pos);
	public Position backward(Position pos);
}
