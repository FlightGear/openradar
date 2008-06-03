package de.knewcleus.openradar.ui.core;

import java.util.EventListener;

public interface ISymbolActivationListener extends EventListener {
	public void symbolActivated(WorkObjectSymbol symbol);
	public void symbolDeactivated(WorkObjectSymbol symbol);
}
