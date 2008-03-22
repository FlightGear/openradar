package de.knewcleus.radar.ui.rpvd;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.radar.aircraft.AircraftState;
import de.knewcleus.radar.aircraft.AircraftTaskState;
import de.knewcleus.radar.autolabel.Label;
import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.ui.aircraft.ActualLevelLabelElement;
import de.knewcleus.radar.ui.aircraft.Aircraft;
import de.knewcleus.radar.ui.aircraft.CallsignLabelElement;
import de.knewcleus.radar.ui.aircraft.GroundSpeedLabelElement;
import de.knewcleus.radar.ui.labels.ILabelDisplay;
import de.knewcleus.radar.ui.labels.ILabelElement;
import de.knewcleus.radar.ui.labels.LabelLine;
import de.knewcleus.radar.ui.labels.MultilineLabel;
import de.knewcleus.radar.ui.labels.StaticTextLabelElement;

public class AircraftLabel implements Label, ILabelDisplay {
	protected final AircraftSymbol associatedSymbol;
	protected double hookX,hookY;
	protected double dirX,dirY;;
	protected double centerX,centerY;
	protected final List<ILabelElement> labelLines=new ArrayList<ILabelElement>();
	protected final MultilineLabel labelLayout=new MultilineLabel(labelLines);
	
	protected final LabelLine labelLine[]=new LabelLine[5];
	protected final ILabelElement callsignElement;
	protected final ILabelElement nextSectorElement;
	protected final ILabelElement actualLevelElement;
	protected final ILabelElement exitPointElement;
	protected final ILabelElement groundSpeedElement;
	protected final ILabelElement clearedLevelElement;
	protected final ILabelElement exitLevelElement;
	protected final ILabelElement assignedHeadingElement;
	protected final ILabelElement assignedSpeedElement;
	protected final ILabelElement assignedClimbRateElement;
	
	public AircraftLabel(AircraftSymbol associatedSymbol) {
		this.associatedSymbol=associatedSymbol;
		hookX=1.0;
		hookY=1.0;
		dirX=1.0;
		dirY=1.0;
		
		for (int i=0;i<labelLine.length;i++) {
			labelLine[i]=new LabelLine(new ArrayList<ILabelElement>());
		}
		
		final Aircraft aircraft=associatedSymbol.getAircraft();
		callsignElement=new CallsignLabelElement(this,aircraft);
		nextSectorElement=new StaticTextLabelElement(this,aircraft);
		actualLevelElement=new ActualLevelLabelElement(this,aircraft);
		exitPointElement=new StaticTextLabelElement(this,aircraft);
		groundSpeedElement=new GroundSpeedLabelElement(this,aircraft);
		clearedLevelElement=new StaticTextLabelElement(this,aircraft);
		exitLevelElement=new StaticTextLabelElement(this,aircraft);
		assignedHeadingElement=new StaticTextLabelElement(this,aircraft);
		assignedSpeedElement=new StaticTextLabelElement(this,aircraft);
		assignedClimbRateElement=new StaticTextLabelElement(this,aircraft);
		
		updateLabelContents();
	}
	
	public Rectangle getDisplayBounds() {
		return getBounds2D().getBounds();
	}
	
	@Override
	public RadarPlanViewPanel getDisplayComponent() {
		return associatedSymbol.getRadarPlanViewContext().getRadarPlanViewPanel();
	}
	
	public void processMouseEvent(MouseEvent e) {
		labelLayout.processMouseEvent(e);
	}
	
	public void updateLabelContents() {
		final AircraftState aircraftState=associatedSymbol.getAircraft().getAircraftState();
		
		if (aircraftState==null) {
			prepareNoncorrelatedLabel();
		} else {
			final AircraftTaskState aircraftTaskState=aircraftState.getTaskState();
			labelLines.clear();
			switch (aircraftTaskState) {
			case OTHER:
				prepareOtherLabel();
				break;
			case NOT_CONCERNED:
				prepareNotConcernedLabel();
				break;
			case ASSUMED:
			case CONCERNED:
				prepareStandardLabel();
				break;
			}
		}
	}
	
	private void prepareNoncorrelatedLabel() {
		labelLine[1].getElements().clear();
		
		labelLine[1].getElements().add(actualLevelElement);
		labelLine[1].getElements().add(groundSpeedElement);
		
		labelLines.clear();
		labelLines.add(callsignElement);
		labelLines.add(labelLine[1]);
	}
	
	private void prepareOtherLabel() {
		labelLine[0].getElements().clear();
		
		labelLine[0].getElements().add(callsignElement);
		labelLine[0].getElements().add(nextSectorElement);
		
		labelLines.clear();
		labelLines.add(labelLine[0]);
		labelLines.add(actualLevelElement);
	}
	
