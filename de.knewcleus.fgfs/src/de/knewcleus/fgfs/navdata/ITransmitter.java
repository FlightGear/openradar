package de.knewcleus.fgfs.navdata;

public interface ITransmitter extends INavPointWithElevation {
	public IFrequency getFrequency();
	public float getRange();

}
