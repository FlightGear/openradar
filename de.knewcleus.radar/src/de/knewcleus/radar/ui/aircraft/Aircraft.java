package de.knewcleus.radar.ui.aircraft;

import de.knewcleus.radar.aircraft.Target;

public class Aircraft {
	protected final Target target;
	protected boolean isSelected=false;
	
	public Aircraft(Target target) {
		this.target=target;
	}
	
	public Target getTarget() {
		return target;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean canSelect() {
		// TODO: implement properly
		return true;
	}
}