	private void prepareNotConcernedLabel() {
		labelLine[0].getElements().clear();
		labelLine[1].getElements().clear();
		labelLine[2].getElements().clear();
		labelLine[3].getElements().clear();
		
		labelLine[0].getElements().add(callsignElement);
		labelLine[0].getElements().add(nextSectorElement);
		
		labelLine[1].getElements().add(actualLevelElement);
		labelLine[1].getElements().add(exitPointElement);
		labelLine[1].getElements().add(groundSpeedElement);
		
		labelLine[2].getElements().add(clearedLevelElement);
		labelLine[2].getElements().add(exitLevelElement);
		
		labelLine[3].getElements().add(assignedHeadingElement);
		labelLine[3].getElements().add(assignedSpeedElement);
		labelLine[3].getElements().add(assignedClimbRateElement);
		
		// TODO: fifth line contains optional information...
		
		labelLines.clear();
		labelLines.add(labelLine[0]);
		labelLines.add(labelLine[1]);
		labelLines.add(labelLine[2]);
		labelLines.add(labelLine[3]);
	}
	
	private void prepareStandardLabel() {
		labelLine[0].getElements().clear();
		labelLine[1].getElements().clear();
		labelLine[2].getElements().clear();
		labelLine[3].getElements().clear();
		
		labelLine[0].getElements().add(callsignElement);
		labelLine[0].getElements().add(nextSectorElement);
		
		labelLine[1].getElements().add(actualLevelElement);
		labelLine[1].getElements().add(exitPointElement);
		labelLine[1].getElements().add(groundSpeedElement);
		
		labelLine[2].getElements().add(clearedLevelElement);
		labelLine[2].getElements().add(exitLevelElement);
		
		labelLine[3].getElements().add(assignedHeadingElement);
		labelLine[3].getElements().add(assignedSpeedElement);
		labelLine[3].getElements().add(assignedClimbRateElement);
		
		labelLines.clear();
		labelLines.add(labelLine[0]);
		labelLines.add(labelLine[1]);
		labelLines.add(labelLine[2]);
		labelLines.add(labelLine[3]);
	}
	
	public void layout() {
		labelLayout.layout();
		
		Dimension size=labelLayout.getMinimumSize();
		
		Rectangle2D newBounds=new Rectangle2D.Double(-size.width/2.0,-size.height/2.0,size.width,size.height);
		labelLayout.setBounds(newBounds.getBounds());
	}
	
	public void paint(Graphics2D g2d) {
		double x,y;
		
		Rectangle2D bounds=getBounds2D();
		x=bounds.getCenterX();
		y=bounds.getCenterY();
		
		int w,h;
		w=getSize().width;
		h=getSize().height;
		
		g2d.translate(x,y);
		
		final Aircraft aircraft=associatedSymbol.getAircraft();
		// TODO: implement properly
		final AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED;
		if (aircraft.isSelected()) {
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
		double x=symbolDevicePosition.getX()+hookX+dirX*size.getWidth()/2.0;
		double y=symbolDevicePosition.getY()+hookY+dirY*size.getHeight()/2.0;
		Rectangle2D newBounds=new Rectangle2D.Double(x-size.width/2.0,y-size.height/2.0,size.width,size.height);
		
		return newBounds;
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
	
	public void setHookPosition(double dx, double dy) {
		hookX=dx;
		hookY=dy;
		
		final double len=Math.sqrt(hookX*hookX+hookY*hookY);
		
		if (len>1E-3) {
			dirX=hookX/len;
			dirY=hookY/len;
		} else {
			dirX=1.0;
			dirY=0.0;
		}
		
		if (len<AircraftSymbol.minLabelDist) {
			hookX=dirX*AircraftSymbol.minLabelDist;
			hookY=dirY*AircraftSymbol.minLabelDist;
		} else if (len>AircraftSymbol.maxLabelDist) {
			hookX=dirX*AircraftSymbol.maxLabelDist;
			hookY=dirY*AircraftSymbol.maxLabelDist;
		}
	}
	
	@Override
	public void move(double dx, double dy) {
		setHookPosition(hookX+dx, hookY+dy);
	}

	@Override
	public double getTop() {
		return getBounds2D().getMinY();
	}

	@Override
	public double getBottom() {
		return getBounds2D().getMaxY();
	}

	@Override
	public double getLeft() {
		return getBounds2D().getMinX();
	}

	@Override
	public double getRight() {
		return getBounds2D().getMaxX();
	}
	
	@Override
	public String toString() {
		return "hookX="+hookX+" hookY="+hookY;
	}
}
