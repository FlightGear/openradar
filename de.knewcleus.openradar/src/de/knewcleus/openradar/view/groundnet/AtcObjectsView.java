/**
 * Copyright (C) 2012,2015 Wolfram Wagner
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
package de.knewcleus.openradar.view.groundnet;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
import de.knewcleus.openradar.view.map.IProjection;
import de.knewcleus.openradar.view.map.ProjectionNotification;
import de.knewcleus.openradar.view.painter.AViewObjectPainter;

public class AtcObjectsView implements IBoundedView, INotificationListener {
    protected final IMapViewerAdapter mapViewAdapter;
    protected final GuiMasterController master;
    protected final AirportData data;

    protected boolean visible = true;

    protected Point2D logicalPosition = new Point2D.Double();
    protected Point2D displayPosition = new Point2D.Double();
    protected Path2D displayShape = null;

    protected AViewObjectPainter<?> viewObjectPainter;
    protected Rectangle2D displayExtents;

	public AtcObjectsView(IMapViewerAdapter mapViewAdapter, GuiMasterController master) {
		this.mapViewAdapter = mapViewAdapter;
		this.master=master;
		this.data = master.getAirportData();
		mapViewAdapter.registerListener(this);
        // factory method
        viewObjectPainter = AViewObjectPainter.getPainterForNavpoint(mapViewAdapter, master, data);
        updateLogicalPosition();
	}
    @Override
    public synchronized Rectangle2D getDisplayExtents() {
        return viewObjectPainter.getDisplayExtents();
    }

    @Override
    public void accept(IViewVisitor visitor) {
        visitor.visitView(this);
    }

    @Override
    public synchronized boolean isVisible() {
        return visible;
    }

    public synchronized  void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        mapViewAdapter.getUpdateManager().markRegionDirty(viewObjectPainter.getDisplayExtents()/*displayExtents*/);
    }

    @Override
    public synchronized  void paint(Graphics2D g2d) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("Arial", Font.PLAIN, 4));
    
            viewObjectPainter.paint(g2d);
    }

    @Override
    public synchronized  void validate() {
        mapViewAdapter.getUpdateManager().markRegionDirty(viewObjectPainter.getDisplayExtents()/*displayExtents*/);
    }

    @Override
    public synchronized  void acceptNotification(INotification notification) {
            if (notification instanceof ProjectionNotification) {
                updateLogicalPosition();
            } else if (notification instanceof CoordinateSystemNotification) {
                updateDisplayPosition();
            }
    }

    protected synchronized void updateLogicalPosition() {
        final IProjection projection = mapViewAdapter.getProjection();
        logicalPosition = projection.toLogical(data.getAirportPosition());
        updateDisplayPosition();
    }

    protected synchronized void updateDisplayPosition() {
        final AffineTransform logical2display = mapViewAdapter.getLogicalToDeviceTransform();
        displayPosition = logical2display.transform(logicalPosition, null);
        viewObjectPainter.updateDisplayPosition(displayPosition);
    }

    @Override
    public String getTooltipText(Point p) {
        return viewObjectPainter.isPickable() ? viewObjectPainter.getTooltipText(p) : null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        viewObjectPainter.mouseClicked(e);
    }
}
