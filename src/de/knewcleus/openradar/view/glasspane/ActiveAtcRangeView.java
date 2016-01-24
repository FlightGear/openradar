/**
 * Copyright (C) 2013,2015 Wolfram Wagner
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.glasspane;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.flightplan.FpAtc;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

/**
 * This class is a top layer on the radar screen and is used to display textual information
 * on the screen.
 *
 * @author wolfram
 */

public class ActiveAtcRangeView implements IBoundedView, INotificationListener {

    protected final IMapViewerAdapter mapViewAdapter;
    private final GuiMasterController master;

    protected boolean visible = true;

    protected Point2D logicalPosition = new Point2D.Double();
    protected Point2D displayPosition = new Point2D.Double();
    protected Path2D displayShape = null;
    protected Rectangle2D displayExtents = new Rectangle2D.Double();

    private Point2D currentPosition;
    Path2D path = new Path2D.Double();

    protected volatile Path2D textContainer = new Path2D.Double();
    protected volatile Point2D textOrigin = new Point2D.Double();
    protected volatile FontMetrics fontMetrics = null;
    protected volatile Rectangle2D lastTextDisplayExtents = new Rectangle2D.Double();


    public ActiveAtcRangeView(IMapViewerAdapter mapViewAdapter, GuiMasterController master) {
        this.mapViewAdapter = mapViewAdapter;
        this.master = master;
        displayExtents=mapViewAdapter.getViewerExtents();
        mapViewAdapter.registerListener(this);
    }

    @Override
    public Rectangle2D getDisplayExtents() {
        return mapViewAdapter.getViewerExtents();
    }

    @Override
    public void accept(IViewVisitor visitor) {
        visitor.visitView(this);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
    }

    @Override
    public void paint(Graphics2D g2d) {

        // construct the objects at their new place
        displayExtents=mapViewAdapter.getViewerExtents();

        if(master.getAirportData().getRadarObjectFilterState("STP")) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            if(fontMetrics==null) {
                fontMetrics = g2d.getFontMetrics();
            }
            
            
            path = new Path2D.Double();
            for(FpAtc atc : master.getRadarContactManager().getActiveAtcs()) {
                
                currentPosition = mapViewAdapter.getLogicalToDeviceTransform().transform(mapViewAdapter.getProjection().toLogical(atc.geoPosition),null);
                
                // range
                g2d.setColor(Palette.ATC_RANGE);
                if(atc.distance>100) {
                    /* Thanks to Michael for helping out in a tired moment...
                     
                        If I am at A1 with radius r1, and ATC2 is at A2 with radius r2, and 
                        D = distance(A1, A2), the general 2-point circle intersection case (K1
                        and K2) gives us that line K1K2 intersects A1A2 at a point K located
                        at distance d1 from A1:
                           d1 = (D^2 + r1^2 - r2^2) / (2 * D)
                        and distance d2 from A2:
                           d2 = (D^2 + r2^2 - r1^2) / (2 * D)
                        The arc you want is:
                           acos(d2 / r2)
                        
                        If r1 = r2 = 100 NM, then your are right:
                           angle = acos(D / 200 NM)
                     */
                    
                    Point2D ownPos =  mapViewAdapter.getLogicalToDeviceTransform().transform(mapViewAdapter.getProjection().toLogical(master.getAirportData().getAirportPosition()),null);
                    Point2D atcPos = currentPosition;

                    double d = Math.sqrt(Math.pow(ownPos.getX()-atcPos.getX(),2)+Math.pow(ownPos.getY()-atcPos.getY(),2)); 
                    double r1 = Converter2D.getFeetToDots(100*Units.NM/Units.FT, mapViewAdapter);
                    double r2 = Converter2D.getFeetToDots(95*Units.NM/Units.FT, mapViewAdapter);
                    double d2 = (Math.pow(d,2) + Math.pow(r2,2) - Math.pow(r1,2)) / (2 * d);
                    
                    double angle = Math.toDegrees(Math.acos(d2/r2))+10;
                    double heading = 90-Converter2D.getDirection(atcPos,ownPos);
                    
                    g2d.draw(new Arc2D.Double(currentPosition.getX()-r2, currentPosition.getY()-r2, 2*r2, 2*r2, heading-angle, 2 * angle,Arc2D.OPEN));

                }                
            }
            g2d.draw(path);
            displayExtents= path.getBounds2D();
        }
    }

    @Override
    public void validate() {
    }

    @Override
    public void acceptNotification(INotification notification) {
    }

    @Override
    public String getTooltipText(Point p) {
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent p) {  }
}
