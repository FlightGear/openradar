package de.knewcleus.radar.ui.rpvd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.sector.Polygon;
import de.knewcleus.radar.sector.PolygonContour;

public class PolygonMapLayer implements IMapLayer {
	protected final Color color;
	protected final List<Polygon> polygons;
	protected IDeviceTransformation lastTransform;
	protected List<Area> lastAreas;
	
	public PolygonMapLayer(Color color, List<Polygon> polygons) {
		this.color=color;
		this.polygons=polygons;
	}
	
	public void draw(Graphics2D g2d, IDeviceTransformation transform) {
		g2d.setColor(color);
		if (transform.equals(lastTransform)) {
			for (Area area: lastAreas) {
				g2d.fill(area);
			}
		} else {
			lastAreas=new ArrayList<Area>();
			for (Polygon polygon: polygons) {
				Area polygonArea=polygonToArea(polygon, transform);
				lastAreas.add(polygonArea);
				g2d.fill(polygonArea);
			}
			lastTransform=transform;
		}
	}
	
	protected Area polygonToArea(Polygon polygon, IDeviceTransformation transform) {
		Iterator<PolygonContour> contourIterator=polygon.iterator();
		
		if (!contourIterator.hasNext())
			return new Area();
		
		PolygonContour contour=contourIterator.next();
		
		Shape mainShape=contourToShape(contour, transform);
		
		Area area=new Area(mainShape);
		
		while (contourIterator.hasNext()) {
			contour=contourIterator.next();
			
			Shape holeShape=contourToShape(contour, transform);
			Area hole=new Area(holeShape);
			area.subtract(hole);
		}
		
		return area;
	}
	
	protected Shape contourToShape(PolygonContour contour, IDeviceTransformation transform) {
		Path2D path=new Path2D.Double();
		
		Iterator<Position> posIterator=contour.iterator();
		
		if (!posIterator.hasNext())
			return path;
		
		Point2D pos=transform.toDevice(posIterator.next());
		
		path.moveTo(pos.getX(), pos.getY());
		
		while (posIterator.hasNext()) {
			pos=transform.toDevice(posIterator.next());
			path.lineTo(pos.getX(), pos.getY());
		}
		
		path.closePath();
		
		return path;
	}

}
