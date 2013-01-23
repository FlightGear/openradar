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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.JTextField;

import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.NavaidDB;
import de.knewcleus.openradar.view.IRadarViewChangeListener;

/**
 * The controller for the radar map.
 * 
 * @author Wolfram Wagner
 *
 */
public class RadarManager {

    private GuiMasterController master = null;
    private GuiRadarBackend backend = null;
    
    private ZoomMouseListener zoomMouseListener = new ZoomMouseListener();
    private ObjectFilterMouseListener objectFilterMouseListener = new ObjectFilterMouseListener();

    private NavaidSearchActionListener navaidSearchActionListener = new NavaidSearchActionListener();
    
    public RadarManager(GuiMasterController master, GuiRadarBackend backend) {
        this.master = master;
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
                } else {
                    backend.defineZoomLevel(zl);
                }
    
                RadarPanel parent = (RadarPanel)lSource.getParent().getParent().getParent();
                parent.selectFilter(lSource.getName());
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
                    || lSource.getName().equals("APT") || lSource.getName().equals("PPN") || lSource.getName().equals("GSH")) {
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

    public ActionListener getNavaidSearchActionListener() {
        return navaidSearchActionListener;
    }
    
    private class NavaidSearchActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            NavaidDB navaidDB = master.getDataRegistry().getNavaidDB();
            navaidDB.resetHighlighting();
            StringTokenizer st = new StringTokenizer(((JTextField)e.getSource()).getText()," ,.-;/");
            Point2D airportPos = master.getDataRegistry().getAirportPosition();
            Rectangle2D bounds = new Rectangle2D.Double(airportPos.getX(),airportPos.getY(),0,0);
            while(st.hasMoreTokens()) {
                IIntersection inters = navaidDB.highlight(st.nextToken().trim().toUpperCase());
                // determine bounds for setting center and zoom
                if(inters!=null) {
                    Rectangle2D rect = new Rectangle2D.Double(inters.getGeographicPosition().getX(),inters.getGeographicPosition().getY(),0,0);
                    Rectangle2D.union(bounds, rect, bounds);
                }                
            }
            backend.showGeoRectangle(bounds);
            
            backend.repaint();
        }
        
    }
}
