/**
 * 
 */
package de.knewcleus.radar.ui.rpvd;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.Deque;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.radar.aircraft.IAircraft;

class AircraftDisplay {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);

	protected final IAircraft aircraft;
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>(5);
	protected int maxTrailPointCount=5;
	protected float tagX=1.0f,tagY=1.0f;
	protected boolean isSelected=false;
	protected double headingVectorTime=0.25*Units.MIN;
	protected Vector3D lastVelocityVector=new Vector3D();
	protected AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	protected static float aircraftSymbolSize=6.0f;
	protected static float aircraftTagDistance=5.0f;
	protected static float tagLineLength=aircraftTagDistance;
	
	protected static Stroke aircraftStroke=new BasicStroke(0.0f);
	
	public AircraftDisplay(IAircraft aircraft) {
		this.aircraft=aircraft;
	}
	
	public void update(double dt) {
		Position currentPosition=aircraft.getPosition();
		if (positionBuffer.size()>0) {
			Position lastPosition=positionBuffer.getLast();
			Vector3D newVelocityVector=aircraft.getPosition().subtract(lastPosition);
			newVelocityVector.scale(1.0/dt);
			lastVelocityVector=newVelocityVector;
		}
		positionBuffer.addLast(new Position(currentPosition));
		/* We always keep at least the last position, so the limit is maxTrailPointCount+1 */
		if (positionBuffer.size()>maxTrailPointCount+1) {
			positionBuffer.removeFirst();
		}
	}
	
	public void drawAircraft(Graphics2D g2d, IDeviceTransformation mapTransformation) {
		if (positionBuffer.size()==0)
			return;
		Position realPos=geodToCartTransformation.backward(positionBuffer.getLast());
		Point2D mapPos=mapTransformation.toDevice(realPos);
		
		g2d.setColor(aircraftTaskState.getSymbolColor());
		Ellipse2D symbol=new Ellipse2D.Double(mapPos.getX()-aircraftSymbolSize/2.0,mapPos.getY()-aircraftSymbolSize/2.0,aircraftSymbolSize,aircraftSymbolSize);
		g2d.fill(symbol);
	}
	
	public void drawHeadingVector(Graphics2D g2d, IDeviceTransformation mapTransformation) {
		if (lastVelocityVector==null)
			return;
		Position lastPosition=positionBuffer.getLast();
		Vector3D distanceMade=new Vector3D(lastVelocityVector);
		distanceMade.scale(headingVectorTime);
		Position vectorHead=new Position(lastPosition);
		vectorHead.translate(distanceMade);
		
		Position realPos=geodToCartTransformation.backward(lastPosition);
		Position realHead=geodToCartTransformation.backward(vectorHead);
		
		Point2D mapPos=mapTransformation.toDevice(realPos);
		Point2D mapHead=mapTransformation.toDevice(realHead);
		
		g2d.setColor(aircraftTaskState.getSymbolColor());
		Line2D headingVector=new Line2D.Double(mapPos,mapHead);
		g2d.draw(headingVector);
	}
	
	public void drawTrail(Graphics2D g2d, IDeviceTransformation mapTransformation) {
		if (positionBuffer.size()==0)
			return;
		Position realPos;
		Point2D mapPos;

		Color symbolColor=aircraftTaskState.getSymbolColor();
		/* Draw the trail with previous positions */
		int dotCount=positionBuffer.size();
		float alphaIncrease=1.0f/dotCount;
		float alpha=0.0f;
		for (Position position: positionBuffer) {
			alpha+=alphaIncrease;
			realPos=geodToCartTransformation.backward(position);
			g2d.setColor(new Color(symbolColor.getRed(),symbolColor.getGreen(),symbolColor.getBlue(),(int)(symbolColor.getAlpha()*alpha)));
			mapPos=mapTransformation.toDevice(realPos);
			Ellipse2D symbol=new Ellipse2D.Double(mapPos.getX()-aircraftSymbolSize/2.0,mapPos.getY()-aircraftSymbolSize/2.0,aircraftSymbolSize,aircraftSymbolSize);
			g2d.fill(symbol);
		}
	}
	
	public void drawTag(Graphics2D g2d, IDeviceTransformation mapTransformation) {
		Position realPos=geodToCartTransformation.backward(positionBuffer.getLast());
		Point2D mapPos=mapTransformation.toDevice(realPos);
		
		float symbolWidth=aircraftSymbolSize*2.0f/3.0f;
		float symbolHeight=aircraftSymbolSize;
		
		TagLayout tagLayout=new TagLayout(g2d.getFont(),g2d.getFontRenderContext(),tagX,tagY,Math.max(symbolWidth,symbolHeight)+tagLineLength);
		tagLayout.addLine(String.format("%03d",(int)Math.ceil(realPos.getZ()/Units.FT/100.0)));
		tagLayout.addLine(aircraft.getOperator()+aircraft.getCallsign());
		tagLayout.addLine(String.format("%03d",(int)Math.round(aircraft.getVelocity()/Units.KNOTS/10.0)));

		/* Fill the background in selected state */
		if (isSelected) {
			g2d.setPaint(aircraftTaskState.getSelectedBackgroundColor());
			g2d.fill(tagLayout.getTagBounds(mapPos));
		}
		
		/* Draw the tag and the tag line */
		g2d.setColor(isSelected?aircraftTaskState.getSelectedTextColor():aircraftTaskState.getNormalTextColor());
		g2d.setStroke(aircraftStroke);
		tagLayout.drawTag(g2d, mapPos);
		tagLayout.drawTagLine(g2d, mapPos, Math.max(symbolWidth, symbolHeight));
	}
}