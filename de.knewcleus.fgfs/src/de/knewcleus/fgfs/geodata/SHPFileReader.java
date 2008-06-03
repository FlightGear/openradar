package de.knewcleus.fgfs.geodata;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SHPFileReader {
	protected final DataInputStream shapeDataStream;
	
	protected final static int TYPE_NULL_SHAPE=0;
	protected final static int TYPE_POINT=1;
	protected final static int TYPE_POLYLINE=3;
	protected final static int TYPE_POLYGON=5;
	protected final static int TYPE_MULTIPOINT=8;
	protected final static int TYPE_POINTZ=11;
	protected final static int TYPE_POLYLINEZ=13;
	protected final static int TYPE_POLYGONZ=15;
	protected final static int TYPE_MULTIPOINTZ=18;
	protected final static int TYPE_POINTM=21;
	protected final static int TYPE_POLYLINEM=23;
	protected final static int TYPE_POLYGONM=25;
	protected final static int TYPE_MULTIPOINTM=28;
	protected final static int TYPE_MULTIPATCH=31;
	
	protected final int fileLengthBytes;
	protected final int version;
	protected final int shapeType;
	protected final double xMin, xMax, yMin, yMax, zMin, zMax, mMin, mMax;
	protected int lastRecordNumber=-1;
	
	public SHPFileReader(InputStream shapeFileStream) throws IOException, DataFormatException {
		this.shapeDataStream=new DataInputStream(shapeFileStream);
		
		if (shapeDataStream.readInt()!=9994) {
			throw new DataFormatException("Invalid shapefile header");
		}
		shapeDataStream.skipBytes(20);
		fileLengthBytes=shapeDataStream.readInt()*2;
		version=LittleEndianHelper.readInt(shapeDataStream);
		shapeType=LittleEndianHelper.readInt(shapeDataStream);
		xMin=LittleEndianHelper.readDouble(shapeDataStream);
		yMin=LittleEndianHelper.readDouble(shapeDataStream);
		xMax=LittleEndianHelper.readDouble(shapeDataStream);
		yMax=LittleEndianHelper.readDouble(shapeDataStream);
		zMin=LittleEndianHelper.readDouble(shapeDataStream);
		zMax=LittleEndianHelper.readDouble(shapeDataStream);
		mMin=LittleEndianHelper.readDouble(shapeDataStream);
		mMax=LittleEndianHelper.readDouble(shapeDataStream);
	}
	
	public SHPFileReader(URL location) throws DataFormatException, IOException {
		this(new DataInputStream(location.openStream()));
	}
	
	public SHPFileReader(File shapeFile) throws IOException, DataFormatException {
		this(new DataInputStream(new FileInputStream(shapeFile)));
	}
	
	public double getXMin() {
		return xMin;
	}

	public double getXMax() {
		return xMax;
	}

	public double getYMin() {
		return yMin;
	}

	public double getYMax() {
		return yMax;
	}

	public double getZMin() {
		return zMin;
	}

	public double getZMax() {
		return zMax;
	}

	public double getMMin() {
		return mMin;
	}

	public double getMMax() {
		return mMax;
	}
	
	public int getLastRecordNumber() {
		return lastRecordNumber;
	}
	
	public Geometry readRecord() throws IOException, DataFormatException {
		final int contentLength;
		lastRecordNumber=shapeDataStream.readInt();
		contentLength=shapeDataStream.readInt()*2;
		final int recordShapeType=LittleEndianHelper.readInt(shapeDataStream);
		
		switch (recordShapeType) {
		case TYPE_NULL_SHAPE:
			return NullShape.getInstance();
		case TYPE_POINT:
			return readPoint(false, false);
		case TYPE_POINTM:
			return readPoint(false, true);
		case TYPE_POINTZ:
			return readPoint(true, true);
		case TYPE_POLYLINE:
			return readPolyLineRecord(false, false);
		case TYPE_POLYLINEM:
			return readPolyLineRecord(false, true);
		case TYPE_POLYLINEZ:
			return readPolyLineRecord(true, true);
		case TYPE_POLYGON:
			return readPolygonRecord(false, false);
		case TYPE_POLYGONM:
			return readPolygonRecord(false, true);
		case TYPE_POLYGONZ:
			return readPolygonRecord(true, true);
		default:
			shapeDataStream.skipBytes(2*contentLength);
			throw new DataFormatException("Cannot read records of type "+Integer.toHexString(recordShapeType)+", index "+Integer.toHexString(lastRecordNumber));
		}
	}
	
	protected Point readPoint(boolean hasZ, boolean hasM) throws IOException {
		final double x=LittleEndianHelper.readDouble(shapeDataStream);
		final double y=LittleEndianHelper.readDouble(shapeDataStream);
		final double z=(hasZ?LittleEndianHelper.readDouble(shapeDataStream):0);
		final double m=(hasM?LittleEndianHelper.readDouble(shapeDataStream):0);
		return new Point(x,y,z,m,hasZ,hasM);
	}
	
	protected Geometry readPolyLineRecord(boolean hasZ, boolean hasM) throws IOException {
		shapeDataStream.skipBytes(4*8); // skip bounding box, we're calculating our own
		final int numParts=LittleEndianHelper.readInt(shapeDataStream);
		final int numPoints=LittleEndianHelper.readInt(shapeDataStream);
		final int parts[]=new int[numParts];
		final double points[]=new double[numPoints*2];
		final double pointsz[];
		final double pointsm[];
		
		for (int i=0;i<numParts;i++) {
			parts[i]=LittleEndianHelper.readInt(shapeDataStream);
		}
		
		for (int i=0;i<numPoints*2;i++) {
			points[i]=LittleEndianHelper.readDouble(shapeDataStream);
		}
		
		if (hasZ) {
			shapeDataStream.skipBytes(2*8); // skip z-range
			pointsz=new double[numPoints];
			for (int i=0;i<numPoints;i++) {
				pointsz[i]=LittleEndianHelper.readDouble(shapeDataStream);
			}
		} else {
			pointsz=null;
		}
		
		if (hasM) {
			shapeDataStream.skipBytes(2*8); // skip m-range
			pointsm=new double[numPoints];
			for (int i=0;i<numPoints;i++) {
				pointsm[i]=LittleEndianHelper.readDouble(shapeDataStream);
			}
		} else {
			pointsm=null;
		}
		
		final MultiLineString multiLineString=new MultiLineString();
		
		int j=0;
		
		for (int i=0;i<numParts;i++) {
			final int endIndex=(i+1<numParts?parts[i+1]:numPoints);
			final LineString lineString=new LineString();
			for (;j<endIndex;j++) {
				final Point point=new Point(points[2*j], points[2*j+1], (hasZ?pointsz[j]:0), (hasM?pointsm[j]:0), hasZ, hasM);
				lineString.add(point);
			}
			multiLineString.add(lineString);
		}
		
		if (numParts==1) {
			return multiLineString.getContainedGeometry().get(0);
		} else {
			return multiLineString;
		}
	}
	
	protected Geometry readPolygonRecord(boolean hasZ, boolean hasM) throws IOException, DataFormatException {
		shapeDataStream.skipBytes(4*8); // skip bounding box, we're calculating our own
		final int numParts=LittleEndianHelper.readInt(shapeDataStream);
		final int numPoints=LittleEndianHelper.readInt(shapeDataStream);
		final int parts[]=new int[numParts];
		final double points[]=new double[numPoints*2];
		final double pointsz[];
		final double pointsm[];
		
		for (int i=0;i<numParts;i++) {
			parts[i]=LittleEndianHelper.readInt(shapeDataStream);
		}
		
		for (int i=0;i<numPoints*2;i++) {
			points[i]=LittleEndianHelper.readDouble(shapeDataStream);
		}
		
		if (hasZ) {
			shapeDataStream.skipBytes(2*8); // skip z-range
			pointsz=new double[numPoints];
			for (int i=0;i<numPoints;i++) {
				pointsz[i]=LittleEndianHelper.readDouble(shapeDataStream);
			}
		} else {
			pointsz=null;
		}
		
		if (hasM) {
			shapeDataStream.skipBytes(2*8); // skip m-range
			pointsm=new double[numPoints];
			for (int i=0;i<numPoints;i++) {
				pointsm[i]=LittleEndianHelper.readDouble(shapeDataStream);
			}
		} else {
			pointsm=null;
		}
		
		final List<Ring> innerRings=new ArrayList<Ring>();
		Ring outerRing=null;
		
		int j=0;
		
		for (int i=0;i<numParts;i++) {
			final int endIndex=(i+1<numParts?parts[i+1]:numPoints);
			final Ring ring=new Ring();
			for (;j<endIndex;j++) {
				final Point point=new Point(points[2*j], points[2*j+1], (hasZ?pointsz[j]:0), (hasM?pointsm[j]:0), hasZ, hasM);
				ring.add(point);
			}
			ring.closeRing();
			if (ring.isClockWise()) {
				if (outerRing!=null) {
					throw new DataFormatException("Cannot handle polygon with multiple outer rings");
				}
				outerRing=ring;
			} else {
				innerRings.add(ring);
			}
		}
		
		if (outerRing==null) {
			throw new DataFormatException("Polygon has no outer ring");
		}
		
		final Polygon polygon=new Polygon();
		polygon.add(outerRing);
		for (Ring innerRing: innerRings) {
			polygon.add(innerRing);
		}
		
		return polygon;
	}
}
