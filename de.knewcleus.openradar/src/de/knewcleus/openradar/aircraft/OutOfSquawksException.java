package de.knewcleus.openradar.aircraft;

public class OutOfSquawksException extends Exception {
	private static final long serialVersionUID = -6135736616363464061L;

	public OutOfSquawksException() {
	}

	public OutOfSquawksException(String message) {
		super(message);
	}

	public OutOfSquawksException(Throwable cause) {
		super(cause);
	}

	public OutOfSquawksException(String message, Throwable cause) {
		super(message, cause);
	}

}
