package de.knewcleus.openradar.ui.rpvd.tracks;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.IMapProjection;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.openradar.ui.Palette;
import de.knewcleus.openradar.ui.core.DisplayElement;
import de.knewcleus.openradar.ui.map.RadarMapPanel;
import de.knewcleus.openradar.ui.rpvd.RadarPlanViewSettings;
import de.knewcleus.openradar.vessels.Track;

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
		final IMapProjection projection=mapPanel.getProjection();
		final AffineTransform deviceXForm=mapPanel.getMapTransformation();
		final Position currentPosition=associatedTrack.getPosition();
		final double deltaS=associatedTrack.getGroundSpeed()*settings.getSpeedVectorMinutes()*Units.MIN;
		
		final GeodesicInformation geodesicInformation;
		geodesicInformation=geodesicUtils.direct(currentPosition.getX(), currentPosition.getY(), 
				associatedTrack.getTrueCourse(), deltaS);
		final Position futurePosition=geodesicInformation.getEndPos();
		
		final Point2D currentMapPosition=projection.forward(currentPosition);
		final Point2D futureMapPosition=projection.forward(futurePosition);
		deviceXForm.transform(currentMapPosition, currentMapPosition);
		deviceXForm.transform(futureMapPosition, futureMapPosition);
		
		headingLine=new Line2D.Double(currentMapPosition, futureMapPosition);
		invalidate();
	}

}
