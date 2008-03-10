package de.knewcleus.fgfs.environment;

public class AtmosphereHelper {

	public static double getGeometricHeight(double hgeop) {
		return hgeop*R_EARTH/(AtmosphereHelper.R_EARTH-hgeop);
	}

	public static double getGeopotentialHeight(double hgeom) {
		return hgeom*R_EARTH/(AtmosphereHelper.R_EARTH+hgeom);
	}

	/* radius of earth (m) */
	public static final double R_EARTH=6356766;

}
