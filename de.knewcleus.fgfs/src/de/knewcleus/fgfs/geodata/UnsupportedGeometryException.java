package de.knewcleus.fgfs.geodata;

import de.knewcleus.fgfs.geodata.geometry.GeometryException;

public class UnsupportedGeometryException extends GeometryException {
	private static final long serialVersionUID = 6910694957385366958L;

	public UnsupportedGeometryException() {
	}

	public UnsupportedGeometryException(String message) {
		super(message);
	}

	public UnsupportedGeometryException(Throwable cause) {
		super(cause);
	}

	public UnsupportedGeometryException(String message, Throwable cause) {
		super(message, cause);
	}

}
