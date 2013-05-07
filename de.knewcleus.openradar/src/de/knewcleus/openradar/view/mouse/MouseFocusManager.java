/**
 * Copyright (C) 2008-2009 Ralf Gerlich
 * Copyright (C) 2012,2013 Wolfram Wagner
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
package de.knewcleus.openradar.view.mouse;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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
        if (e.getButton()==MouseEvent.BUTTON3 && e.getClickCount()==2) {
            centerScreen();
            shiftOrigin=null;
        } else
        if (e.getButton()==MouseEvent.BUTTON1 && e.getClickCount()==2) {
            centerScreenOn(e.getPoint());
        }
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        shiftOrigin = e.getPoint();
    }

	@Override
	public synchronized void mouseReleased(MouseEvent e) {
	    if(shiftOrigin==null) return; // monkey clicking
		if ((e.getModifiersEx() & BUTTON_DOWN_MASKS) != 0) {
			/* Ignore, as long as any button is down */
			return;
		}
		if(shiftOrigin.equals(e.getPoint()) ) {
		    updateFocus(e);
		    shiftOrigin=null;
		}
	}

	private void centerScreenOn(java.awt.Point point) {
        Point2D newCenter = radarMapViewerAdapter.getDeviceToLogicalTransform().transform(point, null);

	    radarMapViewerAdapter.setLogicalOrigin(newCenter);
	}

	private void shiftScreen(java.awt.Point shiftOrigin, java.awt.Point shiftTarget) {
	    Point2D logOrigin = radarMapViewerAdapter.getDeviceToLogicalTransform().transform(shiftOrigin, null);
	    Point2D logTarget = radarMapViewerAdapter.getDeviceToLogicalTransform().transform(shiftTarget, null);

	    radarMapViewerAdapter.shiftLogicalOrigin(logOrigin.getX()-logTarget.getX(),logOrigin.getY()-logTarget.getY());
    }

	private void centerScreen() {
	    radarMapViewerAdapter.centerMap();
    }
    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        if(shiftOrigin!=null) {
            if(e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK) {
                shiftScreen(shiftOrigin,e.getPoint());
                shiftOrigin = e.getPoint();
            } else {
                adaptZoom(e.getPoint());
            }
        }
    }

    private void adaptZoom(Point point) {
        Rectangle2D radarSize = radarMapViewerAdapter.getViewerExtents();
        double xDistance = (point.getX() - shiftOrigin.getX()) / radarSize.getWidth();
        double yDistance = (point.getY() - shiftOrigin.getY()) / radarSize.getHeight();
        double mouseScale = (Math.abs(xDistance)>Math.abs(yDistance)) ? xDistance : yDistance;

        double oldScale = radarMapViewerAdapter.getLogicalScale();
        double newScale = oldScale * 1+mouseScale * 20;
        newScale = ( newScale<1 ) ? 1 : newScale ;
        newScale = ( newScale>10000 ) ? 10000 : newScale ;

        if(oldScale!=newScale) radarMapViewerAdapter.setLogicalScale( newScale );
    }

    @Override
	public synchronized void mouseMoved(MouseEvent e) {
        // preparations for tooltip text in map
//        final FocuseablePickIterator iterator = new FocuseablePickIterator();
//        final PickVisitor pickVisitor = new PickVisitor(e.getPoint(), iterator);
//        rootView.accept(pickVisitor);
//        IFocusableView view = iterator.getTopFocusable();
//
//        if(view!=null) {
//            System.out.println(view.getAirSpeed());
//        }

	    // update StP

	    GuiRadarContact contact = guiInteractionManager.getRadarContactManager().getSelectedContact();
	    if(contact!=null) {
	        guiInteractionManager.getStatusManager().updateMouseRadarMoved(contact,e);
	        guiInteractionManager.getDataRegistry().updateMouseRadarMoved(contact,e);
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
