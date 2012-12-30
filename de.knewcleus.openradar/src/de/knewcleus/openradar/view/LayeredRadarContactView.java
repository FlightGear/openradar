package de.knewcleus.openradar.view;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact.State;
import de.knewcleus.openradar.rpvd.RadarTargetView;

/**
 * This class exists to paint radar contacts in a sequence, 
 * 
 * (1) at first the uncontrolled
 * (2) the controlled
 * (3) the important
 * (4) the selected 
 * 
 * @author Wolfram Wagner
 *
 */
public class LayeredRadarContactView extends LayeredView {

    
    public LayeredRadarContactView( IViewerAdapter mapViewAdapter ) {
        super(mapViewAdapter);
    }
    
    @Override
    public synchronized void traverse(IViewVisitor visitor) {
        RadarTargetView selectedView = null;
        // paint the expired
        for (IView view: views) {
            if(view instanceof RadarTargetView) {
                RadarTargetView radarView = (RadarTargetView)view;
                if(radarView.getTrackDisplayState().getGuiContact().isExpired()) {
                    view.accept(visitor);
                }
            }
        }
        // paint the inactive
        for (IView view: views) {
            if(view instanceof RadarTargetView) {
                RadarTargetView radarView = (RadarTargetView)view;
                if(!radarView.getTrackDisplayState().getGuiContact().isActive()) {
                    view.accept(visitor);
                }
            }
        }
        // paint the uncontrolled
        for (IView view: views) {
            if(view instanceof RadarTargetView) {
                RadarTargetView radarView = (RadarTargetView)view;
                if(radarView.getTrackDisplayState().getGuiContact().isSelected()) {
                    selectedView = radarView;
                }
                if(radarView.getTrackDisplayState().getGuiContact().getState()==State.UNCONTROLLED 
                        && !radarView.getTrackDisplayState().getGuiContact().isSelected()
                        && radarView.getTrackDisplayState().getGuiContact().isActive()) {
                    view.accept(visitor);
                }
            }
        }
        // paint the controlled
        for (IView view: views) {
            if(view instanceof RadarTargetView) {
                RadarTargetView radarView = (RadarTargetView)view;
                if(radarView.getTrackDisplayState().getGuiContact().getState()==State.CONTROLLED 
                        && !radarView.getTrackDisplayState().getGuiContact().isSelected()
                        && radarView.getTrackDisplayState().getGuiContact().isActive()) {
                    view.accept(visitor);
                }
            }
        }
        // paint the important
        for (IView view: views) {
            if(view instanceof RadarTargetView) {
                RadarTargetView radarView = (RadarTargetView)view;
                if(radarView.getTrackDisplayState().getGuiContact().getState()==State.IMPORTANT && 
                        !radarView.getTrackDisplayState().getGuiContact().isSelected()
                        && radarView.getTrackDisplayState().getGuiContact().isActive()) { 
                    view.accept(visitor);
                }
            }
        }
        // paint the selected
        if(selectedView!=null) {
            selectedView.accept(visitor);
        }

    }
}
