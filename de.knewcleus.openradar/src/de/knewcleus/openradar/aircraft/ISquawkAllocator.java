package de.knewcleus.openradar.aircraft;

public interface ISquawkAllocator {
	public abstract String allocateSquawk() throws OutOfSquawksException;
	public abstract void returnSquawk(String squawk);
}
