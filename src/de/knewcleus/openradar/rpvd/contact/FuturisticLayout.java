package de.knewcleus.openradar.rpvd.contact;

import java.awt.Color;

import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

/**
 * This data block layout aims to be closer on the reality. There is no heading displayed and as long as no or the wrong
 * squawk code is being transmitted, only the known data are transmitted. If no transmitter data arrives, the contact is
 * displayed like it is fully assigned.
 * 
 * @author Wolfram Wagner
 */
public class FuturisticLayout extends SimulationLayout {

	public FuturisticLayout(DatablockLayoutManager manager) {
		super(manager);
	}

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getMenuText() {
        return "Futuristic (like Simulation, altitude colors)";
    }

	@Override
	public Color getColor(GuiRadarContact c) {
		if (c.isIdentActive()|| c.isOnEmergency() || !c.isActive() || c.isNeglect()) return super.getColor(c);
		return Palette.getAltitudeColor(c);
	}

	@Override
	public Color getDataBlockColor(GuiRadarContact c) {
		return getColor(c);
	}

}
