package de.knewcleus.openradar.view.mouse;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import de.knewcleus.fgfs.util.IOutputIterator;
import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.IView;
import de.knewcleus.openradar.view.PickVisitor;

public class FocusMouseMotionListener implements MouseMotionListener {
	protected final IFocusManager focusManager;
	protected final IView rootView;

	public FocusMouseMotionListener(IFocusManager focusManager, IView rootView) {
		this.focusManager = focusManager;
		this.rootView = rootView;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		final IFocusableView currentFocusOwner = focusManager.getCurrentFocusOwner();
		if (currentFocusOwner!=null && currentFocusOwner.contains(e.getPoint())) {
			/* The current focus owner takes precedence, even if it is now obscured */
			return;
		}
		
		final FocuseablePickIterator iterator = new FocuseablePickIterator();
		final PickVisitor pickVisitor = new PickVisitor(e.getPoint(), iterator);
		rootView.accept(pickVisitor);
		focusManager.forceCurrentFocusOwner(iterator.getTopFocusable());
	}

	@Override
	public void mouseDragged(MouseEvent e) {}
	
	protected class FocuseablePickIterator implements IOutputIterator<IPickable> {
		protected IFocusableView topFocusable = null;
		
		@Override
		public void next(IPickable v) {
			if (v instanceof IFocusableView) {
				topFocusable = (IFocusableView)v;
			}
		}
		
		@Override
		public boolean wantsNext() {
			return true;
		}
		
		public IFocusableView getTopFocusable() {
			return topFocusable;
		}
	}
}
