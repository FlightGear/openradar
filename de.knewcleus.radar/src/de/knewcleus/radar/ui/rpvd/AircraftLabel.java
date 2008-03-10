package de.knewcleus.radar.ui.rpvd;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.autolabel.Label;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.ui.aircraft.AircraftState;
import de.knewcleus.radar.ui.aircraft.AircraftTaskState;
import de.knewcleus.radar.ui.labels.CallsignLabelElement;
import de.knewcleus.radar.ui.labels.StaticTextLabelElement;
import de.knewcleus.radar.ui.labels.ILabelElement;
import de.knewcleus.radar.ui.labels.MultilineLabel;

public class AircraftLabel implements Label {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
	
	protected final AircraftSymbol associatedSymbol;
	protected double hookX,hookY;
	protected double centerX,centerY;
	protected final List<ILabelElement> labelLines=new ArrayList<ILabelElement>();
	protected final MultilineLabel labelLayout=new MultilineLabel(labelLines);
	
	protected final CallsignLabelElement callsignElement;
	protected final StaticTextLabelElement speedElement;
	protected final StaticTextLabelElement levelElement;
	
	public AircraftLabel(AircraftSymbol associatedSymbol) {
		this.associatedSymbol=associatedSymbol;
		hookX=1.0;
		hookY=1.0;
		
		callsignElement=new CallsignLabelElement(associatedSymbol);
		speedElement=new StaticTextLabelElement(associatedSymbol);
		levelElement=new StaticTextLabelElement(associatedSymbol);
		
		labelLines.add(callsignElement);
		labelLines.add(speedElement);
		labelLines.add(levelElement);
	}
	
	public void processMouseEvent(MouseEvent e) {
		labelLayout.processMouseEvent(e);
	}
	
	public void updateLabelContents() {
		final AircraftState aircraftState=associatedSymbol.getAircraftState();
		final Position currentPosition=aircraftState.getPositionBuffer().getLast();
		final Position currentGeodPosition=geodToCartTransformation.backward(currentPosition);
		
		speedElement.setText(String.format("%03d",(int)Math.round(aircraftState.getLastVelocityVector().getLength()/Units.KNOTS/10.0)));
		levelElement.setText(String.format("%03d",(int)Math.ceil(currentGeodPosition.getZ()/Units.FT/100.0)));
	}
	
	public void layout() {
		labelLayout.layout();
		
		Dimension size=labelLayout.getMinimumSize();
		
		Rectangle2D newBounds=new Rectangle2D.Double(-size.width/2.0,-size.height/2.0,size.width,size.height);
		labelLayout.setBounds(newBounds.getBounds());
	}
	
	public void paint(Graphics2D g2d) {
		Point2D symbolDevicePosition=associatedSymbol.getCurrentDevicePosition();
		double x,y;
		
		x=symbolDevicePosition.getX()+centerX;
		y=symbolDevicePosition.getY()+centerY;
		
		int w,h;
		w=getSize().width;
		h=getSize().height;
		
		g2d.translate(x,y);
		
		final AircraftState aircraftState=associatedSymbol.getAircraftState();
		final AircraftTaskState aircraftTaskState=aircraftState.getTaskState();
		if (aircraftState.isSelected()) {
			g2d.setColor(aircraftTaskState.getSelectedBackgroundColor());
			g2d.fillRect(-w/2,-h/2,w,h);
			g2d.setColor(aircraftTaskState.getSelectedTextColor());
		} else {
			g2d.setColor(aircraftTaskState.getNormalTextColor());
		}
		labelLayout.paint(g2d);
		g2d.translate(-x,-y);
	}
	
	public Rectangle2D getBounds2D() {
		final Point2D symbolDevicePosition=associatedSymbol.getCurrentDevicePosition();
		Dimension size=getSize();
		double x=symbolDevicePosition.getX()+centerX;
		double y=symbolDevicePosition.getY()+centerY;
		Rectangle2D newBounds=new Rectangle2D.Double(x-size.width/2.0,y-size.height/2.0,size.width,size.height);
		
		return newBounds;
	}
	
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}
	
	public Dimension getSize() {
		return labelLayout.getMinimumSize();
	}
	
	@Override
	public LabeledObject getAssociatedObject() {
		return associatedSymbol;
	}
	
	@Override
	public double getChargeDensity() {
		return 1;
	}
	
	@Override
	public double getHookX() {
		return hookX;
	}
	
	@Override
	public double getHookY() {
		return hookY;
	}
	
	public boolean containsPosition(double x, double y) {
		if (getLeft()<=x && x<=getRight() && getTop()<=y && y<=getBottom())
			return true;
		return false;
	}
	
	@Override
	public void move(double dx, double dy) {
		hookX+=dx;
		hookY+=dy;
		
		final double len=Math.sqrt(hookX*hookX+hookY*hookY);
		final double dirX,dirY;
		
		dirX=hookX/len;
		dirY=hookY/len;
		
		if (len<AircraftSymbol.minLabelDist) {
			hookX=dirX*AircraftSymbol.minLabelDist;
			hookY=dirY*AircraftSymbol.minLabelDist;
		} else if (len>AircraftSymbol.maxLabelDist) {
			hookX=dirX*AircraftSymbol.maxLabelDist;
			hookY=dirY*AircraftSymbol.maxLabelDist;
		}
		
		Dimension size=getSize();
		centerX=hookX+dirX*size.width/2.0;
		centerY=hookY+dirY*size.height/2.0;
	}

	@Override
	public double getTop() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double h=getSize().height;
		
		return y+centerY-h/2.0;
	}

	@Override
	public double getBottom() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double h=getSize().height;
		
		return y+centerY+h/2.0;
	}

	@Override
	public double getLeft() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double w=getSize().width;
		
		return x+centerX-w/2.0;
	}

	@Override
	public double getRight() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double w=getSize().width;
		
		return x+centerX+w/2.0;
	}
	
	@Override
	public String toString() {
		return "cx="+centerX+" cy="+centerY+" hookX="+hookX+" hookY="+hookY;
	}
}
