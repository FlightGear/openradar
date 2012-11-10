package de.knewcleus.openradar.rpvd;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.contacts.RadarContactController;
import de.knewcleus.openradar.notify.Notifier;
import de.knewcleus.openradar.tracks.ITrack;

public class TrackDisplayState extends Notifier {
	protected final ITrack track;
	protected boolean selected = false;
	
	protected RadarContactController radarContactManager;
	protected GuiRadarContact guiContact;

	protected RadarTargetView view;
	
	public TrackDisplayState(ITrack track) {
		this.track = track;
	}
	
	public boolean isSelected() {
		//return selected;
	    
	    return guiContact!=null?guiContact.isSelected():false;
	}
	
	public void setSelected(boolean selected) {
		if (isSelected() == selected) {
			return;
		}
		//this.selected = selected;
		radarContactManager.select(guiContact,false);
		notify(new SelectionChangeNotification(this));
	}
	
	public ITrack getTrack() {
		return track;
	}

    public void setGuiLink(RadarContactController radarContactManager, GuiRadarContact guiContact, RadarTargetView view) {
        this.radarContactManager = radarContactManager;
        this.guiContact=guiContact;
        this.view = view;
        guiContact.setView(view);
    }

    public GuiRadarContact getGuiContact() {
        return guiContact;
    }
}
