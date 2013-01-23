/**
 * Copyright (C) 2008-2009 Ralf Gerlich
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
package de.knewcleus.openradar.rpvd;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.radardata.fgmp.RadarDataPacket;
import de.knewcleus.openradar.radardata.fgmp.TargetStatus;
import de.knewcleus.openradar.tracks.ITrack;
import de.knewcleus.openradar.tracks.ITrackManager;
import de.knewcleus.openradar.tracks.TrackLifetimeNotification;
import de.knewcleus.openradar.view.LayeredView;

public class RadarTargetProvider implements INotificationListener {
    protected final IRadarMapViewerAdapter radarMapViewAdapter;
    protected final LayeredView radarTargetLayer;
    protected final ITrackManager trackManager;
    protected final GuiMasterController guiInteractionManager;

    protected final Map<ITrack, RadarTargetView> viewMap = Collections.synchronizedMap(new HashMap<ITrack, RadarTargetView>());

    public RadarTargetProvider(IRadarMapViewerAdapter radarMapViewAdapter, LayeredView radarTargetLayer, ITrackManager trackManager,
            GuiMasterController guiInteractionManager) {
        this.radarMapViewAdapter = radarMapViewAdapter;
        this.radarTargetLayer = radarTargetLayer;
        this.trackManager = trackManager;
        this.guiInteractionManager = guiInteractionManager;
        trackManager.registerListener(this);
    }

    @Override
    public void acceptNotification(INotification notification) {
        if (notification instanceof TrackLifetimeNotification) {
            final TrackLifetimeNotification lifetimeNotification;
            lifetimeNotification = (TrackLifetimeNotification) notification;

            final ITrack track = lifetimeNotification.getTrack();
            switch (lifetimeNotification.getLifetimeState()) {
            case CREATED:
                final TrackDisplayState displayState = new TrackDisplayState(track);
                
                TargetStatus targetStatus = (TargetStatus) ((RadarDataPacket) track.getCurrentState()).getTrackingIdentifier();
                GuiRadarContact guiContact = guiInteractionManager.getRadarContactManager().getContactFor(targetStatus.getCallsign());
                if(guiContact!=null) {
                    final RadarTargetView newView = new RadarTargetView(radarMapViewAdapter, displayState);
                    displayState.setGuiLink(guiInteractionManager, guiContact, newView);
                viewMap.put(track, newView);
                radarTargetLayer.pushView(newView);
                } else {
                    System.out.println("No contact found for package "+targetStatus.getCallsign());
                }
                break;
            case RETIRED:
                final RadarTargetView oldView = viewMap.get(track);
                radarTargetLayer.removeView(oldView);
                viewMap.remove(track);
                break;
            }
        }
    }

}
