package de.knewcleus.radar.ui;

import java.awt.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JInternalFrame;

public abstract class WorkstationGlobalFrame extends JInternalFrame {
	protected final RadarWorkstation workstation;
	protected final String shortTitle;
	protected final ButtonGroup parkingRankButtonGroup=new ButtonGroup();
	protected RadarDesktop currentDesktop;
	
	public WorkstationGlobalFrame(RadarWorkstation workstation, String shortTitle,
			String title, boolean resizable,
			boolean closable, boolean maximizable, boolean iconifiable)
	{
		super(title, resizable, closable, maximizable, iconifiable);
		this.workstation=workstation;
		this.shortTitle=shortTitle;
	}
	
	public RadarWorkstation getWorkstation() {
		return workstation;
	}
	
	public String getShortTitle() {
		return shortTitle;
	}
	
	public ButtonGroup getParkingRankButtonGroup() {
		return parkingRankButtonGroup;
	}
	
	public RadarDesktop getCurrentDesktop() {
		return currentDesktop;
	}
	
	public void acquire(RadarDesktop desktop) {
		if (desktop==currentDesktop)
			return;
		
		if (currentDesktop!=null) {
			currentDesktop.setGlobalFrameBounds(this, getBounds());
			setVisible(false);
			currentDesktop.remove(this);
		}
		
		assert(isIconifiable() || desktop!=null);
		
		if (desktop!=null) {
			final Rectangle newBounds=desktop.getGlobalFrameBounds(this);
			if (newBounds!=null) {
				setBounds(newBounds);
			}
			
			desktop.add(this);
			setVisible(true);
		}
		currentDesktop=desktop;
	}
}
