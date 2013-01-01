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
package de.knewcleus.openradar.view.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * A vertical flow layout manager positions the layout parts in a column with sides aligned.
 * 
 * @author Ralf Gerlich
 *
 */
public class VerticalFlowLayoutManager implements ILayoutManager {
	protected final ILayoutPartContainer container;
	
	protected Alignment alignment;
	protected double gap;
	
	protected Dimension2D minimumSize = null;
	protected Dimension2D preferredSize = null;
	
	public VerticalFlowLayoutManager(ILayoutPartContainer container,
			Alignment alignment, double gap) {
		this.container = container;
		this.alignment = alignment;
		this.gap = gap;
	}
	
	public VerticalFlowLayoutManager(ILayoutPartContainer container,
			Alignment alignment) {
		this(container, alignment, 0);
	}
	
	public VerticalFlowLayoutManager(ILayoutPartContainer container) {
		this(container, Alignment.LEADING, 0);
	}
	
	public Alignment getAlignment() {
		return alignment;
	}
	
	public double getGap() {
		return gap;
	}
	
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
		container.invalidate();
	}
	
	public void setGap(double gap) {
		this.gap = gap;
		container.invalidate();
	}

	@Override
	public Dimension2D getMinimumSize() {
		recalculateSizes();
		return minimumSize;
	}

	@Override
	public Dimension2D getPreferredSize() {
		recalculateSizes();
		return preferredSize;
	}

	@Override
	public void invalidate() {
		minimumSize = null;
		preferredSize = null;
	}
	
	protected void recalculateSizes() {
		if (minimumSize != null && preferredSize != null) {
			return;
		}
		final SizeCalculator sizeCalculator = new SizeCalculator();
		container.traverse(sizeCalculator);
		final Insets2D insets = container.getInsets();
		
		minimumSize = new Size2D(
				sizeCalculator.maxMinWidth+insets.getHorizontalInsets(),
				sizeCalculator.sumMinHeight+insets.getVerticalInsets());
		preferredSize = new Size2D(
				sizeCalculator.maxPrefWidth+insets.getHorizontalInsets(),
				sizeCalculator.sumPrefHeight+insets.getVerticalInsets());
	}
	
	protected class SizeCalculator implements ILayoutPartVisitor {
		boolean firstVisiblePart = true;
		double maxMinWidth=0, maxPrefWidth=0;
		double sumMinHeight=0, sumPrefHeight=0;
		
		@Override
		public void visit(ILayoutPart part) {
			if (!part.isVisible()) {
				/* Ignore invisible parts */
				return;
			}
			if (!firstVisiblePart) {
				sumMinHeight += gap;
				sumPrefHeight += gap;
			}
			
			final Dimension2D minSize = part.getMinimumSize();
			final Dimension2D prefSize = part.getPreferredSize();
			
			maxMinWidth = Math.max(maxMinWidth, minSize.getWidth());
			maxPrefWidth = Math.max(maxPrefWidth, prefSize.getWidth());
			
			sumMinHeight += minSize.getHeight();
			sumPrefHeight += prefSize.getHeight();
			
			firstVisiblePart = false;
		}
	}

	@Override
	public void layout(Rectangle2D bounds) {
		final Layouter layouter = new Layouter(bounds);
		container.traverse(layouter);
	}
	
	protected double getAlignedStart(Alignment alignment, double start, double usedWidth, double availableWidth) {
		throw new IllegalArgumentException();
	}
	
	protected class Layouter implements ILayoutPartVisitor {
		protected final double x;
		protected double y;
		protected final double allocationFactor;
		protected final double usedWidth, usedHeight;
		
		public Layouter(Rectangle2D bounds) {
			final Insets2D insets = container.getInsets();
			final double availableWidth = bounds.getWidth() - insets.getHorizontalInsets();
			final double availableHeight = bounds.getHeight() - insets.getVerticalInsets();
			
			usedWidth = availableWidth;
			usedHeight = Math.min(availableHeight, preferredSize.getHeight());
			if (preferredSize.getHeight() > minimumSize.getHeight()) {
				allocationFactor = (usedHeight - minimumSize.getHeight()) / (preferredSize.getHeight() - minimumSize.getHeight());
			} else {
				allocationFactor = 0;
			}
			
			x = bounds.getMinX() + insets.getLeftInset();
			final double colStart = bounds.getMinY() + insets.getTopInset();
			
			switch (alignment) {
			case LEADING:
			default:
				y = colStart;
				break;
			case CENTER:
				y = colStart + (availableHeight - usedHeight)/2.0;
			case TRAILING:
				y = colStart + availableHeight - usedHeight;
			}
		}
		
		@Override
		public void visit(ILayoutPart part) {
			if (!part.isVisible()) {
				/* Ignore invisible parts */
				return;
			}
			final Dimension2D minPartSize = part.getMinimumSize();
			final Dimension2D prefPartSize = part.getPreferredSize();
			
			final double height;
			
			height = minPartSize.getHeight() + allocationFactor * (prefPartSize.getHeight() - minPartSize.getHeight());
			
			part.setBounds(new Rectangle2D.Double(x,y, usedWidth, height));
			
			y += height + gap;
		}
	}

}
