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
