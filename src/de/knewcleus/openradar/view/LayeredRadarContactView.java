/**
 * Copyright (C) 2012, 2016 Wolfram Wagner
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
package de.knewcleus.openradar.view;

import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightstrips.ColumnData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.SectionData;
import de.knewcleus.openradar.rpvd.RadarTargetView;

/**
 * This class exists to paint radar contacts in a sequence, 
 * 
 * @author Wolfram Wagner
 * @author Andreas Vogel
 *
 */
public class LayeredRadarContactView extends LayeredView {

//    private static final Logger log = Logger.getLogger(LayeredRadarContactView.class);
	
    public LayeredRadarContactView( IViewerAdapter mapViewAdapter ) {
        super(mapViewAdapter);
    }

    @Override
    public synchronized void traverse(IViewVisitor visitor) {
        List<IView> viewsToRepaint=new ArrayList<IView>(views);
        
        // --- sort views by paint priority ---
        List<List<RadarTargetView>> radarTargetViews = new ArrayList<List<RadarTargetView>>();
        for (int i = 0; i < ColumnData.PaintLevel.values().length + 2; i++) radarTargetViews.add(new ArrayList<RadarTargetView>());
        
        for (IView view: new ArrayList<IView>(viewsToRepaint)) {
            if(view instanceof RadarTargetView) {
            	int i = 0;
                RadarTargetView radarView = (RadarTargetView)view;
                GuiRadarContact contact = radarView.getTrackDisplayState().getGuiContact();
                if (contact.isSelected()) i = radarTargetViews.size() - 1;
                else {
                	FlightStrip flightstrip = contact.getFlightStrip();
                	if (flightstrip != null) {
                		SectionData section = flightstrip.getSection();
                		if (section != null) {
                			i = 1 + section.getColumn(flightstrip.getColumn()).getPaintLevel().ordinal();
                		}
                	}
                }
                radarTargetViews.get(i).add(radarView);
            }
        }
        // --- visit views ---
        for (List<RadarTargetView> views : radarTargetViews)
        	for (RadarTargetView view : views)
        		view.accept(visitor);
        
        radarTargetViews.clear();
        viewsToRepaint.clear();
    }
}
    