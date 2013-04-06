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
package de.knewcleus.openradar.view.stdroutes;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.map.ProjectionNotification;

public class StdRouteView implements IBoundedView, INotificationListener {
    protected final IMapViewerAdapter mapViewAdapter;
    protected final GuiMasterController master;
    protected final StdRoute route;

    protected boolean visible = true;

    protected final Point2D geoPosition;
    protected Point2D logicalPosition = new Point2D.Double();
    protected Point2D displayPosition = new Point2D.Double();
    protected Path2D displayShape = null;
    protected Rectangle2D displayExtents;


	public StdRouteView(IMapViewerAdapter mapViewAdapter, StdRoute route, GuiMasterController master) {
		this.mapViewAdapter = mapViewAdapter;
		this.master=master;
		this.route=route;
		geoPosition = route.getSize()>0 ? route.getElements().get(0).getGeoReferencePoint() : master.getDataRegistry().getAirportPosition();
		mapViewAdapter.registerListener(this);
        updateLogicalPosition();
	}

    @Override
    public Rectangle2D getDisplayExtents() {
        if(displayExtents == null || mapViewAdapter.getViewerExtents().getHeight()>0) {
            displayExtents = mapViewAdapter.getViewerExtents();
        }
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

        boolean isVisible = route.isVisible(master.getDataRegistry()) && master.getDataRegistry().getRadarObjectFilterState("STARSID")==true;
        if(isVisible && mapViewAdapter.getLogicalScale()>10) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("Arial", Font.PLAIN, 4));

            displayExtents = null;
            Stroke origStroke = g2d.getStroke();
            Font origFont = g2d.getFont();
            for(AStdRouteElement e : route.getElements()) {
                g2d.setStroke(route.getStroke());
                g2d.setColor(route.getColor());
                if(displayExtents==null) {
                    displayExtents = e.paint(g2d, mapViewAdapter);
                } else {
                    Rectangle2D.union(displayExtents, e.paint(g2d, mapViewAdapter), displayExtents);
                }
            }
            g2d.setFont(origFont);
            g2d.setStroke(origStroke);
        }
    }

    @Override
    public void validate() {
    }

    @Override
    public void acceptNotification(INotification notification) {
        if (notification instanceof ProjectionNotification) {
            updateLogicalPosition();
        }
        if (notification instanceof CoordinateSystemNotification) {
            updateDisplayPosition();
        }
    }

    protected void updateLogicalPosition() {
        updateDisplayPosition();
    }

    protected void updateDisplayPosition() {
        final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
        displayPosition = logical2display.transform(logicalPosition, null);
    }
}
