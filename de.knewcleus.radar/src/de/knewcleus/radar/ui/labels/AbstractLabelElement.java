package de.knewcleus.radar.ui.labels;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPopupMenu;

import de.knewcleus.radar.ui.aircraft.AircraftState;

public abstract class AbstractLabelElement implements ILabelElement {
	protected final ILabelDisplay labelDisplay;
	protected final AircraftState aircraftState;
	
	protected int ascent;
	protected Dimension minimumSize;
	protected Rectangle bounds;

	public AbstractLabelElement(ILabelDisplay labelDisplay, AircraftState aircraftState) {
		this.labelDisplay=labelDisplay;
		this.aircraftState=aircraftState;
	}
	
	public ILabelDisplay getLabelDisplay() {
		return labelDisplay;
	}
	
	public AircraftState getAircraftState() {
		return aircraftState;
	}
	
	@Override
	public int getAscent() {
		return ascent;
	}

	@Override
	public Dimension getMinimumSize() {
		return minimumSize;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public void setBounds(Rectangle rectangle) {
		bounds=rectangle;
	}
	
	public void showPopupMenu(JPopupMenu popupMenu, int x, int y) {
		Rectangle labelBounds=getLabelDisplay().getDisplayBounds();
		// FIXME: We should become independent of the RPVD, as we will also display labels in other windows....
		popupMenu.show(getLabelDisplay().getDisplayComponent(), x+labelBounds.x, y+labelBounds.y);
	}

}