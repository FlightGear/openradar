package de.knewcleus.radar.ui.rpvd;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

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
import de.knewcleus.radar.autolabel.PotentialGradient;
import de.knewcleus.radar.targets.Track;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.vehicles.IVehicle;

public abstract class AbstractVehicleSymbol implements IVehicleSymbol {

	protected static final Logger logger = Logger.getLogger(AbstractVehicleSymbol.class.getName());

	private static final GeodesicUtils geodesicUtils = new GeodesicUtils(Ellipsoid.WGS84);
	protected static final double EPSILON = 1E-10;
	protected static final double potAngleMax = 5E2;
	protected static final double angle0 = 0.75*Math.PI;
	protected static final double angleMax = 0.3*Math.PI;
	protected static final double potDistanceMax = 5E2;
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

	@Override
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

	@Override
	public PotentialGradient getPotentialGradient(Point2D p) {
		final double dx,dy;
		final double vx,vy;
		
		dx=p.getX()-currentDevicePosition.getX();
		dy=p.getX()-currentDevicePosition.getY();
		
		vx=currentDeviceHeadPosition.getX()-currentDevicePosition.getX();
		vy=currentDeviceHeadPosition.getY()-currentDevicePosition.getY();
		logger.fine("Getting potential gradient for "+aircraft+" currentDeviceHeadPosition="+currentDeviceHeadPosition+" currentDevicePosition="+currentDevicePosition);
		
		final double dvx,dvy; // position relative to the trueCourse vector
		
		final double r=Math.sqrt(dx*dx+dy*dy);
		final double v=Math.sqrt(vx*vx+vy*vy);
	
		dvx=dx*vx+dy*vy;
		dvy=dx*vy-dy*vx;
		
		/*
		 * Calculate the angle contribution to the gradient.
		 * 
		 * The angle contribution to the weight is found as follows
		 * 
		 * w=wmax*((angle-a0)/amax)^2
		 * angle=abs(atan(y/x))
		 */
		
		// d/dt atan(t)=cos^2(atan(t))
		
		// dw/dx=wmax * d/dx ((angle-a0)/amax)^2
		// dw/dy=wmax * d/dy ((angle-a0)/amax)^2
		// d/dx ((angle-a0)/amax)^2 = 2*(angle-a0)/amax^2 * d/dx angle
		// d/dy ((angle-a0)/amax)^2 = 2*(angle-a0)/amax^2 * d/dy angle
		
		// d/dx angle=d/dx abs(atan(y/x)) = (d/dx atan(y/x)) * d/da abs(a) | a=atan(y/x)
		// d/dy angle=d/dy abs(atan(y/x)) = (d/dy atan(y/x)) * d/da abs(a) | a=atan(y/x)
		
		// d/dx atan(y/x) = (d/dx y/x) * d/dt atan(t) | t=y/x = -y/x^2 * cos^2(atan(y/x))
		// d/dy atan(y/x) = (d/dy y/x) * d/dt atan(t) | t=y/x =  1/x   * cos^2(atan(y/x))
		
		// dw/dx = 2 * wmax * (angle-a0)/amax^2 * sig(atan(y/x)) * cos^2(atan(y/x) * (-y/x^2)
		// dw/dy = 2 * wmax * (angle-a0)/amax^2 * sig(atan(y/x)) * cos^2(atan(y/x)) * 1/x
	
		final double angleForceX,angleForceY;
		
		if (abs(dvx)<EPSILON || v<EPSILON) {
			angleForceX=angleForceY=0.0;
		} else {
			final double angle=abs(Math.atan2(dvy,dvx));
			
			final double cangle=Math.cos(angle);
			final double cangle2=cangle*cangle;
			
			final double angleForce;
			
			angleForce=2.0 * potAngleMax * (angle-angle0)/(angleMax*angleMax) * signum(angle) * cangle2;
			
			final double angleForceVX, angleForceVY;
			
			angleForceVX=-dvy * angleForce / (dvx*dvx);
			angleForceVY=       angleForce / dvx;
			
			/* Transform from velocity relative frame to global frame */
			angleForceX=(vx*angleForceVX+vy*angleForceVY)/v;
			angleForceY=(vy*angleForceVX-vx*angleForceVY)/v;
		}
		
		/*
		 * Calculate the distance contribution.
		 * 
		 * w=wmax*(4*((r-rmean)/rd)^2-1)
		 */
		
		// dw/dx = dr/dx * dw/dr
		// dw/dy = dr/dy * dw/dr
		// dw/dr = 8*wmax*(r-rmean)/rd^2
		// dr/dx = x/r
		// dr/dy = y/r
		
		final double distanceForce;
		
		distanceForce=-8.0*potDistanceMax*(r-AbstractVehicleLabel.meanLabelDist)/(AbstractVehicleLabel.labelDistRange*AbstractVehicleLabel.labelDistRange);
		
		final double distanceForceX,distanceForceY;
		
		if (r<EPSILON) {
			distanceForceX=distanceForce;
			distanceForceY=0;
		} else {
			distanceForceX=dx/r*distanceForce;
			distanceForceY=dy/r*distanceForce;
		}
		
		
		return new PotentialGradient(angleForceX+distanceForceX,angleForceY+distanceForceY);
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