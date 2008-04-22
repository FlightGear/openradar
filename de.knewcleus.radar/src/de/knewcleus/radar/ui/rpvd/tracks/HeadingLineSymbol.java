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
import de.knewcleus.radar.ui.map.RadarMapPanel;
import de.knewcleus.radar.ui.rpvd.RadarPlanViewSettings;
import de.knewcleus.radar.vessels.Track;

public class HeadingLineSymbol extends ComposedTrackSymbolPart {
	protected final static GeodesicUtils geodesicUtils=new GeodesicUtils(Ellipsoid.WGS84);
	protected Line2D headingLine;
	
	public HeadingLineSymbol(ComposedTrackSymbol parent) {
		super(parent);
	}

	@Override
	public Rectangle2D getBounds() {
		if (headingLine==null)
			return null;
		return headingLine.getBounds2D();
	}

	@Override
	public void paint(Graphics2D g) {
		if (headingLine==null)
			return;
		g.setColor(Palette.BLACK);
		g.draw(headingLine);
	}

	@Override
	public void validate() {
		invalidate();
		final RadarMapPanel mapPanel=getParent().getDisplayComponent();
		final RadarPlanViewSettings settings=mapPanel.getSettings();
		final ICoordinateTransformation mapTransformation=mapPanel.getMapTransformation();
		final IDeviceTransformation deviceTransformation=mapPanel.getDeviceTransformation();
		final Track track=getParent().getAssociatedTrack();
		final Position currentPosition=track.getPosition();
		final double deltaS=track.getGroundSpeed()*settings.getSpeedVectorMinutes()*Units.MIN;
		
		final GeodesicInformation geodesicInformation;
		geodesicInformation=geodesicUtils.direct(currentPosition.getX(), currentPosition.getY(), 
				track.getTrueCourse(), deltaS);
		final Position futurePosition=geodesicInformation.getEndPos();
		
		final Position currentMapPosition=mapTransformation.forward(currentPosition);
		final Point2D currentDevicePosition=deviceTransformation.toDevice(currentMapPosition);
		
		final Position futureMapPosition=mapTransformation.forward(futurePosition);
		final Point2D futureDevicePosition=deviceTransformation.toDevice(futureMapPosition);
		
		headingLine=new Line2D.Double(currentDevicePosition, futureDevicePosition);
		invalidate();
	}

}
