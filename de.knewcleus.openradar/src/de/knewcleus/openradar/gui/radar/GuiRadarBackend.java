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
package de.knewcleus.openradar.gui.radar;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.GeoUtil;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.IRadarDataProvider;
import de.knewcleus.openradar.radardata.IRadarDataRecipient;
import de.knewcleus.openradar.rpvd.RadarMapViewerAdapter;
import de.knewcleus.openradar.view.IRadarViewChangeListener;

/**
 * This class is a convenient wrapper in front of the RadarScreen details (viewerAdapter).
 *
 * @author Wolfram Wagner
 *
 */
public class GuiRadarBackend implements IRadarDataRecipient {

    private final GuiMasterController master;
    private RadarPanel radarPanel;
    private volatile RadarMapViewerAdapter viewerAdapter;

    private Map<String, ZoomLevel> zoomLevelMap = new HashMap<String,ZoomLevel>();

    private ZoomLevel zoomLevel;

    private final static Logger log = LogManager.getLogger(GuiRadarBackend.class);
    
    public GuiRadarBackend(GuiMasterController master) {
        this.master=master;
        zoomLevelMap.put("GROUND",new ZoomLevel("GROUND",14,master.getAirportData().getAirportPosition()));
        zoomLevelMap.put("TOWER",new ZoomLevel("TOWER",26,master.getAirportData().getAirportPosition()));
        zoomLevelMap.put("APP",new ZoomLevel("APP",100,master.getAirportData().getAirportPosition()));
        zoomLevelMap.put("SECTOR",new ZoomLevel("SECTOR",300,master.getAirportData().getAirportPosition()));
    }

    public void setPanel(RadarPanel radarPanel) {
        this.radarPanel=radarPanel;

    }

    public void setViewerAdapter(RadarMapViewerAdapter viewerAdapter) {
        this.viewerAdapter=viewerAdapter;
    }

    public void start() {
        validateToggles();
        addRadarViewListener(master.getMpChatManager()); // forwards Zoom and center changes to MPChat
    }

    public void acceptRadarData(IRadarDataProvider provider, IRadarDataPacket radarData) {
        // TODO Auto-generated method stub

    }

    public ZoomLevel getZoomLevel() {
        return zoomLevel;
    }

    public void setZoomLevel(String zoomLevelKey) {
        if(zoomLevelMap.containsKey(zoomLevelKey)) {
            this.zoomLevel=zoomLevelMap.get(zoomLevelKey);
            radarPanel.selectFilter(zoomLevelKey);
            viewerAdapter.setZoom(zoomLevel.getLogicalScale(), zoomLevel.getCenter());
        } else {
            // parsing of airport data delivered tower position, move center
            Point2D center = new Point2D.Double(master.getAirportData().getLon(), master.getAirportData().getLat());

            viewerAdapter.setLogicalScale(100.0,center); // initial case
        }
    }

    public void defineZoomLevel(String zoomLevelKey) {
        if(zoomLevelMap.containsKey(zoomLevelKey)) {
            ZoomLevel zl =zoomLevelMap.get(zoomLevelKey);
            zl.setLogicalScale(viewerAdapter.getLogicalScale());
            zl.setCenter(viewerAdapter.getCenter());
        }
        master.getAirportData().storeAirportData(master); // persist settings
    }

    public boolean isContactInRange(GuiRadarContact contact) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isContactVisible(GuiRadarContact contact) {
        if(contact==null) return false;
        return viewerAdapter.getViewerExtents().contains(contact.getCenterViewCoordinates());
    }

    public void repaint() {
        viewerAdapter.getUpdateManager().markViewportDirty();
        //viewerAdapter.setLogicalScale(viewerAdapter.getLogicalScale());
    }
    public void forceRepaint() {
        viewerAdapter.forceRepaint();
    }
    /**
     * This class is prepared for flexible user defined ZoomLevels
     *
     * @author wolfram
     *
     */
    public class ZoomLevel {
        private final String name;
        private volatile double logicalScale;
        private volatile Point2D center;

        public ZoomLevel(String name, double logicalScale, Point2D center) {
            this.name = name;
            this.logicalScale = logicalScale;
            this.center = center;
        }

