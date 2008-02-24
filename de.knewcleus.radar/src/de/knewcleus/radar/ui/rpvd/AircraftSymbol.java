package de.knewcleus.radar.ui.rpvd;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.ICoordinateTransformation;
import de.knewcleus.fgfs.location.IDeviceTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.radar.aircraft.IAircraft;
import de.knewcleus.radar.autolabel.LabelCandidate;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.autolabel.ProtectedSymbol;

public class AircraftSymbol implements LabeledObject, ProtectedSymbol {
	// priorisation of label positions: top-right, bottom-right, bottom-left, top-left, right, left
	protected static final double[] labelDeltaX=new double[] {1.0,1.0,-1.0,-1.0,1.0,-1.0};
	protected static final double[] labelDeltaY=new double[] {-1.0,1.0,1.0,-1.0,0.0,0.0};
	protected static final float aircraftSymbolSize=6.0f;
	protected static final float leadingLineLength=10.0f;
	protected static final int maximumTrailLength=6;
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);

	protected final RadarPlanViewContext radarPlanViewContext;
	protected final IAircraft aircraft;
	
	protected final Set<AircraftLabelCandidate> labelCandidates=new HashSet<AircraftLabelCandidate>();
	
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>(maximumTrailLength);
	protected Point2D currentDevicePosition;
	protected final String labelLine[]=new String[5];
	protected double currentLabelWidth;
	protected double currentLabelHeight;
	protected boolean isSelected=false;
	protected Vector3D lastVelocityVector=new Vector3D();
	protected AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	public AircraftSymbol(RadarPlanViewContext radarPlanViewContext, IAircraft aircraft) {
		this.radarPlanViewContext=radarPlanViewContext;
		this.aircraft=aircraft;
		
		assert(labelDeltaX.length==labelDeltaY.length);
		
		for (int i=0;i<labelDeltaX.length;i++) {
			AircraftLabelCandidate labelCandidate=new AircraftLabelCandidate(this,labelDeltaX[i],labelDeltaY[i],i);
			labelCandidates.add(labelCandidate);
		}
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
			Vector3D newVelocityVector=aircraft.getPosition().subtract(lastPosition);
			newVelocityVector.scale(1.0/dt);
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
		
		labelLine[0]=String.format("%03d",(int)Math.ceil(currentGeodPosition.getZ()/Units.FT/100.0));
		labelLine[1]=aircraft.getOperator()+aircraft.getCallsign();
		labelLine[2]=String.format("%03d",(int)Math.round(aircraft.getVelocity()/Units.KNOTS/10.0));
		labelLine[3]=null;
		labelLine[4]=null;

		FontMetrics fontMetrics=g2d.getFontMetrics();
		currentLabelWidth=0.0;
		currentLabelHeight=0.0;
		for (int i=0;i<labelLine.length && labelLine[i]!=null;i++) {
			currentLabelHeight+=fontMetrics.getMaxAscent()+fontMetrics.getMaxDescent()+fontMetrics.getLeading();
			currentLabelWidth=Math.max(currentLabelWidth, fontMetrics.stringWidth(labelLine[i]));
		}
	}
	
	public void drawSymbol(Graphics2D g2d) {
		g2d.setColor(aircraftTaskState.getSymbolColor());
		Ellipse2D symbol=new Ellipse2D.Double(
				currentDevicePosition.getX()-aircraftSymbolSize/2.0,currentDevicePosition.getY()-aircraftSymbolSize/2.0,
				aircraftSymbolSize,aircraftSymbolSize);
		g2d.fill(symbol);
	}
	
	public void drawLabel(Graphics2D g2d, LabelCandidate labelCandidate) {
		AircraftLabelCandidate candidate=(AircraftLabelCandidate)labelCandidate;
		double x=labelCandidate.getLeft();
		double y=labelCandidate.getTop();

		double devX=currentDevicePosition.getX();
		double devY=currentDevicePosition.getY();
		
		double leStartX=devX+candidate.getDx()*aircraftSymbolSize/2.0;
		double leStartY=devY+candidate.getDy()*aircraftSymbolSize/2.0;
		double leEndX=leStartX+candidate.getDx()*leadingLineLength;
		double leEndY=leStartY+candidate.getDy()*leadingLineLength;
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
		if (lastVelocityVector==null)
			return;
		Position lastPosition=positionBuffer.getLast();
		Vector3D distanceMade=new Vector3D(lastVelocityVector);
		distanceMade.scale(radarPlanViewContext.getRadarPlanViewSettings().getSpeedVectorMinutes()*Units.MIN);
		Position vectorHead=new Position(lastPosition);
		vectorHead.translate(distanceMade);
		
		Position realPos=geodToCartTransformation.backward(lastPosition);
		Position realHead=geodToCartTransformation.backward(vectorHead);
		
		Position mapPos=radarPlanViewContext.getRadarPlanViewSettings().getMapTransformation().forward(realPos);
		Position mapHead=radarPlanViewContext.getRadarPlanViewSettings().getMapTransformation().forward(realHead);
		
		Point2D devicePos=radarPlanViewContext.getDeviceTransformation().toDevice(mapPos);
		Point2D deviceHead=radarPlanViewContext.getDeviceTransformation().toDevice(mapHead);
		
		g2d.setColor(aircraftTaskState.getSymbolColor());
		Line2D headingVector=new Line2D.Double(devicePos,deviceHead);
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
		float alpha=0.0f;
		
		Iterator<Position> positionIterator=positionBuffer.iterator();
		positionIterator.next(); // skip current position
		for (int i=0;i<dotCount;i++) {
			assert(positionIterator.hasNext());
			Position position=positionIterator.next();
			alpha+=alphaIncrease;
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
	public double getOverlapPenalty() {
		return 1.0E3;
	}
	
	@Override
	public Set<AircraftLabelCandidate> getLabelCandidates() {
		return Collections.unmodifiableSet(labelCandidates);
	}
}
