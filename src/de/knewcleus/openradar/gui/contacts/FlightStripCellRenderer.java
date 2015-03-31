/**
 * Copyright (C) 2012,2013 Wolfram Wagner
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
package de.knewcleus.openradar.gui.contacts;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.rpvd.contact.ADatablockLayout;
import de.knewcleus.openradar.rpvd.contact.PureSimulationLayout;
import de.knewcleus.openradar.rpvd.contact.SimulationLayout;
import de.knewcleus.openradar.rpvd.contact.TraditionalLayout;

/**
 * This class renders the flight strip like radar contacts, better, it forwards this to specialized classes.
 *
 * @author Wolfram Wagner
 */

public class FlightStripCellRenderer extends JComponent implements ListCellRenderer<GuiRadarContact> {
    private static final long serialVersionUID = 4683696532302543565L;

    public final static int STRIP_WITDH = 250;
    
    private FlightStripCellRendererTrad tradRenderer;
    private FlightStripCellRendererSim simRenderer;
    private FlightStripCellRendererPureSim pureSimRenderer;
    
    private final GuiMasterController master;
    
    public FlightStripCellRenderer(GuiMasterController master) {

        this.master=master;
        
        this.tradRenderer = new FlightStripCellRendererTrad(master);
        this.simRenderer = new FlightStripCellRendererSim(master);
        this.pureSimRenderer = new FlightStripCellRendererPureSim(master);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends GuiRadarContact> list, GuiRadarContact value, int index, boolean isSelected, boolean cellHasFocus) {
        
        ADatablockLayout layout = master.getAirportData().getDatablockLayoutManager().getActiveLayout();

        if(layout instanceof PureSimulationLayout) {
            return pureSimRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } else if(layout instanceof SimulationLayout) {
            return simRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } else if(layout instanceof TraditionalLayout) {
            return tradRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        } else {
            return simRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
