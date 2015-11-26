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

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class SwingUpdateManager extends AbstractUpdateManager {
	protected final JComponent managedComponent;
	protected boolean buffering = true;

	public SwingUpdateManager(JComponent managedComponent) {
		this.managedComponent = managedComponent;
	}

	@Override
	protected void scheduleRevalidation() {
		managedComponent.revalidate();
	}

	@Override
	public void markRegionDirty(Rectangle2D bounds) {
		managedComponent.repaint(bounds.getBounds());
	}

	@Override
	public void markViewportDirty() {
		managedComponent.repaint();
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		if (buffering) {
			performBufferedPaint(g2d);
		} else {
			super.paint(g2d);
		}
	}
	
	protected void performBufferedPaint(Graphics2D g2d) {
		if (rootView==null) {
			return;
		}
		final Rectangle clipBounds = g2d.getClipBounds();
		try {
			final BufferedImage bufferImage = new BufferedImage(
					clipBounds.width, clipBounds.height,
					BufferedImage.TYPE_INT_ARGB);
			
			final Graphics2D bufferedGraphics = bufferImage.createGraphics();
			try {
				bufferedGraphics.translate(-clipBounds.x, -clipBounds.y);
				bufferedGraphics.setBackground(g2d.getBackground());
				bufferedGraphics.setClip(
						clipBounds.x, clipBounds.y,
						clipBounds.width, clipBounds.height);
				bufferedGraphics.setComposite(g2d.getComposite());
				bufferedGraphics.setColor(g2d.getColor());
				bufferedGraphics.setFont(g2d.getFont());
				bufferedGraphics.setPaint(g2d.getPaint());
				bufferedGraphics.setStroke(g2d.getStroke());
				
				super.paint(bufferedGraphics);
				g2d.drawImage(bufferImage, null, clipBounds.x, clipBounds.y);
			} finally {
				bufferedGraphics.dispose();
			}
		} catch (OutOfMemoryError e) {
			/* We could not construct the buffer image, so we do an unbuffered repaint */
			super.paint(g2d);
		}
	}
}
