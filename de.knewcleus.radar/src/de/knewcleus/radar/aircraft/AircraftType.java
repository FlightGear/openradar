package de.knewcleus.radar.aircraft;

import java.util.Properties;

public class AircraftType {
	protected final String vendor;
	protected final String type;
	protected final String designationICAO;
	
	public AircraftType(String prefix, Properties properties) {
		vendor=properties.getProperty(prefix+".vendor","<unknown>");
		type=properties.getProperty(prefix+".type","<unknown>");
		designationICAO=properties.getProperty(prefix+".icao","<unknown>");
	}

	public String getVendor() {
		return vendor;
	}

	public String getType() {
		return type;
	}

	public String getDesignationICAO() {
		return designationICAO;
	}
	
	@Override
	public String toString() {
		return getVendor()+" "+getType();
	}
}
