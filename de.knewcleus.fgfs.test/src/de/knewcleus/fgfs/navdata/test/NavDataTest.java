package de.knewcleus.fgfs.navdata.test;

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
import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.xplane.NavDatStream;

public class NavDataTest {

	public static void main(String[] args) throws IOException, NavDataStreamException {
		final File inputFile = new File(args[0]);
		final InputStream compressedStream = new FileInputStream(inputFile);
		final GZIPInputStream uncompressedStream = new GZIPInputStream(compressedStream);
		final NavDatStream navStream = new NavDatStream(new InputStreamReader(uncompressedStream));
		final INavDataStream<INavPoint> filteredStream;
		final INavDatumFilter<INavPoint> filter;
		
		filter = new SpatialFilter(new Rectangle2D.Double(8, 46, 3.0, 3.0));
		filteredStream = new FilteredNavDataStream<INavPoint>(navStream, filter);
		
		INavPoint point;
		do {
			point=filteredStream.readDatum();
			System.out.println(point);
		} while (point!=null);
	}

	protected static class SpatialFilter implements INavDatumFilter<INavPoint> {
		protected final Rectangle2D bounds;
		
		public SpatialFilter(Rectangle2D bounds) {
			this.bounds = bounds;
		}
		
		@Override
		public boolean allow(INavPoint datum) {
			return bounds.contains(datum.getGeographicPosition());
		}
	}
}
