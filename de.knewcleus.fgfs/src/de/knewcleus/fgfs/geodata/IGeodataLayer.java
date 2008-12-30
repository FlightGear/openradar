package de.knewcleus.fgfs.geodata;

public interface IGeodataLayer {

	public abstract int getRecordCount();

	public abstract FeatureDefinition getFeatureDefinition();

	public abstract Feature getNextFeature() throws GeodataException;

}