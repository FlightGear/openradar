package de.knewcleus.fgfs.geodata;

public class DataFormatException extends Exception {
	private static final long serialVersionUID = 9098601392110567837L;

	public DataFormatException() {
	}

	public DataFormatException(String message) {
		super(message);
	}

	public DataFormatException(Throwable cause) {
		super(cause);
	}

	public DataFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
