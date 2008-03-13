package de.knewcleus.radar.aircraft;

public enum SSRMode {
	NONE("None",false,false),
	MODEA("Mode A",true,false),
	MODEC("Mode A/C",true,true),
	MODES("Mode S",true,true);
	
	private final String name;
	private final boolean hasAltitudeEncoding;
	private final boolean hasSSRCode;
	
	private SSRMode(String name, boolean hasSSRCode, boolean hasAltitudeEncoding) {
		this.name=name;
		this.hasSSRCode=hasSSRCode;
		this.hasAltitudeEncoding=hasAltitudeEncoding;
	}
	
	public boolean hasSSRCode() {
		return hasSSRCode;
	}
	
	public boolean hasAltitudeEncoding() {
		return hasAltitudeEncoding;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
