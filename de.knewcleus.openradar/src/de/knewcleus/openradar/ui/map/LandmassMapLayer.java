package de.knewcleus.openradar.ui.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.geodata.Geometry;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.util.GeometryConversionException;
import de.knewcleus.fgfs.util.GeometryToShapeConverter;
import de.knewcleus.fgfs.util.TransformedShape;

public class LandmassMapLayer implements IMapLayer {
	protected final String name;
	protected final Color landmassColor;
	protected final Color waterColor;
	protected final List<Shape> landmassShapes=new ArrayList<Shape>();
	protected final List<Shape> waterShapes=new ArrayList<Shape>();
	protected boolean visible=true;

	public LandmassMapLayer(String name,
			Color landmassColor, List<? extends Geometry> landmass,
			Color waterColor, List<? extends Geometry> water) throws GeometryConversionException
	{
		this.name=name;
		this.landmassColor=landmassColor;
		this.waterColor=waterColor;
		final GeometryToShapeConverter converter=new GeometryToShapeConverter();
		for (Geometry geometry: landmass) {
			landmassShapes.add(converter.convert(geometry));
		}
		for (Geometry geometry: water) {
			waterShapes.add(converter.convert(geometry));
		}
	}
	
	@Override
	public void draw(Graphics2D g2d, IDeviceTransformation transform) {
		final Rectangle clipBounds=g2d.getClipBounds();
		g2d.setColor(isVisible()?waterColor:landmassColor);
		g2d.fill(clipBounds);
		
		if (!isVisible())
			return;
		
		g2d.setColor(landmassColor);
		for (Shape shape: landmassShapes) {
			final Shape transformedShape=new TransformedShape(shape,transform);
			g2d.fill(transformedShape);
		}
		g2d.setColor(waterColor);
		for (Shape shape: waterShapes) {
			final Shape transformedShape=new TransformedShape(shape,transform);
			g2d.fill(transformedShape);
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible=visible;
	}

}
