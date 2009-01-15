package de.knewcleus.openradar.adexp;

public class ParserException extends Exception {
	private static final long serialVersionUID = -8624404505756678455L;

	public ParserException() {
	}

	public ParserException(String message) {
		super(message);
	}

	public ParserException(Throwable cause) {
		super(cause);
	}

	public ParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
