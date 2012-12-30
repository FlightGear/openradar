package de.knewcleus.openradar.view.textview;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;
import de.knewcleus.openradar.view.IViewerAdapter;
import de.knewcleus.openradar.view.layout.ILayoutPart;
import de.knewcleus.openradar.view.layout.ILayoutPartContainer;
import de.knewcleus.openradar.view.layout.Size2D;

public class TextView implements IBoundedView, ILayoutPart {
	protected final IViewerAdapter viewerAdapter;
	protected final ILayoutPartContainer container;
	
	protected Rectangle2D displayExtents = new Rectangle2D.Double();
	
	protected Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
	protected boolean visible = true;
	protected String text = "";
	
	protected boolean sizesValid = false;
	protected Dimension2D preferredSize = new Size2D();
	protected double baselineOffset = 0;
	
	public TextView(IViewerAdapter viewerAdapter, ILayoutPartContainer container) {
		this.viewerAdapter = viewerAdapter;
		this.container = container;
	}
	
	public void setText(String text) {
		this.text = text;
		invalidate();
		viewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}
	
	protected void invalidate() {
		sizesValid = false;
		container.invalidate();
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		g2d.drawString(text,
				(float)displayExtents.getMinX(), (float)(displayExtents.getMinY()+baselineOffset));
	}

	@Override
	public Dimension2D getMinimumSize() {
		calculateSizes();
		return preferredSize;
	}
	
	@Override
	public Dimension2D getPreferredSize() {
		calculateSizes();
		return preferredSize;
	}
	
	@Override
	public double getBaselineOffset(Dimension2D size) {
		calculateSizes();
		return baselineOffset;
	}
	
	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}
	
	@Override
	public void setBounds(Rectangle2D bounds) {
		/* Ensure that the formerly occupied region is repainted */
		viewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
		displayExtents = bounds;
		viewerAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}
	
	@Override
	public ILayoutPartContainer getLayoutPartContainer() {
		return container;
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	protected void calculateSizes() {
		if (sizesValid) {
			return;
		}
		final FontMetrics fm = viewerAdapter.getCanvas().getFontMetrics(font);
		final FontRenderContext frc = fm.getFontRenderContext();
		baselineOffset = fm.getMaxAscent();
		final Rectangle2D stringBounds = font.getStringBounds(text, frc);
		preferredSize = new Size2D(stringBounds.getWidth(), stringBounds.getHeight());
		sizesValid = true;
	}
	
	@Override
	public void validate() {}
}
