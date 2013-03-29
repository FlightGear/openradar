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
package de.knewcleus.openradar.gui.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;

/**
 * The front end data object for a chat message.
 *
 * @author Wolfram Wagner
 */
public class GuiChatMessage {

    private GuiRadarContact knownRadarContact = null;

    private Date created = null;
    private String timestamp = null;
    private String callSign = null;
    private String message = null;
    private String frequency = null;
    private boolean airportMentioned = false;
    private boolean isOwnMessage = false;

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public GuiChatMessage(GuiMasterController master, Date time, String callSign, String frequency, String message) {
        this.created = time;
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        timestamp = sdf.format(time);
        this.callSign = callSign;
        this.message = message;
        this.frequency = frequency;
        this.airportMentioned = message.contains(master.getDataRegistry().getAirportCode());
        this.isOwnMessage = (master.getCurrentATCCallSign()!=null) ? callSign.contains(master.getCurrentATCCallSign()) : false;
    }

    public Date getCreated() {
        return created;
    }

    public GuiRadarContact getKnownRadarContact() {
        return knownRadarContact;
    }

    public void setKnownRadarContact(GuiRadarContact knownRadarContact) {
        this.knownRadarContact = knownRadarContact;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getCallSign() {
        return  callSign;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAirportMentioned() {
        return airportMentioned;
    }

    public boolean isContactSelected() {
        return knownRadarContact!=null && knownRadarContact.isSelected();
    }

    public boolean isOwnMessage() {
        return isOwnMessage;
    }

    public String getFrequency() {
        return frequency;
    }

    public String toString() {
        return message;
    }

    public boolean isNeglectOrInactive() {
        return (knownRadarContact!=null && knownRadarContact.isNeglect()) || (knownRadarContact!=null && !knownRadarContact.isActive());
    }

    public boolean messageContainsSelectedContact(GuiMasterController master) {
        GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();
        if(selectedContact==null) {
            return false;
        } else {
            return message.contains(selectedContact.getCallSign());
        }
    }
}
