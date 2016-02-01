/**
 * Copyright (C) 2013-2016 Wolfram Wagner
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
package de.knewcleus.openradar.view.stdroutes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.groundnet.ISelectable;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;

public class StdRouteView implements IBoundedView, INotificationListener,ISelectable {
    protected final IMapViewerAdapter mapViewAdapter;
    protected final GuiMasterController master;
    protected final StdRoute route;

    protected boolean visible = true;

    protected final Point2D geoPosition;
    protected Point2D logicalPosition = new Point2D.Double();
    protected Point2D displayPosition = new Point2D.Double();
    protected Path2D displayShape = null;
    protected Rectangle2D displayExtents;
    protected final StdRouteAttributes attributes;


	public StdRouteView(IMapViewerAdapter mapViewAdapter, StdRoute route, GuiMasterController master) {
		this.mapViewAdapter = mapViewAdapter;
		this.master=master;
		this.route=route;
		geoPosition = route.getSize()>0 ? route.getElements().get(0).getGeoReferencePoint() : master.getAirportData().getAirportPosition();
        StdRouteAttributes defaultAttributes = new StdRouteAttributes ();
		this.attributes = new StdRouteAttributes (defaultAttributes, "line,1", "255,255,255","Arial","4");
		mapViewAdapter.registerListener(this);
        updateLogicalPosition();
	}

	public synchronized void destroy() {
		mapViewAdapter.unregisterListener(this);
	}

	@Override
    public synchronized Rectangle2D getDisplayExtents() {
        if(displayExtents == null || mapViewAdapter.getViewerExtents().getHeight()>0) {
            displayExtents = mapViewAdapter.getViewerExtents();
        }
        return displayExtents;
    }

    @Override
    public synchronized void accept(IViewVisitor visitor) {
        visitor.visitView(this);
    }

    @Override
    public synchronized boolean isVisible() {
        return visible;
    }

    public synchronized void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
    }

    @Override
    public synchronized void paint(Graphics2D g2d) {
    	GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();
        boolean isVisible = route.isVisible(master,selectedContact);
        if(isVisible) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            this.attributes.applyAttributes(g2d, false);

            Boolean selected = route.isRouteAssigned(master);

            displayExtents = null;
            for(AStdRouteElement e : route.getElements()) {
                if(displayExtents==null) {
                    displayExtents = e.doPaint(g2d, mapViewAdapter, selected);
                } else {
                    Rectangle2D.union(displayExtents, e.doPaint(g2d, mapViewAdapter, selected), displayExtents);
                }
            }
            this.attributes.restoreAttributes(g2d);

        }
    }

    @Override
    public synchronized void validate() {
        mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
    }

    @Override
    public synchronized void acceptNotification(INotification notification) {
        if (notification instanceof ProjectionNotification) {
            updateLogicalPosition();
        }
        if (notification instanceof CoordinateSystemNotification) {
            updateDisplayPosition();
        }
    }

    protected synchronized void updateLogicalPosition() {
        final IProjection projection = mapViewAdapter.getProjection();
        logicalPosition = projection.toLogical(geoPosition);
        updateDisplayPosition();
    }

    protected synchronized void updateDisplayPosition() {
        final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
        displayPosition = logical2display.transform(logicalPosition, null);
        if(displayExtents!=null) {
            mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
        }
    }

    @Override
    public String getTooltipText(Point p) {
        return null;
    }

    @Override
    public boolean isSelected() {
        return master.getRadarContactManager().isRouteAssigned(route.getName());
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    	GuiRadarContact selectedContact = master.getRadarContactManager().getSelectedContact();
        if(e.getClickCount()==1 && route.isVisible(master,selectedContact)) {
            for(AStdRouteElement re : route.getElements()) {
                if(re.contains(e.getPoint()) && re.isClickable() ) {
                    master.getRadarContactManager().assignRoute(route.getName());
                    //route.setSelected(!route.isSelected());
                    e.consume();
                    break;
                }
            }
        }
    }
}
