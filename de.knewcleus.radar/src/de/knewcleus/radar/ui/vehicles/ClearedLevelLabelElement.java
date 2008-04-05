package de.knewcleus.radar.ui.vehicles;

import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;

import de.knewcleus.radar.aircraft.AircraftState;
import de.knewcleus.radar.ui.ListMenu;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;

public class ClearedLevelLabelElement extends AbstractTextLabelElement {
	protected final Aircraft aircraft;

	public ClearedLevelLabelElement(Aircraft aircraft) {
		this.aircraft=aircraft;
	}

	@Override
	protected String getText() {
		final AircraftState aircraftState=aircraft.getAircraftState();
		if (aircraftState==null)
			return null;
		Integer clearedLevel=aircraftState.getClearedFlightLevel();
		if (clearedLevel==null)
			return "CFL";
		return String.format("%03d",aircraftState.getClearedFlightLevel());
	}


	@Override
	public void processMouseEvent(MouseEvent event) {
		switch (event.getID()) {
		case MouseEvent.MOUSE_CLICKED:
			if (event.getButton()==MouseEvent.BUTTON1) {
				final AircraftState aircraftState=aircraft.getAircraftState();
				assert(aircraftState!=null);
				FlightLevelListModel levelListModel=new FlightLevelListModel(40,600,10);
				ListMenu listMenu=new ListMenu(levelListModel);
				final int currentSelectedLevel;
				if (aircraftState.getClearedFlightLevel()!=null) {
					currentSelectedLevel=aircraftState.getClearedFlightLevel();
				} else {
					currentSelectedLevel=aircraft.getTrack().getFlightLevel();
				}
				int selectedIndex=levelListModel.getIndexForLevel(currentSelectedLevel);
				PopupComponent popup=new PopupComponent(aircraft.getCallsign());
				popup.setLayout(new BoxLayout(popup, BoxLayout.Y_AXIS));
				popup.add(listMenu);
				popup.invalidate();
				System.out.println("popup.getMinimumSize()="+popup.getMinimumSize());
				System.out.println("popup.getSize()="+popup.getSize());
				popup.show(getDisplayComponent(),event.getX(), event.getY());
				listMenu.ensureIndexIsVisible(selectedIndex);
				event.consume();
			}
			break;
		}
	}
	
	@Override
	public boolean isEnabled() {
		return aircraft.isCorrelated();
	}
}
