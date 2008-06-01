package de.knewcleus.radar.ui.core;

import java.util.EventListener;

public interface IWorkObjectSelectionListener extends EventListener {
	public abstract void workObjectSelected(WorkObject workObject);
	public abstract void workObjectDeselected(WorkObject workObject);
}
