package de.knewcleus.radar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class TrackDisplayHelper {
	protected static final double symbolRadius=3;
	protected static final double trailRadius=2;
	
	public static void drawTrackSymbol(Graphics2D g2d, Point2D center) {
		Ellipse2D trackSymbol=new Ellipse2D.Double(center.getX()-symbolRadius, center.getY()-symbolRadius,
				2*symbolRadius, 2*symbolRadius);
		g2d.fill(trackSymbol);
	}
	
	public static void drawTrailSymbol(Graphics2D g2d, Point2D center) {
		Ellipse2D trackSymbol=new Ellipse2D.Double(center.getX()-trailRadius, center.getY()-trailRadius,
				2*trailRadius, 2*trailRadius);
		g2d.fill(trackSymbol);
	}
	
	public static Rectangle2D getTrackSymbolBounds(Point2D center) {
		return new Rectangle2D.Double(center.getX()-symbolRadius, center.getY()-symbolRadius,
				2*symbolRadius, 2*symbolRadius);
	}
	
	public static Rectangle2D getTrailSymbolBounds(Point2D center) {
		return new Rectangle2D.Double(center.getX()-trailRadius, center.getY()-trailRadius,
				2*trailRadius, 2*trailRadius);
	}
	
	public static Point2D getRelativeTrackSymbolHookPoint(double vx, double vy) {
		final double len=Math.sqrt(vx*vx+vy*vy);
		return new Point2D.Double(vx*(symbolRadius+1)/len, vy*(symbolRadius+1)/len);
	}
}
