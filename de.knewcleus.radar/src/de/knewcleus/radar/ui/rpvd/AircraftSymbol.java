package de.knewcleus.radar.ui.rpvd;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.autolabel.PotentialGradient;

public class AircraftSymbol implements LabeledObject {
	protected static final float aircraftSymbolSize=6.0f;
	protected static final int maximumTrailLength=6;
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);

	protected static final double EPSILON=1E-10;
	
	protected static final double potAngleMax=.5E1;
	protected static final double angle0=0.75*Math.PI;
	protected static final double angleMax=0.3*Math.PI;

	protected static final double potDistanceMax=1E2;
	protected static final double minLabelDist=10;
	protected static final double maxLabelDist=100;
	protected static final double meanLabelDist=(minLabelDist+maxLabelDist)/2.0;
	protected static final double labelDistRange=(maxLabelDist-minLabelDist);

	protected final RadarPlanViewContext radarPlanViewContext;
	protected final IAircraft aircraft;
	protected final AircraftLabel label;
	
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>(maximumTrailLength);
	protected Point2D currentDevicePosition;
	protected Point2D currentDeviceHeadPosition;
	protected final String labelLine[]=new String[5];
	protected double currentLabelWidth;
	protected double currentLabelHeight;
	protected boolean isSelected=false;
	protected boolean isLocked=false;
	protected Vector3D lastVelocityVector=new Vector3D();
	protected AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	public AircraftSymbol(RadarPlanViewContext radarPlanViewContext, IAircraft aircraft) {
		this.radarPlanViewContext=radarPlanViewContext;
		this.aircraft=aircraft;
		this.label=new AircraftLabel(this);
	}
	
	public IAircraft getAircraft() {
		return aircraft;
	}
	
	public Point2D getCurrentDevicePosition() {
		return currentDevicePosition;
	}
	
	public double getCurrentLabelWidth() {
		return currentLabelWidth;
	}
	
	public double getCurrentLabelHeight() {
		return currentLabelHeight;
	}
	
	public void update(double dt) {
		Position currentPosition=aircraft.getPosition();
		if (positionBuffer.size()>0) {
			Position lastPosition=positionBuffer.getLast();
			Vector3D newVelocityVector=aircraft.getPosition().subtract(lastPosition).scale(1.0/dt);
			lastVelocityVector=newVelocityVector;
		}
		positionBuffer.addLast(new Position(currentPosition));
		/* We always keep at least the last position, so the limit is historyLength+1 */
		if (positionBuffer.size()>maximumTrailLength) {
			positionBuffer.removeFirst();
		}
	}
	
	public void prepareForDrawing(Graphics2D g2d) {
		Position currentPosition=positionBuffer.getLast();
		ICoordinateTransformation mapTransformation=radarPlanViewContext.getRadarPlanViewSettings().getMapTransformation();
		IDeviceTransformation deviceTransformation=radarPlanViewContext.getDeviceTransformation();
		Position currentGeodPosition=geodToCartTransformation.backward(currentPosition);
		Position currentMapPosition=mapTransformation.forward(currentGeodPosition);
		currentDevicePosition=deviceTransformation.toDevice(currentMapPosition);

		if (lastVelocityVector!=null) {
			double dt=radarPlanViewContext.getRadarPlanViewSettings().getSpeedVectorMinutes()*Units.MIN;
			Vector3D distanceMade=lastVelocityVector.scale(dt);
			Position vectorHead=currentPosition.add(distanceMade);
			
			Position realHead=geodToCartTransformation.backward(vectorHead);
			Position mapHead=radarPlanViewContext.getRadarPlanViewSettings().getMapTransformation().forward(realHead);
			currentDeviceHeadPosition=radarPlanViewContext.getDeviceTransformation().toDevice(mapHead);
		} else {
			currentDeviceHeadPosition=currentDevicePosition;
		}

		labelLine[0]=String.format("%03d",(int)Math.ceil(currentGeodPosition.getZ()/Units.FT/100.0));
		labelLine[1]=aircraft.getOperator()+aircraft.getCallsign();
		labelLine[2]=String.format("%03d",(int)Math.round(lastVelocityVector.getLength()/Units.KNOTS/10.0));
		labelLine[3]=null;
		labelLine[4]=null;

		FontMetrics fontMetrics=g2d.getFontMetrics();
		currentLabelWidth=0.0;
		currentLabelHeight=0.0;
		for (int i=0;i<labelLine.length && labelLine[i]!=null;i++) {
			currentLabelHeight+=fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent()+fontMetrics.getLeading();
			currentLabelWidth=Math.max(currentLabelWidth, fontMetrics.stringWidth(labelLine[i]));
		}
		
		label.move(0,0);
	}
	
	public void drawSymbol(Graphics2D g2d) {
		g2d.setColor(aircraftTaskState.getSymbolColor());
		Ellipse2D symbol=new Ellipse2D.Double(
				currentDevicePosition.getX()-aircraftSymbolSize/2.0,currentDevicePosition.getY()-aircraftSymbolSize/2.0,
				aircraftSymbolSize,aircraftSymbolSize);
		g2d.fill(symbol);
	}
	
	public void drawLabel(Graphics2D g2d) {
		double x=label.getLeft();
		double y=label.getTop();
		
		double devX=currentDevicePosition.getX();
		double devY=currentDevicePosition.getY();
		
		double leStartX=devX;
		double leStartY=devY;
		double leEndX=leStartX+label.getHookX();
		double leEndY=leStartY+label.getHookY();
		g2d.setColor(Palette.WHITE);
		Line2D leadingLine=new Line2D.Double(leStartX,leStartY,leEndX,leEndY);
		g2d.draw(leadingLine);
		
		if (isSelected) {
			g2d.setColor(aircraftTaskState.getSelectedBackgroundColor());
			Rectangle2D labelBackground=new Rectangle2D.Double(x,y,currentLabelWidth,currentLabelHeight);
			g2d.fill(labelBackground);
			g2d.setColor(aircraftTaskState.getSelectedTextColor());
		} else {
			g2d.setColor(aircraftTaskState.getNormalTextColor());
		}
		FontMetrics fontMetrics=g2d.getFontMetrics();
		for (int i=0;i<labelLine.length && labelLine[i]!=null;i++) {
			y+=fontMetrics.getMaxAscent();
			g2d.drawString(labelLine[i], (float)x, (float)y);
			y+=fontMetrics.getLeading()+fontMetrics.getMaxDescent();
		}
	}
	
	public void drawHeadingVector(Graphics2D g2d) {
		g2d.setColor(aircraftTaskState.getSymbolColor());
		Line2D headingVector=new Line2D.Double(currentDevicePosition,currentDeviceHeadPosition);
		g2d.draw(headingVector);
	}
	
	public void drawTrail(Graphics2D g2d) {
		Position realPos;
		Position mapPos;
		Point2D devicePos;

		/* Draw the trail with previous positions */
		int dotCount=Math.min(positionBuffer.size()-1,radarPlanViewContext.getRadarPlanViewSettings().getTrackHistoryLength());
		if (dotCount<1)
			return;
		Color symbolColor=aircraftTaskState.getSymbolColor();
		float alphaIncrease=1.0f/(dotCount+1);
		float alpha=1.0f;
		
		Iterator<Position> positionIterator=positionBuffer.descendingIterator();
		positionIterator.next(); // skip current position
		for (int i=0;i<dotCount;i++) {
			assert(positionIterator.hasNext());
			Position position=positionIterator.next();
			alpha-=alphaIncrease;
			realPos=geodToCartTransformation.backward(position);
			mapPos=radarPlanViewContext.getRadarPlanViewSettings().getMapTransformation().forward(realPos);
			devicePos=radarPlanViewContext.getDeviceTransformation().toDevice(mapPos);
			g2d.setColor(new Color(symbolColor.getRed(),symbolColor.getGreen(),symbolColor.getBlue(),(int)(symbolColor.getAlpha()*alpha)));
			Ellipse2D symbol=new Ellipse2D.Double(devicePos.getX()-aircraftSymbolSize/2.0,devicePos.getY()-aircraftSymbolSize/2.0,
					aircraftSymbolSize,aircraftSymbolSize);
			g2d.fill(symbol);
		}
	}

	@Override
	public double getTop() {
		return currentDevicePosition.getY()-aircraftSymbolSize/2.0;
	}
	
	@Override
	public double getBottom() {
		return currentDevicePosition.getY()+aircraftSymbolSize/2.0;
	}
	
	@Override
	public double getLeft() {
		return currentDevicePosition.getX()-aircraftSymbolSize/2.0;
	}
	
	@Override
	public double getRight() {
		return currentDevicePosition.getX()+aircraftSymbolSize/2.0;
	}
	
	@Override
	public double getChargeDensity() {
		return 1E4;
	}
	
	public boolean containsPosition(double x, double y) {
		if (currentDevicePosition==null) {
			return false;
		}
		if (label.containsPosition(x, y)) {
			return true;
		}
		
		double dx,dy;
		
		dx=x-currentDevicePosition.getX();
		dy=y-currentDevicePosition.getY();
		
		if ((dx*dx+dy*dy)<=aircraftSymbolSize*aircraftSymbolSize) {
			return true;
		}
		
		return false;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	@Override
	public boolean isLocked() {
		return isSelected || isLocked;
	}
	
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	
	@Override
	public AircraftLabel getLabel() {
		return label;
	}
	
	@Override
	public PotentialGradient getPotentialGradient(double dx, double dy) {
		final double vx,vy;
		
		vx=currentDeviceHeadPosition.getX()-currentDevicePosition.getX();
		vy=currentDeviceHeadPosition.getY()-currentDevicePosition.getY();
		
		final double dvx,dvy; // position relative to the heading vector
		
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
		
		if (abs(dvx)<EPSILON) {
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
		
		distanceForce=-8.0*potDistanceMax*(r-meanLabelDist)/(labelDistRange*labelDistRange);
		
		final double distanceForceX,distanceForceY;
		
		distanceForceX=dx/r*distanceForce;
		distanceForceY=dy/r*distanceForce;
		
		return new PotentialGradient(angleForceX+distanceForceX,angleForceY+distanceForceY);
	}
}
