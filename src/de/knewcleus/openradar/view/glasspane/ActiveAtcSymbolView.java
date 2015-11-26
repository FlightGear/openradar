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
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.glasspane;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

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

public class ActiveAtcSymbolView implements IBoundedView, INotificationListener {

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


    public ActiveAtcSymbolView(IMapViewerAdapter mapViewAdapter, GuiMasterController master) {
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
        //constructBackgroundShapes();
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
                g2d.setColor(new Color(0,230,0));
                
                // symbol
                double dotRangeRadius = 5;
                double x1 = Converter2D.getMapDisplayPoint(currentPosition, 270d, dotRangeRadius).getX();
                double y1 = Converter2D.getMapDisplayPoint(currentPosition, 0d, 0.8*dotRangeRadius).getY();
                double x2 = Converter2D.getMapDisplayPoint(currentPosition, 90d, dotRangeRadius).getX();
                double y2 = Converter2D.getMapDisplayPoint(currentPosition, 180d, 0.8*dotRangeRadius).getY();

                g2d.fill(new Ellipse2D.Double(x1, y1, x2-x1, y2-y1));
                
                // text
                String text = atc.callSign;
                Rectangle2D bounds = fontMetrics.getStringBounds(text, g2d);
                AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.80f);
                Composite formerComposite = g2d.getComposite();
                g2d.setComposite(ac);
                g2d.setColor(Palette.LANDMASS);
                g2d.fill(new RoundRectangle2D.Double(currentPosition.getX()+8,currentPosition.getY()-20-bounds.getHeight(), bounds.getWidth()+6,bounds.getHeight()+12,5d,5d));
                g2d.setComposite(formerComposite);
                g2d.setColor(new Color(0,230,0));                
                g2d.drawString(atc.callSign,(float)(currentPosition.getX()+10),(float)(currentPosition.getY()-20));
                g2d.drawString(atc.frequency,(float)(currentPosition.getX()+10),(float)(currentPosition.getY()-10));
                if(atc.distance>100) {
                    // outside range //
                    Point2D ownGeo =  mapViewAdapter.getLogicalToDeviceTransform().transform(mapViewAdapter.getProjection().toLogical(master.getAirportData().getAirportPosition()),null);
                    Point2D atcGeo = mapViewAdapter.getLogicalToDeviceTransform().transform(mapViewAdapter.getProjection().toLogical(atc.geoPosition),null);
                    Point2D ownPosition = mapViewAdapter.getLogicalToDeviceTransform().transform(ownGeo,null);
                    double heading = Converter2D.getDirection(ownGeo, atcGeo);
                    Point2D currentPosition2 = Converter2D.getMapDisplayPoint(ownPosition, heading, Converter2D.getFeetToDots(108*Units.NM/Units.FT, mapViewAdapter));
                    g2d.setColor(Palette.LANDMASS);
                    g2d.fill(new RoundRectangle2D.Double(currentPosition2.getX()-bounds.getWidth()/2-2,currentPosition2.getY()-bounds.getHeight()/2-10, bounds.getWidth()+6,bounds.getHeight()+12,5d,5d));
                    g2d.setComposite(formerComposite);
                    g2d.setColor(new Color(0,230,0));         
                    String line1 = atc.callSign+String.format(" %03.0f°", heading);
                    String line2 = atc.frequency+ String.format(" +%01.0fNM", atc.distance-100);
                    g2d.drawString(line1,(float)(currentPosition2.getX()-bounds.getWidth()/2),(float)(currentPosition2.getY()-10));
                    g2d.drawString(line2,(float)(currentPosition2.getX()-bounds.getWidth()/2),(float)(currentPosition2.getY()));
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
