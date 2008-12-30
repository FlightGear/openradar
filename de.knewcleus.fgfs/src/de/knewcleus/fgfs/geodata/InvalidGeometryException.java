package de.knewcleus.fgfs.geodata;


public class InvalidGeometryException extends GeodataException {
	private static final long serialVersionUID = 2887009174425401096L;

	public InvalidGeometryException() {
	}

	public InvalidGeometryException(String message) {
		super(message);
	}

	public InvalidGeometryException(Throwable cause) {
		super(cause);
	}

	public InvalidGeometryException(String message, Throwable cause) {
		super(message, cause);
	}
}
