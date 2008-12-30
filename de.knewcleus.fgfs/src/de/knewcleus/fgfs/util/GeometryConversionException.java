package de.knewcleus.fgfs.util;

import de.knewcleus.fgfs.geodata.geometry.Geometry;

public class GeometryConversionException extends Exception {
	private static final long serialVersionUID = 7886609488023149701L;
	protected final Geometry geometry;
	
	public GeometryConversionException(Geometry geometry) {
		this.geometry=geometry;
	}

	public GeometryConversionException(Geometry geometry, String message) {
		super(message);
		this.geometry=geometry;
	}

	public GeometryConversionException(Geometry geometry, Throwable cause) {
		super(cause);
		this.geometry=geometry;
	}

	public GeometryConversionException(Geometry geometry, String message, Throwable cause) {
		super(message, cause);
		this.geometry=geometry;
	}
	
	public GeometryConversionException() {
		this((Geometry)null);
	}

	public GeometryConversionException(String message) {
		this((Geometry)null, message);
	}

	public GeometryConversionException(Throwable cause) {
		this((Geometry)null, cause);
	}

	public GeometryConversionException(String message, Throwable cause) {
		this((Geometry)null, message, cause);
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
}
