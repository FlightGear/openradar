/**
 * Copyright (C) 2013 Wolfram Wagner
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import de.knewcleus.fgfs.location.Vector2D;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.weather.MetarData;

/**
 * This class is a top layer on the radar screen and is used to display textual information
 * on the screen.
 *
 * @author wolfram
 */

public class StPView implements IBoundedView, INotificationListener {

    protected final IMapViewerAdapter mapViewAdapter;
    private final GuiMasterController master;

    private Long degreesToPointer = null;
    private Long degreesToSelection = null;
    private Double distanceMiles = null;
    private Integer timeMinutes = null;
    private Integer timeSeconds = null;

    protected boolean visible = true;

    protected Point2D logicalPosition = new Point2D.Double();
    protected Point2D displayPosition = new Point2D.Double();
    protected Path2D displayShape = null;
    protected Rectangle2D displayExtents = new Rectangle2D.Double();

    private Point2D currentPosition;
    private volatile RoundRectangle2D background;

    private final static double SPACE = 2d;

    private Point2D anchor = null;
    private double newX ;
    private double newY ;
    private Rectangle2D boundsLine1 = null;
    private Rectangle2D boundsLine2 = null;
    private Rectangle2D boundsText = null;

    protected volatile Path2D textContainer = new Path2D.Double();
    protected volatile Point2D textOrigin = new Point2D.Double();
    protected volatile FontMetrics fontMetrics = null;
    protected volatile Rectangle2D lastTextDisplayExtents = new Rectangle2D.Double();


    public StPView(IMapViewerAdapter mapViewAdapter, GuiMasterController master) {
        this.mapViewAdapter = mapViewAdapter;
        this.master = master;
        master.getDataRegistry().setDirectionMessageView(this);

        mapViewAdapter.registerListener(this);
    }

    @Override
    public Rectangle2D getDisplayExtents() {
        return displayExtents;
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
        constructBackgroundShapes();
        displayExtents = background.getBounds2D();

        if(master.getDataRegistry().getRadarObjectFilterState("STP")) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            if(fontMetrics==null) {
                fontMetrics = g2d.getFontMetrics();
            }

            String dTP = degreesToPointer==null ? "n/a" : String.format("%03d",degreesToPointer);
            String dTS = degreesToSelection==null ? "n/a" : String.format("%03d",degreesToSelection);
            String dist = distanceMiles==null ? "n/a" : String.format("%.1f", distanceMiles);
            String min = timeMinutes==null ? "n/a" : String.format("%1d:%02d",timeMinutes,timeSeconds);;

            String textLine1 = String.format("%s° (%2s°)",dTP,dTS);
            String textLine2 = String.format("%1s NM, ETA %2s", dist,min);
            boundsLine1 = fontMetrics.getStringBounds(textLine1, g2d);
            boundsLine2 = fontMetrics.getStringBounds(textLine2, g2d);
            boundsText = new Rectangle2D.Double(boundsLine1.getX(), boundsLine1.getY()-boundsLine1.getHeight(), Math.max(boundsLine1.getWidth(), boundsLine2.getWidth()), boundsLine1.getHeight()+boundsLine2.getHeight());

            if(anchor==null) constructBackgroundShapes();

            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.60f);
            g2d.setComposite(ac);
            g2d.setColor(Palette.LANDMASS);
            g2d.fill(background);
            lastTextDisplayExtents = background.getBounds2D();

            ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1f);
            g2d.setComposite(ac);

            g2d.setColor(Color.white);

            g2d.drawString(textLine1,(float)(newX+SPACE),(float)(newY+boundsLine1.getHeight()));
            g2d.drawString(textLine2,(float)(newX+SPACE),(float)(newY+boundsText.getHeight()));
        }
    }

    private void constructBackgroundShapes() {
        if(boundsText==null) {
            // intial case, we don't know about the fontMetricts,so we construct a dummy
            background = new RoundRectangle2D.Double(currentPosition.getX()-34d,currentPosition.getY()-16d, 60+8, 24+8,10,10);
        } else {
            // construct the objects at their new place
            // first text line will start at (dx, dy)
            newX = currentPosition.getX()+SPACE;
            newY = currentPosition.getY()+boundsText.getHeight()-2*SPACE;

            background = new RoundRectangle2D.Double(newX-SPACE,newY-SPACE, boundsText.getWidth()+4*SPACE, boundsText.getHeight()+4*SPACE,10d,10d);
        }
    }

    @Override
    public void validate() {
    }

    @Override
    public void acceptNotification(INotification notification) {
    }

    public synchronized void updateMouseRadarMoved(GuiRadarContact contact, MouseEvent e) {
        currentPosition = e.getPoint();
        if(displayExtents.equals(new Rectangle2D.Double())) {
            // initially we need to paint everything
            displayExtents = mapViewAdapter.getViewerExtents();
        }
        //displayExtents = mapViewAdapter.getViewerExtents();

        double milesPerHour = contact.getAirSpeedD();
        Point2D currSelectionPoint = contact.getCenterViewCoordinates();

        double dx = e.getX()-currSelectionPoint.getX();
        double dy = currSelectionPoint.getY()-e.getY();
        Vector2D vDistance = new Vector2D(dx, dy);
        double distance = vDistance.getLength();
        Double angle = vDistance.getAngle();
        // angle corrections
        // 1. magnetic
        angle = angle + Math.round(master.getDataRegistry().getMagneticDeclination());
        // 2. wind
        MetarData metar = master.getAirportMetar();
        Vector2D vOriginalAngle = Vector2D.createScreenVector2D(angle,contact.getAirSpeedD());
        Vector2D vWind = Vector2D.createVector2D((double)90-metar.getWindDirectionI(),metar.getWindSpeed());
        Vector2D vResult = contact.getAirSpeedD()>1 ? vOriginalAngle.add(vWind) : vOriginalAngle;
        Long lAngle = vResult.getAngleL();

        degreesToPointer = lAngle!=null ? ( lAngle<0 ? lAngle+360 : lAngle) : null;
        degreesToSelection = lAngle!=null ? (degreesToPointer<180 ? degreesToPointer+180 : degreesToPointer-180) : null;
        // distances
        distanceMiles = distance*Converter2D.getMilesPerDot(mapViewAdapter);
        timeMinutes = milesPerHour>10 ? (int)Math.floor(60*distanceMiles/(double)milesPerHour) : null;
        timeSeconds = milesPerHour>10 ? (int)Math.floor(60*60*distanceMiles/(double)milesPerHour) - timeMinutes * 60 : null;

        //    System.out.println("orig "+angle+" vOA: "+vOriginalAngle.getAngleL()+" vW "+vWind.getAngleL()+" result "+lAngle);
        constructBackgroundShapes();
        Rectangle2D.union(displayExtents,background.getBounds2D(),displayExtents); // old and new position need to be painted
        mapViewAdapter.getUpdateManager().markViewportDirty();
    }

    @Override
    public String getTooltipText(Point p) {
        return null;
    }

}
