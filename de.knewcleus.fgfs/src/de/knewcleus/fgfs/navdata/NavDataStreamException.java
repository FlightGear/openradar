package de.knewcleus.fgfs.navdata;

public class NavDataStreamException extends Exception {
	private static final long serialVersionUID = 3356276002204657693L;

	public NavDataStreamException() {}

	public NavDataStreamException(String message) {
		super(message);
	}

	public NavDataStreamException(Throwable cause) {
		super(cause);
	}

	public NavDataStreamException(String message, Throwable cause) {
		super(message, cause);
	}
}
