package de.knewcleus.openradar.gui.radar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.view.IRadarViewChangeListener;

/**
 * The controller for the radar map.
 * 
 * @author Wolfram Wagner
 *
 */
public class RadarManager {

//    private GuiMasterController master = null;
    private GuiRadarBackend backend = null;
    
    private ZoomMouseListener zoomMouseListener = new ZoomMouseListener();
    private ObjectFilterMouseListener objectFilterMouseListener = new ObjectFilterMouseListener();
    
    public RadarManager(GuiMasterController master, GuiRadarBackend backend) {
//        this.master = master;
        this.backend = backend;
    }
    
    public void setFilter(String zoomLevel) {
        backend.setZoomLevel(zoomLevel);
    }
    
    public ZoomMouseListener getZoomMouseListener() { return zoomMouseListener; }
    
    public class ZoomMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel lSource = (JLabel)e.getSource();
            String zl;
            if(lSource.getName().equals("GROUND") || lSource.getName().equals("TOWER") || lSource.getName().equals("APP") || lSource.getName().equals("SECTOR")) {
                zl = lSource.getName();
                if(e.getButton()==1) {
                    setFilter(zl);
                } else if(e.getButton()==2) {
                    backend.defineZoomLevel(zl);
                }
    
                RadarPanel parent = (RadarPanel)lSource.getParent().getParent().getParent();
                parent.resetFilters();
                parent.selectFilter(lSource);
            }
        }
    }

    public MouseListener getObjectFilterListener() {
        return objectFilterMouseListener;
    }

    public class ObjectFilterMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel lSource = (JLabel)e.getSource();
            
            if(lSource.getName().equals("FIX") || lSource.getName().equals("NDB") || lSource.getName().equals("VOR") || lSource.getName().equals("CIRCLES") 
                    || lSource.getName().equals("APT") || lSource.getName().equals("PPN")) {
                String objectName = lSource.getName();
                if(e.getButton()==1) {
                    backend.toggleRadarObjectFilter(objectName);
                }
                RadarPanel parent = (RadarPanel)lSource.getParent().getParent().getParent();
                parent.setObjecFilter(lSource, backend.getRadarObjectFilterState(objectName));
            } 
        }
    }

    public void addRadarViewListener(IRadarViewChangeListener  l) {
        backend.addRadarViewListener(l);
    }

}
