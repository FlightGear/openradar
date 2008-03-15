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

	public GeodesicInformation direct(double startLongitude, double startLatitude, double startAzimuth, double length) {
		if (abs(length) < 0.01) {
			/* Distance lower than 1cm, that's much more detailed than we want it */
			double az2=startAzimuth+180.0*Units.DEG;
			if (az2>360.0*Units.DEG) {
				az2-=360.0*Units.DEG;
			}
			return new GeodesicInformation(startLongitude,startLatitude,startAzimuth,startLongitude,startLatitude,az2,length);
		}
		
		final double epsilon=1.0E-10;
		final double f=ellipsoid.getF();
		final double b=ellipsoid.getB();
		final double e2=ellipsoid.getEsquared();
		final double phi1=startLatitude/Units.RAD;
		final double lambda1=startLongitude/Units.RAD;
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
			final double cos2alpha = 1.0-sinalpha*sinalpha;
			final double usq = cos2alpha*e2/(1.0-e2);

			final double ta = 1.0+usq*(4096.0+usq*(-768+usq*(320.0-175.0*usq)))/16384.0;
			final double tb = usq*(256.0+usq*(-128.0+usq*(74.0-47.0*usq)))/1024.0;

			final double firstsigma = length/(b*ta);
			double oldsigma;
			double cossigmam2, sinsigma, cossigma;
			double sigma=firstsigma;

			do {
				cossigmam2 = cos(2.0*sigma1+sigma);
				sinsigma = sin(sigma);
				cossigma = cos(sigma);
				oldsigma=sigma;
				sigma = firstsigma+tb*sinsigma*(cossigmam2+tb*(cossigma*(-1.0+2.0*cossigmam2*cossigmam2) -  tb*cossigmam2*(-3.0+4.0*sinsigma*sinsigma)*(-3.0+4.0*cossigmam2*cossigmam2)/6.0)/4.0);
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
			final double tc = f*cos2alpha*(4.0+f*(4.0-3.0*cos2alpha))/16.0;
			final double dl = dlambda - (1.0-tc)*f*sinalpha*(sigma+tc*sinsigma*(cossigmam2+tc*cossigma*(-1.0+2.0*cossigmam2*cossigmam2)));
			final double lambda2 = lambda1+dl;
			
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
			
			return new GeodesicInformation(startLongitude,startLatitude,startAzimuth,endLongitude,endLatitude,endAzimuth,length);
		} else {
			/* Polar origin */
			final double ds = ellipsoid.getA()*ellipsoid.getM0() - length;
			final double az = ( startLatitude < 0 ? 180.0 * Units.DEG : 0.0);
			return direct(startLongitude,0,az,ds);
		}
	}
	
	public GeodesicInformation inverse(double startLon, double startLat, double endLon, double endLat) {
		final double epsilon=1.0E-10;
		final double phi1=startLat/Units.RAD;
		final double phi2=endLat/Units.RAD;
		final double lambda1 = startLon/Units.RAD;
		final double lambda2 = endLon/Units.RAD;
		
		if (abs(lambda2-lambda1)<epsilon && abs(phi2-phi1)<epsilon) {
			/* Start and endpoint are identical */
			return new GeodesicInformation(startLon,startLat,0,endLon,endLat,0,0);
		}
		final double sinphi1=sin(phi1);
		final double cosphi1=cos(phi1);
		
		if (abs(cosphi1)<epsilon) {
			/* Initial station is polar */
			GeodesicInformation geodesicInformation=inverse(endLon,endLat,startLon,startLat);
			return new GeodesicInformation(startLon,startLat,geodesicInformation.endAzimuth,endLon,endLat,geodesicInformation.startAzimuth,geodesicInformation.length);
		}
		final double sinphi2=sin(phi2);
		final double cosphi2=cos(phi2);
		
		if (abs(cosphi2)<epsilon) {
			/* Final point is polar => calculate distance/azimuth to mirrored start point and divide it into two halves */
			GeodesicInformation geodesicInformation=inverse(startLon, startLat, startLon+180.0*Units.DEG,startLat);
			double endAzimuth=geodesicInformation.startAzimuth+180.0*Units.DEG;
			if (endAzimuth>360.0*Units.DEG) {
				endAzimuth-=360.0*Units.DEG;
			}
			return new GeodesicInformation(startLon, startLat, geodesicInformation.startAzimuth, endLon, endLat, endAzimuth, geodesicInformation.length/2);
		}
		
		if (abs(abs(startLon-endLon)-180)<epsilon && abs(startLat+endLat)<epsilon) {
			/* Geodesic passes through the pole */
			// FIXME: Not sure about that...
			GeodesicInformation geodesicInformation1=inverse(startLon,startLat,endLon,startLat);
			GeodesicInformation geodesicInformation2=inverse(endLon,endLat,endLon,startLat);
			
			return new GeodesicInformation(startLon,startLat,geodesicInformation2.startAzimuth,endLon,endLat,geodesicInformation2.startAzimuth,geodesicInformation1.length+geodesicInformation2.length);
		}

		final double dl = lambda2 - lambda1;
		
		final double f=ellipsoid.getF();
		final double b=ellipsoid.getB();
		final double e2=ellipsoid.getEsquared();
		
		final double tanu1 = (1-f)*sinphi1/cosphi1;
		final double cosu1 = 1.0/sqrt(1.0 + tanu1*tanu1);
		final double sinu1 = tanu1*cosu1;
		final double tanu2 = (1-f)*sinphi2/cosphi2;
		final double cosu2 = 1.0/sqrt(1.0 + tanu2*tanu2);
		final double sinu2 = tanu2*cosu2;
		
		final double firstlambda = dl;
		double lambda = firstlambda;
		double oldlambda,sigma;
		double sinlambda,coslambda,cos2alpha,cossigmam2,sinsigma,cossigma;
		
		do {
			sinlambda=sin(lambda);
			coslambda=cos(lambda);
			final double temp1 = cosu2*sinlambda;
			final double temp2 = cosu2*sinu2-sinu1*cosu2*coslambda;
			final double sin2sigma = temp1*temp1+temp2*temp2;
			sinsigma = sqrt(sin2sigma);
			cossigma = sinu1*sinu2+cosu1*cosu2*coslambda;
			sigma = atan2(sinsigma,cossigma);
			final double sinalpha = cosu1*cosu2*sinlambda/sinsigma;
			cos2alpha = 1.0-sinalpha*sinalpha;
			cossigmam2 = cossigma - 2*sinu1*sinu2/cos2alpha;
			final double tc = f*cos2alpha*(4.0+f*(4.0-3.0*cos2alpha))/16.0;
			oldlambda=lambda;
			lambda=dl+(1-tc)*f*sinalpha*(sigma+tc*sinsigma*(cossigmam2+tc*cossigma*(-1.0+2.0*cossigmam2*cossigmam2)));
		} while (abs(lambda-oldlambda) > epsilon);
		
		final double usq = cos2alpha*e2/(1.0-e2);
		final double ta = 1.0+usq*(4096.0+usq*(-768+usq*(320.0-175.0*usq)))/16384.0;
		final double tb = usq*(256.0+usq*(-128.0+usq*(74.0-47.0*usq)))/1024.0;
		
		final double dsigma = tb*sinsigma*(cossigmam2+tb*(cossigma*(-1.0+2.0*cossigmam2*cossigmam2) -  tb*cossigmam2*(-3.0+4.0*sinsigma*sinsigma)*(-3.0+4.0*cossigmam2*cossigmam2)/6.0)/4.0);
		
		final double length = b*ta*(sigma-dsigma);
		
		final double az1=atan2(cosu2*sinlambda,cosu1*sinu2-sinu1*cosu2*coslambda);
		final double az2=atan2(-cosu1*sinlambda,sinu1*cosu2-cosu1*sinu2*coslambda);
		
		double startAzimuth=az1*Units.RAD;
		double endAzimuth=az2*Units.RAD;
		if (startAzimuth<=0) {
			startAzimuth+=360.0*Units.DEG;
		}
		if (endAzimuth<=0) {
			endAzimuth+=360.0*Units.DEG;
		}
		
		return new GeodesicInformation(startLon,startLat,startAzimuth,endLon,endLat,endAzimuth,length);
	}
}
