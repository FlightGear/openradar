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
	private ShiftFilter zoomFilter = new ShiftFilter();

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
		if(shiftOrigin.distance(e.getPoint())<5 ) {
		    updateFocus(e);
		    shiftOrigin=null;
		}
	}

	private void centerScreenOn(java.awt.Point point) {
	    radarMapViewerAdapter.setCenterOnDevicePoint(point);
	}

	private void centerScreen() {
	    radarMapViewerAdapter.centerMap();
    }
    @Override
    public synchronized void mouseDragged(MouseEvent e) {
        if(shiftOrigin!=null) {
            zoomFilter.shiftOrigin(shiftOrigin,e.getPoint());
            
            radarMapViewerAdapter.shiftDeviceOrigin(shiftOrigin,e.getPoint());
            
            shiftOrigin = e.getPoint();
        }
    }

    @Override
	public synchronized void mouseMoved(MouseEvent e) {
	    // update StP

	    GuiRadarContact contact = guiInteractionManager.getRadarContactManager().getSelectedContact();
	    if(contact!=null) {
	        guiInteractionManager.getStatusManager().updateMouseRadarMoved(contact,e);
	        guiInteractionManager.getAirportData().updateMouseRadarMoved(contact,e);
	    }
	}

	protected void updateFocus(MouseEvent e) {
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

	/**
     * This class exists to filter too many repaint requests. It saves only the last request and performs it, when it is ready with the last repaint.
     * So OR responds much faster 
     * 
     * @author Wolfram Wagner
     *
     */
    private class ShiftFilter implements Runnable{
        
        private Point shiftOrigin = null;
        private Point shiftTarget = null;
        private final Thread thread; 
        
        public ShiftFilter() {
            thread = new Thread(this, "MouseShiftFilter" );
            thread.start();
        }
        
        synchronized void shiftOrigin(Point origin, Point target) {
            if(shiftOrigin==null) {
                this.shiftOrigin=origin;
                this.shiftTarget=target;
            } else {
                this.shiftTarget=new Point(shiftTarget.x+target.x-origin.x, shiftTarget.y+target.y-origin.y);
            }
            thread.interrupt();
        }
        
        public void run() {
            while(true) {
                Point pO=null;
                Point pT=null;
                
                synchronized(this) {
                    if(shiftOrigin!=null) {
                        pO=shiftOrigin;
                        pT=shiftTarget;
                        shiftOrigin=null;
                    }
                }
                if(pO!=null) {
                    radarMapViewerAdapter.shiftDeviceOrigin(pO, pT);
                } 
                if(pO==null) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }
}
