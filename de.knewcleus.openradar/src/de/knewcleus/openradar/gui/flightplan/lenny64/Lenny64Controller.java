/**
 * Copyright (C) 2014 Wolfram Wagner
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.gui.flightplan.lenny64;

import java.awt.event.MouseEvent;
import java.util.List;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.FlightPlanDialog;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.setup.AirportData;

public class Lenny64Controller {

    private final GuiMasterController master;
    private final FlightPlanDialog dialog;
    private final AirportData airportData;
    private final Lenny64FlightplanServerConnector lenny64Connector;
    private final Lenny64FpSelectionDialog fpSelectionDialog;

    public Lenny64Controller(GuiMasterController master, FlightPlanDialog dialog, AirportData airportData) {
        this.master = master;
        this.airportData = airportData;
        this.dialog = dialog;
        this.lenny64Connector = new Lenny64FlightplanServerConnector();

        fpSelectionDialog = new Lenny64FpSelectionDialog(this, dialog);
    }

    public boolean isDialogOpen() {
        return fpSelectionDialog.isVisible();
    }

    public void closeFpSelectionDialog() {
        fpSelectionDialog.setVisible(false);
    }

    /**
     * Retrieves the list of possibly matching flightplans and displays a dialog to select the one you want to import.
     * 
     * @param callsign
     */
    public void downloadFlightPlansFor(MouseEvent e, String callsign) {
        dialog.saveData();
        GuiRadarContact c = master.getRadarContactManager().getContactFor(callsign);
        List<FlightPlanData> existingFPs = lenny64Connector.checkForFlightplan(airportData, c);
        if (existingFPs.isEmpty()) {
            dialog.setLennyButtonText("none found");
        } else {
            dialog.setLennyButtonText("(please select)");
            fpSelectionDialog.show(callsign, existingFPs);
            fpSelectionDialog.setLocation(e);
        }
    }

    /**
     * Merges the data from lenny64 into the existing flightplan
     */
    public void mergeFlightplans(FlightPlanData lenny64Flightplan) {
        GuiRadarContact c = master.getRadarContactManager().getContactFor(lenny64Flightplan.getCallsign());
        synchronized (c) {
            c.setFlightPlan(lenny64Flightplan);
            dialog.setLennyButtonText("(loaded)");
            dialog.setData(c);
            dialog.saveData();
        }
    }

}
