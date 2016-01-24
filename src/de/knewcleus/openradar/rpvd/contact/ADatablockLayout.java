/**
 * Copyright (C) 2013,2015 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.rpvd.contact;

import java.awt.Color;
import java.awt.Font;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.State;

public abstract class ADatablockLayout {

	protected volatile int altSpeedIndex = -1;
	protected volatile Font font = new Font("Courier", Font.PLAIN, 11);;

	/**
	 * Returns a name of the layout that can be used as a key to store and load
	 * properties or identify menu items
	 */
	public abstract String getName();

	public boolean supportsSquawk() {
		return true;
	}

	/** Returns the text that should be displayed in menu */
	public abstract String getMenuText();

    public Font getFont() {
        return font;
    }

    public Color getBackgroundColor(GuiRadarContact contact, boolean highlighted) {
        if (highlighted || contact.isIdentActive()) {
            return Palette.WHITE;
        }
        return Palette.LANDMASS;
    }

	/**
	 * This method defines the color of a radar contact in the layout mode. 
	 * Override it to adapt it to individual settings! 
	 * 
	 * @param c the current contact
	 * @return the Color
	 */
	public Color getColor(GuiRadarContact c) {
		Color color = Palette.RADAR_UNCONTROLLED;

		if (c.isIdentActive()) {
			color = Palette.BLACK;
			c.setHighlighted();
		} else if (c.isOnEmergency()) {
			// Emergency
			color = Palette.RADAR_EMERGENCY;

		} else if (!c.isActive()) {
			// INACCTIVE GHOSTS
			color = Palette.RADAR_GHOST;

		} else if (c.isNeglect()) {
			// BAD GUYS
			color = Palette.RADAR_GHOST;

		} else if (c.getState() == State.IMPORTANT) {
			// CONTROLLED left column
			color = Palette.RADAR_CONTROLLED;

		} else if (c.getState() == State.CONTROLLED) {
			// WATCHED middle column
			color = Palette.RADAR_IMPORTANT;
		} else {
			// UNCONTROLLED right column
			color = Palette.RADAR_UNCONTROLLED;
		}

		return color;
	}

	/**
	 * This method defines the data block color of a radar contact in the layout mode. 
	 * Override it to adapt it to individual settings! 
	 * 
	 * @param c the current contact
	 * @return the Color
	 */
	public Color getDataBlockColor(GuiRadarContact c) {
		Color color = Palette.RADAR_UNCONTROLLED;

		if (c.isIdentActive()) {
			// IDENT sent
			color = Palette.BLACK;
			c.setHighlighted();
		} else if (c.isOnEmergency()) {
			// Emergency
			color = Palette.RADAR_EMERGENCY;

		} else if (!c.isActive()) {
			// INACTIVE GHOSTS
			color = Palette.RADAR_GHOST;

		} else if (c.isNeglect()) {
			// BAD GUYS
			color = Palette.RADAR_GHOST;

		} else if (c.getState() == State.IMPORTANT) {
			// CONTROLLED left column
			color = Palette.RADAR_CONTROLLED;

		} else if (c.getState() == State.CONTROLLED) {
			// WATCHED middle column
			color = Palette.RADAR_IMPORTANT;
		} else {
			// UNCONTROLLED right column
			color = Palette.RADAR_UNCONTROLLED;
		}

		return color;
	}
	
	/**
	 * Returns the text that should be displayed in the data block
	 * 
	 * @param master
	 * @param c
	 * @return
	 */
	public abstract String getDataBlockText(GuiMasterController master, GuiRadarContact c);
	
	/**
	 * Returns the contact shape matching to the current layout mode and contact
	 * state...
	 */
	public abstract void modify(ContactShape shape, GuiRadarContact c);

	public synchronized int getAltSpeedIndex() {
		return altSpeedIndex;
	}

	public synchronized void setAltSpeedIndex(int altSpeedIndex) {
		this.altSpeedIndex = altSpeedIndex;
	}

	@Override
	public String toString() {
		return getMenuText();
	}

	public abstract boolean displayVSpeedArrow(GuiRadarContact c);

}
