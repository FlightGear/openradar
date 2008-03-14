package de.knewcleus.fgfs.location;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import de.knewcleus.fgfs.Units;

/**
 * Utilities for geodesic calculations.
 * 
 * Based on
 * 
 *     T. Vicenty, Direct and Inverse Solutions of Geodesics on the
 *                 Ellipsoid with Application of Nested Equations,
 *                 Survey Review Vol. XXIII, No. 176,
 *                 Ministry of Overseas Department, UK, April 1975
 *  
 * @author Ralf Gerlich
 *
 */
public class GeodesicUtils {
	protected final Ellipsoid ellipsoid;

	public static class GeodesicInformation {
		public final double startLon;
		public final double startLat;
		public final double startAzimuth;
		public final double endLon;
		public final double endLat;
		public final double endAzimuth;
		public final double length;

		public GeodesicInformation(double startLon, double startLat, double startAzimuth,
				double endLon, double endLat, double endAzimuth,
				double length)
		{
			this.startLon=startLon;
			this.startLat=startLat;
			this.startAzimuth=startAzimuth;
			this.endLon=endLon;
			this.endLat=endLat;
			this.endAzimuth=endAzimuth;
			this.length=length;
		}

		public Position getStartPos() {
			return new Position(startLon,startLat,0);
		}

		public Position getEndPos() {
			return new Position(endLon,endLat,0);
		}

		public double getStartLon() {
			return startLon;
		}

		public double getStartLat() {
			return startLat;
		}

		public double getEndLon() {
			return endLon;
		}

		public double getEndLat() {
			return endLat;
		}

		public double getStartAzimuth() {
			return startAzimuth;
		}

		public double getEndAzimuth() {
			return endAzimuth;
		}

		public double getLength() {
			return length;
		}
	}

	public GeodesicUtils(Ellipsoid ellipsoid) {
		this.ellipsoid=ellipsoid;
	}

	public GeodesicInformation forward(double startLon, double startLat, double startAzimuth, double length) {
		if (abs(length) < 0.01) {
			/* Distance lower than 1cm, that's much more detailed than we want it */
			double az2=startAzimuth+180.0*Units.DEG;
			if (az2>360.0*Units.DEG) {
				az2-=360.0*Units.DEG;
			}
			return new GeodesicInformation(startLon,startLat,startAzimuth,startLon,startLat,az2,length);
		}
		
		final double epsilon=1.0E-10;
		final double f=ellipsoid.getF();
		final double b=ellipsoid.getB();
		final double e2=ellipsoid.getEsquared();
		final double phi1=startLat/Units.RAD;
		final double lamda1=startLon/Units.RAD;
		final double sinphi1=sin(phi1);
		final double cosphi1=cos(phi1);
		final double alpha1=startAzimuth/Units.RAD;
		final double sinalpha1=sin(alpha1);
		final double cosalpha1=cos(alpha1);
		
		if ( epsilon < abs(cosphi1) ) {
			/* Non-polar origin */
			final double tanu1 = (1-f)*sinphi1/cosphi1;
			final double sigma1 = atan2(tanu1,cosalpha1);
			final double cosu1 = 1.0/sqrt(1.0 + tanu1*tanu1);
			final double sinu1 = tanu1*cosu1;
			final double sinalpha = cosu1*sinalpha1;
			final double cos2alphasq = 1.0-sinalpha*sinalpha;
			final double usq = cos2alphasq*e2/(1.0-e2);

			final double ta = 1.0+usq*(4096.0+usq*(-768+usq*(320.0-175.0*usq)))/16384.0;
			final double tb = usq*(256.0+usq*(-128.0+usq*(74.0-47.0*usq)))/1024.0;

			final double firstsigma = length/(b*ta);
			double sigma = firstsigma;
			double csigmam, sinsigma, cossigma, oldsigma;

			do {
				csigmam = cos(2.0*sigma1+sigma);
				sinsigma = sin(sigma);
				cossigma = cos(sigma);
				oldsigma = sigma;
				sigma = firstsigma + tb*sinsigma*(csigmam+tb*(cossigma*(-1.0+2.0*csigmam*csigmam) -  tb*csigmam*(-3.0+4.0*sinsigma*sinsigma)*(-3.0+4.0*csigmam*csigmam)/6.0)/4.0);
			} while ( abs(sigma-oldsigma) > epsilon );
			
			final double temp = sinu1*sinsigma-cosu1*cossigma*cosalpha1;
			
			/* Calculate endpoint latitude */
			final double numerphi2 = sinu1*cossigma+cosu1*sinsigma*cosalpha1;
			final double denomphi2 = (1.0-f)*sqrt(sinalpha*sinalpha+temp*temp);
			final double phi2 = atan2(numerphi2,denomphi2);
			
			/* Calculate endpoint longitude */
			final double numerdlambda = sinsigma*sinalpha1;
			final double denomdlambda = cosu1*cossigma-sinu1*sinsigma*cosalpha1;
			final double dlambda = atan2(numerdlambda, denomdlambda);
			final double tc = f*cos2alphasq*(4.0+f*(4.0-3.0*cos2alphasq))/16.0;
			final double dl = dlambda - (1.0-tc)*f*sinalpha*(sigma+tc*sinsigma*(csigmam+tc*cossigma*(-1.0+2.0*csigmam*csigmam)));
			final double lambda2 = lamda1+dl;
			
			/* Calculate endpoint azimuth */
			final double az2 = atan2(-sinalpha,temp);
			
			double endLatitude = phi2 * Units.RAD;
			double endLongitude = lambda2 * Units.RAD;
			double endAzimuth = az2 * Units.RAD;
			
			if (endLongitude > 180.0 * Units.DEG) {
				endLongitude-=180.0 * Units.DEG;
			}
			if (endLongitude < -180.0 * Units.DEG) {
				endLongitude+=180.0 * Units.DEG;
			}
			if (endAzimuth<=0) {
				endAzimuth+=360.0*Units.DEG;
			}
			
			return new GeodesicInformation(startLon,startLat,startAzimuth,endLongitude,endLatitude,endAzimuth,length);
		} else {
			/* Polar origin */
			final double ds = ellipsoid.getA()*ellipsoid.getM0() - length;
			final double az = ( startLat < 0 ? 180.0 * Units.DEG : 0.0);
			return forward(startLon,0,az,ds);
		}
	}
}
