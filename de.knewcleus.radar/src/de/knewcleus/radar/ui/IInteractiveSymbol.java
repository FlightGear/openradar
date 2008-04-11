package de.knewcleus.radar.ui;

import java.awt.Rectangle;

/**
 * An interactive symbol is a user interface element representing a workable object and
 * providing a means for interaction with said object.
 * 
 * @author Ralf Gerlich
 * @see IWorkableObject
 *
 */
public interface IInteractiveSymbol {
	public abstract IWorkableObject getRepresentedObject();
	
	public abstract DefaultActivationModel getActivationModel();
	
	public abstract Rectangle getBounds();
	public abstract boolean contains(int x, int y);
}
