package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class BufferedCanvas implements ICanvas {
	protected final ICanvas delegateCanvas;
	protected Graphics2D delegateGraphics = null;
	protected BufferedImage bufferImage = null;
	protected Rectangle usedBounds = null;
	
	public BufferedCanvas(ICanvas delegateCanvas) {
		this.delegateCanvas = delegateCanvas;
	}

	@Override
	public Graphics2D getGraphics(Rectangle2D region) {
		assert(bufferImage == null);
		delegateGraphics = delegateCanvas.getGraphics(region);
		if (delegateGraphics == null) {
			return null;
		}
		
		Graphics2D graphics = delegateGraphics;
		
		/* Determine the bounds of the part actually drawn to */
		final Rectangle delegateClipBounds = delegateGraphics.getClipBounds();
		final Rectangle regionBounds = region.getBounds();
		usedBounds = regionBounds.intersection(delegateClipBounds);
		
		if (usedBounds.isEmpty()) {
			return delegateGraphics;
		}
		
		try {
			bufferImage = new BufferedImage(usedBounds.width, usedBounds.height, BufferedImage.TYPE_INT_ARGB);
		} catch (OutOfMemoryError e) {
			/* The image area is too big, so we'll go unbuffered */
			bufferImage = null;
		}
		
		if (bufferImage != null ) {
			graphics = bufferImage.createGraphics();
			graphics.translate(-usedBounds.x, -usedBounds.y);
			graphics.setBackground(delegateGraphics.getBackground());
			graphics.setClip(usedBounds.x, usedBounds.y, usedBounds.width, usedBounds.height);
			graphics.setComposite(delegateGraphics.getComposite());
			graphics.setColor(delegateGraphics.getColor());
			graphics.setFont(delegateGraphics.getFont());
			graphics.setPaint(delegateGraphics.getPaint());
			graphics.setStroke(delegateGraphics.getStroke());
		}
		
		return graphics;
	}
	
	@Override
	public void flushGraphics() {
		if (delegateGraphics == null) {
			/* We did not deliver any graphics, so no painting could have happened. */
			return;
		}
		if (bufferImage == null) {
			/* The bufferImage can be null if double-buffering failed.
			 * In this case, there is nothing to flush. */
			return;
		}
		assert(usedBounds!=null);
		if (!usedBounds.isEmpty()) {
			delegateGraphics.drawImage(bufferImage, null, usedBounds.x, usedBounds.y);
		}
		delegateGraphics = null;
		bufferImage = null;
	}
}
