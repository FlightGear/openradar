package de.knewcleus.radar.ui.rpvd;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.aircraft.AircraftState;
import de.knewcleus.radar.aircraft.AircraftTaskState;
import de.knewcleus.radar.ui.labels.Justification;
import de.knewcleus.radar.ui.labels.LabelElement;
import de.knewcleus.radar.ui.labels.LabelElementContainer;
import de.knewcleus.radar.ui.labels.LabelLineLayoutManager;
import de.knewcleus.radar.ui.labels.MultiLineLabelLayoutManager;
import de.knewcleus.radar.ui.labels.StaticTextLabelElement;
import de.knewcleus.radar.ui.vehicles.ActualLevelLabelElement;
import de.knewcleus.radar.ui.vehicles.Aircraft;
import de.knewcleus.radar.ui.vehicles.CallsignLabelElement;
import de.knewcleus.radar.ui.vehicles.GroundSpeedLabelElement;

public class AircraftLabel implements IVehicleLabel {
	protected final AircraftSymbol associatedSymbol;
	protected Rectangle2D bounds2d;
	protected double hookX,hookY;
	protected double dirX,dirY;;
	protected final LabelElementContainer label=new LabelElementContainer();
	protected final LabelElementContainer labelLines[]=new LabelElementContainer[5];
	
	protected final LabelElement callsignElement;
	protected final LabelElement nextSectorElement;
	protected final LabelElement actualLevelElement;
	protected final LabelElement exitPointElement;
	protected final LabelElement groundSpeedElement;
	protected final LabelElement clearedLevelElement;
	protected final LabelElement exitLevelElement;
	protected final LabelElement assignedHeadingElement;
	protected final LabelElement assignedSpeedElement;
	protected final LabelElement assignedClimbRateElement;
	
	public AircraftLabel(AircraftSymbol associatedSymbol) {
		this.associatedSymbol=associatedSymbol;
		hookX=1.0;
		hookY=1.0;
		dirX=1.0;
		dirY=1.0;
		
		final Aircraft aircraft=associatedSymbol.getVehicle();
		callsignElement=new CallsignLabelElement(aircraft);
		nextSectorElement=new StaticTextLabelElement();
		actualLevelElement=new ActualLevelLabelElement(aircraft);
		exitPointElement=new StaticTextLabelElement();
		groundSpeedElement=new GroundSpeedLabelElement(aircraft);
		clearedLevelElement=new StaticTextLabelElement();
		exitLevelElement=new StaticTextLabelElement();
		assignedHeadingElement=new StaticTextLabelElement();
		assignedSpeedElement=new StaticTextLabelElement();
		assignedClimbRateElement=new StaticTextLabelElement();
		
		label.setDisplayComponent(associatedSymbol.getRadarPlanViewContext().getRadarPlanViewPanel());
		label.setLayoutManager(new MultiLineLabelLayoutManager(Justification.JUSTIFY));
		for (int i=0;i<labelLines.length;i++) {
			labelLines[i]=new LabelElementContainer();
			labelLines[i].setLayoutManager(new LabelLineLayoutManager(Justification.LEADING));
		}
		
		updateLabelContents();
	}
	
	@Override
	public void updatePosition() {
		final Rectangle2D vehicleSymbolBounds=getVehicleSymbol().getSymbolBounds();
		final Rectangle2D labelBounds=label.getBounds();
		final double symcx,symcy,symw,symh;
		final double labelx,labely,labelw,labelh;
		
		symcx=vehicleSymbolBounds.getCenterX();
		symcy=vehicleSymbolBounds.getCenterY();
		symw=vehicleSymbolBounds.getWidth();
		symh=vehicleSymbolBounds.getHeight();
		
		labelw=labelBounds.getWidth();
		labelh=labelBounds.getHeight();
		
		labelx=symcx+dirX*(symw+labelw)/2-labelw/2+hookX;
		labely=symcy+dirY*(symh+labelh)/2-labelh/2+hookY;
		
		bounds2d=new Rectangle2D.Double(labelx,labely,labelw,labelh);
		label.setPosition(labelx, labely);
	}
	
