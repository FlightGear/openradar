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

public class HorizontalFlowLayoutManager implements ILayoutManager {
	protected final ILayoutPartContainer container;

	protected Alignment alignment;
	protected double gap;
	
	protected boolean sizesValid = false;
	protected Dimension2D minimumSize = null;
	protected Dimension2D preferredSize = null;
	protected double maxMinAscent = 0, maxMinDescent = 0;
	protected double maxPrefAscent = 0, maxPrefDescent = 0;
	protected int partCount = 0;

	public HorizontalFlowLayoutManager(ILayoutPartContainer container) {
		this(container, Alignment.LEADING, 0);
	}

	public HorizontalFlowLayoutManager(ILayoutPartContainer container, Alignment alignment) {
		this(container, alignment, 0);
	}
	
	public HorizontalFlowLayoutManager(ILayoutPartContainer container, Alignment alignment, double gap) {
		this.container = container;
		this.alignment = alignment;
		this.gap = gap;
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
		sizesValid = false;
	}
	
	protected void recalculateSizes() {
		if (sizesValid) {
			return;
		}
		final SizeCollector sizeCollector = new SizeCollector();
		container.traverse(sizeCollector);
		
		maxMinAscent = sizeCollector.maxMinAscent;
		maxMinDescent = sizeCollector.maxMinDescent;
		maxPrefAscent = sizeCollector.maxPrefAscent;
		maxPrefDescent = sizeCollector.maxPrefDescent;
		partCount = sizeCollector.partCount;
		
		minimumSize = new Size2D(
				sizeCollector.minWidthSum, maxMinAscent+maxMinDescent);
		preferredSize = new Size2D(
				sizeCollector.prefWidthSum, maxPrefAscent+maxPrefDescent);
	}
	
	@Override
	public void layout(Rectangle2D bounds) {
		final LayoutInformation information = new LayoutInformation(bounds);
		final BaselineAndWidthLayouter baselineLayouter = new BaselineAndWidthLayouter(information);
		container.traverse(baselineLayouter);
		final FinalLayouter finalLayouter = new FinalLayouter(information);
		container.traverse(finalLayouter);
	}
	
	protected class SizeCollector implements ILayoutPartVisitor {
		protected double minWidthSum = 0, prefWidthSum = 0;
		protected double maxMinAscent = 0, maxMinDescent = 0;
		protected double maxPrefAscent = 0, maxPrefDescent = 0;
		protected int partCount = 0;
		
		@Override
		public void visit(ILayoutPart part) {
			if (!part.isVisible()) {
				/* Ignore invisible parts */
				return;
			}
			if (partCount > 0) {
				minWidthSum += gap;
				prefWidthSum += gap;
			}
			
			final Dimension2D minSize = part.getMinimumSize();
			final Dimension2D prefSize = part.getPreferredSize();
			final double minBaseline = part.getBaselineOffset(minSize);
			final double prefBaseline = part.getBaselineOffset(prefSize);
			
			maxMinAscent = Math.max(maxMinAscent, minBaseline);
			maxPrefAscent = Math.max(maxPrefAscent, prefBaseline);
			maxMinDescent = Math.max(maxMinDescent, minSize.getHeight() - minBaseline);
			maxPrefDescent = Math.max(maxPrefDescent, prefSize.getHeight() - prefBaseline);
			
			minWidthSum += minSize.getWidth();
			prefWidthSum += prefSize.getWidth();
			
			partCount++;
		}
	}
	
	protected class LayoutInformation {
		protected final double startX, startY;
		protected final double partWidth[]=new double[partCount];
		protected double maxBaseline = 0;
		protected final double availableHeight;
		protected final double usedWidth;
		protected final double allocationFactor;

		public LayoutInformation(Rectangle2D bounds) {
			final Insets2D insets = container.getInsets();

			final double availableWidth;
			availableWidth = bounds.getWidth() - insets.getHorizontalInsets();
			usedWidth = Math.min(preferredSize.getWidth(), availableWidth);
			
			availableHeight = bounds.getHeight() - insets.getVerticalInsets();
			
			final double leftX = bounds.getMinX() + insets.getLeftInset();
			
			switch (alignment) {
			case LEADING:
			default:
				startX = leftX;
				break;
			case CENTER:
				startX = leftX + (availableWidth - usedWidth) / 2.0;
				break;
			case TRAILING:
				startX = leftX + availableWidth - usedWidth;
				break;
			}
			
			startY = bounds.getMinY() + insets.getTopInset();
			
			if (preferredSize.getWidth() > minimumSize.getWidth()) {
				allocationFactor = (usedWidth - minimumSize.getWidth()) / (preferredSize.getWidth() - minimumSize.getWidth());
			} else {
				allocationFactor = 0;
			}
		}
	}
	
	protected class BaselineAndWidthLayouter implements ILayoutPartVisitor {
		protected final LayoutInformation layoutInformation;
		protected int partIndex = 0;
		
		public BaselineAndWidthLayouter(LayoutInformation layoutInformation) {
			this.layoutInformation = layoutInformation;
		}
		
		@Override
		public void visit(ILayoutPart part) {
			if (!part.isVisible()) {
				return;
			}
			final Dimension2D partMinSize = part.getMinimumSize();
			final Dimension2D partPrefSize = part.getPreferredSize();
			final double width;
			
			width = partMinSize.getWidth() + (partPrefSize.getWidth() - partMinSize.getWidth()) * layoutInformation.allocationFactor;
			
			final double baseline = part.getBaselineOffset(new Size2D(width, layoutInformation.availableHeight));
			
			layoutInformation.partWidth[partIndex] = width;
			
			layoutInformation.maxBaseline = Math.max(layoutInformation.maxBaseline, baseline);
			
			partIndex++;
		}
	}
	
	protected class FinalLayouter implements ILayoutPartVisitor {
		protected final LayoutInformation layoutInformation;
		protected int partIndex = 0;
		protected double x;
		protected final double y;

		public FinalLayouter(LayoutInformation layoutInformation) {
			this.layoutInformation = layoutInformation;
			x = layoutInformation.startX;
			y = layoutInformation.startY+layoutInformation.maxBaseline;
		}
		
		@Override
		public void visit(ILayoutPart part) {
			if (!part.isVisible()) {
				return;
			}
			if (partIndex > 0) {
				x += gap;
			}
			final double width = layoutInformation.partWidth[partIndex];
			final double baseline = part.getBaselineOffset(new Size2D(width, layoutInformation.availableHeight));
			part.setBounds(new Rectangle2D.Double(
					x, y-baseline, width, layoutInformation.availableHeight));
			x+=width;
			partIndex++;
		}
	}
}
