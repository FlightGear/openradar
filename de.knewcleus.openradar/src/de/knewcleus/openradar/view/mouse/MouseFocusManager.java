/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
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
