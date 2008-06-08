package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;

public class CoordinateMapTransformation implements IMapProjection {
	protected final ICoordinateTransformation coordinateTransformation;
	protected final IMapProjection mapProjection;

	public CoordinateMapTransformation(ICoordinateTransformation coordinateTransformation, IMapProjection mapProjection) {
		this.coordinateTransformation=coordinateTransformation;
		this.mapProjection=mapProjection;
	}

	public Position backward(Point2D point) {
		return coordinateTransformation.backward(mapProjection.backward(point));
	}

	public Point2D forward(Position pos) {
		return mapProjection.forward(coordinateTransformation.forward(pos));
	}
}
