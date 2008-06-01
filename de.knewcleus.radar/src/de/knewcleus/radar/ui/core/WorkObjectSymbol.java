package de.knewcleus.radar.ui.core;


import javax.swing.event.EventListenerList;

public abstract class WorkObjectSymbol extends DisplayElement {
	protected final EventListenerList eventListenerList=new EventListenerList();
	protected boolean active=false;
	
	public abstract WorkObject getAssociatedObject();
	
	public void activateSymbol() {
		if (active)
			return;
		active=true;
		fireSymbolActivated();
	}
	
	public void deactivateSymbol() {
		if (!active)
			return;
		active=false;
		fireSymbolDeactivated();
	}
	
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
}
