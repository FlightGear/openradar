package de.knewcleus.openradar.view.layout;

import java.awt.LayoutManager;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class HorizontalLayoutManager implements ILayoutManager {
	protected final ILayoutPartContainer container;
	protected double horizontalSpacing = 0.0;
	protected double verticalAlignment = 0.5;
	protected double horizontalAlignment = 0.5;

	protected boolean valid = false;
	protected double maxMinAscent, maxMinDescent;
	protected double maxPrefAscent, maxPrefDescent;
	protected double minWidthSum, prefWidthSum;
	protected Insets2D insets;
	
	protected Dimension2D minimumSize, preferredSize;
	
	public HorizontalLayoutManager(ILayoutPartContainer container) {
		this.container = container;
	}
	
	@Override
	public Dimension2D getMinimumSize() {
		revalidateSizes();
		return minimumSize;
	}
	
	@Override
	public Dimension2D getPreferredSize() {
		revalidateSizes();
		return preferredSize;
	}
	
	@Override
	public Dimension2D getMaximumSize() {
		/* We can stretch to any size by placing space inbetween the elements */
		return null;
	}
	
	@Override
	public void invalidate() {
		valid = false;
	}

	@Override
	public void layout() {
		revalidateSizes();
		final Layouter layouter = new Layouter();
		container.traverse(layouter);
	}

	protected void revalidateSizes() {
		if (valid) {
			return;
		}
		final Insets2D padding = container.getPadding();
		final Insets2D border = container.getBorderPadding();
		insets = Insets2D.add(padding, border, null);
		
		maxMinDescent = 0;
		maxMinAscent = 0;
		maxPrefDescent = 0;
		maxPrefAscent = 0;
		minWidthSum = 0;
		prefWidthSum = 0;
		
		final SizeCollector collector = new SizeCollector();
		container.traverse(collector);
		minimumSize = new Size2D(
				minWidthSum+insets.getHorizontalInsets(),
				maxMinAscent+maxMinDescent+insets.getVerticalInsets());
		preferredSize = new Size2D(
				prefWidthSum+insets.getHorizontalInsets(),
				maxPrefAscent+maxPrefDescent+insets.getVerticalInsets());
	}
	
	protected class SizeCollector implements ILayoutPartVisitor {
		protected boolean firstPart = true;
		
		@Override
		public void visit(ILayoutPart part) {
			if (!firstPart) {
				minWidthSum += horizontalSpacing;
				prefWidthSum += horizontalSpacing;
			}
			firstPart = false;
			final Dimension2D partMinSize = part.getMinimumSize();
			final Dimension2D partPrefSize = part.getPreferredSize();
			if (partMinSize!=null) {
				final double minBaselineOffset = part.getBaselineOffset(partMinSize);
				minWidthSum += partMinSize.getWidth();
				maxMinAscent = Math.max(maxMinAscent, minBaselineOffset);
				maxMinDescent = Math.max(maxMinDescent, partMinSize.getHeight()-minBaselineOffset);
			}
			assert(partPrefSize!=null);
			final double prefBaselineOffset = part.getBaselineOffset(partPrefSize);
			prefWidthSum += partPrefSize.getWidth();
			maxPrefAscent = Math.max(maxPrefAscent, prefBaselineOffset);
			maxPrefDescent = Math.max(maxPrefDescent, partPrefSize.getHeight()-prefBaselineOffset);
		}
	}
	
	protected class Layouter implements ILayoutPartVisitor {
		final double totalWidth, totalHeight;
		double startX;
		double slackX;
		double prefStartX = 0;
		final double baseline;
		
		public Layouter() {
			Rectangle2D bounds = container.getBounds();
			totalWidth = bounds.getWidth();
			totalHeight = bounds.getHeight();
			startX = bounds.getMinX() + insets.getLeftInset();
			
			slackX = totalWidth - prefWidthSum;
			double slackY = totalHeight - maxMinAscent - maxMinDescent;
			baseline = bounds.getMinY() + insets.getTopInset() + slackY*verticalAlignment;
		}
		
		@Override
		public void visit(ILayoutPart part) {
			final Dimension2D partPrefSize = part.getPreferredSize();
			double endX = slackX - startX + 2.0*prefStartX;
			final Dimension2D partSize = new Size2D(endX - startX, totalHeight);
			
			double y = baseline - part.getBaselineOffset(partSize);
			
			part.setBounds(new Rectangle2D.Double(startX, y, endX-startX, totalHeight));
			
			prefStartX += partPrefSize.getWidth() + horizontalSpacing;
			startX = endX + horizontalSpacing;
		}
	}
}
