package de.knewcleus.radar;

import java.util.EventListener;

public interface ISymbolActivationListener extends EventListener {
	public void symbolActivated(Symbol symbol);
	public void symbolDeactivated(Symbol symbol);
}
