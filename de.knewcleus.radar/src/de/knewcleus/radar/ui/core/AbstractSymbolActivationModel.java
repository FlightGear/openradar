package de.knewcleus.radar.ui.core;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public abstract class AbstractSymbolActivationModel implements ISymbolActivationModel {
	protected final EventListenerList eventListenerList = new EventListenerList();
	protected boolean armedForActivation=false;

	public AbstractSymbolActivationModel() {
		super();
	}
	
	public void setArmedForActivation(boolean armedForFocus) {
		if (armedForFocus==this.armedForActivation)
			return;
		this.armedForActivation = armedForFocus;
		fireChangeEvent(new ChangeEvent(this));
	}
	
	@Override
	public boolean isArmedForActivation() {
		return armedForActivation;
	}

	@Override
	public void registerChangeListener(ChangeListener changeListener) {
		eventListenerList.add(ChangeListener.class, changeListener);
	}

	@Override
	public void unregisterChangeListener(ChangeListener changeListener) {
		eventListenerList.remove(ChangeListener.class, changeListener);
	}

	protected void fireChangeEvent(ChangeEvent e) {
		for (ChangeListener listener: eventListenerList.getListeners(ChangeListener.class)) {
			listener.stateChanged(e);
		}
	}
}