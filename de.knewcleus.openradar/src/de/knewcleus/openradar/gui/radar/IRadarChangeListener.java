package de.knewcleus.openradar.gui.radar;

import de.knewcleus.openradar.gui.radar.GuiRadarBackend.ZoomLevel;
/**
 * The interface for classes that want to listen to radar changes
 *  
 * @author Wolfram Wagner
 */
public interface IRadarChangeListener {

    public void radarZoomLevelChanged(ZoomLevel formerLevel, ZoomLevel newLevel);
}
