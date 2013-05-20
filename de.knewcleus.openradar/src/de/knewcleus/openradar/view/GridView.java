/**
 * Copyright (C) 2008-2009 Ralf Gerlich
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
package de.knewcleus.openradar.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;

public class GridView implements IView, INotificationListener {
	protected final IViewerAdapter viewAdapter;
	protected double gridLogicalSize;
	protected boolean visible = true;

	public GridView(IViewerAdapter viewAdapter, double gridLogicalSize) {
		super();
		this.viewAdapter = viewAdapter;
		this.gridLogicalSize = gridLogicalSize;
		viewAdapter.registerListener(this);
	}

	public double getGridLogicalSize() {
		return gridLogicalSize;
	}

	public void setGridLogicalSize(double gridLogicalSize) {
		this.gridLogicalSize = gridLogicalSize;
		viewAdapter.getUpdateManager().markViewportDirty();
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
		if (visible==this.visible) {
			return;
		}
		this.visible = visible;
		viewAdapter.getUpdateManager().markViewportDirty();
	}

	@Override
	public void paint(Graphics2D g2d) {
		final AffineTransform oldTransform = g2d.getTransform();
		g2d.transform(viewAdapter.getLogicalToDeviceTransform());

		final Rectangle2D clipBounds = g2d.getClipBounds();
		final double minX, maxX, minY, maxY;

		minX=Math.floor(clipBounds.getMinX()/gridLogicalSize)*gridLogicalSize;
		minY=Math.floor(clipBounds.getMinY()/gridLogicalSize)*gridLogicalSize;
		maxX=Math.ceil(clipBounds.getMaxX()/gridLogicalSize)*gridLogicalSize;
		maxY=Math.ceil(clipBounds.getMaxY()/gridLogicalSize)*gridLogicalSize;

		g2d.setColor(Color.BLACK);
		for (double x=minX; x<=maxX; x+=gridLogicalSize) {
			Line2D line=new Line2D.Double(x,minY,x,maxY);
			g2d.draw(line);
		}

		for (double y=minY; y<=maxY; y+=gridLogicalSize) {
			Line2D line=new Line2D.Double(minX,y,maxX,y);
			g2d.draw(line);
		}

		g2d.setTransform(oldTransform);
	}

	@Override
	public void validate() {}

	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			/* When the logical coordinate system has changed, update the view */
			viewAdapter.getUpdateManager().markViewportDirty();
		}
	}

    @Override
    public String getTooltipText(Point p) {
        return null;
    }
}
