package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;

public class CoordinateDeviceTransformation implements IDeviceTransformation {
	protected final ICoordinateTransformation coordinateTransformation;
	protected final IDeviceTransformation deviceTransformation;

	public CoordinateDeviceTransformation(ICoordinateTransformation coordinateTransformation, IDeviceTransformation deviceTransformation) {
		this.coordinateTransformation=coordinateTransformation;
		this.deviceTransformation=deviceTransformation;
	}

	public Position fromDevice(Point2D point) {
		return coordinateTransformation.backward(deviceTransformation.fromDevice(point));
	}

	public Position fromDeviceRelative(Point2D dimension) {
		throw new UnsupportedOperationException();
	}

	public Point2D toDevice(Position pos) {
		return deviceTransformation.toDevice(coordinateTransformation.forward(pos));
	}

	public Point2D toDeviceRelative(Vector3D dimension) {
		throw new UnsupportedOperationException();
	}
}