	public Rectangle getBounds() {
		return getBounds2D().getBounds();
	}
	
	public Rectangle2D getBounds2D() {
		return bounds2d;
	}
	
	public void processMouseEvent(MouseEvent e) {
		label.processMouseEvent(e);
	}
	
	public void updateLabelContents() {
		final AircraftState aircraftState=associatedSymbol.getVehicle().getAircraftState();
		
		if (aircraftState==null) {
			prepareNoncorrelatedLabel();
		} else {
			final AircraftTaskState aircraftTaskState=aircraftState.getTaskState();
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
		label.pack();
	}
	
	private void prepareNoncorrelatedLabel() {
		label.removeAll();

		label.add(callsignElement);

		labelLines[1].removeAll();
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(groundSpeedElement);
		label.add(labelLines[1]);
	}
	
	private void prepareOtherLabel() {
		label.removeAll();
		
		labelLines[0].removeAll();
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		
		label.add(labelLines[0]);
		label.add(actualLevelElement);
	}
	
	private void prepareNotConcernedLabel() {
		label.removeAll();
		
		labelLines[0].removeAll();
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		label.add(labelLines[0]);
		
		labelLines[1].removeAll();
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(exitPointElement);
		labelLines[1].add(groundSpeedElement);
		label.add(labelLines[1]);
		
		labelLines[2].removeAll();
		labelLines[2].add(clearedLevelElement);
		labelLines[2].add(exitLevelElement);
		label.add(labelLines[2]);
		
		labelLines[3].removeAll();
		labelLines[3].add(assignedHeadingElement);
		labelLines[3].add(assignedSpeedElement);
		labelLines[3].add(assignedClimbRateElement);
		label.add(labelLines[3]);
		
		// TODO: fifth line contains optional information...
	}
	
	private void prepareStandardLabel() {
		labelLines[0].removeAll();
		labelLines[1].removeAll();
		labelLines[2].removeAll();
		labelLines[3].removeAll();
		
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(exitPointElement);
		labelLines[1].add(groundSpeedElement);
		
		labelLines[2].add(clearedLevelElement);
		labelLines[2].add(exitLevelElement);
		
		labelLines[3].add(assignedHeadingElement);
		labelLines[3].add(assignedSpeedElement);
		labelLines[3].add(assignedClimbRateElement);
		
		label.removeAll();
		label.add(labelLines[0]);
		label.add(labelLines[1]);
		label.add(labelLines[2]);
		label.add(labelLines[3]);
	}
	
	public void paint(Graphics2D g2d) {
		final Rectangle2D bounds=getBounds2D();
		
		final Aircraft aircraft=associatedSymbol.getVehicle();
		// TODO: implement properly
		final AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED;
		if (aircraft.isSelected()) {
			g2d.setColor(aircraftTaskState.getSelectedBackgroundColor());
			g2d.fill(bounds);
			g2d.setColor(aircraftTaskState.getSelectedTextColor());
		} else {
			g2d.setColor(aircraftTaskState.getNormalTextColor());
		}
		label.paint(g2d);
	}
	
	@Override
	public AircraftSymbol getVehicleSymbol() {
		return associatedSymbol;
	}
	
	@Override
	public AircraftSymbol getAssociatedObject() {
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
	
	public void setInitialHookPosition(double dx, double dy) {
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
	
	public void setHookPosition(double dx, double dy) {
		setInitialHookPosition(dx, dy);
		updatePosition();
	}
	
	@Override
	public void move(double dx, double dy) {
		setHookPosition(hookX+dx, hookY+dy);
	}
	
	@Override
	public String toString() {
		return "hookX="+hookX+" hookY="+hookY;
	}
}
