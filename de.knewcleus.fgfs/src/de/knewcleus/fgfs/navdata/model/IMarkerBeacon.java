package de.knewcleus.fgfs.navdata.model;

public interface IMarkerBeacon extends INavPointWithElevation, IILSComponent {
	public enum Type {
		Outer, Middle, Inner;
	};
	
	public Type getType();
}
