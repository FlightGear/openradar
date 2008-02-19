package de.knewcleus.fgfs.navaids;

public class DBParserException extends Exception {
	private static final long serialVersionUID = 7414264077607878878L;

	public DBParserException() {
	}

	public DBParserException(String message) {
		super(message);
	}

	public DBParserException(Throwable cause) {
		super(cause);
	}

	public DBParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
