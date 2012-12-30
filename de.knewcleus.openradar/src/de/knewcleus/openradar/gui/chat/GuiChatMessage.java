package de.knewcleus.openradar.gui.chat;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        timestamp = sdf.format(time);
        this.callSign = callSign;
        this.message = message;
        this.frequency = frequency;
        this.airportMentioned = message.contains(master.getDataRegistry().getAirportCode());
        this.isOwnMessage = callSign.contains(master.getCurrentATCCallSign());
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
