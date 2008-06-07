package de.knewcleus.openradar.ui.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.geodata.Geometry;
import de.knewcleus.fgfs.location.IDeviceTransformation;
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
	public void draw(Graphics2D g2d, IDeviceTransformation transform) {
		if (!isVisible())
			return;
		g2d.setColor(color);
		for (Shape shape: shapes) {
			final Shape transformedShape=new TransformedShape(shape,transform);
			g2d.fill(transformedShape);
		}
	}
}
