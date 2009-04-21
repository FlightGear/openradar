package de.knewcleus.openradar.sector;

public class SectorParserException extends Exception {
	private static final long serialVersionUID = -2540341590516452929L;

	public SectorParserException() {
	}

	public SectorParserException(String message) {
		super(message);
	}

	public SectorParserException(Throwable cause) {
		super(cause);
	}

	public SectorParserException(String message, Throwable cause) {
		super(message, cause);
	}
}
