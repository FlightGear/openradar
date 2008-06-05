package de.knewcleus.fgfs.geodata;

public class Feature {
	protected final FeatureDefinition featureDefinition;
	protected final int featureID;
	protected final Geometry geometry;
	protected final DatabaseRow databaseRow;
	
	public Feature(FeatureDefinition featureDefinition, int featureID, Geometry geometry, DatabaseRow databaseRow) {
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
	
	public DatabaseRow getDatabaseRow() {
		return databaseRow;
	}
}
