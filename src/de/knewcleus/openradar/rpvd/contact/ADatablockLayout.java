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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
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
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

public abstract class ADatablockLayout {
    
    protected volatile int altSpeedIndex = -1; 
    
    /**
     * Returns a name of the layout that can be used as a key to store and load properties
     * or identify menu items
     */
    public abstract String getName();

    public boolean supportsSquawk() {
        return true;
    }

    /** Returns the text that should be displayed in menu */
    public abstract String getMenuText();

    public abstract Color getBackgroundColor(GuiRadarContact contact, boolean highlighted);

    public abstract Color getColor(GuiRadarContact contact);

    public abstract Color getDataBlockColor(GuiRadarContact c) ;

    /** Returns the text that will be displayed in data block. Lines separated by newline...*/
    public abstract String getDataBlockText(GuiMasterController master, GuiRadarContact contact);

    public abstract Font getFont();
    /** Returns the contact shape matching to the current layout mode and contact state... */
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

    public abstract boolean displayVSpeedArrow(GuiRadarContact c) ;

}
