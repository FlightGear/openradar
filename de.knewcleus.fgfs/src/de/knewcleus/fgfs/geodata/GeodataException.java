package de.knewcleus.fgfs.geodata;

public class GeodataException extends Exception {
	private static final long serialVersionUID = -6072151507068076325L;

	public GeodataException() {
	}

	public GeodataException(String message) {
		super(message);
	}

	public GeodataException(Throwable cause) {
		super(cause);
	}

	public GeodataException(String message, Throwable cause) {
		super(message, cause);
	}
}
