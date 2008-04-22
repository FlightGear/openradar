package de.knewcleus.radar;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.event.EventListenerList;

public abstract class Symbol {
	protected final EventListenerList eventListenerList=new EventListenerList();
	protected boolean active=false;
	public abstract WorkObject getAssociatedObject();
	
	public abstract void paint(Graphics2D g);
	public abstract void validate();
	public abstract Rectangle2D getBounds();
	public abstract JComponent getDisplayComponent();
	
	public void invalidate() {
		final Rectangle2D bounds=getBounds();
		if (bounds!=null) {
			getDisplayComponent().repaint(bounds.getBounds());
		}
	}
	
	public void activateSymbol() {
		active=true;
		fireSymbolActivated();
	}
	
	public void deactivateSymbol() {
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
