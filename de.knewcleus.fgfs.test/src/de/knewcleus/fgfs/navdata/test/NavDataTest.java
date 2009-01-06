package de.knewcleus.fgfs.navdata.test;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import de.knewcleus.fgfs.navdata.FilteredNavDataStream;
import de.knewcleus.fgfs.navdata.INavDatumFilter;
import de.knewcleus.fgfs.navdata.NavDataStreamException;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavDatum;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.AwyDatStream;
import de.knewcleus.fgfs.navdata.xplane.FixDatStream;
import de.knewcleus.fgfs.navdata.xplane.NavDatStream;

public class NavDataTest {

	public static void main(String[] args) throws IOException, NavDataStreamException {
		final INavDatumFilter<INavDatum> filter;
		filter = new SpatialFilter(new Rectangle2D.Double(8, 46, 3.0, 3.0));
		
		final INavDataStream<INavPoint> navdatStream = openXPlaneNavDat(args[0]);
		final INavDataStream<INavPoint> filteredNavDatStream;
		filteredNavDatStream = new FilteredNavDataStream<INavPoint>(navdatStream, filter);
		
		INavPoint navaid;
		do {
			navaid=filteredNavDatStream.readDatum();
			System.out.println(navaid);
		} while (navaid!=null);
		
		final INavDataStream<IIntersection> fixDatStream = openXPlaneFixDat(args[1]);
		final INavDataStream<IIntersection> filteredFixDatStream;
		filteredFixDatStream = new FilteredNavDataStream<IIntersection>(fixDatStream, filter);
		
		IIntersection intersection;
		do {
			intersection = filteredFixDatStream.readDatum();
			System.out.println(intersection);
		} while (intersection!=null);
		
		final INavDataStream<IAirwaySegment> awyDatStream = openXPlaneAwyDat(args[2]);
		final INavDataStream<IAirwaySegment> filteredAwyDatStream;
		filteredAwyDatStream = new FilteredNavDataStream<IAirwaySegment>(awyDatStream, filter);
		
		IAirwaySegment segment;
		do {
			segment = filteredAwyDatStream.readDatum();
			System.out.println(segment);
		} while (segment!=null);
	}
	
	protected static INavDataStream<INavPoint> openXPlaneNavDat(String filename) throws IOException {
		final File inputFile = new File(filename);
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new NavDatStream(new InputStreamReader(uncompressedStream));
	}
	
	protected static INavDataStream<IIntersection> openXPlaneFixDat(String filename) throws IOException {
		final File inputFile = new File(filename);
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new FixDatStream(new InputStreamReader(uncompressedStream));
	}
	
	protected static INavDataStream<IAirwaySegment> openXPlaneAwyDat(String filename) throws IOException {
		final File inputFile = new File(filename);
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		return new AwyDatStream(new InputStreamReader(uncompressedStream));
	}

	protected static class SpatialFilter implements INavDatumFilter<INavDatum> {
		protected final Rectangle2D bounds;
		
		public SpatialFilter(Rectangle2D bounds) {
			this.bounds = bounds;
		}
		
		@Override
		public boolean allow(INavDatum datum) {
			if (datum instanceof INavPoint) {
				final INavPoint point = (INavPoint) datum;
				return bounds.contains(point.getGeographicPosition());
			} else if (datum instanceof IAirwaySegment) {
				final IAirwaySegment segment = (IAirwaySegment) datum;
				final IIntersection start, end;
				start = segment.getStartPoint();
				end = segment.getEndPoint();
				final Line2D line = new Line2D.Double(start.getGeographicPosition(), end.getGeographicPosition());
				return line.intersects(bounds);
			}
			return true;
		}
	}
}
