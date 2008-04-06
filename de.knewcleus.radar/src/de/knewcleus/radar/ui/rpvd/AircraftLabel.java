package de.knewcleus.radar.ui.rpvd;


import java.awt.Color;

import de.knewcleus.radar.aircraft.AircraftState;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.labels.Justification;
import de.knewcleus.radar.ui.labels.LabelElement;
import de.knewcleus.radar.ui.labels.LabelElementContainer;
import de.knewcleus.radar.ui.labels.LabelLineLayoutManager;
import de.knewcleus.radar.ui.labels.MultiLineLabelLayoutManager;
import de.knewcleus.radar.ui.labels.StaticTextLabelElement;
import de.knewcleus.radar.ui.vehicles.ActualLevelLabelElement;
import de.knewcleus.radar.ui.vehicles.Aircraft;
import de.knewcleus.radar.ui.vehicles.CallsignLabelElement;
import de.knewcleus.radar.ui.vehicles.ClearedLevelLabelElement;
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
		
		final Aircraft aircraft=associatedSymbol.getVehicle();
		callsignElement=new CallsignLabelElement(aircraft);
		nextSectorElement=new StaticTextLabelElement();
		actualLevelElement=new ActualLevelLabelElement(aircraft);
		exitPointElement=new StaticTextLabelElement();
		groundSpeedElement=new GroundSpeedLabelElement(aircraft);
		clearedLevelElement=new ClearedLevelLabelElement(aircraft);
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
		} else if (associatedSymbol.getVehicle().isSelected()) {
			prepareStandardLabel();
		} else { 
			prepareMinimumLabel();
		}
		pack();
	}
	
	private void prepareNoncorrelatedLabel() {
		clearAllLines();

		labelLines[0].add(callsignElement);
		
		labelLines[1].add(actualLevelElement);
		labelLines[1].add(groundSpeedElement);
	}
	
	private void prepareMinimumLabel() {
		clearAllLines();
		labelLines[0].add(callsignElement);
		labelLines[0].add(nextSectorElement);
		
		labelLines[1].add(actualLevelElement);
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
		
		// TODO: fifth line contains optional information...
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
	public Color getNormalTextColor() {
		final AircraftState aircraftState=associatedSymbol.getVehicle().getAircraftState();
		if (aircraftState==null) {
			/* Not correlated */
			return Palette.BEACON;
		}
		
		switch (aircraftState.getTaskState()) {
		case NOT_CONCERNED:
		case ASSUMED_OUT:
			return Palette.BEACON;
		case PENDING:
		case PENDING_IN:
		case ASSUMED:
			return Palette.WHITE;
		}
		
		return Palette.BEACON;
	}

	@Override
	public Color getSelectedBackgroundColor() {
		return getNormalTextColor();
	}

	@Override
	public Color getSelectedTextColor() {
		return Palette.BLACK;
	}
}
