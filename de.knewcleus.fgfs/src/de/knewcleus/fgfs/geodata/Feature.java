package de.knewcleus.fgfs.geodata;

public class Feature {
	protected final FeatureDefinition featureDefinition;
	protected final int featureID;
	protected final Geometry geometry;
	protected final Object[] fields;
	
	public Feature(FeatureDefinition featureDefinition, int featureID, Geometry geometry, Object[] fields) {
		this.featureDefinition=featureDefinition;
		this.featureID=featureID;
		this.geometry=geometry;
		this.fields=fields;
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
	
	public Object[] getFields() {
		return fields;
	}
}
