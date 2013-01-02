package de.knewcleus.fgfs.navdata.test;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.FilteredNavDataStream;
import de.knewcleus.fgfs.navdata.INavDatumFilter;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavDatum;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.AptDatStream;
import de.knewcleus.fgfs.navdata.xplane.AwyDatStream;
import de.knewcleus.fgfs.navdata.xplane.FixDatStream;
import de.knewcleus.fgfs.navdata.xplane.NavDatStream;

public class NavDataTest {

	public static void main(String[] args) throws IOException, NavDataStreamException {
		final File basedir=new File(args[0]);
		final double width = 3.0*Units.DEG;
		final double height = 3.0*Units.DEG;
		final double centerLon = -122.37489 * Units.DEG;
		final double centerLat = 37.61896 * Units.DEG;
		
		final Rectangle2D bounds = new Rectangle2D.Double(
				centerLon-width/2.0, centerLat-height/2.0,
				width, height);
		final Point2D center = new Point2D.Double(centerLon, centerLat);
		final INavDatumFilter<INavDatum> spatialFilter = new SpatialFilter(bounds);
		
		if (false) {
			
			final INavDataStream<INavPoint> navdatStream = openXPlaneNavDat(basedir);
			final INavDataStream<INavPoint> filteredNavDatStream;
			filteredNavDatStream = new FilteredNavDataStream<INavPoint>(navdatStream, spatialFilter);
			
			INavPoint navaid;
			do {
				navaid=filteredNavDatStream.readDatum();
				System.out.println(navaid);
			} while (navaid!=null);
			
			final INavDataStream<IIntersection> fixDatStream = openXPlaneFixDat(basedir);
			final INavDataStream<IIntersection> filteredFixDatStream;
			filteredFixDatStream = new FilteredNavDataStream<IIntersection>(fixDatStream, spatialFilter);
			
			IIntersection intersection;
			do {
				intersection = filteredFixDatStream.readDatum();
				System.out.println(intersection);
			} while (intersection!=null);
			
			final INavDataStream<IAirwaySegment> awyDatStream = openXPlaneAwyDat(basedir);
			final INavDataStream<IAirwaySegment> filteredAwyDatStream;
			filteredAwyDatStream = new FilteredNavDataStream<IAirwaySegment>(awyDatStream, spatialFilter);
			
			IAirwaySegment segment;
			do {
				segment = filteredAwyDatStream.readDatum();
				System.out.println(segment);
			} while (segment!=null);
		}
		
		final INavDataStream<INavPoint> aptDatStream = openXPlaneAptDat(basedir);
		final INavDataStream<INavPoint> filteredAptDatStream;
		filteredAptDatStream = new FilteredNavDataStream<INavPoint>(aptDatStream, spatialFilter);
		
		INavPoint datum;
		do {
			datum = filteredAptDatStream.readDatum();
			System.out.println(datum);
		} while (datum!=null);
	}
	
	protected static INavDataStream<INavPoint> openXPlaneNavDat(File basedir) throws IOException {
		final File inputFile = new File(basedir, "nav.dat.gz");
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new NavDatStream(new InputStreamReader(uncompressedStream));
	}
	
	protected static INavDataStream<IIntersection> openXPlaneFixDat(File basedir) throws IOException {
		final File inputFile = new File(basedir, "fix.dat.gz");
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new FixDatStream(new InputStreamReader(uncompressedStream));
	}
	
	protected static INavDataStream<IAirwaySegment> openXPlaneAwyDat(File basedir) throws IOException {
		final File inputFile = new File(basedir, "awy.dat.gz");
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new AwyDatStream(new InputStreamReader(uncompressedStream));
	}
	
	protected static INavDataStream<INavPoint> openXPlaneAptDat(File basedir) throws IOException {
		final File inputFile = new File(basedir, "apt.dat.gz");
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new AptDatStream(new InputStreamReader(uncompressedStream));
	}
}
