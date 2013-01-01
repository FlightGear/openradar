/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
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

import java.awt.event.MouseEvent;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.notify.Notifier;
import de.knewcleus.openradar.tracks.ITrack;

public class TrackDisplayState extends Notifier {
	protected final ITrack track;
	protected boolean selected = false;
	
	protected GuiMasterController master;
	protected GuiRadarContact guiContact;

	protected RadarTargetView view;
	
	public TrackDisplayState(ITrack track) {
		this.track = track;
	}
	
	public boolean isSelected() {
	    return guiContact!=null?guiContact.isSelected():false;
	}
	
	public void setSelected(MouseEvent e, boolean selected) {
	    //System.out.println(e.getButton());
        if(e.getButton()==java.awt.event.MouseEvent.BUTTON1) {
            master.getRadarContactManager().select(guiContact, true, false);
            if(guiContact.isSelected()) {
                master.getMpChatManager().requestFocusForInput();
            }
        }
        if(e.getButton()==java.awt.event.MouseEvent.BUTTON2) {
            master.getRadarContactManager().selectNShowContactDialog(guiContact, e);
        } else if(e.getButton()==java.awt.event.MouseEvent.BUTTON3) {
            master.getRadarContactManager().selectNShowAtcMsgDialog(guiContact, e);
        }
		notify(new SelectionChangeNotification(this));
	}
	
	public ITrack getTrack() {
		return track;
	}

    public void setGuiLink(GuiMasterController master, GuiRadarContact guiContact, RadarTargetView view) {
        this.master = master;
        this.guiContact=guiContact;
        this.view = view;
        guiContact.setView(view);
    }

    public GuiRadarContact getGuiContact() {
        return guiContact;
    }
}
