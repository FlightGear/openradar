package de.knewcleus.openradar.map;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * The view paint visitor is responsible for painting the map.
 * 
 * @author Ralf Gerlich
 *
 */
public class ViewPaintVisitor implements IViewVisitor {
	protected final Graphics2D g2d;
	protected final Rectangle2D clip;
	
	public ViewPaintVisitor(Graphics2D g2d) {
		this.g2d = g2d;
		clip = g2d.getClipBounds();
	}

	@Override
	public void visitContainer(IContainer container) {
		if (intersectsClip(container)) {
			container.paint(g2d);
			container.traverse(this);
		}
	}

	@Override
	public void visitElement(IElement element) {
		visitView(element);
	}

	@Override
	public void visitView(IView view) {
		if (intersectsClip(view)) {
			view.paint(g2d);
		}
	}

	protected boolean intersectsClip(IView view) {
		if (view instanceof IBoundedView) {
			final Rectangle2D extents = ((IBoundedView)view).getDisplayExtents();
			return extents.intersects(clip);
		}
		return true;
	}
}
