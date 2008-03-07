package de.knewcleus.radar.ui.rpvd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.sector.Polygon;
import de.knewcleus.radar.sector.PolygonContour;
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
		Iterator<PolygonContour> contourIterator=polygon.iterator();
		
		if (!contourIterator.hasNext())
			return new Area();
		
		PolygonContour contour=contourIterator.next();
		
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
	
	protected Shape contourToShape(PolygonContour contour) {
		Path2D path=new Path2D.Double();
		
		Iterator<Position> posIterator=contour.iterator();
		
		if (!posIterator.hasNext())
			return path;
		
		Position position=posIterator.next();
		
		path.moveTo(position.getX(), position.getY());
		
		while (posIterator.hasNext()) {
			position=posIterator.next();
			path.lineTo(position.getX(), position.getY());
		}
		
		path.closePath();
		
		return path;
	}

}
