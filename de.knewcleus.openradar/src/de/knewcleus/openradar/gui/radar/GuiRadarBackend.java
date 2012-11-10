package de.knewcleus.openradar.gui.radar;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.IRadarDataProvider;
import de.knewcleus.openradar.radardata.IRadarDataRecipient;
import de.knewcleus.openradar.view.IViewerAdapter;

/**
 * This class is a convenient wrapper in front of the RadarScreen details (viewerAdapter).
 * 
 * @author Wolfram Wagner
 *
 */
public class GuiRadarBackend implements IRadarDataRecipient {

    private IViewerAdapter viewerAdapter = null;

    public enum ZoomLevel { GROUND, CLOSE, SECTOR };
    
    private ZoomLevel zoomLevel = ZoomLevel.CLOSE;
    
    private double logicalScaleGround = 10.0;
    private double logicalScaleClose=  30.0;
    private double logicalScaleSector = 100.0;
    
    public void setViewerAdapter(IViewerAdapter viewerAdapter) {
        this.viewerAdapter=viewerAdapter;
        setZoomLevel(ZoomLevel.CLOSE);
    }
    
    public void acceptRadarData(IRadarDataProvider provider, IRadarDataPacket radarData) {
        // TODO Auto-generated method stub

    }

    public ZoomLevel getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(ZoomLevel zoomLevel) {
        this.zoomLevel=zoomLevel;
        switch(zoomLevel) {
        case GROUND:
            viewerAdapter.setLogicalScale(logicalScaleGround);
            break;
        case CLOSE:
            viewerAdapter.setLogicalScale(logicalScaleClose);
            break;
        case SECTOR:
            viewerAdapter.setLogicalScale(logicalScaleSector);
        }
    }

    public void defineZoomLevel(ZoomLevel zoomLevel) {
        switch(zoomLevel) {
        case GROUND:
            logicalScaleGround = viewerAdapter.getLogicalScale();
            break;
        case CLOSE:
            logicalScaleClose = viewerAdapter.getLogicalScale();
            break;
        case SECTOR:
            logicalScaleSector = viewerAdapter.getLogicalScale();
        }
    }
    
    public boolean isContactInRange(GuiRadarContact contact) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isContactVisible(GuiRadarContact contact) {
        return viewerAdapter.getViewerExtents().contains(contact.getCenterViewCoordinates());
    }

    public void repaint() {
        viewerAdapter.setLogicalScale(viewerAdapter.getLogicalScale());
    }
}
