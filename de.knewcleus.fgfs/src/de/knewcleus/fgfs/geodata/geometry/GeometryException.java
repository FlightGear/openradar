package de.knewcleus.fgfs.geodata.geometry;

public class GeometryException extends Exception {
	private static final long serialVersionUID = -6072151507068076325L;

	public GeometryException() {
	}

	public GeometryException(String message) {
		super(message);
	}

	public GeometryException(Throwable cause) {
		super(cause);
	}

	public GeometryException(String message, Throwable cause) {
		super(message, cause);
	}
}
