/**
 * Copyright (C) 2008-2009 Ralf Gerlich
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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.rpvd;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.ViewerAdapter;

public class ScaleMarkerView implements IBoundedView, INotificationListener {
	protected final ViewerAdapter viewerAdapter;
	protected final Side side;

	protected Color color;
	protected boolean visible = true;

	protected final static double scaleMarkerDisplayHeight = 10.0;
	protected final static double scaleMarkerDisplayWidth = 0.5 * scaleMarkerDisplayHeight;
	protected double scaleMarkerLogicalDistance = 10.0 * Units.NM;

	protected Rectangle2D displayExtents = new Rectangle2D.Double();

	public enum Side {
		NORTH, SOUTH, EAST, WEST;
	}

	public ScaleMarkerView(ViewerAdapter viewerAdapter, Side side, Color color) {
		this.viewerAdapter = viewerAdapter;
		this.side = side;
		this.color = color;

		viewerAdapter.registerListener(this);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		viewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
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
		viewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}

	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}

	protected void updateDisplayExtents() {
		/* Ensure that the formerly occupied region is repainted */
		viewerAdapter.getUpdateManager().markRegionDirty(displayExtents);

		final Rectangle2D viewerExtents = viewerAdapter.getViewerExtents();
		switch (side) {
		case NORTH:
			displayExtents = new Rectangle2D.Double(
					viewerExtents.getMinX(), viewerExtents.getMinY(),
					viewerExtents.getWidth(), scaleMarkerDisplayHeight);
			break;
		case SOUTH:
			displayExtents = new Rectangle2D.Double(
					viewerExtents.getMinX(), viewerExtents.getMaxY()-scaleMarkerDisplayHeight,
					viewerExtents.getWidth(), scaleMarkerDisplayHeight);
			break;
		case EAST:
			displayExtents = new Rectangle2D.Double(
					viewerExtents.getMaxX()-scaleMarkerDisplayHeight, viewerExtents.getMinY(),
					scaleMarkerDisplayHeight, viewerExtents.getHeight());
			break;
		case WEST:
			displayExtents = new Rectangle2D.Double(
					viewerExtents.getMinX(), viewerExtents.getMinY(),
					scaleMarkerDisplayHeight, viewerExtents.getHeight());
			break;
		}
		viewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}

	@Override
	public void validate() {}

	@Override
	public void paint(Graphics2D g2d) {
		final Rectangle2D deviceClipBounds = g2d.getClipBounds();

		g2d.setColor(Palette.WINDOW_BLUE);

		switch (side) {
		case NORTH:
			paintHorizontalMarkers(
					g2d,
					deviceClipBounds.getMinX(), deviceClipBounds.getMaxX(),
					displayExtents.getMaxY(), displayExtents.getMinY());
			break;
		case SOUTH:
			paintHorizontalMarkers(
					g2d,
					deviceClipBounds.getMinX(), deviceClipBounds.getMaxX(),
					displayExtents.getMinY(), displayExtents.getMaxY());
			break;
		case EAST:
			paintVerticalMarkers(
					g2d,
					deviceClipBounds.getMinY(), deviceClipBounds.getMaxY(),
					displayExtents.getMinX(), displayExtents.getMaxX());
			break;
		case WEST:
			paintVerticalMarkers(
					g2d,
					deviceClipBounds.getMinY(), deviceClipBounds.getMaxY(),
					displayExtents.getMaxX(), displayExtents.getMinX());
			break;
		}
	}

	protected void paintHorizontalMarkers(Graphics2D g2d, double minX, double maxX, double headY, double baseY) {
		final Point2D deviceOrigin = viewerAdapter.getDeviceOrigin();
		final Point2D logicalOrigin = viewerAdapter.getLogicalOrigin();
		final double scale = viewerAdapter.getLogicalScale();
		final double minLogicalX = (minX - deviceOrigin.getX()) * scale + logicalOrigin.getX();
		final double maxLogicalX = (maxX - deviceOrigin.getX()) * scale + logicalOrigin.getX();
		final double minMarkerX = Math.floor(minLogicalX/scaleMarkerLogicalDistance) * scaleMarkerLogicalDistance;
		final double maxMarkerX = Math.ceil(maxLogicalX/scaleMarkerLogicalDistance) * scaleMarkerLogicalDistance;

		for (double x = minMarkerX; x < maxMarkerX; x += scaleMarkerLogicalDistance) {
			final double deviceX = (x - logicalOrigin.getX())/scale + deviceOrigin.getX();
			final Path2D marker = new Path2D.Double();
			marker.moveTo(deviceX, headY);
			marker.lineTo(deviceX+scaleMarkerDisplayWidth/2.0, baseY);
			marker.lineTo(deviceX-scaleMarkerDisplayWidth/2.0, baseY);
			marker.closePath();

			g2d.fill(marker);
		}
	}

	protected void paintVerticalMarkers(Graphics2D g2d, double minY, double maxY, double headX, double baseX) {
		final Point2D deviceOrigin = viewerAdapter.getDeviceOrigin();
		final Point2D logicalOrigin = viewerAdapter.getLogicalOrigin();
		final double scale = viewerAdapter.getLogicalScale();
		final double minLogicalY = logicalOrigin.getY() - (maxY - deviceOrigin.getY()) * scale;
		final double maxLogicalY = logicalOrigin.getY() - (minY - deviceOrigin.getY()) * scale;
		final double minMarkerY = Math.floor(minLogicalY/scaleMarkerLogicalDistance) * scaleMarkerLogicalDistance;
		final double maxMarkerY = Math.ceil(maxLogicalY/scaleMarkerLogicalDistance) * scaleMarkerLogicalDistance;

		for (double y = minMarkerY; y < maxMarkerY; y += scaleMarkerLogicalDistance) {
			final double deviceY = deviceOrigin.getY() - (y - logicalOrigin.getY())/scale;
			final Path2D marker = new Path2D.Double();
			marker.moveTo(headX, deviceY);
			marker.lineTo(baseX, deviceY-scaleMarkerDisplayWidth/2.0);
			marker.lineTo(baseX, deviceY+scaleMarkerDisplayWidth/2.0);
			marker.closePath();

			g2d.fill(marker);
		}
	}

	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			updateDisplayExtents();
		}
	}

    @Override
    public String getTooltipText(Point p) {
        // TODO Automatisch generierter Methodenstub
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent p) {  }
}
