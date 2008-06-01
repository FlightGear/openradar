package de.knewcleus.radar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.core.DisplayElement;
import de.knewcleus.radar.ui.map.RadarMapPanel;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;
import de.knewcleus.radar.vessels.Track;

public class HeadingLineSymbol extends DisplayElement {
	protected final static GeodesicUtils geodesicUtils=new GeodesicUtils(Ellipsoid.WGS84);
	protected final Track associatedTrack;
	protected Line2D headingLine=null;
	
	public HeadingLineSymbol(Track associatedTrack) {
		this.associatedTrack=associatedTrack;
	}
	
	@Override
	public boolean isHit(Point2D position) {
		/* The heading line symbol is inactive by default */
		return false;
	}

	@Override
	public Rectangle2D getBounds() {
		if (headingLine==null)
			return null;
		return headingLine.getBounds2D();
	}

	@Override
	public void paintElement(Graphics2D g) {
		if (headingLine==null)
			return;
		g.setColor(Palette.BLACK);
		g.draw(headingLine);
	}

	@Override
	public void validate() {
		invalidate();
		final RadarMapPanel mapPanel=(RadarMapPanel)getDisplayComponent();
		final RadarPlanViewSettings settings=mapPanel.getSettings();
		final ICoordinateTransformation mapTransformation=mapPanel.getMapTransformation();
		final IDeviceTransformation deviceTransformation=mapPanel.getDeviceTransformation();
		final Position currentPosition=associatedTrack.getPosition();
		final double deltaS=associatedTrack.getGroundSpeed()*settings.getSpeedVectorMinutes()*Units.MIN;
		
		final GeodesicInformation geodesicInformation;
		geodesicInformation=geodesicUtils.direct(currentPosition.getX(), currentPosition.getY(), 
				associatedTrack.getTrueCourse(), deltaS);
		final Position futurePosition=geodesicInformation.getEndPos();
		
		final Position currentMapPosition=mapTransformation.forward(currentPosition);
		final Point2D currentDevicePosition=deviceTransformation.toDevice(currentMapPosition);
		
		final Position futureMapPosition=mapTransformation.forward(futurePosition);
		final Point2D futureDevicePosition=deviceTransformation.toDevice(futureMapPosition);
		
		headingLine=new Line2D.Double(currentDevicePosition, futureDevicePosition);
		invalidate();
	}

}
