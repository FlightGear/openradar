package de.knewcleus.radar.ui.rpvd;

import java.awt.Graphics2D;
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

public class AircraftLabel extends LabelElementContainer implements IVehicleLabel {
	protected final AircraftSymbol associatedSymbol;
	protected double hookX,hookY;
	protected double dirX,dirY;;
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
		
		setDisplayComponent(associatedSymbol.getRadarPlanViewContext().getRadarPlanViewPanel());
		setLayoutManager(new MultiLineLabelLayoutManager(Justification.JUSTIFY));
		for (int i=0;i<labelLines.length;i++) {
			labelLines[i]=new LabelElementContainer();
			labelLines[i].setLayoutManager(new LabelLineLayoutManager(Justification.LEADING));
			add(labelLines[i]);
		}
		
		updateLabelContents();
	}
	
	@Override
	public void updatePosition() {
		final Rectangle2D vehicleSymbolBounds=getVehicleSymbol().getSymbolBounds();
		final Rectangle2D labelBounds=getBounds2D();
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
		
		setPosition(labelx, labely);
	}
	
	@Override
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
		pack();
	}
	
	private void prepareNoncorrelatedLabel() {
		removeAll();

		labelLines[0].removeAll();
		labelLines[0].add(callsignElement);

		labelLines[1].removeAll();
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(groundSpeedElement);
	}
	
	private void prepareOtherLabel() {
		labelLines[0].removeAll();
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		
		labelLines[1].removeAll();
		labelLines[1].add(actualLevelElement);
	}
	
	private void prepareNotConcernedLabel() {
		labelLines[0].removeAll();
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		
		labelLines[1].removeAll();
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(exitPointElement);
		labelLines[1].add(groundSpeedElement);
		
		labelLines[2].removeAll();
		labelLines[2].add(clearedLevelElement);
		labelLines[2].add(exitLevelElement);
		
		labelLines[3].removeAll();
		labelLines[3].add(assignedHeadingElement);
		labelLines[3].add(assignedSpeedElement);
		labelLines[3].add(assignedClimbRateElement);
		
		// TODO: fifth line contains optional information...
		labelLines[4].removeAll();
	}
	
	private void prepareStandardLabel() {
		labelLines[0].removeAll();
		
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		
		labelLines[1].removeAll();
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(exitPointElement);
		labelLines[1].add(groundSpeedElement);
		
		labelLines[2].removeAll();
		labelLines[2].add(clearedLevelElement);
		labelLines[2].add(exitLevelElement);
		
		labelLines[3].removeAll();
		labelLines[3].add(assignedHeadingElement);
		labelLines[3].add(assignedSpeedElement);
		labelLines[3].add(assignedClimbRateElement);
	}
	
	public void paint(Graphics2D g2d) {
		
		final Aircraft aircraft=associatedSymbol.getVehicle();
		// TODO: implement properly
		final AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED;
		if (aircraft.isSelected()) {
			final Rectangle2D bounds=getBounds2D();
			g2d.setColor(aircraftTaskState.getSelectedBackgroundColor());
			g2d.fill(bounds);
			g2d.setColor(aircraftTaskState.getSelectedTextColor());
		} else {
			g2d.setColor(aircraftTaskState.getNormalTextColor());
		}
		super.paint(g2d);
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
