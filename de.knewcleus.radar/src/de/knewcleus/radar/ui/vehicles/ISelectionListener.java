package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.radar.ui.IWorkableObject;

public interface ISelectionListener {
	public abstract void selectionChanged(IWorkableObject oldSelection, IWorkableObject newSelection);
}
