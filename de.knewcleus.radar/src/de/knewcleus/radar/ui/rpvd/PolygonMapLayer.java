package de.knewcleus.radar.ui.rpvd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.geodata.Point;
import de.knewcleus.fgfs.geodata.Polygon;
import de.knewcleus.fgfs.geodata.Ring;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.radar.utils.TransformedShape;

public class PolygonMapLayer implements IMapLayer {
	protected final Color color;
	protected final List<Polygon> polygons;
	protected Area combinedArea;
	
	public PolygonMapLayer(Color color, List<Polygon> polygons) {
		this.color=color;
		this.polygons=polygons;
		combinedArea=new Area();
		for (Polygon polygon: polygons) {
			Area polygonArea=polygonToArea(polygon);
			combinedArea.add(polygonArea);
		}
	}
	
	@Override
	public void draw(Graphics2D g2d, IDeviceTransformation transform) {
		g2d.setColor(color);
		Shape transformedArea=new TransformedShape(combinedArea,transform);
		g2d.fill(transformedArea);
	}
	
	protected Area polygonToArea(Polygon polygon) {
		Iterator<Ring> contourIterator=polygon.iterator();
		
		if (!contourIterator.hasNext())
			return new Area();
		
		Ring contour=contourIterator.next();
		
		Shape mainShape=contourToShape(contour);
		
		Area area=new Area(mainShape);
		
		while (contourIterator.hasNext()) {
			contour=contourIterator.next();
			
			Shape holeShape=contourToShape(contour);
			Area hole=new Area(holeShape);
			area.subtract(hole);
		}
		
		return area;
	}
	
	protected Shape contourToShape(Ring contour) {
		Path2D path=new Path2D.Double();
		
		Iterator<Point> posIterator=contour.iterator();
		
		if (!posIterator.hasNext())
			return path;
		
		Point point=posIterator.next();
		
		path.moveTo(point.getX(), point.getY());
		
		while (posIterator.hasNext()) {
			point=posIterator.next();
			path.lineTo(point.getX(), point.getY());
		}
		
		path.closePath();
		
		return path;
	}

}
