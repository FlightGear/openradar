package de.knewcleus.radar.ui;

/**
 * A workable object is an object which can be manipulated by the user by some means
 * provided by the operating system.
 * 
 * @author Ralf Gerlich
 */
public interface IWorkableObject {
	public abstract boolean isEnabled();
	public abstract boolean isSelected();
	public abstract void setSelected(boolean selected);
}
