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
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact.State;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.rpvd.contact.ContactShape.Symbol;

/**
 * This class implements the orginal layout introduced into OR by W.Wagner.
 * It is for from being realistic as it relies on multiplayer protocol data and
 * does not support squawk codes, but it is easier to understand for beginners.
 *
 * @author Wolfram Wagner
 *
 */
public class TraditionalLayout extends ADatablockLayout {

//    private final DatablockLayoutManager manager;
    private final Font font = new Font("Courier", Font.PLAIN, 11);

    public TraditionalLayout(DatablockLayoutManager manager) {
//        this.manager = manager;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public String getMenuText() {
        return "Traditional (no transponder interaction)";
    }

    @Override
    public boolean supportsSquawk() {
        return false;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public Color getBackgroundColor(GuiRadarContact contact, boolean highlighted) {
        if(highlighted || contact.isIdentActive()) {
            return Color.white;
        }
        return Palette.LANDMASS;
    }

    @Override
    public Color getColor(GuiRadarContact c) {
        Color color = Palette.RADAR_UNCONTROLLED;

        if (c.isIdentActive()) {
            color = Color.black;
            c.setHighlighted();

        } else if (c.getTranspSquawkCode() != null && (7700 == c.getTranspSquawkCode() || 7600 == c.getTranspSquawkCode() || 7500 == c.getTranspSquawkCode()) ) {
            // Emergency
            color = new Color(255,100,0);

        } else if(!c .isActive()) {
            // INCACTIVE GHOSTS
            color=Palette.RADAR_GHOST;

        } else if(c.isNeglect()) {
            // BAD GUYS
            color=Palette.RADAR_GHOST;

        } else if(c.getState()==State.IMPORTANT) {
            // CONTROLLED left column
            color=Palette.RADAR_CONTROLLED;

        } else if(c.getState()==State.CONTROLLED) {
            // WATCHED middle column
            color=Palette.RADAR_IMPORTANT;
        } else {
            // UNCONTROLLED right column
            color=Palette.RADAR_UNCONTROLLED;
        }

        return color;
    }

    @Override
    public Color getDataBlockColor(GuiRadarContact c) {
        Color color = Palette.RADAR_UNCONTROLLED;

        if (c.isIdentActive()) {
            color = Color.black;
            c.setHighlighted();
        } else if (c.getTranspSquawkCode() != null && (7700 == c.getTranspSquawkCode() || 7600 == c.getTranspSquawkCode() || 7500 == c.getTranspSquawkCode()) ) {
            // Emergency
            color = new Color(255,100,0);

        } else if(c.isSelected()) {
            // SELECTED
            color=Palette.RADAR_SELECTED;

        } else if(!c .isActive()) {
            // INCACTIVE GHOSTS
            color=Palette.RADAR_GHOST;

        } else if(c.isNeglect()) {
            // BAD GUYS
            color=Palette.RADAR_GHOST;

        } else if(c.getState()==State.IMPORTANT) {
            // CONTROLLED left column
            color=Palette.RADAR_CONTROLLED;

        } else if(c.getState()==State.CONTROLLED) {
            // WATCHED middle column
            color=Palette.RADAR_IMPORTANT;
        } else {
            // UNCONTROLLED right column
            color=Palette.RADAR_UNCONTROLLED;
        }

        return color;
    }

    @Override
    public String getDataBlockText(GuiMasterController master, GuiRadarContact c) {
        if(c.isAtc()) {
            setAltSpeedIndex(-1);
            return String.format("%s\n%s",c.getCallSign(),c.getAircraftCode());

        }
        String addData = getAddData(c);
        setAltSpeedIndex(2);
        return  String.format("%s %2s",c.getCallSign(),c.getMagnCourse())  +"\n"+
                c.getModel()+" "+addData+"\n"+
                String.format("%1s %2s", c.getFlightLevel(),c.getGroundSpeed());
    }

    private String getAddData(GuiRadarContact c) {
        FlightPlanData fp = c.getFlightPlan();
        if(fp.getAssignedRoute()!=null && !fp.getAssignedRoute().isEmpty()) {
            return fp.getAssignedRoute();
        }
        if(fp.getAssignedRunway()!=null && !fp.getAssignedRunway().isEmpty()) {
            return "rw"+fp.getAssignedRunway();
        }
        if(fp.getDestinationAirport()!=null && !fp.getDestinationAirport().isEmpty()) {
            return fp.getDestinationAirport();
        }

        return "";
    }

    @Override
    public void modify(ContactShape shape, GuiRadarContact c) {

        shape.modify(Symbol.FilledDot, c, 6);
    }
    @Override
    public boolean displayVSpeedArrow(GuiRadarContact c) {
        return Math.abs(c.getVerticalSpeedD())>100;
    }

}
