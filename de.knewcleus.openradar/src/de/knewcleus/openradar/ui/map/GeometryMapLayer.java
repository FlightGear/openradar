package de.knewcleus.openradar.ui.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.geodata.Geometry;
import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.fgfs.util.GeometryConversionException;
import de.knewcleus.fgfs.util.GeometryToShapeConverter;
import de.knewcleus.fgfs.util.TransformedShape;

public class GeometryMapLayer implements IMapLayer {
	protected final String name;
	protected final Color color;
	protected List<Shape> shapes=new ArrayList<Shape>();
	protected boolean visible=true;
	
	public GeometryMapLayer(String name, Color color, List<? extends Geometry> geometries) throws GeometryConversionException {
		this.name=name;
		this.color=color;
		final GeometryToShapeConverter converter=new GeometryToShapeConverter();
		for (Geometry geometry: geometries) {
			shapes.add(converter.convert(geometry));
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setVisible(boolean visible) {
		this.visible=visible;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	@Override
	public void draw(Graphics2D g2d, AffineTransform mapTransform, IMapProjection projection) {
		if (!isVisible())
			return;
		final AffineTransform oldTransform=g2d.getTransform();
		g2d.transform(mapTransform);
		g2d.setColor(color);
		for (Shape shape: shapes) {
			final Shape transformedShape=new TransformedShape(shape,projection);
			g2d.fill(transformedShape);
		}
		g2d.setTransform(oldTransform);
	}
}
