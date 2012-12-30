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
