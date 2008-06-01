package de.knewcleus.radar.ui.core;

import javax.swing.event.EventListenerList;

public abstract class WorkObject implements ISymbolActivationListener {
	protected boolean isEnabled=false;
	protected boolean isArmed=false;
	protected final EventListenerList eventListenerList=new EventListenerList();
	
	public void setEnabled(boolean isEnabled) {
		if (isEnabled==this.isEnabled)
			return;
		
		this.isEnabled = isEnabled;
		
		if (isArmed) {
			if (isEnabled) {
				fireObjectSelected();
			} else {
				fireObjectDeselected();
			}
		}
	}
	
	public boolean isEnabled() {
		return isEnabled;
	}
	
	public void setArmed(boolean isArmed) {
		if (this.isArmed==isArmed)
			return;
		this.isArmed = isArmed;
		
		if (isEnabled) {
			if (isArmed) {
				fireObjectSelected();
			} else {
				fireObjectDeselected();
			}
		}
	}
	
	public boolean isArmed() {
		return isArmed;
	}
	
	public boolean isSelected() {
		return isArmed && isEnabled;
	}
	
	public void registerWorkObjectSelectionListener(IWorkObjectSelectionListener listener) {
		eventListenerList.add(IWorkObjectSelectionListener.class, listener);
	}
	
	public void unregisterWorkObjectSelectionListener(IWorkObjectSelectionListener listener) {
		eventListenerList.remove(IWorkObjectSelectionListener.class, listener);
	}
	
	protected void fireObjectSelected() {
		for (IWorkObjectSelectionListener l: eventListenerList.getListeners(IWorkObjectSelectionListener.class)) {
			l.workObjectSelected(this);
		}
	}
	
	protected void fireObjectDeselected() {
		for (IWorkObjectSelectionListener l: eventListenerList.getListeners(IWorkObjectSelectionListener.class)) {
			l.workObjectDeselected(this);
		}
	}
	
	@Override
	public void symbolActivated(WorkObjectSymbol symbol) {
		setArmed(true);
	}
	
	@Override
	public void symbolDeactivated(WorkObjectSymbol symbol) {
		setArmed(false);
	}
}
