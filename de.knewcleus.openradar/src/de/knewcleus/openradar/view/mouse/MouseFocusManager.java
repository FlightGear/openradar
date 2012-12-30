package de.knewcleus.openradar.view.mouse;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.util.IOutputIterator;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.rpvd.RadarMapViewerAdapter;
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
	protected final RadarMapViewerAdapter radarMapViewerAdapter;
	protected final GuiMasterController guiInteractionManager;
	protected volatile java.awt.Point shiftOrigin; 

	public MouseFocusManager(GuiMasterController guiInteractionManager, IFocusManager focusManager, IView rootView, RadarMapViewerAdapter radarMapViewerAdapter) {
		this.focusManager = focusManager;
		this.rootView = rootView;
		this.radarMapViewerAdapter = radarMapViewerAdapter;
		this.guiInteractionManager = guiInteractionManager;
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
    public synchronized void mouseClicked(MouseEvent e) {
        if (e.getClickCount()==2) {
            centerScreen();
            shiftOrigin=null;
        }
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        shiftOrigin = e.getPoint();
    }

	@Override
	public synchronized void mouseReleased(MouseEvent e) {
		if ((e.getModifiersEx() & BUTTON_DOWN_MASKS) != 0) {
			/* Ignore, as long as any button is down */
			return;
		}
		if(shiftOrigin.equals(e.getPoint()) ) {
		    updateFocus(e);
		    shiftOrigin=null;
		}
	}

	private void shiftScreen(java.awt.Point shiftOrigin, java.awt.Point shiftTarget) {
	    Point2D geoOrigin = radarMapViewerAdapter.getGeoLocationOf(shiftOrigin);
	    Point2D geoTarget = radarMapViewerAdapter.getGeoLocationOf(shiftTarget);
	    radarMapViewerAdapter.shiftMap(geoOrigin.getX()-geoTarget.getX(),geoOrigin.getY()-geoTarget.getY());
    }

	private void centerScreen() {
	    radarMapViewerAdapter.centerMap();
    }
    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        if(shiftOrigin!=null) {
            shiftScreen(shiftOrigin,e.getPoint());
            shiftOrigin = e.getPoint();
        }
    }

    @Override
	public synchronized void mouseMoved(MouseEvent e) {
	    // ignore the next line to use clicks
		//updateFocus(e);
	    
	    // update StP
	    
	    GuiRadarContact contact = guiInteractionManager.getRadarContactManager().getSelectedContact();
	    if(contact!=null) {
	        guiInteractionManager.getStatusManager().updateMouseRadarMoved(contact,e);
	    }
	}

	protected void updateFocus(MouseEvent e) {
//		final IFocusableView currentFocusOwner = focusManager.getCurrentFocusOwner();
//
//		if (currentFocusOwner!=null && currentFocusOwner.contains(e.getPoint())) {
//			/* The current focus owner takes precedence, even if it is now obscured */
//			return;
//		}

		final FocuseablePickIterator iterator = new FocuseablePickIterator();
		final PickVisitor pickVisitor = new PickVisitor(e.getPoint(), iterator);
		rootView.accept(pickVisitor);
		focusManager.forceCurrentFocusOwner(iterator.getTopFocusable(),e);
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