        public double getLogicalScale() {
            return logicalScale;
        }
        public void setLogicalScale(double logicalScale) {
            this.logicalScale = logicalScale;
        }
        public Point2D getCenter() {
            return center;
        }
        public void setCenter(Point2D center) {
            this.center = center;
        }
        public String getName() {
            return name;
        }
    }

    public void showRadarContact(GuiRadarContact c, boolean changeZoom) {
        if(changeZoom) {
            Point2D apPos = master.getAirportData().getAirportPosition();
            Point2D plPos = c.getCenterGeoCoordinates();;

            double distance = GeoUtil.getDistance(apPos.getX(), apPos.getY(), plPos.getX(), plPos.getY()).length / Units.NM;
            double scale = distance>0 ? distance * 3.5 : 1;
            viewerAdapter.setLogicalScale(scale);
            viewerAdapter.setGeoCenter(new Point2D.Double(apPos.getX()+(plPos.getX()-apPos.getX())/2,apPos.getY()+(plPos.getY()-apPos.getY())/2));
//            viewerAdapter.setGeoCenter(c.getCenterGeoCoordinates());
        } else {
            viewerAdapter.setGeoCenter(c.getCenterGeoCoordinates());
        }
//        setZoomLevel("SECTOR");
    }

    public void addZoomLevelValuesToProperties(Properties p) {
        for(ZoomLevel zl : zoomLevelMap.values()) {
            String name = zl.getName();
            Point2D center = zl.getCenter();
            double scale = zl.getLogicalScale();
            p.setProperty("zoomlevel."+name+".center.x", Double.toString(center.getX()));
            p.setProperty("zoomlevel."+name+".center.y", Double.toString(center.getY()));
            p.setProperty("zoomlevel."+name+".scale", Double.toString(scale));
        }
    }

    public void setZoomLevelValuesFromProperties(Properties p) {
        for(ZoomLevel zl : zoomLevelMap.values()) {
            String name = zl.getName();
            try {
                double x = Double.parseDouble(p.getProperty("zoomlevel."+name+".center.x"));
                double y = Double.parseDouble(p.getProperty("zoomlevel."+name+".center.y"));
                double scale = Double.parseDouble(p.getProperty("zoomlevel."+name+".scale"));
                zl.setCenter(new Point2D.Double(x, y));
                zl.setLogicalScale(scale);
            } catch (Exception e) {
                log.error("Error while reading zoomlevels from properties file!",e);
            }
        }
    }

    public void toggleRadarObjectFilter(String objectName) {
        master.getAirportData().setRadarObjectFilter(master, objectName);
        forceRepaint();
    }

    public boolean getRadarObjectFilterState(String objectName) {
        return master.getAirportData().getRadarObjectFilterState(objectName);
    }

    public boolean getRadarObjectFilterState(String objectName, boolean defaultValue) {
        return master.getAirportData().getToggleState(objectName,defaultValue);
    }

    public void validateToggles() {
        radarPanel.validateToggles();
    }

    public void addRadarViewListener(IRadarViewChangeListener  l) {
        viewerAdapter.addRadarViewChangeListener(l);
    }

    public void showGeoRectangle(Rectangle2D bounds) {

        double distance = GeoUtil.getDistance(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY()).length / Units.NM;
        if(distance>0) {
            double scale = distance>0 ? distance * 2.5 : 1;

            viewerAdapter.setLogicalScale(scale);
        }
        viewerAdapter.setGeoCenter(new Point2D.Double(bounds.getCenterX(),bounds.getCenterY()));
    }

    public void reloadStandardRoutes() {
        radarPanel.getRadarMapPanel().reReadStandardRoutes(master);
    }

    public void copyMouseLocationToClipboard() {
        Point p = radarPanel.getMousePosition();
        if(p!=null) {
            Point2D geoPoint = viewerAdapter.getGeoLocationOf(p);
            StringSelection selection = new StringSelection(String.format("%1.6f,%1.6f",geoPoint.getY(),geoPoint.getX()));
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }

    }

    public void copyZoomLevelToClipboard() {
        StringSelection selection = new StringSelection(String.format("%1.1f",viewerAdapter.getLogicalScale()));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

}
