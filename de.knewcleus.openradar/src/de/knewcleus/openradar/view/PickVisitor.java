package de.knewcleus.openradar.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.util.IOutputIterator;

public class PickVisitor implements IViewVisitor {
	protected final Point2D point;
	protected final IOutputIterator<IPickable> outputIterator;
	
	public PickVisitor(Point2D point, IOutputIterator<IPickable> outputIterator) {
		this.point = point;
		this.outputIterator = outputIterator;
	}

	@Override
	public void visitContainer(IContainer container) {
		if (mayBeInside(container)) {
			checkPick(container);
			if (outputIterator.wantsNext()) {
				container.traverse(this);
			}
		}
	}

	@Override
	public void visitElement(IElement element) {
		if (mayBeInside(element)) {
			checkPick(element);
		}
	}

	@Override
	public void visitView(IView view) {
		if (mayBeInside(view)) {
			checkPick(view);
		}
	}
	
	protected void checkPick(IView view) {
		if (!outputIterator.wantsNext()) {
			return;
		}
		if (!(view instanceof IPickable)) {
			return;
		}
		final IPickable pickable = (IPickable) view;
		if (pickable.contains(point)) {
			outputIterator.next(pickable);
		}
	}
	
	protected boolean mayBeInside(IView view) {
		if (view instanceof IBoundedView) {
			final Rectangle2D extents = ((IBoundedView)view).getDisplayExtents();
			return extents.contains(point);
		}
		return true;
	}

}
