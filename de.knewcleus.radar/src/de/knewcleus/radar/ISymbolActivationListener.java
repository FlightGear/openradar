package de.knewcleus.radar;

import java.util.EventListener;

public interface ISymbolActivationListener extends EventListener {
	public void symbolActivated(WorkObjectSymbol symbol);
	public void symbolDeactivated(WorkObjectSymbol symbol);
}
