package de.knewcleus.fgfs.geodata;

import java.io.IOException;

public interface Layer {

	public abstract int getRecordCount();

	public abstract FeatureDefinition getFeatureDefinition();

	public abstract Feature getNextFeature() throws IOException,
			DataFormatException;

}