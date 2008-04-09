package de.knewcleus.radar.ui.rpvd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Deque;
import java.util.Iterator;
import java.util.logging.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.radar.targets.Track;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.vehicles.IVehicle;

public abstract class AbstractVehicleSymbol implements IVehicleSymbol {
	protected static final Logger logger = Logger.getLogger(AbstractVehicleSymbol.class.getName());

	private static final GeodesicUtils geodesicUtils = new GeodesicUtils(Ellipsoid.WGS84);
	protected final RadarPlanViewContext radarPlanViewContext;
	protected final IVehicle aircraft;
	protected IVehicleLabel label=null;
	protected Point2D currentDevicePosition;
	protected Point2D currentDeviceHeadPosition;
	protected boolean isLocked = false;

	public AbstractVehicleSymbol(RadarPlanViewContext radarPlanViewContext, IVehicle aircraft) {
		this.radarPlanViewContext=radarPlanViewContext;
		this.aircraft=aircraft;
	}

	public abstract double getPriority();

	public abstract boolean containsPoint(double x, double y);

	protected abstract Color getSymbolColor();

	@Override
	public IVehicle getVehicle() {
		return aircraft;
	}

	public RadarPlanViewContext getRadarPlanViewContext() {
		return radarPlanViewContext;
	}

	public Point2D getCurrentDevicePosition() {
		return currentDevicePosition;
	}

	public boolean canSelect() {
		return getVehicle().canSelect();
	}

	public void updatePosition() {
		logger.fine("Updating symbol position for aircraft "+aircraft);
		final Track target=getVehicle().getTrack();
		final ICoordinateTransformation mapTransformation=radarPlanViewContext.getRadarPlanViewSettings().getMapTransformation();
		final IDeviceTransformation deviceTransformation=radarPlanViewContext.getDeviceTransformation();
	
		/* Calculate current device position of associatedTarget symbol */
		final Position currentGeodPosition=target.getPosition();
		final Position currentMapPosition=mapTransformation.forward(currentGeodPosition);
		currentDevicePosition=deviceTransformation.toDevice(currentMapPosition);
		
		/* Calculate current device position of leading line head position */
		double trueCourse=target.getTrueCourse();
		double gs=target.getGroundSpeed();
		double dt=radarPlanViewContext.getRadarPlanViewSettings().getSpeedVectorMinutes()*Units.MIN;
		double ds=gs*dt;
		GeodesicInformation geodesicInformation=geodesicUtils.direct(currentGeodPosition.getX(), currentGeodPosition.getY(), trueCourse, ds);
		Position currentLeadingLineHeadMapPosition=mapTransformation.forward(geodesicInformation.getEndPos()); 
		currentDeviceHeadPosition=deviceTransformation.toDevice(currentLeadingLineHeadMapPosition);
	}

	public void paintLabel(Graphics2D g2d) {
		final Point2D hookPosition=label.getHookPosition();
		g2d.setColor(Palette.WHITE);
		Line2D leadingLine=new Line2D.Double(currentDevicePosition, hookPosition);
		g2d.draw(leadingLine);
		
		label.paint(g2d);
	}

	public void paintHeadingVector(Graphics2D g2d) {
		g2d.setColor(getSymbolColor());
		Line2D headingVector=new Line2D.Double(currentDevicePosition,currentDeviceHeadPosition);
		g2d.draw(headingVector);
	}

	public boolean isLocked() {
		return aircraft.isSelected() || isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	@Override
	public IVehicleLabel getLabel() {
		return label;
	}

	protected abstract void paintPositionSymbol(Graphics2D g2d, Point2D position);

	@Override
	public void paintSymbol(Graphics2D g2d) {
		g2d.setColor(getSymbolColor());
		paintPositionSymbol(g2d, currentDevicePosition);
	}

	@Override
	public void paintTrail(Graphics2D g2d) {
		Position mapPos;
		Point2D devicePos;
	
		final Track target=getVehicle().getTrack();
		final Deque<Position> positionBuffer=target.getPositionBuffer();
		/* Draw the trail with previous positions */
		int dotCount=Math.min(positionBuffer.size()-1,radarPlanViewContext.getRadarPlanViewSettings().getTrackHistoryLength());
		if (dotCount<1)
			return;
		Color symbolColor=getSymbolColor();
		float alphaIncrease=1.0f/(dotCount+1);
		float alpha=1.0f;
		
		Iterator<Position> positionIterator=positionBuffer.descendingIterator();
		positionIterator.next(); // skip current position
		for (int i=0;i<dotCount;i++) {
			assert(positionIterator.hasNext());
			Position position=positionIterator.next();
			alpha-=alphaIncrease;
			mapPos=radarPlanViewContext.getRadarPlanViewSettings().getMapTransformation().forward(position);
			devicePos=radarPlanViewContext.getDeviceTransformation().toDevice(mapPos);
			g2d.setColor(new Color(symbolColor.getRed(),symbolColor.getGreen(),symbolColor.getBlue(),(int)(symbolColor.getAlpha()*alpha)));
			paintPositionSymbol(g2d, devicePos);
		}
	}
}