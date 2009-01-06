package de.knewcleus.fgfs.navdata.model;

public interface ITransmitter extends INavPointWithElevation {
	public IFrequency getFrequency();
	public float getRange();

}
