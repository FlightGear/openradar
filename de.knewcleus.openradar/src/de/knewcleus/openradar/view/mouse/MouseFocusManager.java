package de.knewcleus.openradar.view.mouse;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import de.knewcleus.fgfs.util.IOutputIterator;
import de.knewcleus.openradar.view.IPickable;
import de.knewcleus.openradar.view.IView;
import de.knewcleus.openradar.view.PickVisitor;

public class MouseFocusManager extends MouseAdapter {
	protected final static int BUTTON_DOWN_MASKS =
		MouseEvent.BUTTON1_DOWN_MASK |
		MouseEvent.BUTTON2_DOWN_MASK |
		MouseEvent.BUTTON3_DOWN_MASK;
	protected final IFocusManager focusManager;
	protected final IView rootView;

	public MouseFocusManager(IFocusManager focusManager, IView rootView) {
		this.focusManager = focusManager;
		this.rootView = rootView;
	}
	
	public void install(Component component) {
		component.addMouseMotionListener(this);
		component.addMouseListener(this);
	}
	
	public void uninstall(Component component) {
		component.removeMouseMotionListener(this);
		component.removeMouseListener(this);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if ((e.getModifiersEx() & BUTTON_DOWN_MASKS) != 0) {
			/* Ignore, as long as any button is down */
			return;
		}
		updateFocus(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updateFocus(e);
	}
	
	protected void updateFocus(MouseEvent e) {
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
