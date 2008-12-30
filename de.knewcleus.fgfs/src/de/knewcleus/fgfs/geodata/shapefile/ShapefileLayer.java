package de.knewcleus.fgfs.geodata.shapefile;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import de.knewcleus.fgfs.geodata.DataFormatException;
import de.knewcleus.fgfs.geodata.Feature;
import de.knewcleus.fgfs.geodata.FeatureDefinition;
import de.knewcleus.fgfs.geodata.FieldDescriptor;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.IDatabaseRow;
import de.knewcleus.fgfs.geodata.IGeodataLayer;
import de.knewcleus.fgfs.geodata.geometry.Geometry;

public class ShapefileLayer implements IGeodataLayer {
	protected final SHPFileReader shpFileReader;
	protected final DBFFileReader dbfFileReader;
	protected final FeatureDefinition featureDefinition;
	protected final int recordCount;
	protected int nextFeatureID=0;
	
	public ShapefileLayer(URL datasource, String layer) throws GeodataException {
		try {
			final URL shapeFileURL=new URL(datasource, layer+".shp");
			final URL dbfFileURL=new URL(datasource, layer+".dbf");
			final URL indexFileURL=new URL(datasource, layer+".shx");
			
			final InputStream shapeFileStream=shapeFileURL.openStream();
			shpFileReader=new SHPFileReader(shapeFileStream);
			final InputStream dbfFileStream=dbfFileURL.openStream();
			DBFFileReader dbfReader;
			try {
				dbfReader=new DBFFileReader(dbfFileStream);
			} catch (IOException e) {
				dbfReader=null;
			}
			dbfFileReader=dbfReader;
			if (dbfFileReader!=null) {
				recordCount=dbfFileReader.getRecordCount();
			} else {
				int indexRecordCount=-1;
				try {
					final InputStream indexFileStream=indexFileURL.openStream();
					final DataInputStream indexFileDataStream=new DataInputStream(indexFileStream);
					indexFileDataStream.skipBytes(24);
					final int indexFileLength=LittleEndianHelper.readInt(indexFileDataStream);
					indexRecordCount=(indexFileLength-100)/8;
				} catch (IOException e) {
					// Ignore
				}
				recordCount=indexRecordCount;
			}
			FieldDescriptor[] fieldDescriptors;
			if (dbfFileReader!=null) {
				fieldDescriptors=dbfFileReader.getFieldDescriptors();
			} else {
				fieldDescriptors=new FieldDescriptor[0];
			}
			
			featureDefinition=new FeatureDefinition(fieldDescriptors);
		} catch (IOException e) {
			throw new GeodataException(e);
		} catch (DataFormatException e) {
			throw new GeodataException(e);
		}
	}
	
	public int getRecordCount() {
		return recordCount;
	}
	
	public FeatureDefinition getFeatureDefinition() {
		return featureDefinition;
	}
	
	public Feature getNextFeature() throws GeodataException {
		final Geometry geometry;
		try {
			geometry=shpFileReader.readRecord();
		} catch (IOException e) {
			throw new GeodataException(e);
		} catch (DataFormatException e) {
			throw new GeodataException(e);
		}
		if (geometry==null)
			return null;
		final int featureID=shpFileReader.getLastFeatureID();
		final IDatabaseRow row;
		if (dbfFileReader!=null) {
			try {
				row=dbfFileReader.readRow(featureID-1);
			} catch (IOException e) {
				throw new GeodataException(e);
			}
		} else {
			row=null;
		}
		return new Feature(featureDefinition, featureID, geometry, row);
	}
}
