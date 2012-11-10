package de.knewcleus.openradar.gui.radar;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.radar.GuiRadarBackend.ZoomLevel;

/**
 * The controller for the radar map.
 * 
 * @author Wolfram Wagner
 *
 */
public class RadarManager {

//    private GuiMasterController master = null;
    private GuiRadarBackend backend = null;
    
    private List<IRadarChangeListener> changeListener = new ArrayList<IRadarChangeListener>();
    
    private ZoomMouseListener zoomMouseListener = new ZoomMouseListener();
        
    public RadarManager(GuiMasterController master, GuiRadarBackend backend) {
//        this.master = master;
        this.backend = backend;
    }
    
    public void setFilter(ZoomLevel zoomLevel) {
        ZoomLevel before = backend.getZoomLevel();
        backend.setZoomLevel(zoomLevel);
        fireChange(before,zoomLevel);
    }
    
    // change listener
    public void addChangeListener(IRadarChangeListener l) {
        changeListener.add(l);
    }
    public void removeChangeListener(IRadarChangeListener l) {
        changeListener.remove(l);
    }
    private void fireChange(ZoomLevel formerLevel, ZoomLevel newLevel) {
        for(IRadarChangeListener l: changeListener) {
            l.radarZoomLevelChanged(formerLevel, newLevel);
        }
    }

    public ZoomMouseListener getZoomMouseListener() { return zoomMouseListener; }
    
    public class ZoomMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel lSource = (JLabel)e.getSource();
            ZoomLevel zl = ZoomLevel.SECTOR;
            if(lSource.getName().equals("GROUND")) {
                zl = ZoomLevel.GROUND;
            } else if(lSource.getName().equals("CLOSE")) {
                zl = ZoomLevel.CLOSE;
                
            } else if(lSource.getName().equals("SECTOR")) {
                zl = ZoomLevel.SECTOR;
            } 
            if(e.getButton()==1) {
                setFilter(zl);
            } else if(e.getButton()==2) {
                backend.defineZoomLevel(zl);
            }

            RadarPanel parent = (RadarPanel)lSource.getParent().getParent();
            parent.resetFilters();
            parent.selectFilter(lSource);
        }
    }
}
