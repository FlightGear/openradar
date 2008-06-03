package de.knewcleus.openradar.ui.core;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public abstract class WorkObjectSymbol extends DisplayElement implements MouseListener, MouseMotionListener, ChangeListener {
	protected final EventListenerList eventListenerList=new EventListenerList();
	protected ISymbolActivationModel activationModel=new DefaultSymbolActivationModel();
	protected boolean active=false;
	
	public abstract WorkObject getAssociatedObject();
	
	public WorkObjectSymbol() {
		activationModel.registerChangeListener(this);
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
		activationModel.setMouseover(true);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		activationModel.setMouseover(false);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton()==MouseEvent.BUTTON1) {
			activationModel.setPressed(true);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton()==MouseEvent.BUTTON1) {
			activationModel.setPressed(false);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {}
	
	@Override
	public void mouseClicked(MouseEvent e) {}
	
	public boolean isActive() {
		return active;
	}
	
	public void registerSymbolActivationListener(ISymbolActivationListener listener) {
		eventListenerList.add(ISymbolActivationListener.class, listener);
	}
	
	public void unregisterSymbolActivationListener(ISymbolActivationListener listener) {
		eventListenerList.remove(ISymbolActivationListener.class, listener);
	}
	
	protected void fireSymbolActivated() {
		for (ISymbolActivationListener l: eventListenerList.getListeners(ISymbolActivationListener.class)) {
			l.symbolActivated(this);
		}
	}
	
	protected void fireSymbolDeactivated() {
		for (ISymbolActivationListener l: eventListenerList.getListeners(ISymbolActivationListener.class)) {
			l.symbolDeactivated(this);
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource()==activationModel) {
			final SymbolActivationManager symbolFocusManager=getSymbolActivationManager();
			assert(symbolFocusManager!=null);
			if (activationModel.isArmedForActivation()) {
				/* Try to get the focus */
				final boolean gotFocus=symbolFocusManager.requestFocus(this);
				if (gotFocus) {
					active=true;
					fireSymbolActivated();
				}
			} else {
				/* Release the focus */
				symbolFocusManager.releaseFocus(this);
				active=false;
				fireSymbolDeactivated();
			}
		}
	}
}
