package de.knewcleus.radar.ui.rpvd;


import java.awt.Color;

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

public class AircraftLabel extends AbstractVehicleLabel {
	protected final AircraftSymbol associatedSymbol;
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
		super(5);
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
		clearAllLines();

		labelLines[0].add(callsignElement);
		
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(groundSpeedElement);
	}
	
	private void prepareOtherLabel() {
		clearAllLines();
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		
		labelLines[1].add(actualLevelElement);
	}
	
	private void prepareNotConcernedLabel() {
		clearAllLines();
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
		
		// TODO: fifth line contains optional information...
	}
	
	private void prepareStandardLabel() {
		clearAllLines();
		
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
	}
	
	@Override
	public AircraftSymbol getVehicleSymbol() {
		return associatedSymbol;
	}
	
	@Override
	public double getChargeDensity() {
		return 1;
	}
	
	@Override
	public String toString() {
		return "hookX="+hookX+" hookY="+hookY;
	}

	@Override
	public Color getNormalTextColor() {
		// TODO implement properly
		final AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED;
		return aircraftTaskState.getNormalTextColor();
	}

	@Override
	public Color getSelectedBackgroundColor() {
		// TODO implement properly
		final AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED;
		return aircraftTaskState.getSelectedBackgroundColor();
	}

	@Override
	public Color getSelectedTextColor() {
		// TODO implement properly
		final AircraftTaskState aircraftTaskState=AircraftTaskState.ASSUMED;
		return aircraftTaskState.getSelectedTextColor();
	}
}
