package de.knewcleus.fgfs.geodata;

import de.knewcleus.fgfs.geodata.geometry.Geometry;

public class Feature {
	protected final FeatureDefinition featureDefinition;
	protected final int featureID;
	protected final Geometry geometry;
	protected final IDatabaseRow databaseRow;
	
	public Feature(FeatureDefinition featureDefinition, int featureID, Geometry geometry, IDatabaseRow databaseRow) {
		this.featureDefinition=featureDefinition;
		this.featureID=featureID;
		this.geometry=geometry;
		this.databaseRow=databaseRow;
	}
	
	public FeatureDefinition getFeatureDefinition() {
		return featureDefinition;
	}
	
	public int getFeatureID() {
		return featureID;
	}
	
	public Geometry getGeometry() {
		return geometry;
	}
	
	public IDatabaseRow getDatabaseRow() {
		return databaseRow;
	}
}
