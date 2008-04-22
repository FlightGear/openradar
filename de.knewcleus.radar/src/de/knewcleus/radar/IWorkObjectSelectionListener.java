package de.knewcleus.radar;

import java.util.EventListener;

public interface IWorkObjectSelectionListener extends EventListener {
	public abstract void workObjectSelected(WorkObject workObject);
	public abstract void workObjectDeselected(WorkObject workObject);
}
